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
import synthesijer.lib.BlockRAM;

public class UPLPort extends HDLModule{
	
	// for Software I/F
	public boolean ready;
	public boolean op_done;
	public boolean op_start;
	public int send_length;
	public int recv_length;
	public int[] data;
	
	// for HDLModule
	UPLIn in;
	UPLOut out;
	HDLPort pReady, pOpDone, pOpStart, pSendLength, pRecvLength;
	HDLPort pDataRaddr, pDataDout, pDataWaddr, pDataDin, pDataWe;
	
	private HDLValue ZERO = new HDLValue("0", HDLPrimitiveType.genSignedType(32));
	private HDLValue ONE = new HDLValue("1", HDLPrimitiveType.genSignedType(32));
	
	public UPLPort(String... args){
		super("uplport", "UPLGlobalClk", "UPLReset");

		pReady = newPort("ready", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
		pOpDone = newPort("op_done", HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		pOpStart = newPort("op_start", HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		pSendLength = newPort("send_length", HDLPort.DIR.IN, HDLPrimitiveType.genSignedType(32));
		pRecvLength = newPort("recv_length", HDLPort.DIR.OUT, HDLPrimitiveType.genSignedType(32));
		// for data[]
		pDataRaddr = newPort("data_raddress", HDLPort.DIR.IN,  HDLPrimitiveType.genSignedType(32));
		pDataDout  = newPort("data_dout",     HDLPort.DIR.OUT, HDLPrimitiveType.genSignedType(32));
		pDataWaddr = newPort("data_waddress", HDLPort.DIR.IN,  HDLPrimitiveType.genSignedType(32));
		pDataDin   = newPort("data_din",      HDLPort.DIR.IN,  HDLPrimitiveType.genSignedType(32));
		pDataWe    = newPort("data_we",       HDLPort.DIR.IN,  HDLPrimitiveType.genBitType());
		
		in = new UPLIn(this, "UPLIn_");
		out = new UPLOut(this, "UPLOut_");
		
	}
	
	private HDLSequencer.SequencerState recvWaitState(HDLSequencer s){
		HDLSequencer.SequencerState state = s.addSequencerState("RECV_WAIT");
		in.ack.getSignal().setAssign(state, newExpr(HDLOp.NOT, in.en.getSignal()));
		in.ack.getSignal().setDefaultValue(HDLPreDefinedConstant.LOW); // otherwise
		recv_count.setAssign(state, newExpr(HDLOp.IF, in.en.getSignal(), ONE, ZERO));
		//local_waddr.setAssign(state, newExpr(HDLOp.IF, in.en.getSignal(), ONE, ZERO));
		local_waddr.setAssign(state, ZERO);
		local_we.setAssign(state, in.en.getSignal());
		local_din.setAssign(state, in.data.getSignal());
		return state;
	}

	private HDLSequencer.SequencerState recvDataState(HDLSequencer s){
		HDLSequencer.SequencerState state = s.addSequencerState("RECV_DATA");
		local_waddr.setAssign(state, newExpr(HDLOp.IF, in.en.getSignal(), newExpr(HDLOp.ADD, local_waddr, ONE), local_waddr));
		recv_count.setAssign(state, newExpr(HDLOp.IF, in.en.getSignal(), newExpr(HDLOp.ADD, recv_count, ONE), recv_count));
		local_we.setAssign(state, in.en.getSignal());
		local_din.setAssign(state, in.data.getSignal());
		return state;
	}
	
	private HDLSequencer.SequencerState sendWaitState(HDLSequencer s){
		HDLSequencer.SequencerState state = s.addSequencerState("SEND_WAIT");
		send_count.setAssign(state, newExpr(HDLOp.ID, pSendLength.getSignal()));
		out.req.getSignal().setAssign(state, HDLPreDefinedConstant.HIGH);
		out.req.getSignal().setDefaultValue(HDLPreDefinedConstant.LOW); // otherwise
		local_raddr.setAssign(state, ZERO);
		return state;
	}
	
	private HDLSequencer.SequencerState memWaitState(HDLSequencer s){
		HDLSequencer.SequencerState state = s.addSequencerState("MEM_WAIT");
		local_raddr.setAssign(state, newExpr(HDLOp.ADD, local_raddr, ONE));
		return state;
	}
	
	private HDLSequencer.SequencerState sendDataState(HDLSequencer s){
		HDLSequencer.SequencerState state = s.addSequencerState("SEND_DATA");
		send_count.setAssign(state, newExpr(HDLOp.SUB, send_count, ONE));
		out.en.getSignal().setAssign(state, HDLPreDefinedConstant.HIGH);
		out.en.getSignal().setDefaultValue(HDLPreDefinedConstant.LOW); // otherwise
		out.data.getSignal().setAssign(state, local_dout);
		local_raddr.setAssign(state, newExpr(HDLOp.ADD, local_raddr, ONE));
		return state;
	}
	
	private HDLSequencer.SequencerState operationState0(HDLSequencer s){
		HDLSequencer.SequencerState s0 = s.addSequencerState("OPERATION0");
		pReady.getSignal().setAssign(s0, HDLPreDefinedConstant.HIGH);
		pReady.getSignal().setDefaultValue(HDLPreDefinedConstant.LOW); // others
		return s0;
	}
	
	private HDLSequencer.SequencerState operationState(HDLSequencer s){
		HDLSequencer.SequencerState s1 = s.addSequencerState("OPERATION");
		return s1;
	}
	
	private HDLSequencer.SequencerState operation;
	private HDLSequencer genSequencer(){
		HDLSequencer s = newSequencer("main");
		HDLSequencer.SequencerState recv_wait = recvWaitState(s);
		HDLSequencer.SequencerState recv_data = recvDataState(s);
		HDLSequencer.SequencerState operation0 = operationState0(s);
		operation = operationState(s);
		HDLSequencer.SequencerState send_wait = sendWaitState(s);
		HDLSequencer.SequencerState mem_wait = memWaitState(s);
		HDLSequencer.SequencerState send_data = sendDataState(s);
		
		HDLExpr opReady = newExpr(HDLOp.AND,
				newExpr(HDLOp.EQ, pOpStart.getSignal(), HDLPreDefinedConstant.LOW),
				newExpr(HDLOp.EQ, pOpDone.getSignal(), HDLPreDefinedConstant.LOW));
		s.getIdleState().addStateTransit(opReady, recv_wait);

		recv_wait.addStateTransit(newExpr(HDLOp.EQ, in.en.getSignal(), HDLPreDefinedConstant.HIGH), recv_data);
		recv_data.addStateTransit(newExpr(HDLOp.EQ, in.en.getSignal(), HDLPreDefinedConstant.LOW), operation0);
		operation0.addStateTransit(newExpr(HDLOp.EQ, pOpStart.getSignal(), HDLPreDefinedConstant.HIGH), operation);
		
		HDLExpr hasData = newExpr(HDLOp.GT, pSendLength.getSignal(), ZERO);
		HDLExpr noData = newExpr(HDLOp.NOT, hasData);
		HDLExpr opDone = newExpr(HDLOp.EQ, pOpDone.getSignal(), HDLPreDefinedConstant.HIGH);
		operation.addStateTransit(newExpr(HDLOp.AND, opDone, hasData), send_wait);
		operation.addStateTransit(newExpr(HDLOp.AND, opDone, noData), s.getIdleState());
		
		send_wait.addStateTransit(newExpr(HDLOp.EQ, out.ack.getSignal(), HDLPreDefinedConstant.HIGH), mem_wait);
		mem_wait.addStateTransit(send_data);
		
		HDLExpr sendLastData = newExpr(HDLOp.EQ, send_count, ONE);
		send_data.addStateTransit(sendLastData, s.getIdleState());

		return s;
	}
	
	private void genMuxRam(HDLSequencer s, HDLInstance ram){
		HDLSignal ram_waddr, ram_raddr, ram_we, ram_din, ram_dout;
		ram_waddr = ram.getSignalForPort("waddress");
		ram_raddr = ram.getSignalForPort("raddress");
		ram_we = ram.getSignalForPort("we");
		ram_din = ram.getSignalForPort("din");
		ram_dout = ram.getSignalForPort("dout");
		
		HDLExpr userOp = newExpr(HDLOp.EQ, s.getStateKey(), operation.getStateId());
		ram_waddr.setAssign(null, newExpr(HDLOp.IF, userOp, pDataWaddr.getSignal(), local_waddr));
		ram_raddr.setAssign(null, newExpr(HDLOp.IF, userOp, pDataRaddr.getSignal(), local_raddr));
		ram_we.setAssign(null, newExpr(HDLOp.IF, userOp, pDataWe.getSignal(), local_we));
		ram_din.setAssign(null, newExpr(HDLOp.IF, userOp, pDataDin.getSignal(), local_din));
		pDataDout.getSignal().setAssign(null, ram_dout);
		
		local_dout.setAssign(null, ram_dout);
		pRecvLength.getSignal().setAssign(null, recv_count);
	}
	
	private HDLSignal local_raddr, local_waddr, local_we, local_din, local_dout;
	private HDLSignal recv_count, send_count;
	private void genLocalSignals(){
		recv_count = newSignal("recv_count", HDLPrimitiveType.genSignedType(32), HDLSignal.ResourceKind.REGISTER);
		send_count = newSignal("send_count", HDLPrimitiveType.genSignedType(32), HDLSignal.ResourceKind.REGISTER);
		local_raddr = newSignal("local_raddress", HDLPrimitiveType.genSignedType(32), HDLSignal.ResourceKind.REGISTER);
		local_waddr = newSignal("local_waddress", HDLPrimitiveType.genSignedType(32), HDLSignal.ResourceKind.REGISTER);
		local_we = newSignal("local_we", HDLPrimitiveType.genBitType(), HDLSignal.ResourceKind.REGISTER);
		local_din = newSignal("local_din", HDLPrimitiveType.genSignedType(32), HDLSignal.ResourceKind.REGISTER);
		local_dout = newSignal("local_dout", HDLPrimitiveType.genSignedType(32), HDLSignal.ResourceKind.REGISTER);
	}
	
	public static void main(String... args){
		UPLPort m = new UPLPort();
		BlockRAM bram = new BlockRAM(32, 10, 1024);
		HDLInstance ram = m.newModuleInstance(bram, "U_RAM");
		ram.getSignalForPort(bram.getSysClkName()).setAssign(null, m.getSysClk().getSignal());
		ram.getSignalForPort(bram.getSysResetName()).setAssign(null, m.getSysReset().getSignal());
		
		m.genLocalSignals();
		HDLSequencer s = m.genSequencer();
		m.genMuxRam(s, ram);		
		
		HDLUtils.generate(m, HDLUtils.VHDL);
	}
	
}