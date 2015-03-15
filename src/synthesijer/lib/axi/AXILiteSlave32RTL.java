package synthesijer.lib.axi;


import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLInstance;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLSignal.ResourceKind;
import synthesijer.hdl.HDLUtils;
import synthesijer.hdl.expr.HDLPreDefinedConstant;
import synthesijer.hdl.sequencer.SequencerState;
import synthesijer.lib.BlockRAM;
import synthesijer.utils.Utils;

public class AXILiteSlave32RTL extends HDLModule{
	
	/////////////////////////////////////////////////////////////////
	// dummy variables, for Synthesijer /////////////////////////////
	public int data[];
	/////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////
	// RTL implementation
	
	public final HDLPort addr, wdata, rdata, we, oe;
	public final HDLSignal addr_b, wdata_b, rdata_b, we_b, oe_b;
	
	public final AxiLiteSlavePort axi;
	
	public AXILiteSlave32RTL(String... args){
		super("axi_lite_slave_32", "clk", "reset");
		
//		newParameter("DEPTH", HDLPrimitiveType.genIntegerType(), String.valueOf(8));
		
		HDLPort length = Utils.genOutputPort(this, "data_length", 32);
//		length.getSignal().setAssign(null, new HDLValue("DEPTH", HDLPrimitiveType.genStringType(), HDLPrimitiveType.genDigitType())); 
		addr  = Utils.genInputPort(this, "data_address", 32);
		wdata = Utils.genInputPort(this, "data_din", 32);
		rdata = Utils.genOutputPort(this, "data_dout", 32);
		we = Utils.genInputPort(this, "data_we");
		oe = Utils.genInputPort(this, "data_oe");
		
		addr_b  = newSignal("data_address_b", HDLPrimitiveType.genSignedType(32));
		wdata_b = newSignal("data_din_b", HDLPrimitiveType.genSignedType(32));
		rdata_b = newSignal("data_dout_b", HDLPrimitiveType.genSignedType(32), ResourceKind.WIRE);
		we_b = newSignal("data_we_b", HDLPrimitiveType.genBitType());
		oe_b = newSignal("data_oe_b", HDLPrimitiveType.genBitType());
		
		axi = new AxiLiteSlavePort(this, "axi", 32, 4096);
		
		BlockRAM ram = new BlockRAM(32, 10, 1024);
		HDLInstance inst = newModuleInstance(ram, "U");
		
		inst.getSignalForPort(ram.getSysClkName()).setAssign(null, getSysClk().getSignal());
		inst.getSignalForPort(ram.getSysResetName()).setAssign(null, getSysReset().getSignal());
		
		length.getSignal().setAssign(null, inst.getSignalForPort("length"));
		
		inst.getSignalForPort("address").setAssign(null, addr.getSignal());
		inst.getSignalForPort("din").setAssign(null, wdata.getSignal());
		rdata.getSignal().setAssign(null, inst.getSignalForPort("dout"));
		inst.getSignalForPort("we").setAssign(null, we.getSignal());
		inst.getSignalForPort("oe").setAssign(null, oe.getSignal());
		
		inst.getSignalForPort("address_b").setAssign(null, addr_b);
		inst.getSignalForPort("din_b").setAssign(null, wdata_b);
		rdata_b.setAssign(null, inst.getSignalForPort("dout_b"));
		inst.getSignalForPort("we_b").setAssign(null, we_b);
		inst.getSignalForPort("oe_b").setAssign(null, oe_b);
				
		genSeq();
	}
	
	private void genSeq(){
		HDLSequencer seq = newSequencer("write_seq");
		SequencerState s0 = seq.addSequencerState("s0");
		SequencerState s1 = seq.addSequencerState("s1");
		SequencerState s2 = seq.addSequencerState("s2");

		////// IDLE
		// IDLE -> s0 if awvalid
		seq.getIdleState().addStateTransit(axi.writer.awvalid.getSignal(), s0);
		// addr_b <= (awaddr >> 2) if awvalid = '1' in idle
		addr_b.setAssign(seq.getIdleState(), axi.writer.awvalid.getSignal(),
				newExpr(HDLOp.LOGIC_RSHIFT, axi.writer.awaddr.getSignal(), Utils.value(2, 32)));
		// awready <= awvalid in idle
		axi.writer.awready.getSignal().setAssign(seq.getIdleState(), axi.writer.awvalid.getSignal());
		// bvalid <= bready in idle
		axi.writer.bvalid.getSignal().setAssign(seq.getIdleState(), axi.writer.bready.getSignal());
		
		// read_kick <= arvalid & !awvalid
		HDLExpr read_kick = newExpr(HDLOp.AND, axi.reader.arvalid.getSignal(), newExpr(HDLOp.NOT, axi.writer.awvalid.getSignal())); 
		// IDLE -> s1 if read_kick
		seq.getIdleState().addStateTransit(read_kick, s1);
		// addr_b <= (araddr >> 2) if read_kick in idle
		addr_b.setAssign(seq.getIdleState(), read_kick,
				newExpr(HDLOp.LOGIC_RSHIFT, axi.reader.araddr.getSignal(), Utils.value(2, 32)));
		// arready <= read_kick in idle
		axi.reader.arready.getSignal().setAssign(seq.getIdleState(), read_kick);

		////// S0
		// s0 -> IDLE if wvalid
		s0.addStateTransit(axi.writer.wvalid.getSignal(), seq.getIdleState());
		// data <= wdata if wvalid = '1' in idle
		wdata_b.setAssign(s0, axi.writer.wvalid.getSignal(), axi.writer.wdata.getSignal());
		we_b.setAssign(s0, axi.writer.wvalid.getSignal());
		// awready <= awvalid in idle
		axi.writer.wready.getSignal().setAssign(s0, axi.writer.wvalid.getSignal());
		
		////// S1 (block memory wait)
		s1.addStateTransit(s2);
		
		////// S2
		// s2 -> IDLE if rready
		s2.addStateTransit(axi.reader.rready.getSignal(), seq.getIdleState());
		// rdata <= data in s2
		axi.reader.rdata.getSignal().setAssign(s2, rdata_b);
		// rvalid <= '1' in s2
		axi.reader.rvalid.getSignal().setAssign(s2, HDLPreDefinedConstant.HIGH);
		// rvalid <= '0' in other states
		axi.reader.rvalid.getSignal().setDefaultValue(HDLPreDefinedConstant.LOW);
	}
		
	public static void main(String... args){
		AXILiteSlave32RTL m = new AXILiteSlave32RTL();
		HDLUtils.genHDLSequencerDump(m);
		HDLUtils.genResourceUsageTable(m);
		HDLUtils.generate(m, HDLUtils.VHDL);
		HDLUtils.generate(m, HDLUtils.Verilog);
		//HDLUtils.generate(new SimpleAXIMemIface32RTL_Sim(m), HDLUtils.VHDL);
	}

}
