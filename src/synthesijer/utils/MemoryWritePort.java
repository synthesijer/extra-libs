package synthesijer.utils;

import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLUtils;

public class MemoryWritePort {

	public final HDLPort dout;
	public final HDLPort we;
	public final HDLPort address;
	public final HDLPort wclk;
	
	public MemoryWritePort(HDLModule m, String prefix, int width) {
		dout    = HDLUtils.genOutputPort(m, prefix + "dout", width, EnumSet.of(HDLPort.OPTION.EXPORT));
		we      = HDLUtils.genOutputPort(m, prefix + "we", EnumSet.of(HDLPort.OPTION.EXPORT));
		address = HDLUtils.genOutputPort(m, prefix + "address", 32, EnumSet.of(HDLPort.OPTION.EXPORT));
		wclk    = HDLUtils.genOutputPort(m, prefix + "wclk", EnumSet.of(HDLPort.OPTION.EXPORT));
	}
	
}
