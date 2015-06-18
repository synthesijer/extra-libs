package synthesijer.utils;

import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLUtils;

public class FifoWritePort {
	
	public final HDLPort dout;
	public final HDLPort we;
	public final HDLPort wclk;
	public final HDLPort length;
	public final HDLPort full;

	public FifoWritePort(HDLModule m, String prefix, int width){
		dout = HDLUtils.genOutputPort(m, prefix + "dout", width, EnumSet.of(HDLPort.OPTION.EXPORT));
		we = HDLUtils.genOutputPort(m, prefix + "we", EnumSet.of(HDLPort.OPTION.EXPORT));
		wclk = HDLUtils.genOutputPort(m, prefix + "wclk", EnumSet.of(HDLPort.OPTION.EXPORT));
		length = HDLUtils.genInputPort(m, prefix + "length", 32, EnumSet.of(HDLPort.OPTION.EXPORT));
		full = HDLUtils.genInputPort(m, prefix + "full", EnumSet.of(HDLPort.OPTION.EXPORT));
	}
}
