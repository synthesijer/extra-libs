package synthesijer.lib.wishbone;

import synthesijer.rt.*;

public class OpenCoresI2CMasterIface{

    private final OpenCoresI2CMasterWrapper obj = new OpenCoresI2CMasterWrapper();

    private void reg_write(int addr, byte value){
	obj.wb_adr_i = addr;
	obj.wb_dat_i = value;
	obj.wb_cyc_i = true;
	obj.wb_stb_i = true;
	obj.wb_we_i = true;
	obj.wb_we_i = false;
	obj.wb_stb_i = false;
	obj.wb_cyc_i = false;
    }

    private int dummy = 0;
    private int reg_read(int addr){
	obj.wb_adr_i = addr;
	obj.wb_cyc_i = true;
	obj.wb_stb_i = true;
	dummy = 0; // dummy
	dummy = 0; // dummy
	dummy = (int)obj.wb_dat_o;
	obj.wb_stb_i = false;
	obj.wb_cyc_i = false;
	return dummy;
    }

    /*
      7,   W, STA, generate (repeated) start condition
      6,   W, STO, generate stop condition
      5,   W, RD, read from slave
      4,   W, WR, write to slave
      3,   W, ACK, when a receiver, sent ACK (ACK='0') or NACK (ACK='1')
      2:1, W, Reserved
      0,   W, IACK, Interrupt acknowledge. When set, clears a pending interrupt.
    */

    public void init(int prescale){
	reg_write(0, (byte)(prescale & 0x000000FF));
	reg_write(1, (byte)((prescale>>8) & 0x000000FF));
	reg_write(2, (byte)0x80); // CTR(7) <= '1'
    }

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

}
