package synthesijer.lib.axi;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.utils.Utils;

public class AxiMasterReadPort {
	
	public final HDLPort axi_clk;
	public final HDLPort axi_reset;
	public final HDLPort bresp, bvalid, bready;
	public final HDLPort araddr, arlen, arsize, arburst, arcache, arprot, arvalid, arready;
	public final HDLPort rdata, rresp, rlast, rvalid, rready;
	
	public AxiMasterReadPort(HDLModule m, String prefix, int axi_width){
		axi_clk = Utils.genInputPort(m, "axi_clk");
		axi_reset = Utils.genInputPort(m, "axi_reset");
		
		bresp   = Utils.genInputPort(m, prefix + "BRESP", 2);
		bvalid  = Utils.genInputPort(m, prefix + "BVALID");
		bready  = Utils.genOutputPort(m, prefix + "BREADY");
		araddr  = Utils.genOutputPort(m, prefix + "ARADDR", 32);
		arlen   = Utils.genOutputPort(m, prefix + "ARLEN", 8);
		arsize  = Utils.genOutputPort(m, prefix + "ARSIZE", 3);
		arburst = Utils.genOutputPort(m, prefix + "ARBURST", 2);
		arcache = Utils.genOutputPort(m, prefix + "ARCACHE", 4);
		arprot  = Utils.genOutputPort(m, prefix + "ARPROT", 3);
		arvalid = Utils.genOutputPort(m, prefix + "ARVALID");
		arready = Utils.genInputPort(m, prefix + "ARREADY");
		rdata   = Utils.genInputPort(m, prefix + "RDATA", axi_width);
		rresp   = Utils.genInputPort(m, prefix + "RRESP", 2);
		rlast   = Utils.genInputPort(m, prefix + "RLAST");
		rvalid  = Utils.genInputPort(m, prefix + "RVALID");
		rready  = Utils.genOutputPort(m, prefix + "RREADY");
	}
}
