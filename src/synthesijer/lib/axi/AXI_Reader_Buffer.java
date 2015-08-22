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
import synthesijer.utils.MemoryWritePort;

public class AXI_Reader_Buffer extends HDLModule{
	
	public final MemoryWritePort mem;
	public final AxiMasterReadPort port;
	public final HDLPort req, busy;
	
	public final HDLPort addr, len;

	private HDLExpr arready_high, rvalid_high;
	private HDLSignal next_write_addr;
	
	private final int width;

	public AXI_Reader_Buffer(int width){
		super("axi_reader_buffer_" + width, "clk", "reset");
		this.width = width;
		newParameter("BUF_WIDTH", HDLPrimitiveType.genIntegerType(), new HDLValue(width));

		mem = new MemoryWritePort(this, "mem_", 32);
		port = new AxiMasterReadPort(this, "S_AXI_", width);
		
		req = HDLUtils.genInputPort(this, "request");
		busy = HDLUtils.genOutputPort(this, "busy");
		
		addr = HDLUtils.genInputPort(this, "addr", 32);
		len = HDLUtils.genInputPort(this, "len", 32);
		
		mem.wclk.getSignal().setAssign(null, getSysClk().getSignal());
		
		next_write_addr = newSignal("next_write_addr", HDLPrimitiveType.genSignedType(32));

		arready_high = newExpr(HDLOp.EQ, port.arready.getSignal(), HDLPreDefinedConstant.HIGH);
		rvalid_high = newExpr(HDLOp.EQ, port.rvalid.getSignal(), HDLPreDefinedConstant.HIGH);

		port.setDefaultSetting();
		
		genStateMachine(newSequencer("main"));
		
	}
	
	private HDLSignal read_length;
	private SequencerState genInit(HDLSequencer s){
		SequencerState state = s.addSequencerState("init");
		port.araddr.getSignal().setAssign(state, addr.getSignal());
		port.arvalid.getSignal().setAssign(state, HDLPreDefinedConstant.HIGH);
		port.arlen.getSignal().setAssign(state, newExpr(HDLOp.DROPHEAD, newExpr(HDLOp.SUB, len.getSignal(), HDLPreDefinedConstant.INTEGER_ONE), HDLUtils.value(24, 32)));
		read_length = newSignal("read_length", HDLPrimitiveType.genSignedType(32));
		read_length.setAssign(state, len.getSignal());
		return state;
	}
	
	private SequencerState genRead0(HDLSequencer s){
		SequencerState state = s.addSequencerState("read0");
		port.arvalid.getSignal().setAssign(state, newExpr(HDLOp.IF, arready_high, HDLPreDefinedConstant.LOW, HDLPreDefinedConstant.HIGH));
		read_length.setAssign(state,
				newExpr(HDLOp.IF, rvalid_high,
						newExpr(HDLOp.SUB, read_length, HDLPreDefinedConstant.INTEGER_ONE),
						read_length));
		mem.dout.getSignal().setAssign(state, port.rdata.getSignal());
		mem.we.getSignal().setAssign(state, rvalid_high);
		mem.address.getSignal().setAssign(state, HDLPreDefinedConstant.VECTOR_ZERO);
		next_write_addr.setAssign(state, newExpr(HDLOp.ADD, next_write_addr, 1));
		return state;
	}
	
	private SequencerState genRead(HDLSequencer s){
		SequencerState state = s.addSequencerState("read");
		read_length.setAssign(state,
				newExpr(HDLOp.IF, rvalid_high,
						newExpr(HDLOp.SUB, read_length, HDLPreDefinedConstant.INTEGER_ONE),
						read_length));
		mem.dout.getSignal().setAssign(state, port.rdata.getSignal());
		mem.we.getSignal().setAssign(state, rvalid_high);
		mem.address.getSignal().setAssign(state, next_write_addr);
		next_write_addr.setAssign(state, newExpr(HDLOp.ADD, next_write_addr, 1));
		return state;
	}
	
	private void genStateMachine(HDLSequencer s){
		SequencerState idle = s.getIdleState();
		// after reset
		port.rready.getSignal().setAssign(idle, HDLPreDefinedConstant.HIGH);

		// IDLE
		HDLExpr req_assert = newExpr(HDLOp.EQ, req.getSignal(), HDLPreDefinedConstant.HIGH);
		busy.getSignal().setAssign(idle, req_assert);
		mem.we.getSignal().setDefaultValue(HDLPreDefinedConstant.LOW);
		next_write_addr.setAssign(idle, HDLPreDefinedConstant.INTEGER_ZERO);
		
		SequencerState init = genInit(s);
		SequencerState read0 = genRead0(s);
		SequencerState read = genRead(s);
		
		// idle -> init
		idle.addStateTransit(req_assert, init);

		// init -> read0
		init.addStateTransit(read0);
		
		// READ0
		read0.addStateTransit(arready_high, read); // read0 -> read
		
		// READ
		HDLExpr rlast_high = newExpr(HDLOp.EQ, port.rlast.getSignal(), HDLPreDefinedConstant.HIGH);
		read.addStateTransit(newExpr(HDLOp.EQ, newExpr(HDLOp.AND, rvalid_high, rlast_high), HDLPreDefinedConstant.HIGH), idle);
		read.addStateTransit(newExpr(HDLOp.EQ, newExpr(HDLOp.AND, rvalid_high, rlast_high), HDLPreDefinedConstant.HIGH), idle);
		read.addStateTransit(newExpr(HDLOp.EQ, newExpr(HDLOp.AND, rvalid_high, newExpr(HDLOp.EQ, read_length, HDLPreDefinedConstant.INTEGER_ONE)), HDLPreDefinedConstant.HIGH), idle);
	}
	
	public static void main(String... args){
		int[] width = new int[]{8, 16, 32, 64, 128, 256, 512};
		for(int w: width){
			AXI_Reader_Buffer reader = new AXI_Reader_Buffer(w);
			HDLUtils.genHDLSequencerDump(reader);
			HDLUtils.generate(reader, HDLUtils.VHDL);
			HDLUtils.generate(reader, HDLUtils.Verilog);
		}
	}

}
