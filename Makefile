CORE_FILES = `ls src/cpmobile/core/*.java`
TEST_FILES = `ls src/cpmobile/test/*.java`
BIN_DIR = bin
DATA_DIR = data
CLASSPATH = bin/:src/:lib/junit-4.11.jar/

all:
	mkdir -p bin;
	javac -Xlint -cp $(CLASSPATH) -d $(BIN_DIR) $(TEST_FILES) $(CORE_FILES);

clean:
	rm -rf $(BIN_DIR)/*;
	rm -f $(DATA_DIR)/db.dat;
>>>>>>> 0f92da60f9d34aa2296ef2771636549d24d6eb00
