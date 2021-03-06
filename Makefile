CORE_FILES = `find src/cpmobile/core/ -name '*.java'`
TEST_FILES = `find src/cpmobile/test/ -name '*.java'`
BIN_DIR = bin
DATA_DIR = data
CLASSPATH = bin/:src/:lib/junit-4.11.jar


all: build test

build:
	mkdir -p bin;
	javac -Xlint -cp $(CLASSPATH) -d $(BIN_DIR) $(TEST_FILES) $(CORE_FILES);

clean:
	rm -rf $(BIN_DIR)/*;
	rm -f $(DATA_DIR)/db.dat;

test:
	clear;
	java -cp $(CLASSPATH) cpmobile.test.RunTest;

