package cpmobile.test;
import cpmobile.core.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AllTests {
	CPManager manager;

	@Before
	public void testSetup() {
	}

	@After
	public void testComplete() {
	}

	@Test
	public void testCreate() {
		assertNotNull(CPManager.createDB("Estacoes.txt", "Database.dat"));
	}

	@Test
	public void testLoad() {

	}

	@Test
	public void testSimpleQuery() {

	}

	@Test
	public void testComplexQuery() {

	}
}