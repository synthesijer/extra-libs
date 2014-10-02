package synthesijer.utils;

import synthesijer.hdl.HDLModule;

public class FifoPort {

	public final FifoWritePort writer;
	public final FifoReadPort reader;

	public FifoPort(HDLModule m, String prefix, int width){
		reader = new FifoReadPort(m, prefix, width);
		writer = new FifoWritePort(m, prefix, width);
	}

}
