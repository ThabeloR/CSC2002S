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
	
CLASSES=MeanFilterSerial.class MeanFilterParallel.class MedianFilterSerial.class MedianFilterParallel.class

CLASS_FILES=$(CLASSES:%.class=$(BINDIR)/%.class)
	

default: $(CLASS_FILES)
	
run: $(CLASS_FILES)	$(JAVA) -cp $(BINDIR) imageFilter
	
clean:
	rm $(BINDIR)/*.class
	
Filter_run:
	java -cp bin imageFilter
	 