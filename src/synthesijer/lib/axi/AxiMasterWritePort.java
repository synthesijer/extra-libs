package synthesijer.lib.axi;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.utils.Utils;

public class AxiMasterWritePort {
	
	public final HDLPort awaddr, awlen, awsize, awburst, awcache, awprot, awvalid, awready;
	public final HDLPort wdata, wstrb, wlast, wvalid, wready;
	public final HDLPort bresp, bvalid, bready;
	
	public AxiMasterWritePort(HDLModule m, String prefix, int axi_width){
		awaddr  = Utils.genOutputPort(m, prefix + "AWADDR", 32);
		awlen   = Utils.genOutputPort(m, prefix + "AWLEN", 8);
		awsize  = Utils.genOutputPort(m, prefix + "AWSIZE", 3);
		awburst = Utils.genOutputPort(m, prefix + "AWBURST", 2);
		awcache = Utils.genOutputPort(m, prefix + "AWCACHE", 4);
		awprot  = Utils.genOutputPort(m, prefix + "AWPROT", 3);
		awvalid = Utils.genOutputPort(m, prefix + "AWVALID");
		awready = Utils.genInputPort(m, prefix + "AWREADY");
		
		wdata   = Utils.genOutputPort(m, prefix + "WDATA", axi_width);
		wstrb   = Utils.genOutputPort(m, prefix + "WSTRB", axi_width/8);
		wlast   = Utils.genOutputPort(m, prefix + "WLAST");
		wvalid  = Utils.genOutputPort(m, prefix + "WVALID");
		wready  = Utils.genInputPort(m, prefix + "WREADY");
		
		bresp = Utils.genInputPort(m, prefix + "BRESP", 2);
		bvalid = Utils.genInputPort(m, prefix + "BVALID");
		bready = Utils.genOutputPort(m, prefix + "BREADY");
	}
}
