package synthesijer.lib.axi;


import java.util.EnumSet;

import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLInstance;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLUtils;
import synthesijer.lib.BlockRAM;

public class AXIMemIface32RTL extends HDLModule{
	
	/////////////////////////////////////////////////////////////////
	// dummy variables, for Synthesijer /////////////////////////////
	public boolean busy;
	public boolean write_kick;
	public boolean read_kick;
	public int data[];
	public int axi_addr;
	public int burst_size;
	/////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////
	// RTL implementation
	
	public final HDLPort addr, wdata, rdata, we, oe;
	public final HDLPort hdl_axi_addr, hdl_busy, hdl_write_kick, hdl_read_kick, hdl_burst_size;
	public final HDLPort forbid;
	
	public final AxiMasterPort axi;
	public final HDLSignal local_addr, local_wdata, local_rdata, local_we, local_oe;
	
	private final HDLSignal read_state_busy;
	private final HDLSignal write_state_busy;
	
	private final HDLInstance buf;
	
	private final HDLExpr hdl_write_kick_edge;
	private final HDLExpr hdl_read_kick_edge;
		
	public AXIMemIface32RTL(String... args){
		super("axi_memiface_32", "clk", "reset");
		
		addr = HDLUtils.genInputPort(this, "data_address", 32);
		wdata = HDLUtils.genInputPort(this, "data_din", 32);
		rdata = HDLUtils.genOutputPort(this, "data_dout", 32);
		we = HDLUtils.genInputPort(this, "data_we");
		oe = HDLUtils.genInputPort(this, "data_oe");
		HDLPort length = HDLUtils.genOutputPort(this, "data_length", 32);
		hdl_busy = HDLUtils.genOutputPort(this, "busy");
		hdl_write_kick = HDLUtils.genInputPort(this, "write_kick");
		hdl_read_kick = HDLUtils.genInputPort(this, "read_kick");
		hdl_burst_size = HDLUtils.genInputPort(this, "burst_size", 32);
		hdl_axi_addr = HDLUtils.genInputPort(this, "axi_addr", 32);
		forbid = HDLUtils.genInputPort(this, "forbid", EnumSet.of(HDLPort.OPTION.EXPORT));
		
		axi = new AxiMasterPort(this, "axi", 32, 512*1024*1024);
		
		HDLModule ram = new BlockRAM(32, 10, 1024);
		buf = newModuleInstance(ram, "BUF");
		buf.getSignalForPort(ram.getSysClkName()).setAssign(null, getSysClk().getSignal());
		buf.getSignalForPort(ram.getSysResetName()).setAssign(null, getSysReset().getSignal());
		// for Java-side connection
		buf.getSignalForPort("address").setAssign(null, addr.getSignal());
		buf.getSignalForPort("din").setAssign(null, wdata.getSignal());
		buf.getSignalForPort("we").setAssign(null, we.getSignal());
		buf.getSignalForPort("oe").setAssign(null, oe.getSignal());
		rdata.getSignal().setAssign(null, buf.getSignalForPort("dout"));
		length.getSignal().setAssign(null, buf.getSignalForPort("length"));
		// for internal connection
		local_addr  = buf.getSignalForPort("address_b");
		local_wdata = buf.getSignalForPort("din_b");
		local_we    = buf.getSignalForPort("we_b");
		local_oe    = buf.getSignalForPort("oe_b");
		local_rdata = buf.getSignalForPort("dout_b");
		
		read_state_busy = newSignal("read_state_busy", HDLPrimitiveType.genBitType());
		write_state_busy = newSignal("write_state_busy", HDLPrimitiveType.genBitType());
		
		hdl_busy.getSignal().setAssign(null, newExpr(HDLOp.OR, newExpr(HDLOp.OR, write_state_busy, read_state_busy), forbid.getSignal()));
		
		HDLSignal hdl_write_kick_d = newSignal("hdl_write_kick_d", HDLPrimitiveType.genBitType());
		hdl_write_kick_edge = newExpr(HDLOp.AND, hdl_write_kick.getSignal(), newExpr(HDLOp.NOT, hdl_write_kick_d));
		
		HDLSignal hdl_read_kick_d = newSignal("hdl_read_kick_d", HDLPrimitiveType.genBitType());
		hdl_read_kick_edge = newExpr(HDLOp.AND, newExpr(HDLOp.NOT, hdl_write_kick_edge), 
				newExpr(HDLOp.AND, hdl_read_kick.getSignal(), newExpr(HDLOp.NOT, hdl_read_kick_d)));
		
		HDLSequencer seq = newSequencer("axi");
		seq.addSeqExpr(hdl_write_kick_d, hdl_write_kick.getSignal());
		seq.addSeqExpr(hdl_read_kick_d, hdl_read_kick.getSignal());
		
		axi.writer.genWriteSeq(
				     seq,
				     hdl_write_kick_edge, write_state_busy,
				     hdl_axi_addr.getSignal(), hdl_burst_size.getSignal(),
				     local_addr, local_oe, local_rdata
				     );
		
		axi.reader.genReadSeq(
	               seq,
	               hdl_read_kick_edge, read_state_busy,
	               hdl_axi_addr.getSignal(), hdl_burst_size.getSignal(),
	               local_addr, local_we, local_wdata);
	}
	
	
	public static void main(String... args){
		AXIMemIface32RTL m = new AXIMemIface32RTL();
		HDLUtils.genHDLSequencerDump(m);
		//HDLUtils.genResourceUsageTable(m);
		HDLUtils.generate(m, HDLUtils.VHDL);
		HDLUtils.generate(m, HDLUtils.Verilog);
		//HDLUtils.generate(new SimpleAXIMemIface32RTL_Sim(m), HDLUtils.VHDL);
	}

}

