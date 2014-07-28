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
import synthesijer.utils.FifoPort;
import synthesijer.utils.Utils;

public class AXI_Writer extends HDLModule{
	
	private FifoPort fifo;
	private AxiMasterWritePort port;
	private HDLPort req, busy;
	
	private HDLPort addr, len;

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
		
		fifo.rclk.getSignal().setAssign(null, getSysClk().getSignal());
		
		setDefaultSetting(port);
		
		genStateMachine(newSequencer("main"));
		
	}
	
	private void genStateMachine(HDLSequencer s){
		HDLSequencer.SequencerState idle = s.getIdleState();
		// after reset
		port.bready.getSignal().setAssign(idle, HDLPreDefinedConstant.HIGH);
		
		// IDLE: wait for request
		HDLSequencer.SequencerState init = s.addSequencerState("init");
		HDLExpr req_assert = newExpr(HDLOp.EQ, req.getSignal(), HDLPreDefinedConstant.HIGH);
		idle.addStateTransit(req_assert, init); // idle -> init
		busy.getSignal().setAssign(idle, req_assert);
		fifo.we.getSignal().setAssign(idle, HDLPreDefinedConstant.LOW);
		
		// INIT: check whether FIFO is ready or not 
		HDLSequencer.SequencerState write0 = s.addSequencerState("write0");
		System.out.println(newExpr(HDLOp.EQ, fifo.length.getSignal(), HDLPreDefinedConstant.INTEGER_ZERO));
		System.out.println(fifo.length.getSignal());
		HDLExpr fifo_ready =
				newExpr(HDLOp.OR,
						newExpr(HDLOp.GEQ, fifo.length.getSignal(), len.getSignal()),
						newExpr(HDLOp.AND,
								newExpr(HDLOp.EQ, fifo.empty.getSignal(), HDLPreDefinedConstant.LOW),
								newExpr(HDLOp.EQ, fifo.length.getSignal(), HDLPreDefinedConstant.INTEGER_ZERO)));
		init.addStateTransit(fifo_ready, write0); // init -> write0
		port.awaddr.getSignal().setAssign(init, addr.getSignal());
		port.awvalid.getSignal().setAssign(init, newExpr(HDLOp.IF, fifo_ready, HDLPreDefinedConstant.HIGH, HDLPreDefinedConstant.LOW));
		port.awlen.getSignal().setAssign(init, len.getSignal());
		HDLSignal write_counter = newSignal("write_counter", HDLPrimitiveType.genSignedType(32));
		write_counter.setAssign(init, newExpr(HDLOp.PADDINGHEAD_ZERO, port.awlen.getSignal(), new HDLValue("24", HDLPrimitiveType.genIntegerType())));
		
		// WRITE0
		HDLExpr awready_high = newExpr(HDLOp.EQ, port.awready.getSignal(), HDLPreDefinedConstant.HIGH);
		port.awvalid.getSignal().setAssign(write0, newExpr(HDLOp.IF, awready_high, HDLPreDefinedConstant.LOW, HDLPreDefinedConstant.HIGH));
		HDLSequencer.SequencerState write_pre = s.addSequencerState("write_pre");
		write0.addStateTransit(awready_high, write_pre); // write0 -> write_pre
		fifo.re.getSignal().setAssign(write0, HDLPreDefinedConstant.HIGH);
		
		// WRITE_PRE
		HDLSequencer.SequencerState write = s.addSequencerState("write");
		write_pre.addStateTransit(write); // write_pre -> write
		fifo.re.getSignal().setAssign(write_pre, HDLPreDefinedConstant.LOW);
		
		// WRITE
		port.wdata.getSignal().setAssign(write, fifo.din.getSignal()); // port.wdata <- fifo.wdata
		port.wvalid.getSignal().setAssign(write, HDLPreDefinedConstant.HIGH);
		write_counter.setAssign(write, newExpr(HDLOp.SUB, write_counter, HDLPreDefinedConstant.INTEGER_ONE));
		HDLSequencer.SequencerState write_next = s.addSequencerState("write_next");
		write.addStateTransit(write_next); // write -> write_next
		HDLExpr last_word = newExpr(HDLOp.EQ, write_counter, HDLPreDefinedConstant.INTEGER_ONE);
		port.wlast.getSignal().setAssign(write0, last_word); 
		fifo.re.getSignal().setAssign(write0, 
				newExpr(HDLOp.IF,	last_word, // last word
						HDLPreDefinedConstant.LOW,
						HDLPreDefinedConstant.HIGH)); // for next
		
		// WRITE_NEXT
		fifo.re.getSignal().setAssign(write0, HDLPreDefinedConstant.LOW);
		HDLExpr wready = newExpr(HDLOp.EQ, port.wready.getSignal(), HDLPreDefinedConstant.HIGH);
		HDLExpr write_done = newExpr(HDLOp.EQ, write_counter, HDLPreDefinedConstant.INTEGER_ZERO);
		write_next.addStateTransit(newExpr(HDLOp.AND, wready, write_done), idle);
		write_next.addStateTransit(newExpr(HDLOp.AND, wready, newExpr(HDLOp.NOT, write_done)), write);
		port.wlast.getSignal().setAssign(write_next, newExpr(HDLOp.IF, wready, HDLPreDefinedConstant.LOW, port.wlast.getSignal()));
	}

	private void setDefaultSetting(AxiMasterWritePort port){
		// Bytes in transfer: 8
		port.awsize.getSignal().setAssign(null, new HDLValue(String.valueOf(0b011), HDLPrimitiveType.genVectorType(3)));
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
