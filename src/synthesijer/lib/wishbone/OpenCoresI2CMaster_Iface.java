package synthesijer.lib.wishbone;

import synthesijer.rt.*;

public class OpenCoresI2CMaster_Iface{

    private final OpenCoresI2CMaster_Wrapper iface = new OpenCoresI2CMaster_Wrapper();

    private void reg_write(int addr, byte value){
	iface.wb_adr_i = addr;
	iface.wb_dat_i = value;
	iface.wb_cyc_i = true;
	iface.wb_stb_i = true;
	iface.wb_we_i = true;
	iface.wb_we_i = false;
	iface.wb_stb_i = false;
	iface.wb_cyc_i = false;
    }

    private int dummy = 0;
    private int reg_read(int addr){
	iface.wb_adr_i = addr;
	iface.wb_cyc_i = true;
	iface.wb_stb_i = true;
	dummy = 0; // dummy
	dummy = 0; // dummy
	dummy = (int)iface.wb_dat_o;
	iface.wb_stb_i = false;
	iface.wb_cyc_i = false;
	return dummy;
    }

    //private int SLAVE_WR_ADDR = 0x72;
    //private int SLAVE_RD_ADDR = 0x73;

    /*
      7,   W, STA, generate (repeated) start condition
      6,   W, STO, generate stop condition
      5,   W, RD, read from slave
      4,   W, WR, write to slave
      3,   W, ACK, when a receiver, sent ACK (ACK='0') or NACK (ACK='1')
      2:1, W, Reserved
      0,   W, IACK, Interrupt acknowledge. When set, clears a pending interrupt.
    */

    public void i2c_write(int slave_addr, int addr, byte value){
	// write slave address
	reg_write(3, (byte)((slave_addr<<1)+0));
	reg_write(4, (byte)0x90); // STA | WR
	while(true){ // wait for RxAck
	    int stat = reg_read(4);
	    if((stat & 0x82) == 0) break;
	}
	// write address to write
	reg_write(3, (byte)addr);
	reg_write(4, (byte)0x10); // WR
	while(true){ // wait for RxAck
	    int stat = reg_read(4);
	    if((stat & 0x82) == 0) break;
	}
	// write value
	reg_write(3, value);
	reg_write(4, (byte)0x50); // STO | WR
	while(true){ // wait for RxAck
	    int stat = reg_read(4);
	    if((stat & 0x82) == 0) break;
	}
    }

    public int i2c_read(int slave_addr, int addr){
	// slave address
	reg_write(3, (byte)((slave_addr<<1)+0));
	reg_write(4, (byte)0x90); // STA | WR
	while(true){ // wait for RxAck
	    int stat = reg_read(4);
	    if((stat & 0x82) == 0) break;
	}
	// write address to read
	reg_write(3, (byte)addr); // TXR: 0x39 & '1'
	reg_write(4, (byte)0x10); // WR
	while(true){ // wait for RxAck
	    int stat = reg_read(4);
	    if((stat & 0x82) == 0) break;
	}
	// slave address (read mode)
	reg_write(3, (byte)((slave_addr<<1)+1)); // TXR: 0x39 & '1'
	reg_write(4, (byte)0x90); // STA | WR
	while(true){ // wait for TIP
	    int stat = reg_read(4);
	    if((stat & 0x2) == 0) break;
	}
	reg_write(4, (byte)0x68); // RD, NACK, STO
	while(true){ // wait for ACK
	    int stat = reg_read(4);
	    if((stat & 0x2) == 0) break;
	}
	return reg_read(3);
    }

    public int debug = 0;

    public void test(){
	for(int debug = 0; debug < 256; debug++){;} // warm up

	reg_write(0, (byte)0x83); // 66MHz / (5 * 100KHz) - 1 = 131 = 0x83
	reg_write(1, (byte)0x00); // 66MHz / (5 * 100KHz) - 1 = 131 = 0x83
	reg_write(2, (byte)0x80); // CTR(7) <= '1'

	i2c_write(0x39, 0x80, (byte)1);
	debug = i2c_read(0x39, 0x92);
	

	while(true){;}
    }

}
