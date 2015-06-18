package synthesijer.lib.axi;

import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLUtils;
import synthesijer.hdl.expr.HDLPreDefinedConstant;

public class AxiLiteSlaveWritePort {
	
	public final HDLPort awaddr, awvalid, awready;
	public final HDLPort wdata, wstrb, wvalid, wready;
	public final HDLPort bresp, bvalid, bready;
	
	public AxiLiteSlaveWritePort(HDLModule m, String prefix, int axi_width){
		awaddr  = HDLUtils.genInputPort(m, prefix + "AWADDR", 32, EnumSet.of(HDLPort.OPTION.EXPORT));
		awvalid = HDLUtils.genInputPort(m, prefix + "AWVALID", EnumSet.of(HDLPort.OPTION.EXPORT));
		awready = HDLUtils.genOutputPort(m, prefix + "AWREADY", EnumSet.of(HDLPort.OPTION.EXPORT));
		
		wdata   = HDLUtils.genInputPort(m, prefix + "WDATA", axi_width, EnumSet.of(HDLPort.OPTION.EXPORT));
		wvalid  = HDLUtils.genInputPort(m, prefix + "WVALID", EnumSet.of(HDLPort.OPTION.EXPORT));
		wready  = HDLUtils.genOutputPort(m, prefix + "WREADY", EnumSet.of(HDLPort.OPTION.EXPORT));
		wstrb   = HDLUtils.genInputPort(m, prefix + "WSTRB", axi_width/8, EnumSet.of(HDLPort.OPTION.EXPORT));

		bresp = HDLUtils.genOutputPort(m, prefix + "BRESP", 2, EnumSet.of(HDLPort.OPTION.EXPORT));
		bvalid = HDLUtils.genOutputPort(m, prefix + "BVALID", EnumSet.of(HDLPort.OPTION.EXPORT));
		bready = HDLUtils.genInputPort(m, prefix + "BREADY", EnumSet.of(HDLPort.OPTION.EXPORT));
	}
	
	public void setDefaultSetting(int width){
		awready.getSignal().setDefaultValue(HDLPreDefinedConstant.LOW);
		wready.getSignal().setDefaultValue(HDLPreDefinedConstant.LOW);
	}

}
