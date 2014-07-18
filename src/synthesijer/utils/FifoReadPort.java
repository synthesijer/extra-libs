package synthesijer.utils;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;

public class FifoReadPort {
	
	public final HDLPort din;
	public final HDLPort re;
	public final HDLPort rclk;
	public final HDLPort empty;
	public final HDLPort length;
	public final HDLPort full;

	public FifoReadPort(HDLModule m, String prefix, int width){
		din = Utils.genInputPort(m, prefix + "din", width);
		re = Utils.genOutputPort(m, prefix + "re");
		rclk = Utils.genOutputPort(m, prefix + "rclk");
		empty = Utils.genInputPort(m, prefix + "empty");
		length = Utils.genInputPort(m, prefix + "length", 32);
		full = Utils.genInputPort(m, prefix + "full");
	}
}
