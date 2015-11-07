package synthesijer.utils;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLUtils;

public class SimpleFifo16 extends HDLModule{
	
	public final HDLPort we, din, re, dout, empty, full, count;

	public SimpleFifo16(String... args){
		super("simple_fifo_16", "clk", "reset");

		we = HDLUtils.genInputPort(this, "we");
		din = HDLUtils.genInputPort(this, "din", 16);
		re = HDLUtils.genInputPort(this, "re");
		dout = HDLUtils.genOutputPort(this, "dout", 16);
		empty = HDLUtils.genOutputPort(this, "empty");
		full = HDLUtils.genOutputPort(this, "full");
		count = HDLUtils.genOutputPort(this, "count", 32);
		
	}

}
