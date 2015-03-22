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

public class UPLPort extends HDLModule{
	
	// for Software I/F
	public boolean ready;
	public boolean done;
	public int send_length;
	public int recv_length;
	public int[] data;
	
	// for HDLModule
	public final UPLIn in;
	public final UPLOut out;
	public final HDLPort pReady, pDone, pSendLength, pRecvLength;
	public final HDLSignal local_addr, local_din, local_dout, local_we, local_oe;
	
	private final HDLValue ZERO = new HDLValue("0", HDLPrimitiveType.genSignedType(32));
	private final HDLValue ONE = new HDLValue("1", HDLPrimitiveType.genSignedType(32));
	
	private final HDLSignal send_count, recv_count;
	
	public UPLPort(String... args){
		super("uplport", "UPLGlobalClk", "UPLReset");

		pReady = newPort("ready", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
		pDone = newPort("done", HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		pSendLength = newPort("send_length", HDLPort.DIR.IN, HDLPrimitiveType.genSignedType(32));
		pRecvLength = newPort("recv_length", HDLPort.DIR.OUT, HDLPrimitiveType.genSignedType(32));
		// for data[]
		HDLPort length = newPort("data_length",  HDLPort.DIR.OUT, HDLPrimitiveType.genSignedType(32));
		HDLPort addr   = newPort("data_address", HDLPort.DIR.IN,  HDLPrimitiveType.genSignedType(32));
		HDLPort dout   = newPort("data_dout",    HDLPort.DIR.OUT, HDLPrimitiveType.genSignedType(32));
		HDLPort din    = newPort("data_din",     HDLPort.DIR.IN,  HDLPrimitiveType.genSignedType(32));
		HDLPort we     = newPort("data_we",      HDLPort.DIR.IN,  HDLPrimitiveType.genBitType());
		HDLPort oe     = newPort("data_oe",      HDLPort.DIR.IN,  HDLPrimitiveType.genBitType());
		
		BlockRAM bram = new BlockRAM(32, 10, 1024);
		HDLInstance ram = newModuleInstance(bram, "U");
		ram.getSignalForPort(bram.getSysClkName()).setAssign(null, getSysClk().getSignal());
		ram.getSignalForPort(bram.getSysResetName()).setAssign(null, getSysReset().getSignal());
		
		ram.getSignalForPort("address").setAssign(null, addr.getSignal());
		ram.getSignalForPort("din").setAssign(null, din.getSignal());
		ram.getSignalForPort("we").setAssign(null, we.getSignal());
		ram.getSignalForPort("oe").setAssign(null, oe.getSignal());
		length.getSignal().setAssign(null, ram.getSignalForPort("length"));
		dout.getSignal().setAssign(null, ram.getSignalForPort("dout"));
		
		local_addr = ram.getSignalForPort("address_b");
		local_dout = ram.getSignalForPort("dout");
		local_din = ram.getSignalForPort("din");
		local_we = ram.getSignalForPort("we");
		local_oe = ram.getSignalForPort("oe");

		in = new UPLIn(this, "UPLIn_");
		out = new UPLOut(this, "UPLOut_");
		
		send_count = newSignal("send_count", HDLPrimitiveType.genSignedType(32), HDLSignal.ResourceKind.REGISTER);
		recv_count = newSignal("recv_count", HDLPrimitiveType.genSignedType(32), HDLSignal.ResourceKind.REGISTER);
		
		genSequencer();
		
	}
	
	private SequencerState recvWaitState(HDLSequencer s){
		SequencerState state = s.addSequencerState("RECV_WAIT");
		in.ack.getSignal().setAssign(state, newExpr(HDLOp.NOT, in.en.getSignal()));
		in.ack.getSignal().setDefaultValue(HDLPreDefinedConstant.LOW); // otherwise
		recv_count.setAssign(state, newExpr(HDLOp.IF, in.en.getSignal(), ONE, ZERO));
		local_addr.setAssign(state, ZERO);
		local_we.setAssign(state, in.en.getSignal()); // to save first data
		local_din.setAssign(state, in.data.getSignal()); // to save first data
		return state;
	}

	private SequencerState recvDataState(HDLSequencer s){
		SequencerState state = s.addSequencerState("RECV_DATA");
		local_addr.setAssign(state, newExpr(HDLOp.IF, in.en.getSignal(), newExpr(HDLOp.ADD, local_addr, ONE), local_addr));
		recv_count.setAssign(state, newExpr(HDLOp.IF, in.en.getSignal(), newExpr(HDLOp.ADD, recv_count, ONE), recv_count));
		local_we.setAssign(state, in.en.getSignal());
		local_din.setAssign(state, in.data.getSignal());
		return state;
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
		out.data.getSignal().setAssign(state, local_dout);
		local_addr.setAssign(state, newExpr(HDLOp.ADD, local_addr, ONE));
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
		SequencerState send_wait = sendWaitState(s);
		SequencerState mem_wait = memWaitState(s);
		SequencerState send_data = sendDataState(s);
		
		s.getIdleState().addStateTransit(recv_wait);
		pReady.getSignal().setDefaultValue(HDLPreDefinedConstant.LOW);

		recv_wait.addStateTransit(newExpr(HDLOp.EQ, in.en.getSignal(), HDLPreDefinedConstant.HIGH), recv_data);
		recv_data.addStateTransit(newExpr(HDLOp.EQ, in.en.getSignal(), HDLPreDefinedConstant.LOW), operation);
		pReady.getSignal().setAssign(operation, HDLPreDefinedConstant.HIGH); // ready = '1' in operation state 
		
		HDLExpr hasData = newExpr(HDLOp.GT, pSendLength.getSignal(), ZERO);
		HDLExpr noData = newExpr(HDLOp.NOT, hasData);
		HDLSignal done_reg = newSignal("done_reg", HDLPrimitiveType.genBitType());
		done_reg.setAssignForSequencer(s, pDone.getSignal());
		HDLExpr opDone = newExpr(HDLOp.AND, pDone.getSignal(), newExpr(HDLOp.NOT, done_reg)); // done'rising_edge
		operation.addStateTransit(newExpr(HDLOp.AND, opDone, hasData), send_wait);
		operation.addStateTransit(newExpr(HDLOp.AND, opDone, noData), s.getIdleState());
		
		send_wait.addStateTransit(newExpr(HDLOp.EQ, out.ack.getSignal(), HDLPreDefinedConstant.HIGH), mem_wait);
		mem_wait.addStateTransit(send_data);
		
		HDLExpr sendLastData = newExpr(HDLOp.EQ, send_count, ONE);
		send_data.addStateTransit(sendLastData, s.getIdleState());

		return s;
	}
	
	public static void main(String... args){
		UPLPort m = new UPLPort();
		HDLUtils.generate(m, HDLUtils.VHDL);
		HDLUtils.generate(m, HDLUtils.Verilog);
	}
	
}
