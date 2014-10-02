package synthesijer.utils;

import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPort.OPTION;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.expr.HDLValue;

public class Utils {

	public static HDLPort genOutputPort(HDLModule m, String name){
		return m.newPort(name, HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
	}

	public static HDLPort genOutputPort(HDLModule m, String name, EnumSet<OPTION> opt){
		return m.newPort(name, HDLPort.DIR.OUT, HDLPrimitiveType.genBitType(), opt);
	}

	public static HDLPort genOutputPort(HDLModule m, String name, int width){
		return m.newPort(name, HDLPort.DIR.OUT, HDLPrimitiveType.genVectorType(width));
	}
	
	public static HDLPort genOutputPort(HDLModule m, String name, int width, EnumSet<OPTION> opt){
		return m.newPort(name, HDLPort.DIR.OUT, HDLPrimitiveType.genVectorType(width), opt);
	}

	public static HDLPort genInputPort(HDLModule m, String name){
		return m.newPort(name, HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
	}

	public static HDLPort genInputPort(HDLModule m, String name, EnumSet<OPTION> opt){
		return m.newPort(name, HDLPort.DIR.IN, HDLPrimitiveType.genBitType(), opt);
	}

	public static HDLPort genInputPort(HDLModule m, String name, int width){
		return m.newPort(name, HDLPort.DIR.IN, HDLPrimitiveType.genVectorType(width));
	}

	public static HDLPort genInputPort(HDLModule m, String name, int width, EnumSet<OPTION> opt){
		return m.newPort(name, HDLPort.DIR.IN, HDLPrimitiveType.genVectorType(width), opt);
	}
	
	public static HDLValue value(int value, int width){
		return new HDLValue(String.valueOf(value), HDLPrimitiveType.genSignedType(width));
	}

}
