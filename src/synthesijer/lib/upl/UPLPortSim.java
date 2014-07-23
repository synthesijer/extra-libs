package synthesijer.lib.upl;

import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLUtils;
import synthesijer.hdl.expr.HDLPreDefinedConstant;
import synthesijer.hdl.expr.HDLValue;
import synthesijer.hdl.sample.BasicSim;

public class UPLPortSim extends BasicSim{
	
	public UPLPortSim(UPLPort target, String name){
		super(target, name);
				
		HDLSignal in_en = inst.getSignalForPort(target.in.en.getName());
		HDLSignal in_ack = inst.getSignalForPort(target.in.ack.getName());
		HDLExpr in_pulse = delayPulse(ss, "in_period", in_ack, 10);
		in_en.setAssign(ss, newExpr(HDLOp.IF, in_pulse, HDLPreDefinedConstant.HIGH, HDLPreDefinedConstant.LOW));
		
		HDLSignal opDone = inst.getSignalForPort(target.pOpDone.getName());
		HDLSignal opReady = inst.getSignalForPort(target.pReady.getName());
		HDLSignal opReady_d = newSignal("op_ready_d", HDLPrimitiveType.genBitType());
		opReady_d.setAssign(ss, opReady);
		inst.getSignalForPort(target.pOpStart.getName()).setAssign(ss, newExpr(HDLOp.AND, opReady, newExpr(HDLOp.NOT, opReady_d)));
		
		HDLSignal op_pulse = newSignal("op_pulse", HDLPrimitiveType.genBitType());
		HDLSignal op_pulse_d = newSignal("op_pulse_d", HDLPrimitiveType.genBitType());
		op_pulse.setAssign(ss, delayPulse(ss, "op_period", opReady, 20));
		op_pulse_d.setAssign(ss, op_pulse);
		opDone.setAssign(ss, newExpr(HDLOp.AND, newExpr(HDLOp.NOT, op_pulse), op_pulse_d));  
		
		HDLSignal sendLength = inst.getSignalForPort(target.pSendLength.getName());
		sendLength.setAssign(null, new HDLValue("16", HDLPrimitiveType.genSignedType(32)));
		HDLSignal out_ack = inst.getSignalForPort(target.out.ack.getName());
		out_ack.setAssign(null, HDLPreDefinedConstant.HIGH);
		
	}
	
	
	public static void main(String... args){
		UPLPort obj = new UPLPort();
		UPLPortSim sim = new UPLPortSim(obj, "sim");
		HDLUtils.generate(sim, HDLUtils.VHDL);
	}

}
