package synthesijer.utils;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;

public class SimpleFifo extends HDLModule{
	
	public final HDLPort we, din, re, dout, empty, full, count;

	public SimpleFifo(String... args){
		super("simple_fifo", "clk", "reset");

		we = Utils.genInputPort(this, "we");
		din = Utils.genInputPort(this, "din", 32);
		re = Utils.genInputPort(this, "re");
		dout = Utils.genOutputPort(this, "dout", 32);
		empty = Utils.genOutputPort(this, "empty");
		full = Utils.genOutputPort(this, "full");
		count = Utils.genOutputPort(this, "count", 32);
		
	}

}
