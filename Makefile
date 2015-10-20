SRC = ./bin/synthesijer/lib/axi/AXI_Reader.class \
      ./bin/synthesijer/lib/axi/AXI_Reader_Buffer.class \
      ./bin/synthesijer/lib/axi/AXI_Writer.class \
      ./bin/synthesijer/lib/axi/AXI_Writer_Buffer.class \
      ./bin/synthesijer/lib/axi/AXILiteSlave32RTL.class \
      ./bin/synthesijer/lib/axi/AXIMemIface32RTL.class \
      ./bin/synthesijer/lib/axi/AXIMemIface32RTL_Sim.class \
      ./bin/synthesijer/lib/axi/SimpleAXIMemIface32RTL.class \
      ./bin/synthesijer/lib/axi/AXIMemIface128RTL_BRAM_Ext_Ctrl.class \
      ./bin/synthesijer/lib/axi/AXIMemIface32RTL_BRAM_Ext_Ctrl.class \
      ./bin/synthesijer/lib/axi/AXIMemIface512RTL_BRAM_Ext_Ctrl.class \
      ./bin/synthesijer/lib/upl/UPLInputPort.class \
      ./bin/synthesijer/lib/upl/UPLOutputPort.class \
      ./bin/synthesijer/lib/upl/UPLPort.class \
      ./bin/synthesijer/lib/upl/UPLPortSim.class \
      ./bin/synthesijer/utils/ExternalMemory32.class 

SYNTHESIJER ?= ../synthesijer/bin

all: $(SRC)
	ant -f build.xml
	for i in $(subst /,.,$(subst bin/,,$(basename $^))) ;\
	do \
		java -cp $(SYNTHESIJER):bin $$i ; \
	done
	mkdir -p hdl/vhdl/
	mv *.vhd hdl/vhdl/
	cp hdl_lib/vhdl/*.vhd hdl/vhdl/
	mkdir -p hdl/verilog/
	mv *.v hdl/verilog/
	cp hdl_lib/verilog/*.v hdl/verilog/
	-rm -f *.dot
	-rm -f *.html

clean:
	-rm -f *.vhd
	-rm -f *.v
	-rm -f *.txt
	-rm -f *.html
	-rm -f *.dot
	-rm -f *.o
	-rm -f a.out
	-rm -rf hdl
	-rm -f work-obj93.cf
