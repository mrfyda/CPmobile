package cpmobile.test;
import java.util.List;

import cpmobile.core.*;
import cpmobile.core.web.StationsRetriever;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AllTests {
	static final String FILE = "data/db.dat";
	static final String STATION = "data/estacoes.txt";
	static CPManager manager;
	long before;
	
	@BeforeClass
	public static void init() {
		if ((manager = CPManager.loadDB(FILE)) == null)
			manager = CPManager.createDB(STATION, FILE);
	}

	@Before
	public void testSetup() {		
		before = System.currentTimeMillis();
	}

	@After
	public void testComplete() {
		System.out.println("Executed in: " + (System.currentTimeMillis() - before) + " ms");
	}
	
	/*@Test
	public void testCreateDB() {
		final String file = "data/database.dat";
		final String station = "data/estacoes2.txt";
		
		assertNotNull(CPManager.createDB(station, file));
	}*/
	
	/*@Test
	public void testLoadDB() {
		final String file = "data/database.dat";
		
		assertNotNull(CPManager.loadDB(file));
	}*/

	@Test
	public void testSimpleQuery() {
		System.out.println("TestSimpleQuery");
		List<List<String>> lst = manager.query("Entroncamento", "Santarem", "R", 0, "");
		
		assertNotNull(lst);
		assertEquals(20, lst.size());
	}
	
	@Test
	public void TestWebService() {
		System.out.println("TestWebService");
		StationsRetriever sr = new StationsRetriever();
		List<Station> stations = sr.execute();
		
		assertNotNull(stations);
		assertEquals("abrantes", stations.get(0).getName());
		assertEquals("zibreira", stations.get(stations.size()-1).getName());
	}

	@Test
	public void testComplexQuery() {
		System.out.println("TestComplexQuery");

	}
}
