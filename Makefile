# Parallel Programming with the Java Fork/Join framework
# Thabelo Tshikalange
# 07 August 2022

.SUFFIXES: .java .class
SRCDIR=src
BINDIR=bin
JAVAC=/usr/bin/javac
JAVA=usr/bin/java

$(BINDIR)/%.class: $(SRCDIR)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR) $<
	
CLASSES=MeanFilterSerial.class\
	MeanFilterParallel.class\
	MedianFilterSerial.class\
	MedianFilterParallel.class

CLASS_FILES=$(CLASSES:%.class=$(BINDIR)/%.class)
	

default: $(CLASS_FILES)
	
run: $(CLASS_FILES)	$(JAVA) -cp $(BINDIR) MeanFilterSerial MeanFilterParallel MedianFilterSerial MedianFilterParallel
	
clean:
	rm $(BINDIR)/*.class
	

runFilters: $(CLASS_FILES)
	java -cp bin MeanFilterSerial $(argument)
	java -cp bin MedianFilterSerial $(argument)
	java -cp bin MeanFilterParallel $(argument)
	java -cp bin MedianFilterParallel $(argument)
runMeanFilterSerial: $(CLASS_FILES)
	java -cp bin MeanFilterSerial $(argument)
runMedianFilterSerial: $(CLASS_FILES)
	java -cp bin MedianFilterSerial $(argument)
runMeanFilterParallel: $(CLASS_FILES)
	java -cp bin MeanFilterParallel $(argument)
runMedianFilterParallel: $(CLASS_FILES)
	java -cp bin MedianFilterParallel $(argument)
