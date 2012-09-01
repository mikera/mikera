package mikera.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;

public class LinkedStack<T> implements Queue<T> {

AtomicReference<Node<T>> topOfStack=new AtomicReference<>();


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
		
		public boolean hasNext() {
			return (head!=null);
		}

		public T next() {
			T result=head.object;
			head=head.next;
			return result;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}	
	}

	public int size() {
		Node<T> head=topOfStack.get();
		int result=0;
		while (head!=null) {
			head=head.next;
			result++;
		}
		return result;
	}

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
	

	public boolean contains(Object o) {
		Node<T> head=topOfStack.get();
		while (head!=null) {
			if (Tools.equalsWithNulls(o, head.object)) return true;
			head=head.next;
		}
		return false;
	}

	public Iterator<T> iterator() {
		return new StackIterator<>(topOfStack.get());
	}

	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("hiding")
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}

	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		topOfStack=null;
	}
	
	public boolean add(T e) {
		while(true) {
			Node<T> oldTop=topOfStack.get();
			Node<T> newTop=new Node<>(e,oldTop);
			if (topOfStack.compareAndSet(oldTop, newTop)) break;
		} 
		return true;
	}

	public T remove() {
		while(true) {
			Node<T> oldTop=topOfStack.get();
			if (oldTop==null) throw new NoSuchElementException();
			Node<T> newTop=oldTop.next;
			if (topOfStack.compareAndSet(oldTop, newTop)) return oldTop.object;
		} 
	}


	public boolean offer(T e) {
		return add(e);
	}

	public T poll() {
		while(true) {
			Node<T> oldTop=topOfStack.get();
			if (oldTop==null) return null;
			Node<T> newTop=oldTop.next;
			if (topOfStack.compareAndSet(oldTop, newTop)) return oldTop.object;
		} 
	}

	public T element() {
		Node<T> top=topOfStack.get();
		if (top==null) throw new NoSuchElementException();
		return top.object;
	}

	public T peek() {
		Node<T> top=topOfStack.get();
		if (top==null) return null;
		return top.object;
	}
}
