package mikera.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A stack implemented as a linked list.
 * @author Mike
 * @param <T> Type of object held in the stack
 */
public class LinkedStack<T> implements Queue<T> {

	AtomicReference<Node<T>> topOfStack=new AtomicReference<Node<T>>();

	/**
	 * Private inner class used to represent linked list nodes
	 */
	private static final class Node<T> {
		final T object;
		final Node<T> next;
		
		private Node (T object, Node<T> next) {
			this.object=object;
			this.next=next;
		}
	}
	
	private static final class StackIterator<T> implements Iterator<T> {
		Node<T> head;
		
		private StackIterator (Node<T> top) {
			head=top;
		}		
		
		@Override
		public boolean hasNext() {
			return (head!=null);
		}

		@Override
		public T next() {
			T result=head.object;
			head=head.next;
			return result;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}	
	}

	@Override
	public int size() {
		Node<T> head=topOfStack.get();
		int result=0;
		while (head!=null) {
			head=head.next;
			result++;
		}
		return result;
	}

	@Override
	public boolean isEmpty() {
		return (topOfStack.get()==null);
	}

	
	public int search(Object o) {
		Node<T> head=topOfStack.get();
		int pos=1;
		while (head!=null) {
			if (Tools.equalsWithNulls(o, head.object)) return pos;
			pos++;
		}
		return -1;		
	}
	
	@Override
	public boolean contains(Object o) {
		Node<T> head=topOfStack.get();
		while (head!=null) {
			if (Tools.equalsWithNulls(o, head.object)) return true;
			head=head.next;
		}
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		return new StackIterator<T>(topOfStack.get());
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("hiding")
	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		topOfStack=null;
	}
	
	@Override
	public boolean add(T e) {
		while(true) {
			Node<T> oldTop=topOfStack.get();
			Node<T> newTop=new Node<T>(e,oldTop);
			if (topOfStack.compareAndSet(oldTop, newTop)) break;
		} 
		return true;
	}

	@Override
	public T remove() {
		while(true) {
			Node<T> oldTop=topOfStack.get();
			if (oldTop==null) throw new NoSuchElementException();
			Node<T> newTop=oldTop.next;
			if (topOfStack.compareAndSet(oldTop, newTop)) return oldTop.object;
		} 
	}


	@Override
	public boolean offer(T e) {
		return add(e);
	}

	@Override
	public T poll() {
		while(true) {
			Node<T> oldTop=topOfStack.get();
			if (oldTop==null) return null;
			Node<T> newTop=oldTop.next;
			if (topOfStack.compareAndSet(oldTop, newTop)) return oldTop.object;
		} 
	}

	@Override
	public T element() {
		Node<T> top=topOfStack.get();
		if (top==null) throw new NoSuchElementException();
		return top.object;
	}

	@Override
	public T peek() {
		Node<T> top=topOfStack.get();
		if (top==null) return null;
		return top.object;
	}
}
