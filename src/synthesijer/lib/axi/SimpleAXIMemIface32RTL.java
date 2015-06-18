package synthesijer.lib.axi;


import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLUtils;
import synthesijer.hdl.expr.HDLPreDefinedConstant;
import synthesijer.hdl.sequencer.SequencerState;

public class SimpleAXIMemIface32RTL extends HDLModule{
	
	/////////////////////////////////////////////////////////////////
	// dummy variables, for Synthesijer /////////////////////////////
	public boolean busy;
	public int data[];
	//public int data_din;
	//public int data_dout;
	//public boolean data_we;
	//public boolean data_oe;
	//public int data_address;
	public int read_result;
	/////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////
	// RTL implementation
	
	public final HDLPort addr, wdata, rdata, we, oe, hdl_busy;
	public final HDLPort forbid;
	public final HDLPort hdl_read_result;
	
	public final AxiMasterPort axi;
	//public final AxiMasterReadPort reader;
	//public final AxiMasterWritePort writer;
	
	private final HDLSignal read_state_busy;
	private final HDLSignal write_state_busy;
		
	public SimpleAXIMemIface32RTL(String... args){
		super("simple_axi_memiface_32", "clk", "reset");
		
		addr = HDLUtils.genInputPort(this, "data_address", 32);
		wdata = HDLUtils.genInputPort(this, "data_din", 32);
		rdata = HDLUtils.genOutputPort(this, "data_dout", 32);
		hdl_read_result = HDLUtils.genOutputPort(this, "read_result", 32);
		we = HDLUtils.genInputPort(this, "data_we");
		oe = HDLUtils.genInputPort(this, "data_oe");
		HDLPort length = HDLUtils.genOutputPort(this, "data_length", 32);
		length.getSignal().setAssign(null, HDLUtils.value(0x7FFFFFFF, 32)); 
		hdl_busy = HDLUtils.genOutputPort(this, "busy");
		forbid = HDLUtils.genInputPort(this, "forbid", EnumSet.of(HDLPort.OPTION.EXPORT));
		
		axi = new AxiMasterPort(this, "axi", 32, 512*1024*1024);
		//axi.reader = new AxiMasterReadPort(this, "axi_reader_", 32);
		//axi.reader.setDefaultSetting(32);
		
		//axi.writer = new AxiMasterWritePort(this, "axi_writer_", 32);
		//axi.writer.setDefaultSetting(32);
		
		read_state_busy = newSignal("read_state_busy", HDLPrimitiveType.genBitType());
		write_state_busy = newSignal("write_state_busy", HDLPrimitiveType.genBitType());
		
		hdl_busy.getSignal().setAssign(null, newExpr(HDLOp.OR, newExpr(HDLOp.OR, write_state_busy, read_state_busy), forbid.getSignal()));
				
		genWriteSeq();
		genReadSeq();
	}
	
	private void genWriteSeq(){
		HDLSequencer seq = newSequencer("write_seq");
		axi.writer.bready.getSignal().setAssign(seq.getIdleState(), HDLPreDefinedConstant.HIGH);
		
		// IDLE
		axi.writer.awaddr.getSignal().setAssign(seq.getIdleState(), addr.getSignal());
		axi.writer.awlen.getSignal().setAssign(seq.getIdleState(), HDLUtils.value(0, 8)); // Just 1 Byte
		axi.writer.awvalid.getSignal().setAssign(seq.getIdleState(), we.getSignal()); // kick axi_writer
		write_state_busy.setAssign(seq.getIdleState(), we.getSignal());
		axi.writer.wdata.getSignal().setAssign(seq.getIdleState(), wdata.getSignal());

		SequencerState s0 = seq.addSequencerState("s0");
		seq.getIdleState().addStateTransit(we.getSignal(), s0);
		
		// S0
		axi.writer.awvalid.getSignal().setAssign(s0, newExpr(HDLOp.NOT, axi.writer.awready.getSignal())); // de-assert, just after awready is asserted.
		axi.writer.wlast.getSignal().setAssign(s0, axi.writer.awready.getSignal()); // 
		axi.writer.wvalid.getSignal().setAssign(s0, axi.writer.awready.getSignal()); // 
		SequencerState s1 = seq.addSequencerState("s1");
		s0.addStateTransit(axi.writer.awready.getSignal(), s1);
		
		// S1
		axi.writer.wlast.getSignal().setAssign(s1, newExpr(HDLOp.NOT, axi.writer.wready.getSignal())); // de-assert, just after wready is asserted.
		axi.writer.wvalid.getSignal().setAssign(s1, newExpr(HDLOp.NOT, axi.writer.wready.getSignal())); // de-assert, just after wready is asserted.
		write_state_busy.setAssign(s1, newExpr(HDLOp.NOT, axi.writer.wready.getSignal())); // de-assert, just after wready is asserted.
		s1.addStateTransit(axi.writer.wready.getSignal(), seq.getIdleState());
	}
		
	private void genReadSeq(){
		HDLSequencer seq = newSequencer("read_seq");
		axi.reader.rready.getSignal().setAssign(seq.getIdleState(), HDLPreDefinedConstant.HIGH);
		
		// IDLE
		axi.reader.arvalid.getSignal().setAssign(seq.getIdleState(), oe.getSignal()); // kick axi_reader
		axi.reader.araddr.getSignal().setAssign(seq.getIdleState(), addr.getSignal());
		axi.reader.arlen.getSignal().setAssign(seq.getIdleState(), HDLUtils.value(0, 8)); // Just 1 Byte
		SequencerState s0 = seq.addSequencerState("s0");
		seq.getIdleState().addStateTransit(oe.getSignal(), s0); // idle -> s0, when oe = '1'
		read_state_busy.setAssign(seq.getIdleState(), oe.getSignal()); // to start read sequence
		
		// S0
		axi.reader.arvalid.getSignal().setAssign(s0, newExpr(HDLOp.NOT, axi.reader.arready.getSignal()));
		SequencerState s1 = seq.addSequencerState("s1");
		s0.addStateTransit(axi.reader.arready.getSignal(), s1); // s0 -> s1, when arready = '1'
		
		// S1
		s1.addStateTransit(axi.reader.rvalid.getSignal(), seq.getIdleState());
		read_state_busy.setAssign(s1, newExpr(HDLOp.NOT, axi.reader.rvalid.getSignal())); // de-assert, just after rvalid is asserted.
		rdata.getSignal().setAssign(s1, axi.reader.rdata.getSignal());
		hdl_read_result.getSignal().setAssign(s1, axi.reader.rdata.getSignal());
	}
	public static void main(String... args){
		SimpleAXIMemIface32RTL m = new SimpleAXIMemIface32RTL();
		HDLUtils.genHDLSequencerDump(m);
		HDLUtils.genResourceUsageTable(m);
		HDLUtils.generate(m, HDLUtils.VHDL);
		HDLUtils.generate(m, HDLUtils.Verilog);
		//HDLUtils.generate(new SimpleAXIMemIface32RTL_Sim(m), HDLUtils.VHDL);
	}

}


/*	
class SimpleAXIMemIface32RTL_Sim extends BasicSim{
	
	public SimpleAXIMemIface32RTL_Sim(SimpleAXIMemIface32RTL target){
		super(target, "simple_axi_memiface32_sim");
		inst.getSignalForPort(target.we.getName()).setAssign(null, newExpr(HDLOp.IF, after(100), HDLPreDefinedConstant.HIGH, HDLPreDefinedConstant.LOW));
		inst.getSignalForPort(target.writer.awready.getName()).setAssign(null, newExpr(HDLOp.IF, after(110), HDLPreDefinedConstant.HIGH, HDLPreDefinedConstant.LOW));
		inst.getSignalForPort(target.writer.wready.getName()).setAssign(null, newExpr(HDLOp.IF, after(150), HDLPreDefinedConstant.HIGH, HDLPreDefinedConstant.LOW));
	}
	
}
*/
