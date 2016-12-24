package mikera.engine;

import java.util.Iterator;

import mikera.util.Rand;

/**
 * Data structure for 3D grid implemented as Treap
 * with z-order for cells. Performance for n nodes:
 *   O(log n) write
 *   O(log n) read
 *   O(n) equals
 * 
 * Nodes are z-order ranges of cells with non-null objects
 * 
 * Adjacent nodes automatically combine on write
 * 
 * @author Mike Anderson
 *
 * @param <T> type of object to store in each cell
 */
public final class Octreap<T> extends BaseGrid<T> {
	private static final int BITS=20;
	private static final int BITS_POWER2=1<<BITS;
	private static final int BITS_MASK=BITS_POWER2-1;
	private static final long FULL_MASK=(BITS_MASK)*(1L+((1L+(1L<<BITS))<<BITS));
	public static final long MIN_ZVALUE=0;
	public static final long MAX_ZVALUE=FULL_MASK;
	
	public static interface NodeVisitor {
		public Object visit(ZNode n);
	}
	
	public static final class ZNode implements Comparable<ZNode>, Cloneable {
		public long z1;
		public long z2;
		private ZNode left;
		private ZNode right;
		private Object object;
		private int priority;
		
		public ZNode() {
			priority=Rand.nextInt();
		}
		
		@Override
		public int compareTo(ZNode b) {
			long a=z1-b.z1;
			if (a>0) return 1;
			if (a<0) return -1;
			return 0;
		}
		
		public Object value() {
			return object;
		}
		
		@Override
		public Object clone() {
			try {
				ZNode zn=(ZNode)super.clone();
				if (zn.left!=null) zn.left=(ZNode)(zn.left.clone());
				if (zn.right!=null) zn.right=(ZNode)(zn.right.clone());
				return zn;
			} catch (Throwable t) {
				throw new Error(t);
			}
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof ZNode) {
				return equals((ZNode)o);
			}
			return false;
		}
		
		public boolean equals(ZNode a) {
			// note we ignore priority - not content relevant
			if (this==a) return true;
			if (!object.equals(a.object)) return false;
			if (z1!=a.z1) return false;
			if (z2!=a.z2) return false;
			return true;
		}
		
		@Override
		public int hashCode() {
			return object.hashCode()+(int)(z1*7+z2*1234567);
		}
		
		public void validate() {
			if (z1>z2) throw new Error();
			if (object==null) throw new Error();
			if (left!=null) left.validate();
			if (right!=null) right.validate();
		}
	}
	
	public ZNode head;
	
	@Override
	public Octreap<T> set(int x, int y, int z, T value) {
		long zz=calculateZ(x,y,z);
		setRange(zz,zz,value);
		return this;
	}
	
	public Octreap() {
		
	}
	
	public Octreap(Grid<T> o) {
		set(o);
	}
	
	@Override
	public Octreap<T> set(Grid<T> o) {
		return (Octreap<T>) clear().paste(o);
	}
	
	@Override
	public Octreap<T> clear() {
		head=null;
		return this;
	}
	
	@Override
	public Octreap<T> clearContents() {
		return clear();
	}	
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object b) {
		if (!(b instanceof Octreap)) return false;
		if (this==b) return true;
		Octreap<T> o=(Octreap<T>) b;
		ZNode an=this.getFirstNode();
		ZNode bn=o.getFirstNode();
		while((an!=null)||(bn!=null)) {
			if ((an==null)^(bn==null)) return false;
			if (!an.equals(bn)) return false;
			an=this.nextNode(an.z1);
			bn=o.nextNode(bn.z1);
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		return 0;
	}
	

	private class NodeIterator implements Iterator<ZNode> {
		private ZNode current=getFirstNode();
		
		@Override
		public boolean hasNext() {
			return (current!=null);
		}

		@Override
		public ZNode next() {
			ZNode result=current;
			if (result!=null) {
				ZNode next=nextNode(result.z1);
				current=next;
			}
			return result;
		}

		@Override
		public void remove() {
			throw new Error("Not supported");
		}
	}
	
	public ZNode nextNode(long zz) {
		ZNode next=null;
		ZNode ze=head;
		while (ze!=null) {
			if (ze.z1>zz) {
				if ((next==null)||(ze.z1<next.z1)) next=ze;
				ze=ze.left;
			} else {
				ze=ze.right;
			}
		}
		return next;
	}
	
	public Iterator<ZNode> getNodeIterator() {
		NodeIterator ni=new NodeIterator();
		return ni;
	}
	
	@Override
	public void visitBlocks(IBlockVisitor<T> bf) {
		visitBlocks(head,bf);
	}
	

	
	@SuppressWarnings("unchecked")
	private void visitBlocks(ZNode node, IBlockVisitor<T> bf) {
		if (node==null) return;
		visitBlocks(node.left,bf);
		
		long pos=node.z1;
		while (pos<=node.z2) {
			long size=blockSize(pos,node); // number of cells in next block
			
			long pos2=pos+size-1;
			bf.visit(extractX(pos), extractY(pos), extractZ(pos), extractX(pos2), extractY(pos2), extractZ(pos2), (T)node.object);
			pos+=size;
		}
		
		visitBlocks(node.right,bf);
	}
	
	public void visitCells(BlockVisitor<T> bf, int x1, int y1, int z1, int x2, int y2, int z2, int dx, int dy, int dz) {
		visitCells(bf,x1,y1,z1,x2,y2,z2,dx,dy,dz,head);
	}
	
	private void visitCells(BlockVisitor<T> bf, int x1, int y1, int z1, int x2, int y2, int z2, int dx, int dy, int dz, ZNode head) {
		// TODO
	}
	
	/*
	 * Gets the largest power of two sided block fitting in node and starting at pos
	 * look at trailing zeros
	 */
	protected static final long blockSize(long pos, ZNode node) {
		long size=1;
		while (((pos&size)==0)&&((pos+size*2-1)<=node.z2)) {
			size=size<<1;
		}		
		return size;
	}
	
	public final long blockSize(int x, int y, int z) {
		long zz=calculateZ(x,y,z);
		return blockSize(zz,getNode(zz));
	}
	
	protected static final long blockRoot(long pos, ZNode node) {
		long size=1;
		while (size<FULL_MASK) {
			if ((pos&size)>0) {
				if ((pos-size)<node.z1) return pos;
				pos=pos-size;
			} else {
				if ((pos+size)>node.z2) return pos;
			}
			size<<=1;
		}		
		return pos;
	}
	
	
	public void visitNodes(NodeVisitor nf) {
		visitNodes(head,nf);
	}
	
    private boolean checkEquals(T a, T b) {
    	if (a==b) return true;
    	if (a==null) return false;
    	
		return a.equals(b);
    }
	
	public void delete(Octreap<T> t) {
		NodeVisitor deleter=new NodeVisitor() {
			@Override
			public Object visit(ZNode n) {
				deleteRange(n.z1,n.z2);
				return null;
			}
		};
		t.visitNodes(deleter);
	}
	
	
	public void fillSpace(T value) {
		clear();
		setRange(0,FULL_MASK,value);
	}
	
	@Override
	public void changeAll(T value) {
		changeAll(head,value);
	}
	
	private boolean changeAll(ZNode head, T value) {
		if (head==null) return false;

		head.object=value;
		if (changeAll(head.left,value)) {
			tryMerge(head.z1);
		}
		if (changeAll(head.right,value)) {
			tryMerge(head.z2+1);
		}
		return true;
	}
	
	public void changeAll(T oldValue, T newValue) {
		if (oldValue.equals(newValue)) return;
		changeAll(head,oldValue,newValue);
	}
	
	private boolean changeAll(ZNode head, T oldValue, T newValue) {
		if (head==null) return false;

		boolean checkMerge=false;
		if (head.object.equals(oldValue)) {
			head.object=newValue;
			checkMerge=true;
		} else if (head.object.equals(newValue)) {
			// might merge so need to check
			checkMerge=true;
		}  
		
		if (changeAll(head.left,oldValue,newValue)&&checkMerge) {
			tryMerge(head.z1);
		}
		if (changeAll(head.right,oldValue,newValue)&&checkMerge) {
			tryMerge(head.z2+1);
		}
		return true;
	}
	
	public void floodFill(int x, int y, int z, T value) {
		T fromValue=get(x,y,z);
		if (checkEquals(value,fromValue)) return;
		floodFill(x,y,z,value,fromValue);
	}
	
	public void floodFill(int x, int y, int z, T value, T fromValue) {
		throw new Error("Not yet supported");
	}
	
	public Octreap<T> expand() {
		Octreap<T> o1=this.clone();
		o1.paste(this, -1, 0, 0);
		o1.paste(this, +1, 0, 0);
		
		Octreap<T> o2=o1.clone();
		o2.paste(o1, 0,-1, 0);
		o2.paste(o1, 0,+1, 0);

		Octreap<T> o3=o2.clone();
		o3.paste(o2, 0, 0,-1);
		o3.paste(o2, 0, 0,+1);
		
		return o3;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Octreap<T> clone() {
		try {
			Octreap<T> zn=(Octreap<T>)super.clone();
			if (zn.head!=null) zn.head=(ZNode)(zn.head.clone());
			return zn;
		} catch (Throwable t) {
			throw new Error(t);
		}
	}
	
	private void visitNodes(ZNode node, NodeVisitor nf) {
		if (node==null) return;
		visitNodes(node.left,nf);
		nf.visit(node);
		visitNodes(node.right,nf);
	}
	
	@Override
	public Octreap<T> setBlock(int x1, int y1, int z1, int x2, int y2, int z2, T value) {
		if (x1>(x2)) {int temp=x1; x1=x2; x2=temp;}
		if (y1>(y2)) {int temp=y1; y1=y2; y2=temp;}
		if (z1>(z2)) {int temp=z1; z1=z2; z2=temp;}
		setBlockLocal(x1,y1,z1,x2,y2,z2,value);
		return this;
	}
	
	private void setBlockLocal(int x1, int y1, int z1, int x2, int y2, int z2, T value) {
		long zz1=calculateZ(x1,y1,z1);
		long zz2=calculateZ(x2,y2,z2);
		long mask=FULL_MASK;
		setBlock(zz1,zz2,value,mask);
	}
	
	public void setBlock(long zz1, long zz2, T value, long mask) {
		if (zz1==zz2) {
			setRange(zz1,zz2,value);
			return;
		}
			
		// find next difference
		long hm=mask>>1;
		long hb=mask-hm;
		while ((zz1&hb)==(zz2&hb)) {
			// escape if at single point or adjacent points
			if (mask<=3) {
				setRange(zz1,zz2,value);
				return;
			}
			
			mask=hm;
			hm=mask>>1;
			hb=mask-hm;
		}
		
		// check if single block
		if (((zz1&mask)==0)&&((zz2&mask)==mask)) {
			setRange(zz1,zz2,value);
			return;
		}
		
		//if ((mask&(mask+1))!=0) throw new Error("Mask issue!!");
		long highbits=zz1&(~mask);
		//if (highbits!=(zz1&(~mask))) throw new Error("Diferent high bits issue!!");
		
		long dmask=fillBits3(hb>>3); // lower bits of dimension being split
		long omask=hm-dmask; // other bits
		setBlock(zz1,highbits|(zz1&hb)|dmask|(zz2&omask),value,hm);
		setBlock(highbits|(zz2&hb)|(zz1&omask),zz2,value,hm);
		
	}
	
	public static long fillBits3(long m) {
		m=m|(m>>3);
		m=m|(m>>6);
		m=m|(m>>12);
		m=m|(m>>24);
		m=m|(m>>48);
		return m;
	}

	
	@SuppressWarnings("unchecked")
	public final T get(long zz) {
		ZNode ze=getNode(zz);
		if (ze!=null) return (T)ze.object;
		return null;
	}
	
	public final ZNode getNode(long zz) {
		ZNode ze=head;
		while (ze!=null) {
			if (zz<ze.z1) {
				ze=ze.left;
				continue;
			}
			if (zz>ze.z2) {
				ze=ze.right;
				continue;
			}
			return ze;
		}
		return null;		
	}
	
	private final ZNode getFirstNode() {
		ZNode ze=head;
		while (ze!=null) {
			if (ze.left==null) return ze;
			ze=ze.left;
		}
		return null;			
	}
	
	@Override
	public final T get(int x, int y, int z) {
		long zz=calculateZ(x,y,z);
		return get(zz);
	}
	
	protected ZNode getParentNode(ZNode node) {
		long zz=node.z1;
		ZNode ze=head;
		if (ze==node) return null;
		while (ze!=null) {
			if (zz<ze.z1) {
				if (ze.left==node) return ze;
				ze=ze.left;
				continue;
			}
			if (zz>ze.z2) {
				if (ze.right==node) return ze;
				ze=ze.right;
				continue;
			}
			throw new Error("Node not found!");	
		}
		throw new Error("Node not found!");	
	}
	
	/**
	 * Checks for consistency of ZNode tree
	 */
	public boolean check() {
		return check(head);
	}

	private boolean check(ZNode node) {
		if (node==null) return true;
		if (node.z1>node.z2) throw new Error("Inverted node");
		if (node.object==null) throw new Error("Null object");
		
		if (node.left!=null) {
			if (node.priority<node.left.priority) throw new Error("Priority problem");
			if (node.z1<=node.left.z2) throw new Error("Bounds problem");
			if ((node.z1==node.left.z2+1)&&node.object.equals(node.left.object)) throw new Error("Unmerged blocks");
			if (!check(node.left)) return false;
		}
		
		if (node.right!=null) {
			if (node.priority<node.right.priority) throw new Error("Priority problem");
			if (node.z2>=node.right.z1) throw new Error("Bounds problem");
			if ((node.z2+1==node.right.z1)&&node.object.equals(node.right.object)) throw new Error("Unmerged blocks");
			if (!check(node.right)) return false;
		}
		
		return true;
	}
	
	/**
	 * Checks whether entire range is empty (null)
	 */
	public boolean isEmpty(long za, long zb) {
		return isEmpty(za, zb, head);
	}
	
	public boolean isEmpty(long za, long zb, ZNode node) {
		while (node!=null) {
			if ((za<node.z2)&&(zb>=node.z1)) return false;
			node=(zb<node.z1)?node.left:node.right;
		}
		return true;
	}
	
	public void setRange(long za, long zb, T value) {
		//if (za>zb) {
		//	throw new Error("Setrange inverted");
		//}
		
		// special null case
		if (value==null) {
			deleteRange(za,zb);
			return;
		}
		
		// try to do a quick change
		ZNode node=head;
		while (node!=null) {
			if (zb<node.z1) {
				node=node.left;
				continue;
			}
			if (za>node.z2) {
				node=node.right;
				continue;
			}
			// we have overlap and/or adjacency!
			
			// check for complete overwrite case		
			if ((za<=node.z1)&&(zb>=node.z2)) {
				boolean setLow=false;
				boolean setHigh=false;
				if (za<node.z1) setLow=true;
				if (zb>node.z2) setHigh=true;
				
				boolean matchValue=node.object.equals(value);
				if (!matchValue) node.object=value;
				
				if (setLow) {
					deleteRange(za,node.z1-1);
					node.z1=za; 
				}
				if (setHigh) {
					deleteRange(node.z2+1,zb);		
					node.z2=zb; 
				}
				if (setLow||(!matchValue)) tryMerge(za);
				if (setHigh||(!matchValue)) tryMerge(zb+1);
				return;
			}
			
			// check for extend case
			if (node.object.equals(value)) {
				boolean setLow=false;
				boolean setHigh=false;
				if (za<node.z1) setLow=true;
				if (zb>node.z2) setHigh=true;
				if (setLow) {
					deleteRange(za,node.z1-1);
					node.z1=za; 
				}
				if (setHigh) {
					deleteRange(node.z2+1,zb);		
					node.z2=zb; 
				}
				if (setLow) tryMerge(za);
				if (setHigh) tryMerge(zb+1);
				return;
			}
			
			// otherwise cut out
			deleteRange(za,zb);
			
			break;
		}
		
		// fall back to generic option - guaranteed that range is empty
		addRange(za,zb,value);
		tryMerge(za);
		tryMerge(zb+1);
	}
	
	/**
	 * Add s range with a value, guaranteed to add one node
	 * Does not check for merging!
	 */
	private void addRange(long za, long zb, T value) {
		ZNode nze=new ZNode();
		nze.object=value;
		nze.z1=za;	
		nze.z2=zb;
		head=addNode(nze,head);
	}
	
	@Override
	public int countNodes() {
		return countNodes(head);
	}
	
	public int countLevels() {
		return countLevels(head);
	}
	
	public int countLevels(ZNode node) {
		int subLevels=0;
		if (node.left!=null) {
			subLevels=countLevels(node.left);
		}
		if (node.right!=null) {
			int rl=countLevels(node.right);
			if (rl>subLevels) subLevels=rl;
		}
		return 1+subLevels;
	}
	
	private int countNodes(ZNode a) {
		if (a==null) return 0;
		return 1+countNodes(a.left)+countNodes(a.right);
	}
	
	public int countArea() {
		return countArea(head);
	}
	
	@Override
	public int countNonNull() {
		return countArea();
	}
	
	private int countArea(ZNode a) {
		if (a==null) return 0;
		return 1+(int)(a.z2-a.z1)+countArea(a.left)+countArea(a.right);
	}
	
	private boolean tryMerge(long zz) {
		// if (1==1) return false;
		ZNode a=getNode(zz-1);
		if (a==null) return false; // gap
		if (a.z2>=zz) return false; // already merged
		
		ZNode b=getNode(zz);
		if (b==null) return false; //gap
		if (a.object.equals(b.object)) {
			long temp=b.z2;
			//check();
			deleteNode(b);
			//check();
			a.z2=temp; // ok since we have just deleted entire b range
			//check();
			return true;
		}
		return false;
	}
	
	private final ZNode addNode(ZNode node, ZNode head) {
		if (head==null) return node;
		
		boolean addToLeft=node.compareTo(head)<0;
		if (addToLeft) {
			head.left=addNode(node,head.left);
			if (head.left.priority>head.priority) {
				return pivot(head,addToLeft);
			}
		} else {
			head.right=addNode(node,head.right);
			if (head.right.priority>head.priority) {
				return pivot(head,addToLeft);
			}
		}
		
		return head;
	}
	
	private final ZNode pivot(ZNode head, boolean toLeft) {
		ZNode newHead=toLeft?head.left:head.right;
		if (toLeft) {
			head.left=newHead.right;
			newHead.right=head;
		} else {
			head.right=newHead.left;
			newHead.left=head;
		}
		return newHead;
	}
	
	/**
	 * Deletes a range, setting to null
	 * Guarantees all nodes outside this area continue to exist
	 * @param za
	 * @param zb
	 */
	public void deleteRange(long za, long zb) {
		deleteRange(za,zb,head);
	}
	
	@SuppressWarnings("unchecked")
	private void deleteRange(long za, long zb, ZNode node) {
		if (node==null) return;
		
		if ((za>node.z1)&&(zb<node.z2)) {
			// cut out hole!
			long nza=zb+1;
			long nzb=node.z2;
			
			node.z2=za-1; // shrink range
			addRange(nza,nzb,(T)node.object);
			return;
		}
		
		// delete ranges on either side 
		// this does not alter structure since can't be cutting out any holes 
		if (za<node.z1) deleteRange(za,zb,node.left);
		if (zb>node.z2) deleteRange(za,zb,node.right);
		
		// exit if no overlap
		if ((zb<node.z1)||(za>node.z2)) return;
		
		// at least some overlap, not a hole....
		if ((za<=node.z1)&&(zb>=node.z2)) {
			// delete whole node
			deleteNode(node);
		} else {
			// cut off edges of node from correct side
			if (zb<node.z2) {
				node.z1=zb+1;
			} else {
				node.z2=za-1;
			}
		}
	}
	
	public void deleteNode(ZNode node) {
		head=deleteNode(node,head);
	}
	
	public ZNode deleteNode(ZNode node, ZNode head) {
		if (head==null) throw new Error("deleteNode: Node not found");
		if (node==head) {
			ZNode result= raiseUp(node);
			return result;
		}
		
		int dir=node.compareTo(head);
		if (dir<0) {
			head.left=deleteNode(node,head.left);
		} else {
			// if (dir==0) throw new Error("Duplicate z1 in deleteNode");
			head.right=deleteNode(node,head.right);
		}
		return head;
	}
	
	private ZNode raiseUp(ZNode node) {
		return raiseUp(node.left,node.right);
	}
		
	// makes one of two nodes the parent of the other based on priority
	private ZNode raiseUp(ZNode a, ZNode b) {
		if (a==null) return b;
		if (b==null) return a;
		boolean raiseA=(a.priority>b.priority);
		if (raiseA) {
			a.right=raiseUp(a.right,b);
		} else {
			b.left=raiseUp(a,b.left);
		}
		return raiseA?a:b;
	}

	
	public final static long calculateZ(int x, int y, int z) {
		return split3(x)+(split3(y)<<1)+(split3(z)<<2);
	}
	
	public final static int extractX(long z) {
		return extractComponent(z);
	}
	
	public final static int extractY(long z) {
		return extractComponent(z>>1);
	}
	
	public final static int extractZ(long z) {
		return extractComponent(z>>2);
	}
	
	public final static void extractComponents(long z, int[] pt) {
		pt[0]=extractComponent(z);
		pt[1]=extractComponent(z>>1);
		pt[2]=extractComponent(z>>2);
	}
	
	public static final int extractComponent(long z) {
		int lo=((int)(z))&01111111111;
		int hi=((int)(z>>30))&01111111111;
		return ((compressInt3(hi)<<22)>>12)+compressInt3(lo); // sign extend and combine
	}
	
	public final static int extractComponentOld(long z) {
		int result=0;
		
		int m;
		for (m=1; m<(BITS_POWER2); m<<=1) {
			result+=((int)z)&m;
			z=z>>2;
		}
		result+=z&m; // last bit
		if (result>=(BITS_POWER2>>1)) result-=BITS_POWER2;
		
		return result;
	}
	
	/**
	 * Compresses every third bit in an int
	 * Assumes all other bits are zero
	 * 
	 * @param a
	 * @return
	 */
	public static final int compressInt3(int a) {
		a=(a|(a>>2)) &00303030303; // group into 2/2/2/2/2 bits (octal)
		a=(a|(a>>4)) &00014170017; // group into 2/4/4 bits
		a=(a|(a>>8)) &00014000377; // group into 2/8 bits
		a=(a|(a>>12))&00000003777; // group into 10 bits
		return a;
	}
	
	/**
	 * Splits BITS across a long
	 * via calculation
	 * 
	 * @param a coordinate value in lowest BITS
	 * @return
	 */
	public static long split3c(long a) {
		long result=0;
		long m=1;
		for (int i=0; i<(BITS-1); i++) {
			result+=a&m;
			m=m<<3;
			a=a<<2;
		}
		result+=a&m; // last bit
		return result;
	}
	
	/**
	 * Splits BITS across a long
	 * 
	 * @param a coordinate value in lowest BITS
	 * @return
	 */
	public static long split3(int a) {
		return 	split3i(a&LOWBITS)
  	     | (((long)split3i((a&HIGHBITS)>>10))<<30);
	}

	public static int split3i(int a) {
		// split out the lowest 10 bits to lowest 30 bits, interleaving with zeroes
		a=(a|(a<<12))&00014000377;
		a=(a|(a<<8)) &00014170017;
		a=(a|(a<<4)) &00303030303;
		a=(a|(a<<2)) &01111111111;
		return a;
	}
	
	private static final int LOWBITS=1023; // bottom 10 bits
	private static final int HIGHBITS=1024* 1023; // top 10 bits

	@Override
	public void validate() {
		super.validate();
		if (head!=null) head.validate();
	}
}
