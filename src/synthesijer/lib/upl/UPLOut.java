package synthesijer.lib.upl;

import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPrimitiveType;

public class UPLOut {

	public final HDLPort data, en, req, ack;
	
	public UPLOut(HDLModule m, String prefix){
		data = m.newPort(prefix + "Data", HDLPort.DIR.OUT, HDLPrimitiveType.genVectorType(32), EnumSet.of(HDLPort.OPTION.EXPORT));
		en   = m.newPort(prefix + "En",   HDLPort.DIR.OUT, HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.EXPORT));
		req  = m.newPort(prefix + "Request", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.EXPORT));
		ack  = m.newPort(prefix + "Ack",  HDLPort.DIR.IN,  HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.EXPORT));
	}

}
