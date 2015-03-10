package synthesijer.lib.axi;

import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.expr.HDLPreDefinedConstant;
import synthesijer.utils.Utils;

public class AxiLiteSlaveWritePort {
	
	public final HDLPort awaddr, awvalid, awready;
	public final HDLPort wdata, wstrb, wvalid, wready;
	public final HDLPort bresp, bvalid, bready;
	
	public AxiLiteSlaveWritePort(HDLModule m, String prefix, int axi_width){
		awaddr  = Utils.genInputPort(m, prefix + "AWADDR", 32, EnumSet.of(HDLPort.OPTION.EXPORT));
		awvalid = Utils.genInputPort(m, prefix + "AWVALID", EnumSet.of(HDLPort.OPTION.EXPORT));
		awready = Utils.genOutputPort(m, prefix + "AWREADY", EnumSet.of(HDLPort.OPTION.EXPORT));
		
		wdata   = Utils.genInputPort(m, prefix + "WDATA", axi_width, EnumSet.of(HDLPort.OPTION.EXPORT));
		wvalid  = Utils.genInputPort(m, prefix + "WVALID", EnumSet.of(HDLPort.OPTION.EXPORT));
		wready  = Utils.genOutputPort(m, prefix + "WREADY", EnumSet.of(HDLPort.OPTION.EXPORT));
		wstrb   = Utils.genInputPort(m, prefix + "WSTRB", axi_width/8, EnumSet.of(HDLPort.OPTION.EXPORT));

		bresp = Utils.genOutputPort(m, prefix + "BRESP", 2, EnumSet.of(HDLPort.OPTION.EXPORT));
		bvalid = Utils.genOutputPort(m, prefix + "BVALID", EnumSet.of(HDLPort.OPTION.EXPORT));
		bready = Utils.genInputPort(m, prefix + "BREADY", EnumSet.of(HDLPort.OPTION.EXPORT));
	}
	
	public void setDefaultSetting(int width){
		awready.getSignal().setDefaultValue(HDLPreDefinedConstant.LOW);
		wready.getSignal().setDefaultValue(HDLPreDefinedConstant.LOW);
	}

}
