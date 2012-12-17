CORE_FILES = `ls src/cpmobile/core/*.java`
TEST_FILES = `ls src/cpmobile/test/*.java`
BIN_DIR = bin

all:
	javac -d $(BIN_DIR) $(CORE_FILES);
	javac -d $(BIN_DIR) $(TEST_FILES);

clean:
	rm -rf $(BIN_DIR)/*;
