package synthesijer.lib.axi;

import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLUtils;

public class AxiLiteSlaveReadPort {
	
	public final HDLPort araddr, arvalid, arready;
	public final HDLPort rdata, rresp, rvalid, rready;
	
	public AxiLiteSlaveReadPort(HDLModule m, String prefix, int axi_width){
		araddr  = HDLUtils.genInputPort(m, prefix + "ARADDR", 32, EnumSet.of(HDLPort.OPTION.EXPORT));
		arvalid = HDLUtils.genInputPort(m, prefix + "ARVALID", EnumSet.of(HDLPort.OPTION.EXPORT));
		arready = HDLUtils.genOutputPort(m, prefix + "ARREADY", EnumSet.of(HDLPort.OPTION.EXPORT));
		
		rdata   = HDLUtils.genOutputPort(m, prefix + "RDATA", axi_width, EnumSet.of(HDLPort.OPTION.EXPORT));
		rresp   = HDLUtils.genOutputPort(m, prefix + "RRESP", 2, EnumSet.of(HDLPort.OPTION.EXPORT));
		rvalid  = HDLUtils.genOutputPort(m, prefix + "RVALID", EnumSet.of(HDLPort.OPTION.EXPORT));
		rready  = HDLUtils.genInputPort(m, prefix + "RREADY", EnumSet.of(HDLPort.OPTION.EXPORT));
	}
	
	public void setDefaultSetting(int width){
	}

}
