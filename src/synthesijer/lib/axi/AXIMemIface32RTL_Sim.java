package synthesijer.lib.axi;

import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLUtils;
import synthesijer.hdl.expr.HDLPreDefinedConstant;
import synthesijer.hdl.sample.BasicSim;
import synthesijer.utils.Utils;

public class AXIMemIface32RTL_Sim extends BasicSim{
	
	public AXIMemIface32RTL_Sim(AXIMemIface32RTL target){
		super(target, "axi_memiface32_sim");
		inst.getSignalForPort(target.we.getName()).setAssign(null,
				newExpr(HDLOp.IF, at(10), HDLPreDefinedConstant.HIGH,
				newExpr(HDLOp.IF, at(11), HDLPreDefinedConstant.HIGH,
				newExpr(HDLOp.IF, at(12), HDLPreDefinedConstant.HIGH,
				newExpr(HDLOp.IF, at(13), HDLPreDefinedConstant.HIGH,
				newExpr(HDLOp.IF, at(14), HDLPreDefinedConstant.HIGH,
				newExpr(HDLOp.IF, at(15), HDLPreDefinedConstant.HIGH,
				newExpr(HDLOp.IF, at(16), HDLPreDefinedConstant.HIGH,
				HDLPreDefinedConstant.LOW))))))));
		inst.getSignalForPort(target.wdata.getName()).setAssign(null,
				newExpr(HDLOp.IF, at(10), Utils.value(10,32),
				newExpr(HDLOp.IF, at(11), Utils.value(20,32),
				newExpr(HDLOp.IF, at(12), Utils.value(30,32),
				newExpr(HDLOp.IF, at(13), Utils.value(40,32),
				newExpr(HDLOp.IF, at(14), Utils.value(50,32),
				newExpr(HDLOp.IF, at(15), Utils.value(60,32),
				newExpr(HDLOp.IF, at(16), Utils.value(265,32),
				Utils.value(60,32)))))))));
		inst.getSignalForPort(target.addr.getName()).setAssign(null,
				newExpr(HDLOp.IF, at(10), Utils.value(0,32),
				newExpr(HDLOp.IF, at(11), Utils.value(1,32),
				newExpr(HDLOp.IF, at(12), Utils.value(2,32),
				newExpr(HDLOp.IF, at(13), Utils.value(3,32),
				newExpr(HDLOp.IF, at(14), Utils.value(4,32),
				newExpr(HDLOp.IF, at(15), Utils.value(5,32),
				newExpr(HDLOp.IF, at(16), Utils.value(255,32),
				Utils.value(6,32)))))))));
		inst.getSignalForPort(target.hdl_write_kick.getName()).setAssign(null, newExpr(HDLOp.IF, after(100), HDLPreDefinedConstant.HIGH, HDLPreDefinedConstant.LOW));
		
		//inst.getSignalForPort(target.hdl_burst_size.getName()).setAssign(null, Utils.value(256, 32));
		//inst.getSignalForPort(target.hdl_burst_size.getName()).setAssign(null, Utils.value(1, 32));
		//inst.getSignalForPort(target.hdl_burst_size.getName()).setAssign(null, Utils.value(2, 32));
		//inst.getSignalForPort(target.hdl_burst_size.getName()).setAssign(null, Utils.value(3, 32));
		//inst.getSignalForPort(target.hdl_burst_size.getName()).setAssign(null, Utils.value(4, 32));
		//inst.getSignalForPort(target.hdl_burst_size.getName()).setAssign(null, Utils.value(5, 32));
		inst.getSignalForPort(target.hdl_burst_size.getName()).setAssign(null, Utils.value(6, 32));
		
		inst.getSignalForPort(target.hdl_axi_addr.getName()).setAssign(null, Utils.value(100, 32));
		inst.getSignalForPort(target.axi.writer.awready.getName()).setAssign(null, newExpr(HDLOp.IF, after(100), HDLPreDefinedConstant.HIGH, HDLPreDefinedConstant.LOW));
		inst.getSignalForPort(target.axi.writer.wready.getName()).setAssign(null,
				newExpr(HDLOp.IF, during(110, 112), HDLPreDefinedConstant.HIGH,
				//newExpr(HDLOp.IF, during(150, 152), HDLPreDefinedConstant.HIGH,
				newExpr(HDLOp.IF, after(160), HDLPreDefinedConstant.HIGH,
				HDLPreDefinedConstant.LOW)));
		inst.getSignalForPort(target.axi.writer.bvalid.getName()).setAssign(null, HDLPreDefinedConstant.HIGH);
		
		inst.getSignalForPort(target.hdl_read_kick.getName()).setAssign(null, newExpr(HDLOp.IF, at(400), HDLPreDefinedConstant.HIGH, HDLPreDefinedConstant.LOW));
		inst.getSignalForPort(target.axi.reader.arready.getName()).setAssign(null, newExpr(HDLOp.IF, at(410), HDLPreDefinedConstant.HIGH, HDLPreDefinedConstant.LOW));
		inst.getSignalForPort(target.axi.reader.rvalid.getName()).setAssign(null,
				newExpr(HDLOp.IF, during(415,418), HDLPreDefinedConstant.HIGH,
				newExpr(HDLOp.IF, after(430), HDLPreDefinedConstant.HIGH, HDLPreDefinedConstant.LOW)));
	}
	
	public static void main(String... args){
		AXIMemIface32RTL_Sim sim = new AXIMemIface32RTL_Sim(new AXIMemIface32RTL());
		HDLUtils.generate(sim, HDLUtils.VHDL);
		HDLUtils.generate(sim, HDLUtils.Verilog);
	}
	
}

