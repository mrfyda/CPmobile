package cpmobile.test;
import java.util.List;

import cpmobile.core.*;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AllTests {
	CPManager manager;
	long before;

	@Before
	public void testSetup() {
		before = System.currentTimeMillis();
	}

	@After
	public void testComplete() {
		System.out.println("Executed in: " + (System.currentTimeMillis() - before) + " ms");
	}

	@Test
	public void testCreate() {
		System.out.println("TestCreate");
		assertNotNull(manager = CPManager.createDB("data/estacoes.txt", "data/database.dat"));
	}

	@Test
	public void testLoad() {
		System.out.println("TestLoad");
		assertNotNull(CPManager.loadDB("data/database.dat"));
	}

	@Test
	public void testSimpleQuery() {
		System.out.println("TestSimpleQuery");
		manager = CPManager.loadDB("data/database.dat");
		List<List<String>> lst = manager.query("Entroncamento", "Santarem", "R", 0, "");
		
		assertNotNull(lst);
		assertEquals(20, lst.size());
	}

	@Test
	public void testComplexQuery() {
		System.out.println("TestComplexQuery");

	}
}
