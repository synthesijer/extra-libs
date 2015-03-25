package synthesijer.lib.upl;

import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLInstance;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLUtils;
import synthesijer.hdl.expr.HDLPreDefinedConstant;
import synthesijer.hdl.expr.HDLValue;
import synthesijer.hdl.sequencer.SequencerState;
import synthesijer.lib.BlockRAM;

public class UPLOutputPort extends HDLModule{
	
	// for Software I/F
	public boolean ready;
	public boolean kick;
	public int send_length;
	public int[] data;
	
	// for HDLModule
	public final UPLOut out;
	public final HDLPort pReady, pKick, pSendLength;
	public final HDLSignal local_addr, local_wdata, local_rdata, local_we, local_oe;
	
	private final HDLValue ZERO = new HDLValue("0", HDLPrimitiveType.genSignedType(32));
	private final HDLValue ONE = new HDLValue("1", HDLPrimitiveType.genSignedType(32));
	
	private final HDLSignal send_count;
	
	public UPLOutputPort(String... args){
		super("upl_output_port", "UPLGlobalClk", "UPLReset");

		pReady = newPort("ready", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
		pKick = newPort("kick", HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		pSendLength = newPort("send_length", HDLPort.DIR.IN, HDLPrimitiveType.genSignedType(32));
		// for data[]
		HDLPort pDataLen  = newPort("data_length",  HDLPort.DIR.OUT,  HDLPrimitiveType.genSignedType(32));
		HDLPort pDataAddr = newPort("data_address", HDLPort.DIR.IN,  HDLPrimitiveType.genSignedType(32));
		HDLPort pDataDout = newPort("data_dout",    HDLPort.DIR.OUT, HDLPrimitiveType.genSignedType(32));
		HDLPort pDataDin  = newPort("data_din",     HDLPort.DIR.IN,  HDLPrimitiveType.genSignedType(32));
		HDLPort pDataWe   = newPort("data_we",      HDLPort.DIR.IN,  HDLPrimitiveType.genBitType());
		HDLPort pDataOe   = newPort("data_oe",      HDLPort.DIR.IN,  HDLPrimitiveType.genBitType());
		
		BlockRAM bram = new BlockRAM(32, 10, 1024);
		HDLInstance ram = newModuleInstance(bram, "U_RAM");
		ram.getSignalForPort(bram.getSysClkName()).setAssign(null, getSysClk().getSignal());
		ram.getSignalForPort(bram.getSysResetName()).setAssign(null, getSysReset().getSignal());
		
		local_rdata = newSignal("local_rdata", HDLPrimitiveType.genSignedType(32), HDLSignal.ResourceKind.REGISTER);
		local_addr  = newSignal("local_addr",  HDLPrimitiveType.genSignedType(32), HDLSignal.ResourceKind.REGISTER);
		local_wdata = newSignal("local_din",   HDLPrimitiveType.genSignedType(32), HDLSignal.ResourceKind.REGISTER);
		local_we    = newSignal("local_we",    HDLPrimitiveType.genBitType(), HDLSignal.ResourceKind.REGISTER);
		local_oe    = newSignal("local_oe",    HDLPrimitiveType.genBitType(), HDLSignal.ResourceKind.REGISTER);

		ram.getSignalForPort("address").setAssign(null, pDataAddr.getSignal());
		ram.getSignalForPort("din").setAssign(null, pDataDin.getSignal());
		ram.getSignalForPort("we").setAssign(null, pDataWe.getSignal());
		ram.getSignalForPort("oe").setAssign(null, pDataOe.getSignal());
		pDataLen.getSignal().setAssign(null, ram.getSignalForPort("length"));
		pDataDout.getSignal().setAssign(null, ram.getSignalForPort("dout"));
		
		ram.getSignalForPort("address_b").setAssign(null, local_addr);
		ram.getSignalForPort("din_b").setAssign(null, local_wdata);
		ram.getSignalForPort("we_b").setAssign(null, local_we);
		ram.getSignalForPort("oe_b").setAssign(null, local_oe);
		local_rdata.setAssign(null, ram.getSignalForPort("dout_b"));

		out = new UPLOut(this, "UPLOut_");
		
		send_count = newSignal("send_count", HDLPrimitiveType.genSignedType(32), HDLSignal.ResourceKind.REGISTER);

		genSequencer();
		
	}
	
	private SequencerState sendWaitState(HDLSequencer s){
		SequencerState state = s.addSequencerState("SEND_WAIT");
		send_count.setAssign(state, newExpr(HDLOp.ID, pSendLength.getSignal()));
		out.req.getSignal().setAssign(state, HDLPreDefinedConstant.HIGH);
		out.req.getSignal().setDefaultValue(HDLPreDefinedConstant.LOW); // otherwise
		local_addr.setAssign(state, ZERO);
		return state;
	}
	
	private SequencerState memWaitState(HDLSequencer s){
		SequencerState state = s.addSequencerState("MEM_WAIT");
		local_addr.setAssign(state, newExpr(HDLOp.ADD, local_addr, ONE));
		return state;
	}
	
	private SequencerState sendDataState(HDLSequencer s){
		SequencerState state = s.addSequencerState("SEND_DATA");
		send_count.setAssign(state, newExpr(HDLOp.SUB, send_count, ONE));
		out.en.getSignal().setAssign(state, HDLPreDefinedConstant.HIGH);
		out.en.getSignal().setDefaultValue(HDLPreDefinedConstant.LOW); // otherwise
		out.data.getSignal().setAssign(state, local_rdata);
		local_addr.setAssign(state, newExpr(HDLOp.ADD, local_addr, ONE));
		return state;
	}
	
	private SequencerState operationState(HDLSequencer s){
		SequencerState s1 = s.addSequencerState("OPERATION");
		return s1;
	}
	
	private HDLSequencer genSequencer(){
		HDLSequencer s = newSequencer("main");
		SequencerState operation = operationState(s);
		SequencerState send_wait = sendWaitState(s);
		SequencerState mem_wait = memWaitState(s);
		SequencerState send_data = sendDataState(s);
		
		// idle -> operation, when kick is low 
		HDLExpr opReady = newExpr(HDLOp.EQ, pKick.getSignal(), HDLPreDefinedConstant.LOW);
		s.getIdleState().addStateTransit(opReady, operation);

		HDLExpr hasData = newExpr(HDLOp.GT, pSendLength.getSignal(), ZERO);
		HDLExpr noData = newExpr(HDLOp.NOT, hasData);
		// operation -> send_wait, when kick is high and send_length > 0 
		operation.addStateTransit(newExpr(HDLOp.AND, pKick.getSignal(), hasData), send_wait);
		// operation -> idle, when kick is high and !(send_length > 0) 
		operation.addStateTransit(newExpr(HDLOp.AND, pKick.getSignal(), noData), s.getIdleState());
		
		send_wait.addStateTransit(newExpr(HDLOp.EQ, out.ack.getSignal(), HDLPreDefinedConstant.HIGH), mem_wait);
		mem_wait.addStateTransit(send_data);
		
		HDLExpr sendLastData = newExpr(HDLOp.EQ, send_count, ONE);
		send_data.addStateTransit(sendLastData, s.getIdleState());

		return s;
	}
	
	public static void main(String... args){
		UPLOutputPort m = new UPLOutputPort();
		HDLUtils.generate(m, HDLUtils.VHDL);
	}
	
}
