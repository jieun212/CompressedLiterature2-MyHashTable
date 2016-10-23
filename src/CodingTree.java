/* TCSS 342 - Spring 2016
 * Assignment 4 - Compressed Literature 2
 * Jieun Lee
 */

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Coding Tree.
 * 
 * @author Jieun Lee
 * @version 05-14-2016
 */
public class CodingTree {
	
	/**
	 * (for this assignment you will use capacity 2^15 = 32768)
	 */
	public static int CAPACITY = 32768;

	/**
	 * A hash table of words or separators used as keys to retrieve strings of 1s and 0s as values.
	 */
	public MyHashTable<String, String> codes;
	
	/**
	 * A data member that is the message encoded using the Huffman's codes.
	 */
	public List<Byte> bits;
	

	/**
	 * A data member that is a map of characters to frequency of the character.
	 */
	private MyHashTable<String, Integer> myCharFrequencyTable;
	
	
	/**
	 * Construct a CodingTree with given text and it carries out the Huffman's coding algorithm.
	 * 
	 * @param fulltext The full text of an English message to be compressed.
	 */
	public CodingTree(String fulltext) {
		myCharFrequencyTable = new MyHashTable<String, Integer>(CAPACITY);
		codes = new MyHashTable<String, String>(CAPACITY);
		bits = new ArrayList<Byte>();
		countFrequency(fulltext);
		compressedEncoding(fulltext);
	}
	
	/* For encoding */

	/**
	 * Counts frequency of character in the given message.
	 * 
	 * @param message The message.
	 */
	private void countFrequency(final String message) {
		if (message == null || message.isEmpty()) {
			return;
		}

		final List<String> keyList = new ArrayList<String>();

		int i = 0;
		StringBuilder sb = new StringBuilder();
		while (i < message.length()) {
			char ch = message.charAt(i);
			//System.out.println("[" + ch + "]");
			
			// how to know if character is letter or number
			// https://docs.oracle.com/javase/tutorial/i18n/text/charintro.html
			// https://docs.oracle.com/javase/7/docs/api/java/lang/Character.html#isLetter(char)
			if (Character.isLetter(ch) || Character.isDigit(ch) || ch == '\'' || ch == '-') {
				sb.append(ch);
			} else { 
				
				// if the character is separator, store it in the hash table with its count 
				String seperator = Character.toString(ch);
				//System.out.println("char [" +seperator+ "]");
				if (myCharFrequencyTable.containsKey(seperator)) {
					myCharFrequencyTable.put(seperator, myCharFrequencyTable.get(seperator) + 1);
				} else {
					myCharFrequencyTable.put(seperator, 1);
					keyList.add(seperator);
				}
				
				// if the character is separator, stores the temp word in the hash table with its count. 
				if (sb.length() > 0) {
					String word = sb.toString();
					//System.out.println(word);
					if (myCharFrequencyTable.containsKey(word)) {
						myCharFrequencyTable.put(word, (myCharFrequencyTable.get(word) + 1));
                	} else {
                		myCharFrequencyTable.put(word, 1);
                		keyList.add(word);
					}
				}
				sb.delete(0, sb.length());
			}
			i++;
		}
		if (sb.length() > 0) {
			String word = sb.toString();
			//System.out.println(word);
			if (myCharFrequencyTable.containsKey(word)) {
				myCharFrequencyTable.put(word, (myCharFrequencyTable.get(word) + 1));
        	} else {
        		myCharFrequencyTable.put(word, 1);
        		keyList.add(word);
			}
		}
		sb.delete(0, sb.length());

		buildTree(keyList);
	}

	/**
	 * Build HuffmanTree for each string with a non-zero count.
	 */
	private void buildTree(final List<String> keyList) {

		// To handle selecting the minimum weight tree.
		final PriorityQueue<Node> pq = new PriorityQueue<Node>();
		for (int i = 0; i < keyList.size(); i++) {
			pq.offer(new Node(myCharFrequencyTable.get(keyList.get(i)), keyList.get(i)));
		}

		// Merges 2 trees with minimum weight into a single tree with weight
		// equal to the sum of 2 tree weights
		// by creating a new root and adding two trees as L and R sub tree
		Node root;
		while (pq.size() > 1) { // repeating this step until there's only a
									// single tree
			final Node left = pq.poll();
			final Node right = pq.poll();
			root = new Node(left.myFrequency + right.myFrequency, "", left, right);
			pq.offer(root);
		}

		labeling(pq.poll(), "");
	}

	/**
	 * Labels the single tree's left branch with "0", and right branch with "1".
	 * 
	 * @param node The single tree.
	 * @param label "0" or "1".
	 */
	private void labeling(final Node node, final String label) {
		if (node == null) {
			return;
		}
		if (node != null && node.myLeftNode == null && node.myRightNode == null) {
			codes.put(node.myData, label);
		}
		labeling(node.myRightNode, label + "1");
		labeling(node.myLeftNode, label + "0");
	}

	/**
	 * Creates a compressed encoding of the given message using the codes for
	 * each character.
	 * 
	 * @param message The message.
	 */
	private void compressedEncoding(final String message) {
		if (message == null || message.isEmpty()) {
			return;
		}
		
		StringBuilder temp = new StringBuilder();
		StringBuilder str = new StringBuilder();;
		for (int i = 0; i < message.length(); i++) {
			char ch = message.charAt(i);

			if (Character.isLetter(ch) || Character.isDigit(ch) || ch == '\'' || ch == '-') {
				temp.append(ch);
			} else { 
				// if isChar is true, store the binary string
				if (temp.length() > 0) {
					String word = temp.toString();
					str.append(codes.get(word));	
					//System.out.print(codes.get(word));
				}
				temp.delete(0, temp.length());
				// store the binary string for separators separately
				String seperator = Character.toString(ch);
				str.append(codes.get(seperator));
				
				//System.out.print(codes.get(seperator));
			}
			
			while (str.length() > 8) {
				// how to convert binary string to byte.
				// http://stackoverflow.com/questions/13694312/converting-string-type-binary-number-to-bit-in-java
				int charByte = Integer.parseInt(str.substring(0, 8), 2);
				bits.add((byte) charByte);
				str.delete(0, 8);
			}
		}
		if(str.length() > 0) {
			int charByte = Integer.parseInt(str.substring(0, str.length()), 2);
			bits.add((byte) charByte);
			str.delete(0, str.length());
		}
	}
	
	

	
	
	
	/* inner class */

	/**
	 * Inner Node class for CodingTree.
	 * 
	 * @author Jieun Lee
	 */
	private class Node implements Comparable<Node> {

		/**
		 * A data.
		 */
		private String myData;

		/**
		 * Left Node.
		 */
		private Node myLeftNode;

		/**
		 * Right Node.
		 */
		private Node myRightNode;

		/**
		 * Frequency of occurrence of character.
		 */
		private int myFrequency;

		/**
		 * Constructs new Node with character and character's frequency.
		 * 
		 * @param theData The string
		 * @param theFrequency The frequency of the character
		 */
		public Node(final int theFrequency, final String theData) {
			this(theFrequency, theData, null, null);
		}

		/**
		 * Full constructor.
		 * 
		 * @param theData The string
		 * @param theFrequency The frequency of the character
		 * @param theLeft The left child node
		 * @param theRight The right child node
		 */
		public Node(final int theFrequency, final String theData, final Node theLeft, final Node theRight) {
			myData = theData;
			myFrequency = theFrequency;
			myLeftNode = theLeft;
			myRightNode = theRight;
		}

		/**
		 * Compares this object with the specified object for order.
		 * 
		 * @param theOther The other Node.
		 */
		@Override
		public int compareTo(final Node theOther) {
			if (theOther == null) {
				throw new IllegalArgumentException();
			}
			return myFrequency - theOther.myFrequency;
		}

		/**
		 * Prints a string and its frequency.
		 */
		@Override
		public String toString() {
			return "[" + myData + ", " + myFrequency + "]";
		}

	}

}
