CORE_FILES = `find src/cpmobile/core/ -name '*.java'`
TEST_FILES = `find src/cpmobile/test/ -name '*.java'`
BIN_DIR = bin
DATA_DIR = data
CLASSPATH = bin/:src/:lib/junit-4.11.jar:lib/commons-logging-1.1.1.jar:lib/httpclient-4.2.2.jar:lib/httpcore-4.2.2.jar

all:
	mkdir -p bin;
	javac -Xlint:-path -cp $(CLASSPATH) -d $(BIN_DIR) $(TEST_FILES) $(CORE_FILES);

clean:
	rm -rf $(BIN_DIR)/*;
	rm -f $(DATA_DIR)/db.dat;
