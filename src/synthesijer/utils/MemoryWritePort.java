package synthesijer.utils;

import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;

public class MemoryWritePort {

	public final HDLPort dout;
	public final HDLPort we;
	public final HDLPort address;
	public final HDLPort wclk;
	
	public MemoryWritePort(HDLModule m, String prefix, int width) {
		dout    = Utils.genOutputPort(m, prefix + "dout", width, EnumSet.of(HDLPort.OPTION.EXPORT));
		we      = Utils.genOutputPort(m, prefix + "we", EnumSet.of(HDLPort.OPTION.EXPORT));
		address = Utils.genOutputPort(m, prefix + "address", 32, EnumSet.of(HDLPort.OPTION.EXPORT));
		wclk    = Utils.genOutputPort(m, prefix + "wclk", EnumSet.of(HDLPort.OPTION.EXPORT));
	}
	
}
