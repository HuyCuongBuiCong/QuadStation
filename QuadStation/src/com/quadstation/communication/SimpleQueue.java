package com.quadstation.communication;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Simple Queue (FIFO) based on LinkedList.
 */
public class SimpleQueue<T> {

	private LinkedList<T> list = new LinkedList<T>();

	/**
	 * Puts object in queue.
	 */
	public void put(T o) {
		list.addLast(o);
	}

	/**
	 * Returns an element (object) from queue.
	 * 
	 * @return element from queue or <code>null</code> if queue is empty
	 */
	public T get() {
		if (list.isEmpty()) {
			return null;
		}
		try {
			return list.removeFirst();
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * Returns all elements from the queue and clears it.
	 */
	public Object[] getAll() {
		Object[] res = new Object[list.size()];
		for (int i = 0; i < res.length; i++) {
			res[i] = list.get(i);
		}
		list.clear();
		return res;
	}

	/**
	 * Peeks an element in the queue. Returned elements is not removed from the
	 * queue.
	 */
	public T peek() {
		return list.getFirst();
	}

	/**
	 * Returns <code>true</code> if queue is empty, otherwise <code>false</code>
	 */
	public boolean isEmpty() {
		return list.isEmpty();
	}

	/**
	 * Returns queue size.
	 */
	public int size() {
		return list.size();
	}
}
