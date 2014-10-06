package synthesijer.utils;

import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;

public class CacheMemoeryWrapper extends HDLModule{
	
	public int[] data;
	
	public boolean busy; // must check before access to data

	public CacheMemoeryWrapper(String... args) {
		super("cache_memory", "clk", "reset");

		newPort("data_address", DIR.IN,  HDLPrimitiveType.genSignedType(32));
		newPort("data_din",     DIR.IN,  HDLPrimitiveType.genSignedType(32));
		newPort("data_dout",    DIR.OUT, HDLPrimitiveType.genSignedType(32));
		newPort("data_we",      DIR.IN,  HDLPrimitiveType.genBitType());
		newPort("data_oe",      DIR.IN,  HDLPrimitiveType.genBitType());
		newPort("busy",         DIR.OUT, HDLPrimitiveType.genBitType());
		
		newPort("mem_addr", DIR.OUT, HDLPrimitiveType.genSignedType(32), EnumSet.of(HDLPort.OPTION.EXPORT));
		newPort("mem_din",  DIR.OUT, HDLPrimitiveType.genSignedType(32), EnumSet.of(HDLPort.OPTION.EXPORT));
		newPort("mem_dout", DIR.IN,  HDLPrimitiveType.genSignedType(32), EnumSet.of(HDLPort.OPTION.EXPORT));
		newPort("mem_we",   DIR.OUT, HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.EXPORT));
		newPort("mem_oe",   DIR.OUT, HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.EXPORT));
		newPort("mem_busy", DIR.IN,  HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.EXPORT));
	}

}
