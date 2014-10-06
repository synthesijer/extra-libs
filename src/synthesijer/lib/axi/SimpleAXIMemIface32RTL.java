package synthesijer.lib.axi;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLUtils;
import synthesijer.hdl.expr.HDLPreDefinedConstant;
import synthesijer.hdl.sequencer.SequencerState;
import synthesijer.utils.Utils;

public class SimpleAXIMemIface32RTL extends HDLModule{
	
	/////////////////////////////////////////////////////////////////
	// dummy variables, for Synthesijer /////////////////////////////
	public boolean busy;
	public int data[];
	/////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////
	// RTL implementation
	
	public final HDLPort addr, wdata, rdata, we, oe, hdl_busy;
	
	public final AxiMasterReadPort reader;
	public final AxiMasterWritePort writer;
	
	private final HDLSignal read_state_busy;
	private final HDLSignal write_state_busy;
		
	public SimpleAXIMemIface32RTL(String... args){
		super("simple_axi_memiface_32", "clk", "reset");
		
		addr = Utils.genInputPort(this, "data_address", 32);
		wdata = Utils.genInputPort(this, "data_din", 32);
		rdata = Utils.genOutputPort(this, "data_dout", 32);
		we = Utils.genInputPort(this, "data_we");
		oe = Utils.genInputPort(this, "data_oe");
		HDLPort length = Utils.genOutputPort(this, "data_length", 32);
		length.getSignal().setAssign(null, Utils.value(0x7FFFFFFF, 32)); 
		hdl_busy = Utils.genOutputPort(this, "busy");
		
		reader = new AxiMasterReadPort(this, "axi_reader_", 32);
		reader.setDefaultSetting(32);
		
		writer = new AxiMasterWritePort(this, "axi_writer_", 32);
		writer.setDefaultSetting(32);
		
		read_state_busy = newSignal("read_state_busy", HDLPrimitiveType.genBitType());
		write_state_busy = newSignal("write_state_busy", HDLPrimitiveType.genBitType());
		
		hdl_busy.getSignal().setAssign(null, newExpr(HDLOp.OR, write_state_busy, read_state_busy));
		
		genWriteSeq();
		genReadSeq();
	}
	
	private void genWriteSeq(){
		HDLSequencer seq = newSequencer("write_seq");
		
		// IDLE
		writer.awaddr.getSignal().setAssign(seq.getIdleState(), addr.getSignal());
		writer.awlen.getSignal().setAssign(seq.getIdleState(), Utils.value(1, 8));
		writer.awvalid.getSignal().setAssign(seq.getIdleState(), we.getSignal()); // kick axi_writer
		write_state_busy.setAssign(seq.getIdleState(), we.getSignal());

		SequencerState s0 = seq.addSequencerState("s0");
		seq.getIdleState().addStateTransit(we.getSignal(), s0);
		
		// S0
		writer.awvalid.getSignal().setAssign(s0, newExpr(HDLOp.NOT, writer.awready.getSignal())); // de-assert, just after awready is asserted.
		writer.wdata.getSignal().setAssign(s0, wdata.getSignal());
		writer.wlast.getSignal().setAssign(s0, writer.awready.getSignal()); // 
		writer.wvalid.getSignal().setAssign(s0, writer.awready.getSignal()); // 
		SequencerState s1 = seq.addSequencerState("s1");
		s0.addStateTransit(newExpr(HDLOp.NOT, writer.awready.getSignal()), s1);
		
		// S1
		writer.wlast.getSignal().setAssign(s0, newExpr(HDLOp.NOT, writer.wready.getSignal())); // de-assert, just after wready is asserted.
		writer.wvalid.getSignal().setAssign(s0, newExpr(HDLOp.NOT, writer.wready.getSignal())); // de-assert, just after wready is asserted.
		write_state_busy.setAssign(seq.getIdleState(), HDLPreDefinedConstant.LOW);
		s1.addStateTransit(newExpr(HDLOp.NOT, writer.wready.getSignal()), seq.getIdleState());
	}
		
	private void genReadSeq(){
		HDLSequencer seq = newSequencer("read_seq");
		
		// IDLE
		reader.arvalid.getSignal().setAssign(seq.getIdleState(), oe.getSignal()); // kick axi_reader
		reader.araddr.getSignal().setAssign(seq.getIdleState(), addr.getSignal());
		reader.arlen.getSignal().setAssign(seq.getIdleState(), Utils.value(1, 8));
		SequencerState s0 = seq.addSequencerState("s0");
		seq.getIdleState().addStateTransit(oe.getSignal(), s0); // idle -> s0, when oe = '1'
		read_state_busy.setAssign(seq.getIdleState(), oe.getSignal()); // to start read sequence
		
		// S0
		reader.arvalid.getSignal().setAssign(s0, newExpr(HDLOp.NOT, reader.arready.getSignal()));
		SequencerState s1 = seq.addSequencerState("s1");
		s0.addStateTransit(reader.arready.getSignal(), s1); // s0 -> s1, when arready = '1'
		
		// S1
		s1.addStateTransit(reader.rvalid.getSignal(), seq.getIdleState());
		rdata.getSignal().setAssign(s1, reader.rdata.getSignal());
	}
	
	public static void main(String... args){
		SimpleAXIMemIface32RTL m = new SimpleAXIMemIface32RTL();
		HDLUtils.genHDLSequencerDump(m);
		HDLUtils.genResourceUsageTable(m);
		HDLUtils.generate(m, HDLUtils.VHDL);
		HDLUtils.generate(m, HDLUtils.Verilog);
	}

}
