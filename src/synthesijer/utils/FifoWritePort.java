package synthesijer.utils;

import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;

public class FifoWritePort {
	
	public final HDLPort dout;
	public final HDLPort we;
	public final HDLPort wclk;
	public final HDLPort length;
	public final HDLPort full;

	public FifoWritePort(HDLModule m, String prefix, int width){
		dout = Utils.genOutputPort(m, prefix + "dout", width, EnumSet.of(HDLPort.OPTION.EXPORT));
		we = Utils.genOutputPort(m, prefix + "we", EnumSet.of(HDLPort.OPTION.EXPORT));
		wclk = Utils.genOutputPort(m, prefix + "wclk", EnumSet.of(HDLPort.OPTION.EXPORT));
		length = Utils.genInputPort(m, prefix + "length", 32, EnumSet.of(HDLPort.OPTION.EXPORT));
		full = Utils.genInputPort(m, prefix + "full", EnumSet.of(HDLPort.OPTION.EXPORT));
	}
}
