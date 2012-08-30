package mikera.persistent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import mikera.data.CommonTests;
import mikera.persistent.ListFactory;
import mikera.persistent.LongMap;
import mikera.persistent.MapFactory;
import mikera.persistent.PersistentHashMap;
import mikera.persistent.PersistentList;
import mikera.persistent.PersistentMap;
import mikera.persistent.PersistentSet;
import mikera.persistent.SetFactory;
import mikera.util.Rand;
import mikera.util.Tools;
import org.junit.Test;

public class TestPersistentLongMap {

	@Test public void testMaps() {
		PersistentMap<Long,String> pm=LongMap.create();
		testMap(pm);
		testMap(addRandomMaps(pm));
		
		@SuppressWarnings("unchecked")
		LongMap<String> im=(LongMap<String>) LongMap.EMPTY;
		testMap(im);
		testMap(addRandomMaps(im));
		
	}
	
	private PersistentMap<Long, String> addRandomMaps(PersistentMap<Long,String> lm) {
		for (int i=0; i<Rand.d(50); i++) {
			lm=lm.include((long)Rand.r(50),Rand.nextString());
		}
		return lm;
	}

	@Test public void testConvert() {
		PersistentMap<Long,String> phm=PersistentHashMap.create();

		HashMap<Long,String> hm=new HashMap<Long,String>();
		for (int i=0; i<10; i++) {
			long key=Rand.r(100);
			String value=Rand.nextString();
			hm.put(key, value);
			phm=phm.include(key,value);
			
			long delKey=Rand.r(100);
			hm.remove(delKey);
			phm=phm.delete(delKey);
		}
		testMap(phm);
		
		PersistentMap<Long,String> pm=MapFactory.create(hm);
		testMap(pm);
		
		HashMap<Long,String> hm2=pm.toHashMap();
		assertEquals(hm,hm2);
		
		PersistentSet<Long> ks=SetFactory.createFrom(hm.keySet());
		PersistentSet<Long> ks2=pm.keySet();
		PersistentSet<Long> ks3=phm.keySet();
		assertEquals(ks,ks2);
		assertEquals(ks,ks3);
		
		PersistentList<String> vs=ListFactory.createFromCollection(hm.values());
		PersistentList<String> vs2=ListFactory.createFromCollection(pm.values());
		PersistentList<String> vs3=ListFactory.createFromCollection(phm.values());
		assertEquals(SetFactory.createFrom(vs),SetFactory.createFrom(vs2));
		assertEquals(SetFactory.createFrom(vs),SetFactory.createFrom(vs3));
	}
	
	@Test public void testMerge() {
		PersistentMap<Long,String> pm=PersistentHashMap.create();
		pm=pm.include(1L, "Hello");
		pm=pm.include(2L, "World");
		
		PersistentMap<Long,String> pm2=PersistentHashMap.create();
		pm2=pm2.include(2L, "My");
		pm2=pm2.include(3L, "Good");
		pm2=pm2.include(4L, "Friend");

		PersistentMap<Long,String> mm=pm.include(pm2);
		assertEquals(4,mm.size());
	}
	
	@Test public void testToString() {
		HashMap<Long,String> hm=new HashMap<Long, String>();
		hm.put(1L, "Hello");
		hm.put(2L, "World");
		
		PersistentMap<Long,String> pm=PersistentHashMap.create(1L,"Hello");
		pm=pm.include(2L,"World");
		assertEquals(PersistentHashMap.create(hm).toString(),pm.toString());
		assertEquals("{1=Hello, 2=World}",pm.toString());
	}
	
	@Test public void testChanges() {
		PersistentMap<Long,String> pm=LongMap.create();
		pm=pm.include(1L, "Hello");
		pm=pm.include(2L, "World");
		
		assertEquals(null,pm.get(3L));
		assertEquals(2,pm.size());
		assertEquals("Hello",pm.get(1L));
		assertEquals("World",pm.get(2L));
		
		pm.validate();
		pm=pm.include(2L, "Sonia");
		pm.validate();
		assertEquals("Hello",pm.get(1L));
		assertEquals("Sonia",pm.get(2L));
		assertEquals(2,pm.size());

		pm=pm.delete(1L);
		assertEquals(1,pm.size());		
		assertEquals(null,pm.get(1L));
		assertEquals("Sonia",pm.get(2L));
		
		assertTrue(pm.values().contains("Sonia"));
		assertTrue(pm.keySet().contains(2L));
		
		testMap(pm);
	}
	
	public void testMap(PersistentMap<Long,String> pm) {
		pm.validate();
		testIterator(pm);
		testRandomAdds(pm);
		testNullAdds(pm);
		testEquals(pm);
		CommonTests.testCommonData(pm);
	}
	
	public void testIterator(PersistentMap<Long,String> pm) {
		int i=0;
		for (Map.Entry<Long,String> ent: pm.entrySet()) {
			assertTrue(pm.containsKey(ent.getKey()));
			assertTrue(Tools.equalsWithNulls(ent.getValue(), pm.get(ent.getKey())));
			i++;
		}
		assertEquals(pm.size(),i);
	}
	
	public void testRandomAdds(PersistentMap<Long,String> pm) {
		pm=addRandomStuff(pm,100,1000000);
		int size=pm.size();
		assertTrue(size>90);
		assertEquals(size,pm.entrySet().size());
		assertEquals(size,pm.keySet().size());
		assertEquals(size,pm.values().size());	
	}
	
	public void testNullAdds(PersistentMap<Long,String> pm) {
		pm=pm.include(2L,null);	
		assertTrue(pm.containsKey(2L));
		assertEquals(null,pm.get(2L));	
	}
	
	public void testEquals(PersistentMap<Long,String> pm) {
		PersistentMap<Long,String> pm2=pm.include(2L,new String("Hello"));
		PersistentMap<Long,String> pm3=pm.include(2L,new String("Hello"));
		assertEquals(pm2,pm3);;	
	}


	
	public PersistentMap<Long,String> addRandomStuff(PersistentMap<Long,String> pm, int n , int maxIndex ) {
		for (int i=0; i<n; i++) {
			pm=pm.include((long)Rand.r(maxIndex),Rand.nextString());
		}
		return pm;
	}


}