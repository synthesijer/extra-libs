package synthesijer.utils;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLUtils;

public class SimpleFifo128 extends HDLModule{
	
	public final HDLPort we, din, re, dout, empty, full, count;

	public SimpleFifo128(String... args){
		super("simple_fifo_128", "clk", "reset");

		we = HDLUtils.genInputPort(this, "we");
		din = HDLUtils.genInputPort(this, "din", 128);
		re = HDLUtils.genInputPort(this, "re");
		dout = HDLUtils.genOutputPort(this, "dout", 128);
		empty = HDLUtils.genOutputPort(this, "empty");
		full = HDLUtils.genOutputPort(this, "full");
		count = HDLUtils.genOutputPort(this, "count", 32);
		
                newModuleInstance(new SimpleFifo(), "U");
	}

}
