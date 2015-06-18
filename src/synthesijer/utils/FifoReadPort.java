package synthesijer.utils;

import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLUtils;

public class FifoReadPort {
	
	public final HDLPort din;
	public final HDLPort re;
	public final HDLPort rclk;
	public final HDLPort empty;
	public final HDLPort length;
	public final HDLPort full;

	public FifoReadPort(HDLModule m, String prefix, int width){
		din = HDLUtils.genInputPort(m, prefix + "din", width, EnumSet.of(HDLPort.OPTION.EXPORT));
		re = HDLUtils.genOutputPort(m, prefix + "re", EnumSet.of(HDLPort.OPTION.EXPORT));
		rclk = HDLUtils.genOutputPort(m, prefix + "rclk", EnumSet.of(HDLPort.OPTION.EXPORT));
		empty = HDLUtils.genInputPort(m, prefix + "empty", EnumSet.of(HDLPort.OPTION.EXPORT));
		length = HDLUtils.genInputPort(m, prefix + "length", 32, EnumSet.of(HDLPort.OPTION.EXPORT));
		full = HDLUtils.genInputPort(m, prefix + "full", EnumSet.of(HDLPort.OPTION.EXPORT));
	}
}
