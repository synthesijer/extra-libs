package synthesijer.lib.axi;

import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.utils.Utils;

public class AxiLiteSlaveReadPort {
	
	public final HDLPort araddr, arvalid, arready;
	public final HDLPort rdata, rresp, rvalid, rready;
	
	public AxiLiteSlaveReadPort(HDLModule m, String prefix, int axi_width){
		araddr  = Utils.genInputPort(m, prefix + "ARADDR", 32, EnumSet.of(HDLPort.OPTION.EXPORT));
		arvalid = Utils.genInputPort(m, prefix + "ARVALID", EnumSet.of(HDLPort.OPTION.EXPORT));
		arready = Utils.genOutputPort(m, prefix + "ARREADY", EnumSet.of(HDLPort.OPTION.EXPORT));
		
		rdata   = Utils.genOutputPort(m, prefix + "RDATA", axi_width, EnumSet.of(HDLPort.OPTION.EXPORT));
		rresp   = Utils.genOutputPort(m, prefix + "RRESP", 2, EnumSet.of(HDLPort.OPTION.EXPORT));
		rvalid  = Utils.genOutputPort(m, prefix + "RVALID", EnumSet.of(HDLPort.OPTION.EXPORT));
		rready  = Utils.genInputPort(m, prefix + "RREADY", EnumSet.of(HDLPort.OPTION.EXPORT));
	}
	
	public void setDefaultSetting(int width){
	}

}
