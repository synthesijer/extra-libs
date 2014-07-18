package synthesijer.utils;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;

public class FifoWritePort {
	
	public final HDLPort dout;
	public final HDLPort we;
	public final HDLPort wclk;
	public final HDLPort length;
	public final HDLPort full;

	public FifoWritePort(HDLModule m, String prefix, int width){
		dout = Utils.genInputPort(m, prefix + "dout", width);
		we = Utils.genInputPort(m, prefix + "we");
		wclk = Utils.genInputPort(m, prefix + "wclk");
		length = Utils.genInputPort(m, prefix + "length", 32);
		full = Utils.genInputPort(m, prefix + "full");
	}
}
