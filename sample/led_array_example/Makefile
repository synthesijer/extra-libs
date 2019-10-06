SYNTHESIJER?=$(lastword $(sort $(wildcard $(HOME)/synthesijer/synthesijer*.jar)) $(wildcard $(HOME)/synthesijer/synthesijer.jar) $(wildcard ../../../target/synthesijer))

SYNTHESIJER_LIB?=$(lastword $(sort $(wildcard $(HOME)/synthesijer/synthesijer_lib_????????)) $(wildcard $(HOME)/synthesijer/lib) $(wildcard ../../../../synthesijer/lib))

SYNTHESIJER_EXTRA_LIB?=$(lastword $(sort $(wildcard $(HOME)/synthesijer/synthesijer_extra-libs_????????)) $(wildcard $(HOME)/synthesijer/extra-libs) $(wildcard ../../../../synthesijer/extra-libs))

CLASSPATH=$(SYNTHESIJER):$(SYNTHESIJER_EXTRA_LIB)/bin:.

LIB_SJR_VERILOG=led_array.v

SOURCES=LEDArrayExample.java $(SYNTHESIJER_EXTRA_LIB)/src/synthesijer/lib/led/*.java


all: $(SOURCES) $(LIB_SJR_VERILOG)
	java -cp $(CLASSPATH) synthesijer.Main --chaining --operation-strength-reduction --verilog $(SOURCES)

$(LIB_SJR_VERILOG):
	cp $(SYNTHESIJER_EXTRA_LIB)/hdl_lib/verilog/$@ ./

clean:
	-rm -f *.class
	-rm -f *.v
	-rm -f *.vhd
	-rm -f *.xml
	-rm -f *.dot
	-rm -f *.o
	-rm -f sim
	-rm -f *.vcd
	-rm -f $(filter-out readme.txt, $(wildcard *.txt))
	-rm -f *.ir
