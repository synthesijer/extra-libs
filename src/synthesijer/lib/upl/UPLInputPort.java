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

public class UPLInputPort extends HDLModule{
	
	// for Software I/F
	public boolean ready;
	public boolean done;
	public int recv_length;
	public int[] data;
	
	// for HDLModule
	public final UPLIn in;

	public final HDLPort pReady, pOpDone, pRecvLength;
	public final HDLSignal local_addr, local_wdata, local_rdata, local_we, local_oe;
	
	private final HDLValue ZERO = new HDLValue("0", HDLPrimitiveType.genSignedType(32));
	private final HDLValue ONE = new HDLValue("1", HDLPrimitiveType.genSignedType(32));
	
	private final HDLSignal recv_count;
	
	public UPLInputPort(String... args){
		super("upl_input_port", "UPLGlobalClk", "UPLReset");

		pReady = newPort("ready", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
		pOpDone = newPort("done", HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		pRecvLength = newPort("recv_length", HDLPort.DIR.OUT, HDLPrimitiveType.genSignedType(32));
		// for data[]
		HDLPort pDataLen  = newPort("data_length",  HDLPort.DIR.OUT, HDLPrimitiveType.genSignedType(32));
		HDLPort pDataDout = newPort("data_dout",    HDLPort.DIR.OUT, HDLPrimitiveType.genSignedType(32));
		HDLPort pDataAddr = newPort("data_address", HDLPort.DIR.IN,  HDLPrimitiveType.genSignedType(32));
		HDLPort pDataDin  = newPort("data_din",     HDLPort.DIR.IN,  HDLPrimitiveType.genSignedType(32));
		HDLPort pDataWe   = newPort("data_we",      HDLPort.DIR.IN,  HDLPrimitiveType.genBitType());
		HDLPort pDataOe   = newPort("data_oe",      HDLPort.DIR.IN,  HDLPrimitiveType.genBitType());
		
		local_rdata = newSignal("local_rdata", HDLPrimitiveType.genSignedType(32), HDLSignal.ResourceKind.REGISTER);
		local_addr  = newSignal("local_addr",  HDLPrimitiveType.genSignedType(32), HDLSignal.ResourceKind.REGISTER);
		local_wdata = newSignal("local_din",   HDLPrimitiveType.genSignedType(32), HDLSignal.ResourceKind.REGISTER);
		local_we    = newSignal("local_we",    HDLPrimitiveType.genBitType(), HDLSignal.ResourceKind.REGISTER);
		local_oe    = newSignal("local_oe",    HDLPrimitiveType.genBitType(), HDLSignal.ResourceKind.REGISTER);

		in = new UPLIn(this, "UPLIn_");
		
		BlockRAM bram = new BlockRAM(32, 10, 1024);
		HDLInstance ram = newModuleInstance(bram, "U_RAM");
		ram.getSignalForPort(bram.getSysClkName()).setAssign(null, getSysClk().getSignal());
		ram.getSignalForPort(bram.getSysResetName()).setAssign(null, getSysReset().getSignal());
		
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
		
		recv_count = newSignal("recv_count", HDLPrimitiveType.genSignedType(32), HDLSignal.ResourceKind.REGISTER);
		
		genSequencer();

	}
	
	private SequencerState recvWaitState(HDLSequencer s){
		SequencerState state = s.addSequencerState("RECV_WAIT");
		in.ack.getSignal().setAssign(state, newExpr(HDLOp.NOT, in.en.getSignal()));
		in.ack.getSignal().setDefaultValue(HDLPreDefinedConstant.LOW); // otherwise
		recv_count.setAssign(state, newExpr(HDLOp.IF, in.en.getSignal(), ONE, ZERO));
		local_addr.setAssign(state, ZERO);
		local_we.setAssign(state, in.en.getSignal());
		local_wdata.setAssign(state, in.data.getSignal());
		return state;
	}

	private SequencerState recvDataState(HDLSequencer s){
		SequencerState state = s.addSequencerState("RECV_DATA");
		local_addr.setAssign(state, newExpr(HDLOp.IF, in.en.getSignal(), newExpr(HDLOp.ADD, local_addr, ONE), local_addr));
		recv_count.setAssign(state, newExpr(HDLOp.IF, in.en.getSignal(), newExpr(HDLOp.ADD, recv_count, ONE), recv_count));
		local_we.setAssign(state, in.en.getSignal());
		local_wdata.setAssign(state, in.data.getSignal());
		return state;
	}

	private SequencerState operationState(HDLSequencer s){
		SequencerState s1 = s.addSequencerState("OPERATION");
		return s1;
	}
	
	private HDLSequencer genSequencer(){
		HDLSequencer s = newSequencer("main");
		SequencerState recv_wait = recvWaitState(s);
		SequencerState recv_data = recvDataState(s);
		SequencerState operation = operationState(s);
		
		s.getIdleState().addStateTransit(recv_wait);
		
		recv_wait.addStateTransit(newExpr(HDLOp.EQ, in.en.getSignal(), HDLPreDefinedConstant.HIGH), recv_data);
		recv_data.addStateTransit(newExpr(HDLOp.EQ, in.en.getSignal(), HDLPreDefinedConstant.LOW), operation);
		pReady.getSignal().setAssign(operation, HDLPreDefinedConstant.HIGH);
		pReady.getSignal().setDefaultValue(HDLPreDefinedConstant.LOW);

		HDLSignal opDone_reg = newSignal("done_reg", HDLPrimitiveType.genBitType(), HDLSignal.ResourceKind.REGISTER);
		opDone_reg.setAssignForSequencer(s, pOpDone.getSignal());
		HDLExpr opDone = newExpr(HDLOp.AND, pOpDone.getSignal(), newExpr(HDLOp.NOT, opDone_reg)); // pOpDone'rising_edge
		operation.addStateTransit(opDone, s.getIdleState());

		return s;
	}
	
	public static void main(String... args){
		UPLInputPort m = new UPLInputPort();
		HDLUtils.generate(m, HDLUtils.VHDL);
	}
	
}
