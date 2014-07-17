package synthesijer.utils;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;

public class FifoPort {

	public final HDLPort din, re, rclk, empty, length;
	public final HDLPort dout, we, wclk, full;

	public FifoPort(HDLModule m, String prefix, int width){
		din = Utils.genInputPort(m, prefix + "din", width);
		re = Utils.genOutputPort(m, prefix + "re");
		rclk = Utils.genOutputPort(m, prefix + "rclk");
		empty = Utils.genInputPort(m, prefix + "empty");
		length = Utils.genInputPort(m, prefix + "length", 32);
		
		dout = Utils.genOutputPort(m, prefix + "dout", width);
		we = Utils.genOutputPort(m, prefix + "we");
		wclk = Utils.genOutputPort(m, prefix + "wclk");
		full = Utils.genInputPort(m, prefix + "full");
		
	}

}
