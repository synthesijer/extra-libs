package synthesijer.lib.axi;

import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLUtils;
import synthesijer.hdl.expr.HDLPreDefinedConstant;
import synthesijer.hdl.expr.HDLValue;
import synthesijer.hdl.sequencer.SequencerState;
import synthesijer.utils.FifoPort;
import synthesijer.utils.Utils;

public class AXI_Writer extends HDLModule{
	
	private FifoPort fifo;
	private AxiMasterWritePort port;
	private HDLPort req, busy;
		
	private HDLPort addr, len;
	
	private HDLSignal write_counter = newSignal("write_counter", HDLPrimitiveType.genSignedType(32));
	
	private HDLExpr fifo_ready, wready, write_done, awready_high;
	
	private HDLPort debug;

	public AXI_Writer(){
		super("axi_writer", "clk", "reset");
		int width = 256;
		newParameter("BUF_WIDTH", HDLPrimitiveType.genIntegerType(), String.valueOf(width));
		
		fifo = new FifoPort(this, "fifo_", width);
		port = new AxiMasterWritePort(this, "S_AXI_", width);
		
		req = Utils.genInputPort(this, "request");
		busy = Utils.genOutputPort(this, "busy");
		
		addr = Utils.genInputPort(this, "addr", 32);
		len = Utils.genInputPort(this, "len", 8);
		
		debug = Utils.genOutputPort(this, "debug", 8);
		
		fifo.rclk.getSignal().setAssign(null, getSysClk().getSignal());
		
		fifo_ready =
				newExpr(HDLOp.OR,
						newExpr(HDLOp.GEQ, fifo.length.getSignal(), len.getSignal()),
						newExpr(HDLOp.AND,
								newExpr(HDLOp.EQ, fifo.empty.getSignal(), HDLPreDefinedConstant.LOW),
								newExpr(HDLOp.EQ, fifo.length.getSignal(), HDLPreDefinedConstant.INTEGER_ZERO)));
		wready = newExpr(HDLOp.EQ, port.wready.getSignal(), HDLPreDefinedConstant.HIGH);
		write_done = newExpr(HDLOp.EQ, write_counter, HDLPreDefinedConstant.INTEGER_ZERO);
		awready_high = newExpr(HDLOp.EQ, port.awready.getSignal(), HDLPreDefinedConstant.HIGH);

		setDefaultSetting(port);
		
		genStateMachine(newSequencer("main"));
		
	}

	private SequencerState genInit(HDLSequencer s){
		SequencerState state = s.addSequencerState("init");
		debug.getSignal().setAssign(state, new HDLValue("1", HDLPrimitiveType.genVectorType(8)));
		port.awaddr.getSignal().setAssign(state, addr.getSignal());
		port.awvalid.getSignal().setAssign(state, newExpr(HDLOp.IF, fifo_ready, HDLPreDefinedConstant.HIGH, HDLPreDefinedConstant.LOW));
		port.awlen.getSignal().setAssign(state, newExpr(HDLOp.SUB, len.getSignal(), HDLPreDefinedConstant.INTEGER_ONE));
		write_counter.setAssign(state, newExpr(HDLOp.PADDINGHEAD_ZERO, len.getSignal(), new HDLValue("24", HDLPrimitiveType.genIntegerType())));
		return state;
	}

	private SequencerState genWrite0(HDLSequencer s){
		SequencerState state = s.addSequencerState("write0");
		debug.getSignal().setAssign(state, new HDLValue("2", HDLPrimitiveType.genVectorType(8)));
		port.awvalid.getSignal().setAssign(state, newExpr(HDLOp.IF, awready_high, HDLPreDefinedConstant.LOW, HDLPreDefinedConstant.HIGH));
		fifo.re.getSignal().setAssign(state, HDLPreDefinedConstant.HIGH);
		return state;
	}

	private SequencerState genWritePre(HDLSequencer s){
		SequencerState state = s.addSequencerState("write_pre");		
		debug.getSignal().setAssign(state, new HDLValue("3", HDLPrimitiveType.genVectorType(8)));
		fifo.re.getSignal().setAssign(state, HDLPreDefinedConstant.LOW);
		return state;
	}
	
	private SequencerState genWrite(HDLSequencer s){
		SequencerState state = s.addSequencerState("write");
		debug.getSignal().setAssign(state, new HDLValue("4", HDLPrimitiveType.genVectorType(8)));
		port.wdata.getSignal().setAssign(state, fifo.din.getSignal());
		port.wvalid.getSignal().setAssign(state, HDLPreDefinedConstant.HIGH);
		write_counter.setAssign(state, newExpr(HDLOp.SUB, write_counter, HDLPreDefinedConstant.INTEGER_ONE));
		HDLExpr last_word = newExpr(HDLOp.EQ, write_counter, HDLPreDefinedConstant.INTEGER_ONE);
		port.wlast.getSignal().setAssign(state, last_word); 
		fifo.re.getSignal().setAssign(state, newExpr(HDLOp.IF, last_word, 
				                                         HDLPreDefinedConstant.LOW, // last word
				                                         HDLPreDefinedConstant.HIGH // for next
				                          )); 
		return state;
	}

	private SequencerState genWriteNext(HDLSequencer s){
		SequencerState state = s.addSequencerState("write_next");
		debug.getSignal().setAssign(state, new HDLValue("5", HDLPrimitiveType.genVectorType(8)));
		fifo.re.getSignal().setAssign(state, HDLPreDefinedConstant.LOW);
		port.wlast.getSignal().setAssign(state, newExpr(HDLOp.IF, wready, HDLPreDefinedConstant.LOW, port.wlast.getSignal()));
		port.wvalid.getSignal().setAssign(state, newExpr(HDLOp.IF, wready, HDLPreDefinedConstant.LOW, port.wvalid.getSignal()));
		return state;
	}
	
	private void genStateMachine(HDLSequencer s){
		SequencerState idle = s.getIdleState();
		// after reset
		port.bready.getSignal().setAssign(idle, HDLPreDefinedConstant.HIGH);
		debug.getSignal().setAssign(idle, new HDLValue("0", HDLPrimitiveType.genVectorType(8)));
		
		SequencerState init = genInit(s);
		SequencerState write0 = genWrite0(s);
		SequencerState write_pre = genWritePre(s);
		SequencerState write = genWrite(s);
		SequencerState write_next = genWriteNext(s);
		
		// idle -> init when fifo is ready
		HDLExpr req_assert = newExpr(HDLOp.EQ, req.getSignal(), HDLPreDefinedConstant.HIGH);
		busy.getSignal().setAssign(idle, req_assert);
		idle.addStateTransit(req_assert, init);
		
		// init -> write0
		fifo.we.getSignal().setAssign(idle, HDLPreDefinedConstant.LOW);
		init.addStateTransit(fifo_ready, write0);
		
		// write0 -> write_pre
		write0.addStateTransit(awready_high, write_pre);

		// write_pre -> write
		write_pre.addStateTransit(write);
		
		// write -> write_next
		write.addStateTransit(write_next);
		
		// write_next -> idle, write
		write_next.addStateTransit(newExpr(HDLOp.AND, wready, write_done), idle);
		write_next.addStateTransit(newExpr(HDLOp.AND, wready, newExpr(HDLOp.NOT, write_done)), write);
		
	}

	// for 256-bit width
	private void setDefaultSetting(AxiMasterWritePort port){
		// Bytes in transfer: 32
		port.awsize.getSignal().setAssign(null, new HDLValue(String.valueOf(0b101), HDLPrimitiveType.genVectorType(3)));
		// Burst type encoding: INCR
		port.awburst.getSignal().setAssign(null, new HDLValue(String.valueOf(0b01), HDLPrimitiveType.genVectorType(2)));
		// Normal Non-cache-able Buffer
		port.awcache.getSignal().setAssign(null, new HDLValue(String.valueOf(0b0011), HDLPrimitiveType.genVectorType(4)));
		// protocol
		port.awprot.getSignal().setAssign(null, new HDLValue(String.valueOf(0b000), HDLPrimitiveType.genVectorType(3)));
		// strobe
		//port.wstrb.getSignal().setAssign(null, new HDLValue(String.valueOf(0b11111111), HDLPrimitiveType.genVectorType(8)));
		port.wstrb.getSignal().setAssign(null, new HDLValue(String.valueOf(0xFFFFFFFF), HDLPrimitiveType.genVectorType(32)));
	}
	
	public static void main(String... args){
		AXI_Writer reader = new AXI_Writer();
		HDLUtils.generate(reader, HDLUtils.VHDL);
		HDLUtils.generate(reader, HDLUtils.Verilog);
	}

}
