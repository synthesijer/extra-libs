package synthesijer.lib.axi;

import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLUtils;
import synthesijer.hdl.expr.HDLPreDefinedConstant;
import synthesijer.hdl.sample.BasicSim;

public class AXIMemIface64RTL_Sim extends BasicSim{
	
	public AXIMemIface64RTL_Sim(AXIMemIface64RTL_BRAM_Ext_Ctrl target){
		super(target, "axi_memiface64_sim");
		inst.getSignalForPort("data_we_external").setAssign(null,
				newExpr(HDLOp.IF, at(10), HDLPreDefinedConstant.HIGH,
				newExpr(HDLOp.IF, at(11), HDLPreDefinedConstant.HIGH,
				newExpr(HDLOp.IF, at(12), HDLPreDefinedConstant.HIGH,
				newExpr(HDLOp.IF, at(13), HDLPreDefinedConstant.HIGH,
				newExpr(HDLOp.IF, at(14), HDLPreDefinedConstant.HIGH,
				newExpr(HDLOp.IF, at(15), HDLPreDefinedConstant.HIGH,
				newExpr(HDLOp.IF, at(16), HDLPreDefinedConstant.HIGH,
				HDLPreDefinedConstant.LOW))))))));
		inst.getSignalForPort("data_din_external").setAssign(null,
				newExpr(HDLOp.IF, at(10), HDLUtils.value(10,64),
				newExpr(HDLOp.IF, at(11), HDLUtils.value(20,64),
				newExpr(HDLOp.IF, at(12), HDLUtils.value(30,64),
				newExpr(HDLOp.IF, at(13), HDLUtils.value(40,64),
				newExpr(HDLOp.IF, at(14), HDLUtils.value(50,64),
				newExpr(HDLOp.IF, at(15), HDLUtils.value(60,64),
				newExpr(HDLOp.IF, at(16), HDLUtils.value(265,64),
						                   HDLUtils.value(60,64)))))))));
		inst.getSignalForPort("data_address_external").setAssign(null,
				newExpr(HDLOp.IF, at(10), HDLUtils.value(0,32),
				newExpr(HDLOp.IF, at(11), HDLUtils.value(1,32),
				newExpr(HDLOp.IF, at(12), HDLUtils.value(2,32),
				newExpr(HDLOp.IF, at(13), HDLUtils.value(3,32),
				newExpr(HDLOp.IF, at(14), HDLUtils.value(4,32),
				newExpr(HDLOp.IF, at(15), HDLUtils.value(5,32),
				newExpr(HDLOp.IF, at(16), HDLUtils.value(255,32),
						                   HDLUtils.value(6,32)))))))));
		inst.getSignalForPort(target.hdl_write_kick.getName()).setAssign(null, newExpr(HDLOp.IF, at(100), HDLPreDefinedConstant.HIGH, HDLPreDefinedConstant.LOW));
		
		//inst.getSignalForPort(target.hdl_burst_size.getName()).setAssign(null, Utils.value(256, 32));
		//inst.getSignalForPort(target.hdl_burst_size.getName()).setAssign(null, Utils.value(1, 32));
		//inst.getSignalForPort(target.hdl_burst_size.getName()).setAssign(null, Utils.value(2, 32));
		//inst.getSignalForPort(target.hdl_burst_size.getName()).setAssign(null, Utils.value(3, 32));
		//inst.getSignalForPort(target.hdl_burst_size.getName()).setAssign(null, Utils.value(4, 32));
		//inst.getSignalForPort(target.hdl_burst_size.getName()).setAssign(null, Utils.value(5, 32));
		inst.getSignalForPort(target.hdl_burst_size.getName()).setAssign(null, HDLUtils.value(6, 32));
		
		inst.getSignalForPort(target.hdl_axi_addr.getName()).setAssign(null, HDLUtils.value(512*1024*1024, 32));
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
		AXIMemIface64RTL_Sim sim = new AXIMemIface64RTL_Sim(new AXIMemIface64RTL_BRAM_Ext_Ctrl());
		HDLUtils.generate(sim, HDLUtils.VHDL);
		HDLUtils.generate(sim, HDLUtils.Verilog);
	}
	
}

