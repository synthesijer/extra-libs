package synthesijer.utils;

import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;

public class MemoryReadPort {

	public final HDLPort din;
	public final HDLPort oe;
	public final HDLPort address;
	public final HDLPort rclk;
	
	public MemoryReadPort(HDLModule m, String prefix, int width) {
		din     = Utils.genInputPort(m, prefix + "din", width, EnumSet.of(HDLPort.OPTION.EXPORT));
		oe      = Utils.genOutputPort(m, prefix + "we", EnumSet.of(HDLPort.OPTION.EXPORT));
		address = Utils.genOutputPort(m, prefix + "address", 32, EnumSet.of(HDLPort.OPTION.EXPORT));
		rclk    = Utils.genOutputPort(m, prefix + "wclk", EnumSet.of(HDLPort.OPTION.EXPORT));
	}
	
}
