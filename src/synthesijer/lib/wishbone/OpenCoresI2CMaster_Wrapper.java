package synthesijer.lib.wishbone;
    
import synthesijer.hdl.*;
import synthesijer.hdl.expr.*;
import java.util.*;

public class OpenCoresI2CMaster_Wrapper extends HDLModule{

    public int wb_adr_i;
    public byte wb_dat_i;
    public byte wb_dat_o;
    public boolean wb_we_i;
    public boolean wb_stb_i;
    public boolean wb_cyc_i;
    public boolean wb_ack_i;
    public boolean wb_inta_i;

    public OpenCoresI2CMaster_Wrapper(String... args){

	super("i2c_master_top", "wb_clk_i", "wb_rst_i");

	newParameter("ARST_LVL", HDLPrimitiveType.genBitType(), new HDLValue(false));

	// wishbone signals
	newPort("wb_adr_i",  HDLPort.DIR.IN,  HDLPrimitiveType.genVectorType(3));
	newPort("arst_i", HDLPort.DIR.IN, HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.EXPORT));
	newPort("wb_dat_i",  HDLPort.DIR.IN,  HDLPrimitiveType.genVectorType(8));
	newPort("wb_dat_o",  HDLPort.DIR.OUT, HDLPrimitiveType.genVectorType(8));
	newPort("wb_we_i",   HDLPort.DIR.IN,  HDLPrimitiveType.genBitType());
	newPort("wb_stb_i",  HDLPort.DIR.IN,  HDLPrimitiveType.genBitType());
	newPort("wb_cyc_i",  HDLPort.DIR.IN,  HDLPrimitiveType.genBitType());
	newPort("wb_ack_o",  HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
	newPort("wb_inta_o", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());

	// i2c lines
	newPort("scl_pad_i",    HDLPort.DIR.IN,  HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.EXPORT));
	newPort("scl_pad_o",    HDLPort.DIR.OUT, HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.EXPORT));
	newPort("scl_padoen_o", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.EXPORT));
	newPort("sda_pad_i",    HDLPort.DIR.IN,  HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.EXPORT));
	newPort("sda_pad_o",    HDLPort.DIR.OUT, HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.EXPORT));
	newPort("sda_padoen_o", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.EXPORT));
    }

}
