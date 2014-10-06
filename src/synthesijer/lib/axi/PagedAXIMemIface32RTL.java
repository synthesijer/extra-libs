package synthesijer.lib.axi;

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
import synthesijer.hdl.sequencer.SequencerState;
import synthesijer.lib.BlockRAM;
import synthesijer.utils.Utils;

public class PagedAXIMemIface32RTL extends HDLModule{
	
	/////////////////////////////////////////////////////////////////
	// dummy variables, for Synthesijer /////////////////////////////
	public boolean busy;
	public int data[];
	public int page_base_addr;
	public boolean page_write;
	public boolean page_read;
	/////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////
	// RTL implementation
	
	public final HDLPort addr, wdata, rdata, we, oe, hdl_busy, length;
	
	public final AXI_Reader_Buffer reader;
	public final AXI_Writer_Buffer writer;
	
	private final HDLSignal read_state_busy;
	private final HDLSignal write_state_busy;
		
	private final HDLPort hdl_page_base_addr, hdl_page_write, hdl_page_read;
	private final HDLExpr hdl_page_write_edge, hdl_page_read_edge;

	private int PAGE_MEM_LENGTH = 256; // 256 is max-burst length of AXI3
	private int PAGE_MEM_DEPTH = 8;
	
	private final HDLInstance inst;

	public PagedAXIMemIface32RTL(String... args){
		super("paged_axi_memiface_32", "clk", "reset");
		
		addr = Utils.genInputPort(this, "data_address", 32);
		wdata = Utils.genInputPort(this, "data_din", 32);
		rdata = Utils.genOutputPort(this, "data_dout", 32);
		we = Utils.genInputPort(this, "data_we");
		oe = Utils.genInputPort(this, "data_oe");
		length = Utils.genOutputPort(this, "data_length", 32);
		hdl_busy = Utils.genOutputPort(this, "busy");
		
		hdl_page_base_addr = Utils.genInputPort(this, "page_base_addr", 32);
		hdl_page_read = Utils.genInputPort(this, "page_read");
		hdl_page_write = Utils.genInputPort(this, "page_write");
				
		writer = new AXI_Writer_Buffer(32);
		reader = new AXI_Reader_Buffer(32);
		
		read_state_busy = newSignal("read_state_busy", HDLPrimitiveType.genBitType());
		write_state_busy = newSignal("write_state_busy", HDLPrimitiveType.genBitType());
		
		hdl_busy.getSignal().setAssign(null, newExpr(HDLOp.OR, write_state_busy, read_state_busy));
		
		BlockRAM ram = new BlockRAM(32, PAGE_MEM_DEPTH, PAGE_MEM_LENGTH);
		inst = newModuleInstance(ram, "U_MEM");
		
		// wiring ports of memory
		inst.getSignalForPort("address_b").setAssign(null, addr.getSignal());
		inst.getSignalForPort("din_b").setAssign(null, wdata.getSignal());
		rdata.getSignal().setAssign(null, inst.getSignalForPort("dout_b"));
		inst.getSignalForPort("we_b").setAssign(null, we.getSignal());
		inst.getSignalForPort("oe_b").setAssign(null, oe.getSignal());
		length.getSignal().setAssign(null, inst.getSignalForPort("length"));
		
		inst.getSignalForPort("address").setAssign(null,
				newExpr(HDLOp.IF, read_state_busy, reader.mem.address.getSignal(), writer.mem.address.getSignal()));
		writer.mem.din.getSignal().setAssign(null, inst.getSignalForPort("din"));
		inst.getSignalForPort("oe").setAssign(null, writer.mem.oe.getSignal());
		
		inst.getSignalForPort("we").setAssign(null, reader.mem.we.getSignal());
		inst.getSignalForPort("dout").setAssign(null, reader.mem.dout.getSignal());
	
		HDLSequencer d = newSequencer("d");
		HDLSignal hdl_page_read_d = newSignal("page_read_d", HDLPrimitiveType.genBitType());
		HDLSignal hdl_page_write_d = newSignal("page_write_d", HDLPrimitiveType.genBitType());

		hdl_page_read_d.setAssign(d.getIdleState(), hdl_page_read.getSignal());
		hdl_page_write_d.setAssign(d.getIdleState(), hdl_page_write.getSignal());
		
		hdl_page_read_edge = newExpr(HDLOp.AND, newExpr(HDLOp.NOT, hdl_page_read_d), hdl_page_read.getSignal());
		hdl_page_write_edge = newExpr(HDLOp.AND, newExpr(HDLOp.NOT, hdl_page_write_d), hdl_page_write.getSignal());
		
		genSequence(newSequencer("main"));
	}

	private SequencerState genWriteSequence(HDLSequencer seq){
		SequencerState state = seq.addSequencerState("write_state");
		writer.req.getSignal().setAssign(state, HDLPreDefinedConstant.LOW);
		write_state_busy.setAssign(state, writer.busy.getSignal());
		state.addStateTransit(newExpr(HDLOp.NOT, writer.busy.getSignal()), seq.getIdleState());
		return state;
	}
	
	private SequencerState genReadSequence(HDLSequencer seq){
		SequencerState state = seq.addSequencerState("read_state");
		reader.req.getSignal().setAssign(state, HDLPreDefinedConstant.LOW);
		read_state_busy.setAssign(state, reader.busy.getSignal());
		state.addStateTransit(newExpr(HDLOp.NOT, reader.busy.getSignal()), seq.getIdleState());
		return state;
	}
	
	private void genSequence(HDLSequencer seq){
		
		SequencerState write_state = genWriteSequence(seq);
		SequencerState read_state = genReadSequence(seq);
		
		HDLExpr read_kick, write_kick;
		read_kick = hdl_page_read_edge;
		write_kick = newExpr(HDLOp.AND, hdl_page_write_edge, newExpr(HDLOp.NOT, hdl_page_read_edge));
		
		seq.getIdleState().addStateTransit(write_kick, write_state);
		seq.getIdleState().addStateTransit(read_kick, read_state);
		
		
		writer.addr.getSignal().setAssign(seq.getIdleState(), hdl_page_base_addr.getSignal());
		writer.len.getSignal().setAssign(seq.getIdleState(), Utils.value(PAGE_MEM_LENGTH, 32));
		writer.req.getSignal().setAssign(seq.getIdleState(), write_kick);
		write_state_busy.setAssign(seq.getIdleState(), write_kick);
		
		reader.addr.getSignal().setAssign(seq.getIdleState(), hdl_page_base_addr.getSignal());
		reader.len.getSignal().setAssign(seq.getIdleState(), Utils.value(PAGE_MEM_LENGTH, 32));
		reader.req.getSignal().setAssign(seq.getIdleState(), read_kick);
		read_state_busy.setAssign(seq.getIdleState(), read_kick);

	}
	
	public static void main(String... args){
		SimpleAXIMemIface32RTL m = new SimpleAXIMemIface32RTL();
		HDLUtils.genHDLSequencerDump(m);
		HDLUtils.genResourceUsageTable(m);
		HDLUtils.generate(m, HDLUtils.VHDL);
		HDLUtils.generate(m, HDLUtils.Verilog);
	}

}
