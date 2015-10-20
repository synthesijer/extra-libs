
import synthesijer.rt.*;
import synthesijer.lib.wishbone.*;

/**
 * This is an example how to use OpenCoresI2CMasterIface.
 * This example accesses APDS9960, gesture sensor from AkizukiDenshi
 * http://akizukidenshi.com/catalog/g/gK-09754/
 * This source code is based on the example which is available on AkizukiDenshi
 */
public class OpenCoresI2CMasterIface_Test{

    private final OpenCoresI2CMasterSimpleIface iface = new OpenCoresI2CMasterSimpleIface();
    private final int SLAVE = 0x39;

    public int debug = 0;

    private int DATA_U,DATA_D,DATA_L,DATA_R;
    private int OLD_U,OLD_D,OLD_L,OLD_R;
    private int work;
    private boolean U_PEAK_END_FLAG,D_PEAK_END_FLAG,L_PEAK_END_FLAG,R_PEAK_END_FLAG;
    private int STATUS_UD,STATUS_LR;
    private int OLD_STATUS_UD,OLD_STATUS_LR;
    private boolean DISP_FLAG;
    private int NOISE_LEVEL = 2;
    private int PHASE_COUNTER;

    public int U_PEAK,D_PEAK,L_PEAK,R_PEAK;
    public boolean DECIDE_FLAG;
    public boolean DETECT_L2R, DETECT_R2L, DETECT_U2D, DETECT_D2U;

    @auto
    public void test(){
	for(int debug = 0; debug < 256; debug++){;} // warm up

	iface.init(131);  // 66MHz / (5 * 100KHz) - 1 = 131 = 0x83

	debug = iface.i2c_read(SLAVE, 0x92); // ID
	iface.i2c_write(SLAVE, 0x80, (byte)0B00000000); // POWER OFF
	iface.i2c_write(SLAVE, 0x80, (byte)0B01000101); // POWER ON, GESTURE ENABE, PROXIMITY DETECT ENABLE, AEN=0
	iface.i2c_write(SLAVE, 0x90, (byte)0B00110000); //Gesture LED Drive Strength 300%(max)	
	iface.i2c_write(SLAVE, 0xA3,(byte)0B01100100); //Reserve0, Gain x8(11), LED Drive 100mA(00), Wait Time see under number
	                                               //111=39.2mS 110=30.8mS 101=22.4mS 100=14.0mS 011=8.4mS 010=5.6mS 001=2.8ms 000=0mS
	iface.i2c_write(SLAVE, 0xA4, (byte)70);        //U MINUS OFFSET
	iface.i2c_write(SLAVE, 0xA5, (byte)0);         //D MINUS OFFSET
	iface.i2c_write(SLAVE, 0xA7, (byte)10);        //L MINUS OFFSET
	iface.i2c_write(SLAVE, 0xA9, (byte)34);        //R MINUS OFFSET
	iface.i2c_write(SLAVE, 0xAB, (byte)0B00000001); //GIEN off<1>(INTERRUPT DISABLE), GMODE ON<0>
	
	RESET_VARIABLE();

	while(true){
	    loop();
	}
    }

    private void RESET_VARIABLE(){
	PHASE_COUNTER = 0;
	U_PEAK=0;
	D_PEAK=0;
	L_PEAK=0;
	R_PEAK=0;
	OLD_U = 0;
	OLD_D = 0;
	OLD_L = 0;
	OLD_R = 0;
	U_PEAK_END_FLAG = false;
	D_PEAK_END_FLAG = false;
	L_PEAK_END_FLAG = false;
	R_PEAK_END_FLAG = false;
	STATUS_UD = 0;
	STATUS_LR = 0;
	OLD_STATUS_UD = 0;
	OLD_STATUS_LR = 0;
	DISP_FLAG = false;
	DECIDE_FLAG = false;
    }

    private void loop(){

	work = iface.i2c_read(SLAVE, 0xAE);  //READ GESTUR FIFO LEVEL REGISTER
	if(work != 0)           //IF FIFO HAS SOME DATA
	    {
		DATA_U = iface.i2c_read(SLAVE, 0xFC);
		DATA_D = iface.i2c_read(SLAVE, 0xFD);
		DATA_L = iface.i2c_read(SLAVE, 0xFE);
		DATA_R = iface.i2c_read(SLAVE, 0xFF);
		if((DATA_U > NOISE_LEVEL) && (DATA_D > NOISE_LEVEL)&& (DATA_L> NOISE_LEVEL) && (DATA_R > NOISE_LEVEL)) //NOISE CANCEL
		    {
			DATA_SYORI();       // 
			PHASE_COUNTER++;    //
			DISP_FLAG = true;      //
		    }
		else 
		    {
			if(DISP_FLAG)
			    {
				DISP_FLAG = false;
				//DISP_DIR();
			    }
			RESET_VARIABLE();
		    }
	    }
    }

    private void DATA_SYORI(){
	if (DATA_U > OLD_U)                //IF NEW_DATA > OLD_DATA_BUFFER(APROACH TO PEAK)
	    {
		OLD_U = DATA_U;                  //SAVE NEW_DATA TO OLD_DATA_BUFFER
		U_PEAK = PHASE_COUNTER;          //PEAK_PHASE RENEWAL
		U_PEAK_END_FLAG = false;             //STILL PEAK or APROACH TO PEAK
	    }
	else
	    {
		U_PEAK_END_FLAG = true;             //PEAK WAS GONE
	    }
	//**************************
	if (DATA_D > OLD_D)
	    {
		OLD_D = DATA_D;
		D_PEAK = PHASE_COUNTER;
		D_PEAK_END_FLAG = false;
	    }
	else
	    {
		D_PEAK_END_FLAG = true;
	    }
	//**************************
	if (DATA_L > OLD_L)
	    {
		OLD_L = DATA_L;
		L_PEAK = PHASE_COUNTER;
		L_PEAK_END_FLAG = false;
	    }
	else
	    {
		L_PEAK_END_FLAG = true;
	    }
	//*************************
	if (DATA_R > OLD_R)
	    {
		OLD_R = DATA_R;
		R_PEAK = PHASE_COUNTER;
		R_PEAK_END_FLAG = false;
	    }
	else
	    {
		R_PEAK_END_FLAG = true;
	    }
	//**************************
	if(U_PEAK_END_FLAG && D_PEAK_END_FLAG && L_PEAK_END_FLAG && R_PEAK_END_FLAG) //IF ALL PEAK WAS GONE
	    {
		DECIDE_FLAG = false;
		DETECT_U2D = false;
		DETECT_D2U = false;
		DETECT_L2R = false;
		DETECT_R2L = false;
		if ((U_PEAK > D_PEAK) & (U_PEAK >= L_PEAK) & (U_PEAK >= R_PEAK))           //U_PEAK WAS LAST
		    {
			//SERIAL_STRING = "DOWN";
			DECIDE_FLAG = true;
			DETECT_U2D = true;
		    }
		if ((D_PEAK > U_PEAK) & (D_PEAK >= L_PEAK) & (D_PEAK >= R_PEAK))           //D_PEAK WAS LAST
		    {
			//SERIAL_STRING = "UP";
			DECIDE_FLAG = true;
			DETECT_D2U = true;
		    }
		if ((L_PEAK >= U_PEAK) & (L_PEAK >= D_PEAK) & (L_PEAK > R_PEAK))           //L_PEAK WAS LAST
		    {
			//SERIAL_STRING = "RIGHT";
			DECIDE_FLAG =true;
			DETECT_L2R = true;
		    }
		if ((R_PEAK >= U_PEAK) & (R_PEAK >= D_PEAK) & (R_PEAK > L_PEAK))           //R_PEAK WAS LAST
		    {
			//SERIAL_STRING = "LEFT";
			DECIDE_FLAG = true;
			DETECT_R2L = true;
		    }
		//if (!DECIDE_FLAG)SERIAL_STRING = "NONE";                                   //CAN'T DECIDE
	    }
    }

}
