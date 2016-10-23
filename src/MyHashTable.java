/* TCSS 342 - Spring 2016
 * Assignment 4 - Compressed Literature 2
 * Jieun Lee
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * MyHashMap.
 * 
 * @author Jieun Lee
 * @version 05-14-2016
 *
 * @param <K> the type of the keys.
 * @param <V> the type of the values.
 */
public class MyHashTable<K, V> {
	
	private int myCapacity;

	private List<Node> myElement;
	
	private Map<Integer, Integer> myHistogramMap;

	private int myEntryCount;
	
	private int myMaxProbing;
	
	private int myTotalProbing;

	/**
	 *Constructs MyHashTabel with given capacity number of buckets.
	 *
	 * @param capacity The number of bucket.
	 */
	public MyHashTable(int capacity) {
		if(capacity < 0) {
			throw new IllegalArgumentException("Capacity shoud not be negative!: " + capacity);
		}
		myCapacity = capacity;
		myElement = new ArrayList<Node>(capacity);
		for(int i = 0; i < capacity; i++) {
			myElement.add(null);
		}
		myHistogramMap = new HashMap<Integer, Integer>();
		myTotalProbing = 0;
		myEntryCount = 0;
		myMaxProbing = 0;
	}

	/**
	 * Update or add the newValue to the bucket hash(searchKey).
	 * 
	 * @param searchKey The search key.
	 * @param newValue The new value.
	 */
	public void put(K searchKey, V newValue) {
		if(searchKey == null || newValue == null) {
			throw new IllegalArgumentException("key and value cannot be null" + searchKey + ", " + newValue);
		}

		// gets hash value of the search key

		int h = hash(searchKey);
		int probingCount = 0;
		while (myElement.get(h) != null && !myElement.get(h).myKey.equals(searchKey)) {
			// if full, finds next available 
			h = (h + 1);
			probingCount++;
			if(probingCount >= myCapacity) {
				break;
			}
		}
		
		myElement.set(h, new Node(searchKey, newValue));
		
		// counts for number of entry
		myEntryCount++;
		helpStats(probingCount);
	}

	

	/**
	 * Return a value for the specified key from the bucket hash(searchKey).
	 * 
	 * @param searchKey The search key.
	 * @return The value for the specified key from the bucket hash
	 */
	public V get(K searchKey) {

		V result = null;
		
		// gets hash value of the search key
		int h = hash(searchKey);
		
		int probingCount = 0;
		while (myElement.get(h) != null && !myElement.get(h).myKey.equals(searchKey)) {
			h = (h + 1);
			probingCount++;
			if(probingCount >= myCapacity) {
				break;
			}
		}
		
		if (myElement.get(h) != null) {
			result = myElement.get(h).myValue;
		}

		return result;

	}

	/**
	 * Returns an integer in the range [0, capacity].
	 * 
	 * @param key The key
	 * @return An integer in the range [0, capacity].
	 */
	private int hash(K key) {
		return Math.abs(key.hashCode() % myCapacity);
	}

	/**
	 * Return true if there is a value stored for searchKey.
	 * 
	 * @param searchKey The search key.
	 * @return True if there is a value stored for searchKey.
	 */
	public boolean containsKey(K searchKey) {
		return get(searchKey) != null;
	}
	
	/**
	 * Displays the stat block for the data in this hash table:
	 */
	public void stats() {
		System.out.println("Hash Table Stats\n=================");
		System.out.println("Number of Entries: " + myEntryCount);
		System.out.println("Number of Buckets: " + myCapacity);
		System.out.println("Histogram of Probes: " + getHistogram());
		System.out.println("Fill Percentage: " + String.format("%.6f", ((double) myEntryCount * 100 / (double) myCapacity)) +"%");
		System.out.println("Max Linear Prob: " + myMaxProbing);
		System.out.println("Average Linear Prob: " + String.format("%.6f", ((double) (myTotalProbing + myEntryCount) / (double) myEntryCount)));
	}
	
	
	/**
	 * Calculates max number of probing and put the number of probing into histogram map.
	 * 
	 * @param probingCount The number of probing when a new search key and new value are added.
	 */
	private void helpStats(int probingCount) {
		myTotalProbing += probingCount;
		
		// for max linear probing
		if (probingCount > myMaxProbing) {
			myMaxProbing = probingCount;
		}
		
		// stores the number of probing as a key and put the counted value that key's times probed into the value
		if (myHistogramMap.containsKey(probingCount)) {
			myHistogramMap.put(probingCount, myHistogramMap.get(probingCount) + 1);
		} else {
			myHistogramMap.put(probingCount, 1);
		}
	}
	
	/**
	 * Returns histogram of probing.
	 * 
	 * @return String of probing histogram.
	 */
	private String getHistogram() {
		
		StringBuilder result = new StringBuilder();
		
		List<Integer> histogram = new ArrayList<Integer>();
		for (int i = 0; i <= myMaxProbing; i++) {
			histogram.add(0);
		}

		// sets number of proving of k times into the histogram list
		for(int k : myHistogramMap.keySet()) {
			histogram.set(k, myHistogramMap.get(k));
		}

		// return empty histogram if there is no entry key and value.
		if (myEntryCount < 1) {
			result.append("[]");
		} else {
			result.append('[');
			result.append(histogram.get(0));
			if(histogram.size() > 1) {
				for (int i = 1; i < histogram.size(); i++){
					result.append(", " + histogram.get(i));
				}
			}
			result.append(']');
		}
		return result.toString();
	}
	
	/**
	 * Converts the hash table contents to a String.
	 * 
	 * @return The hash table contents.
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		
		if(myElement.size() < 1 || myEntryCount < 1) {
			result.append("[]");
		} else {
			result.append('{');
			int idx = 0;
			while(myElement.get(idx) == null) {
				idx++;
			}
			result.append(myElement.get(idx));
			if (myElement.size() > idx) {
				for(int i = idx + 1; i < myElement.size(); i++) {
					if(myElement.get(i) != null) {
						result.append(", " + myElement.get(i).toString());
					}
				}
			}
			result.append('}');
		}
		return result.toString();
	}
	


	/*
	 * inner class
	 */
	private class Node {
		
		/**
		 * A key.
		 */
		public K myKey;
		
		/**
		 * A value.
		 */
		public V myValue;
		
		/**
		 * Constructs a new node that takes key and value.
		 * 
		 * @param theKey The key.
		 * @param theValue The Value.
		 */
		public Node(K theKey, V theValue) {
			myKey = theKey;
			myValue = theValue;
		}
		
		public String toString() {
			return "<" + myKey + "=" + myValue + ">";
		}
	}
}
