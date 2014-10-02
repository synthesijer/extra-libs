package synthesijer.lib.axi;

import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.utils.Utils;

public class AxiMasterWritePort {
	
	public final HDLPort awaddr, awlen, awsize, awburst, awcache, awprot, awvalid, awready;
	public final HDLPort wdata, wstrb, wlast, wvalid, wready;
	public final HDLPort bresp, bvalid, bready;
	
	public AxiMasterWritePort(HDLModule m, String prefix, int axi_width){
		awaddr  = Utils.genOutputPort(m, prefix + "AWADDR", 32, EnumSet.of(HDLPort.OPTION.EXPORT));
		awlen   = Utils.genOutputPort(m, prefix + "AWLEN", 8, EnumSet.of(HDLPort.OPTION.EXPORT));
		awsize  = Utils.genOutputPort(m, prefix + "AWSIZE", 3, EnumSet.of(HDLPort.OPTION.EXPORT));
		awburst = Utils.genOutputPort(m, prefix + "AWBURST", 2, EnumSet.of(HDLPort.OPTION.EXPORT));
		awcache = Utils.genOutputPort(m, prefix + "AWCACHE", 4, EnumSet.of(HDLPort.OPTION.EXPORT));
		awprot  = Utils.genOutputPort(m, prefix + "AWPROT", 3, EnumSet.of(HDLPort.OPTION.EXPORT));
		awvalid = Utils.genOutputPort(m, prefix + "AWVALID", EnumSet.of(HDLPort.OPTION.EXPORT));
		awready = Utils.genInputPort(m, prefix + "AWREADY", EnumSet.of(HDLPort.OPTION.EXPORT));
		
		wdata   = Utils.genOutputPort(m, prefix + "WDATA", axi_width, EnumSet.of(HDLPort.OPTION.EXPORT));
		wstrb   = Utils.genOutputPort(m, prefix + "WSTRB", axi_width/8, EnumSet.of(HDLPort.OPTION.EXPORT));
		wlast   = Utils.genOutputPort(m, prefix + "WLAST", EnumSet.of(HDLPort.OPTION.EXPORT));
		wvalid  = Utils.genOutputPort(m, prefix + "WVALID", EnumSet.of(HDLPort.OPTION.EXPORT));
		wready  = Utils.genInputPort(m, prefix + "WREADY", EnumSet.of(HDLPort.OPTION.EXPORT));
		
		bresp = Utils.genInputPort(m, prefix + "BRESP", 2, EnumSet.of(HDLPort.OPTION.EXPORT));
		bvalid = Utils.genInputPort(m, prefix + "BVALID", EnumSet.of(HDLPort.OPTION.EXPORT));
		bready = Utils.genOutputPort(m, prefix + "BREADY", EnumSet.of(HDLPort.OPTION.EXPORT));
	}
}
