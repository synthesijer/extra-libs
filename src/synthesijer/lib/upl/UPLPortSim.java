package synthesijer.lib.upl;

import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLUtils;
import synthesijer.hdl.expr.HDLPreDefinedConstant;
import synthesijer.hdl.sample.BasicSim;

public class UPLPortSim extends BasicSim{
	
	public UPLPortSim(UPLPort target, String name){
		super(target, name);
		
		HDLSignal opDone = inst.getSignalForPort(target.pOpDone.getName());
		opDone.setResetValue(HDLPreDefinedConstant.HIGH);
		
		opDone.setAssign(ss, newExpr(HDLOp.IF, after(10), HDLPreDefinedConstant.HIGH, HDLPreDefinedConstant.LOW));
	}
	
	
	public static void main(String... args){
		UPLPort obj = new UPLPort();
		UPLPortSim sim = new UPLPortSim(obj, "sim");
		HDLUtils.generate(sim, HDLUtils.VHDL);
	}

}
