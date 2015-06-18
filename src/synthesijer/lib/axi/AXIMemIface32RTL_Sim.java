package synthesijer.lib.axi;

import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLUtils;
import synthesijer.hdl.expr.HDLPreDefinedConstant;
import synthesijer.hdl.sample.BasicSim;

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
				newExpr(HDLOp.IF, at(10), HDLUtils.value(10,32),
				newExpr(HDLOp.IF, at(11), HDLUtils.value(20,32),
				newExpr(HDLOp.IF, at(12), HDLUtils.value(30,32),
				newExpr(HDLOp.IF, at(13), HDLUtils.value(40,32),
				newExpr(HDLOp.IF, at(14), HDLUtils.value(50,32),
				newExpr(HDLOp.IF, at(15), HDLUtils.value(60,32),
				newExpr(HDLOp.IF, at(16), HDLUtils.value(265,32),
						                   HDLUtils.value(60,32)))))))));
		inst.getSignalForPort(target.addr.getName()).setAssign(null,
				newExpr(HDLOp.IF, at(10), HDLUtils.value(0,32),
				newExpr(HDLOp.IF, at(11), HDLUtils.value(1,32),
				newExpr(HDLOp.IF, at(12), HDLUtils.value(2,32),
				newExpr(HDLOp.IF, at(13), HDLUtils.value(3,32),
				newExpr(HDLOp.IF, at(14), HDLUtils.value(4,32),
				newExpr(HDLOp.IF, at(15), HDLUtils.value(5,32),
				newExpr(HDLOp.IF, at(16), HDLUtils.value(255,32),
						                   HDLUtils.value(6,32)))))))));
		inst.getSignalForPort(target.hdl_write_kick.getName()).setAssign(null, newExpr(HDLOp.IF, after(100), HDLPreDefinedConstant.HIGH, HDLPreDefinedConstant.LOW));
		
		//inst.getSignalForPort(target.hdl_burst_size.getName()).setAssign(null, Utils.value(256, 32));
		//inst.getSignalForPort(target.hdl_burst_size.getName()).setAssign(null, Utils.value(1, 32));
		//inst.getSignalForPort(target.hdl_burst_size.getName()).setAssign(null, Utils.value(2, 32));
		//inst.getSignalForPort(target.hdl_burst_size.getName()).setAssign(null, Utils.value(3, 32));
		//inst.getSignalForPort(target.hdl_burst_size.getName()).setAssign(null, Utils.value(4, 32));
		//inst.getSignalForPort(target.hdl_burst_size.getName()).setAssign(null, Utils.value(5, 32));
		inst.getSignalForPort(target.hdl_burst_size.getName()).setAssign(null, HDLUtils.value(6, 32));
		
		inst.getSignalForPort(target.hdl_axi_addr.getName()).setAssign(null, HDLUtils.value(100, 32));
		inst.getSignalForPort(target.axi.writer.awready.getName()).setAssign(null, newExpr(HDLOp.IF, after(100), HDLPreDefinedConstant.HIGH, HDLPreDefinedConstant.LOW));
		inst.getSignalForPort(target.axi.writer.wready.getName()).setAssign(null,
				//newExpr(HDLOp.IF, during(110, 112), HDLPreDefinedConstant.HIGH,
				newExpr(HDLOp.IF, during(104, 112), HDLPreDefinedConstant.HIGH,
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

