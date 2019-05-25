EXES=main
VCDS=$(wildcard *.vcd)

all: $(EXES)

run: $(EXES)
	vvp $(EXES)

$(EXES): testbench.v ../*.v
	iverilog -g2012 -o $(EXES) testbench.v ../*.v

clean:
	rm -f $(EXES) $(VCDS)
