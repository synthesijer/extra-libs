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
import synthesijer.utils.MemoryReadPort;
import synthesijer.utils.Utils;

public class AXI_Writer_Buffer extends HDLModule{
	
	public final MemoryReadPort mem;
	public final AxiMasterWritePort port;
	public final HDLPort req, busy;
		
	public final HDLPort addr, len;
	
	private HDLSignal write_counter = newSignal("write_counter", HDLPrimitiveType.genSignedType(32));
	
	private HDLExpr wready, write_done, awready_high;
	private HDLSignal next_read_addr;
	
	private HDLPort debug;
	
	private final int width;

	public AXI_Writer_Buffer(int width){
		super("axi_writer_" + width, "clk", "reset");
		
		this.width = width;

		newParameter("BUF_WIDTH", HDLPrimitiveType.genIntegerType(), String.valueOf(width));
		
		mem = new MemoryReadPort(this, "mem_", width);
		port = new AxiMasterWritePort(this, "S_AXI_", width);
		
		req = Utils.genInputPort(this, "request");
		busy = Utils.genOutputPort(this, "busy");
		
		addr = Utils.genInputPort(this, "addr", 32);
		len = Utils.genInputPort(this, "len", 32);
		
		debug = Utils.genOutputPort(this, "debug", 8);
		
		mem.rclk.getSignal().setAssign(null, getSysClk().getSignal());
		next_read_addr = newSignal("next_read_addr", HDLPrimitiveType.genSignedType(32));
		
		wready = newExpr(HDLOp.EQ, port.wready.getSignal(), HDLPreDefinedConstant.HIGH);
		write_done = newExpr(HDLOp.EQ, write_counter, HDLPreDefinedConstant.INTEGER_ZERO);
		awready_high = newExpr(HDLOp.EQ, port.awready.getSignal(), HDLPreDefinedConstant.HIGH);

		port.setDefaultSetting(width);
		
		genStateMachine(newSequencer("main"));
		
	}

	private SequencerState genInit(HDLSequencer s){
		SequencerState state = s.addSequencerState("init");
		debug.getSignal().setAssign(state, new HDLValue("1", HDLPrimitiveType.genVectorType(8)));
		port.awaddr.getSignal().setAssign(state, addr.getSignal());
		port.awvalid.getSignal().setAssign(state, HDLPreDefinedConstant.HIGH);
		port.awlen.getSignal().setAssign(state, newExpr(HDLOp.DROPHEAD, newExpr(HDLOp.SUB, len.getSignal(), HDLPreDefinedConstant.INTEGER_ONE), Utils.value(24, 32)));
		write_counter.setAssign(state, len.getSignal());
		return state;
	}

	private SequencerState genWrite0(HDLSequencer s){
		SequencerState state = s.addSequencerState("write0");
		debug.getSignal().setAssign(state, new HDLValue("2", HDLPrimitiveType.genVectorType(8)));
		port.awvalid.getSignal().setAssign(state, newExpr(HDLOp.IF, awready_high, HDLPreDefinedConstant.LOW, HDLPreDefinedConstant.HIGH));
		mem.oe.getSignal().setAssign(state, HDLPreDefinedConstant.HIGH);
		mem.address.getSignal().setAssign(state, HDLPreDefinedConstant.VECTOR_ZERO);
		next_read_addr.setAssign(state, newExpr(HDLOp.ADD, next_read_addr, 1));
		return state;
	}

	private SequencerState genWritePre(HDLSequencer s){
		SequencerState state = s.addSequencerState("write_pre");		
		debug.getSignal().setAssign(state, new HDLValue("3", HDLPrimitiveType.genVectorType(8)));
		mem.oe.getSignal().setAssign(state, HDLPreDefinedConstant.LOW);
		return state;
	}
	
	private SequencerState genWrite(HDLSequencer s){
		SequencerState state = s.addSequencerState("write");
		debug.getSignal().setAssign(state, new HDLValue("4", HDLPrimitiveType.genVectorType(8)));
		port.wdata.getSignal().setAssign(state, mem.din.getSignal());
		port.wvalid.getSignal().setAssign(state, HDLPreDefinedConstant.HIGH);
		write_counter.setAssign(state, newExpr(HDLOp.SUB, write_counter, HDLPreDefinedConstant.INTEGER_ONE));
		HDLExpr last_word = newExpr(HDLOp.EQ, write_counter, HDLPreDefinedConstant.INTEGER_ONE);
		port.wlast.getSignal().setAssign(state, last_word); 
		mem.oe.getSignal().setAssign(state, newExpr(HDLOp.IF, last_word, 
				HDLPreDefinedConstant.LOW, // last word
				HDLPreDefinedConstant.HIGH // for next
				));
		mem.address.getSignal().setAssign(state, next_read_addr);
		next_read_addr.setAssign(state, newExpr(HDLOp.ADD, next_read_addr, 1));
		return state;
	}

	private SequencerState genWriteNext(HDLSequencer s){
		SequencerState state = s.addSequencerState("write_next");
		debug.getSignal().setAssign(state, new HDLValue("5", HDLPrimitiveType.genVectorType(8)));
		mem.oe.getSignal().setAssign(state, HDLPreDefinedConstant.LOW);
		port.wlast.getSignal().setAssign(state, newExpr(HDLOp.IF, wready, HDLPreDefinedConstant.LOW, port.wlast.getSignal()));
		port.wvalid.getSignal().setAssign(state, newExpr(HDLOp.IF, wready, HDLPreDefinedConstant.LOW, port.wvalid.getSignal()));
		return state;
	}
	
	private void genStateMachine(HDLSequencer s){
		SequencerState idle = s.getIdleState();
		// after reset
		port.bready.getSignal().setAssign(idle, HDLPreDefinedConstant.HIGH);
		debug.getSignal().setAssign(idle, new HDLValue("0", HDLPrimitiveType.genVectorType(8)));
		next_read_addr.setAssign(idle, HDLPreDefinedConstant.INTEGER_ZERO);
		
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
		init.addStateTransit(write0);
		
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
	
	public static void main(String... args){
		int [] width = new int[]{8, 16, 32, 64, 128, 256, 512};
		for(int w: width){
			AXI_Writer_Buffer writer = new AXI_Writer_Buffer(w);
			HDLUtils.genHDLSequencerDump(writer);
			HDLUtils.genResourceUsageTable(writer);
			HDLUtils.generate(writer, HDLUtils.VHDL);
			HDLUtils.generate(writer, HDLUtils.Verilog);
		}
	}

}
