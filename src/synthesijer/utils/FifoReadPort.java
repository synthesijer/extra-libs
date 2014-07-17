package synthesijer.utils;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;

public class FifoReadPort {
	
	public final HDLPort dout;
	public final HDLPort re;

	public FifoReadPort(HDLModule m, String prefix, int width){
		dout = Utils.genOutputPort(m, prefix + "dout", width);
		re = Utils.genInputPort(m, prefix + "re");
	}
}
