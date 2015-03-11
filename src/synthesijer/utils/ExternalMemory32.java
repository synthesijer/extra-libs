package synthesijer.utils;

import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLUtils;

public class ExternalMemory32 extends HDLModule{
	
	// dummy declaration for synthesijer module
	int[] data;
	
	public ExternalMemory32(String... args){
		super("external_memory_32", "clk", "reset");
		
		HDLPort length = newPort("data_length", DIR.OUT, HDLPrimitiveType.genSignedType(32));
		HDLPort addr   = newPort("data_address",   DIR.IN, HDLPrimitiveType.genSignedType(32));
		HDLPort din    = newPort("data_din",    DIR.IN, HDLPrimitiveType.genSignedType(32));
		HDLPort dout   = newPort("data_dout",   DIR.OUT, HDLPrimitiveType.genSignedType(32));
		HDLPort we     = newPort("data_we",     DIR.IN, HDLPrimitiveType.genBitType());
		HDLPort oe     = newPort("data_oe",     DIR.IN, HDLPrimitiveType.genBitType());
		
		HDLPort ext_length = newPort("ext_length", DIR.IN,  HDLPrimitiveType.genSignedType(32), EnumSet.of(HDLPort.OPTION.EXPORT));
		HDLPort ext_addr   = newPort("ext_address",   DIR.OUT, HDLPrimitiveType.genSignedType(32), EnumSet.of(HDLPort.OPTION.EXPORT));
		HDLPort ext_din    = newPort("ext_din",    DIR.OUT, HDLPrimitiveType.genSignedType(32), EnumSet.of(HDLPort.OPTION.EXPORT));
		HDLPort ext_dout   = newPort("ext_dout",   DIR.IN,  HDLPrimitiveType.genSignedType(32), EnumSet.of(HDLPort.OPTION.EXPORT));
		HDLPort ext_we     = newPort("ext_we",     DIR.OUT, HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.EXPORT));
		HDLPort ext_oe     = newPort("ext_oe",     DIR.OUT, HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.EXPORT));
		
		length.getSignal().setAssign(null, ext_length.getSignal());
		dout.getSignal().setAssign(null, ext_dout.getSignal());
		
		ext_addr.getSignal().setAssign(null, addr.getSignal());
		ext_din.getSignal().setAssign(null, din.getSignal());
		ext_we.getSignal().setAssign(null, we.getSignal());
		ext_oe.getSignal().setAssign(null, oe.getSignal());
		
	}
	
	// execute this program to generate external_memory
	public static void main(String... args){
		ExternalMemory32 o = new ExternalMemory32();
		HDLUtils.generate(o, HDLUtils.VHDL);
		HDLUtils.generate(o, HDLUtils.Verilog);
	}
	
	
}
