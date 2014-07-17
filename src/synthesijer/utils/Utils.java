package synthesijer.utils;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPrimitiveType;

public class Utils {

	public static HDLPort genOutputPort(HDLModule m, String name, int width){
		return m.newPort(name, HDLPort.DIR.OUT, HDLPrimitiveType.genVectorType(width));
	}

	public static HDLPort genOutputPort(HDLModule m, String name){
		return m.newPort(name, HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
	}

	public static HDLPort genInputPort(HDLModule m, String name, int width){
		return m.newPort(name, HDLPort.DIR.IN, HDLPrimitiveType.genVectorType(width));
	}

	public static HDLPort genInputPort(HDLModule m, String name){
		return m.newPort(name, HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
	}

}
