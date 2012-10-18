package mikera.util;

import java.util.ArrayList;
import java.util.Iterator;

public class PrefixTree<K,V> {
	private K head;
	private V value;
	private ArrayList<PrefixTree<K,V>> tails;
	
	// TODO
	
	public PrefixTree() {
		
	}
	
	public class KeyIterator implements Iterator<K[]> {
		private ArrayList<Integer> indexes=new ArrayList<Integer>();
		
		private KeyIterator() {
			
		}
		
		public boolean hasNext() {
			return indexes.size()>0;
		}
		

		public K[] next() {
			// TODO Auto-generated method stub
			return null;
		}

		public void remove() {
			throw new Error("Not supported");
		}
		
	}
	
	public int countNodes() {
		int result=(head==null)?0:1;
		if (tails==null) return result;
		for (PrefixTree<K, V> pt:tails) {
			result+=pt.countNodes();
		}
		return result;
	}
	
	public int countValues() {
		int result=(value==null)?0:1;
		if (tails==null) return result;
		for (PrefixTree<K, V> pt:tails) {
			result+=pt.countValues();
		}
		return result;
	}
	
	public int countLeaves() {
		if ((tails==null)||tails.isEmpty()) return 1;
		int result=0;
		for (PrefixTree<K, V> pt:tails) {
			result+=pt.countLeaves();
		}
		return result;
	}
	
	public int getMaxDepth() {
		// TODO: check logic
		int result=0;
		if (tails!=null) for (PrefixTree<K, V> pt:tails) {
			int pd=pt.getMaxDepth();
			if (pd>result) result=pd;
		}
		return result+1;		
	}
	
	public void add(K[] ts) {
		add(ts,0,ts.length,null);
	}
	
	public void add(K[] ts, V newValue) {
		add(ts,0,ts.length,newValue);
	}
	
	public void add(K[] ts, int offset, int length, V newValue) {
		if (length<=0) return;
		K item=ts[offset];
		PrefixTree<K,V> branch=getBranch(item);
		if (branch==null) {
			branch=addBranch(item);
		}
		if (length==1) {
			branch.value=newValue;
		} else {
			branch.add(ts, offset+1, length-1,newValue);
		}
	}
	
	public PrefixTree<K,V> getBranch(K a) {
		if (tails==null) return null;
		for (PrefixTree<K,V> t:tails) {
			if ((a!=null)&&a.equals(t.head)) {
				return t;
			} else if ((a==null)&&(t.head==null)) {
				return t;
			}
		}
		return null;
	}
	
	public boolean hasBranch(K a) {
		return getBranch(a)!=null;
	}
	
	private void ensureTails() {
		if (tails==null) {
			tails=new ArrayList<PrefixTree<K, V>>();
		}
	}
	
	private PrefixTree<K,V> addBranch(K a) {
		PrefixTree<K,V> t=new PrefixTree<K, V>();
		t.head=a;
		ensureTails();
		tails.add(t);
		return t;
	}
}
