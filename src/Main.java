/* TCSS 342 - Spring 2016
 * Assignment 4 - Compressed Literature 2
 * Jieun Lee
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This main class is for Assignment 4 - Compressed Literature 2.
 * It is a controller that uses the CodingTree to compress a file
 * 
 * @author Jieun Lee
 * @version 05-14-2016
 */
public class Main {
	
	/**
	 * A test text file name.
	 */
	public static String LESMISERABLES_TEXT= "LesMiserables.txt";
	
	/**
	 * A sample text file name.
	 */
	public static String WARANDPEACE_TEXT=  "WarAndPeace.txt";


	public static void main(String[] args) {

		// calls compress() to compress the input file. 
		
		compress(WARANDPEACE_TEXT);

//		compress(LESMISERABLES_TEXT);

		
		// tests CodingTree
		//testCodingTree();
		
		// tests MyHashTable
		//testMyHashTable();

	}
	

	/**
	 * Compresses a text file.
	 * 
	 * @param The String of file name to be compressed.
	 */
	public static void compress(final String fileName) {

		try {
			final File inputFile = new File(fileName);
			FileReader input = new FileReader(inputFile);

			/*
			 * Encoding.
			 */
			// records the starting time for compression.
			final long start = System.currentTimeMillis();
			
			// reads the contents of a text file into a String.
			// how to read text file char by char 
			// http://stackoverflow.com/questions/7941554/reading-in-from-text-file-character-by-character
			int r;
			StringBuilder result = new StringBuilder();
			while ((r = input.read()) != -1) {
				char ch = (char) r;
				result.append(ch);
			}
			input.close();
			
			// passes the String into the Coding Tree in order to initiate
			// Huffman's encoding procedure and generate a map of codes.
			final String message = result.toString();
			final CodingTree tree = new CodingTree(message);
			
			// produces the codes to a text file .
			FileWriter codeOutput = new FileWriter("codes.txt");
			codeOutput.write(tree.codes.toString());
			codeOutput.close();
			
			// produces the compressed message to a binary file.
			final File compressedFile = new File("compressed.txt");
			FileOutputStream compressedOutput = new FileOutputStream(compressedFile);
			// how to convert character to byte
			// https://www.javacodegeeks.com/2010/11/java-best-practices-char-to-byte-and.html
			final byte[] b = new byte[tree.bits.size()];
			for (int i = 0; i < b.length; i++) {
				b[i] = tree.bits.get(i);
			}
			compressedOutput.write(b);
			compressedOutput.close();
			
			// records the ending time for compression.
			final long end = System.currentTimeMillis();

			// gets sizes of original and compressed text files in Kilobytes.
			final double originalSize = inputFile.length() / 1024;
			final double compressedSize = compressedFile.length() / 1024;
			
			// displays hash table stats
			tree.codes.stats();
			
			// displays compression and the elapsed time for compression statistics.
			// expected result: 
			// compressed file size = less than 1KB of 1832KB// time = less than 3 seconds.
			System.out.println("\nFile Name: " + fileName);
			System.out.println("Original file Size: " + String.format("%.2f", originalSize) + " KB");
			System.out.println("Compressed file size: " + String.format("%.2f",compressedSize) + " KB");
			System.out.println("Compression ratio: "+ String.format("%.2f",(compressedSize*100) / originalSize) + " %");
			System.out.println("Elapsed time for compression: " + (end - start) + " milliseconds");
			
			

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	/**
	 * Tests CodingTree.
	 */
	public static void testCodingTree() {
		System.out.println("\n-------------- Tests CodingTree ----------------\n");
		
		// empty test
		final String str0 = null;
		System.out.println("==== Empty message test. " + str0);
		final CodingTree tree0 = new CodingTree(str0);
		System.out.println("codes" + tree0.codes.toString());
		System.out.println("bits size " + tree0.bits.size() + ", " + tree0.bits.toString());
		
		tree0.codes.stats();
		
		System.out.println();

		
		// test1 for simple message
		final String str1 = "ADAM THE MAN AND DAN THE MAD ARE MAD MAD MEN."; 
		System.out.println("==== message 1. " + str1);
		final CodingTree tree1 = new CodingTree(str1);
		System.out.println("codes" + tree1.codes.toString());
		System.out.println("bits size " + tree1.bits.size() + ", " + tree1.bits.toString());
		
		tree1.codes.stats();
		
		// test2 for testing for a word has '-' and '\'', and for a separator exists middle of the message.  
		final String str2 = "AN'NA HAS A BA-NANA IN A BANDANA!BANANA!";
		final CodingTree tree2 = new CodingTree(str2);
		System.out.println("==== message 2. " + str2);
		System.out.println("codes" + tree2.codes.toString());
		System.out.println("bits size " + tree2.bits.size() + ", " + tree2.bits.toString());
		
		tree2.codes.stats();

		
		final String str3 = "\"Come over here, Helene, dear,\" said Anna Pavlovna to the beautiful";
		final CodingTree tree3 = new CodingTree(str3);
		System.out.println("==== message 3. " + str3);
		System.out.println("codes" + tree3.codes.toString());
		System.out.println("bits size " + tree3.bits.size() + ", " + tree3.bits.toString());
		
		tree3.codes.stats();

	}
	
	/**
	 * Tests MyHashTable.
	 */
	public static void testMyHashTable() {
		System.out.println("\n-------------- Tests MyHashMap ----------------\n");
		
		// test1. method test
		MyHashTable<String, String> ht = new MyHashTable<String, String>(14);
		
		// tests put() method
		ht.put("A1", "aa");
		ht.put("A2", "bb");
		ht.put("A3", "cc");
		ht.put("A4", "dd");
		ht.put("B3", "ABC");
		ht.put("B8", "DFE");
		ht.put("C5", "ab12cd");

		// tests toString() method
		System.out.println("Test1. toString(): " + ht.toString());
		
		// tests get() method
		System.out.println("get(key) Expected: ghj, Actual: " + ht.get("A3"));
		
		// tests containsKey() method
		System.out.println("containsKey(key) Expected: true, Actual: " + ht.containsKey("B3"));
		System.out.println("containsKey(key) Expected: false, Actual: " + ht.containsKey("D12"));
		
		// tests stats() method
		ht.stats();

		
		// test2. test with message
		String message = "THE MAN DAN THE MAD MAD MAD.";
		System.out.println("\nTest2. message: " + message);

		MyHashTable<String, Integer> strTable = new MyHashTable<String, Integer>(16);
		boolean isChar = false;
		int strIdx = 0;
		for (int i = 0; i < message.length(); i++) {
			char ch = message.charAt(i);
			if (Character.isLetter(ch) || Character.isDigit(ch) || ch == '\'' || ch == '-') {
				isChar = true;
			} else {

				// for separator character
				String chStr = Character.toString(ch);
				if(strTable.containsKey(chStr)) {
					strTable.put(chStr, strTable.get(chStr) + 1);
				} else {
					strTable.put(chStr, 1);
				}

				// for the word
				if (isChar) {
					String word = message.substring(strIdx, (i));
					if(strTable.containsKey(word)) {
						strTable.put(word, strTable.get(word) + 1);
					} else {
						strTable.put(word, 1);
					}
					strIdx = i + 1;
				}
			}
		}
		System.out.println("myhash : " +strTable.toString());
		strTable.stats();
	}

}
