CORE_FILES = `ls src/cpmobile/core/*.java`
TEST_FILES = `ls src/cpmobile/test/*.java`
BIN_DIR = bin
CLASSPATH = bin/:src/:/usr/share/java/junit4.jar

all:
	javac -d $(BIN_DIR) $(CORE_FILES);
	javac -cp $(CLASSPATH) -d $(BIN_DIR) $(TEST_FILES);

clean:
	rm -rf $(BIN_DIR)/*;
