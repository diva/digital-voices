package com.jonas.CricketAndroid;

/**
 * Copyright 2012 by the authors. All rights reserved.
 *
 * Author: Jonas Michel
 */

import java.util.ArrayList;

public class Huffman implements Constants {

	/**
	 * @param input string consisting of {'a'-'z',' ','.'}
	 * @return list of huffman-encoded integers
	 */
	public static ArrayList<Integer> huffencode(String input) {
		ArrayList<Integer> output = new ArrayList<Integer>();
		for (int i = 0; i < input.length(); i++) {
			switch (input.charAt(i)) {
			case ' ': output.add(4);
				break;
			case 'a': output.add(1); output.add(4);
				break;
			case 'b': output.add(2); output.add(4); output.add(4); output.add(2);
				break;
			case 'c': output.add(1); output.add(1); output.add(3);
				break;
			case 'd': output.add(1); output.add(1); output.add(2);
				break;
			case 'e': output.add(1); output.add(2);
				break;
			case 'f': output.add(2); output.add(4); output.add(3);
				break;
			case 'g': output.add(2); output.add(3); output.add(2);
				break;
			case 'h': output.add(3); output.add(4);
				break;
			case 'i': output.add(2); output.add(2);
				break;
			case 'j': output.add(2); output.add(4); output.add(2); output.add(2);
				break;
			case 'k': output.add(2); output.add(4); output.add(4); output.add(4);
				break;
			case 'l': output.add(1); output.add(1); output.add(1);
				break;
			case 'm': output.add(2); output.add(4); output.add(1);
				break;
			case 'n': output.add(3); output.add(1);
				break;
			case 'o': output.add(2); output.add(1);
				break;
			case 'p': output.add(2); output.add(3); output.add(1);
				break;
			case 'q': output.add(2); output.add(4); output.add(2); output.add(3);
				break;
			case 'r': output.add(3); output.add(3);
				break;
			case 's': output.add(3); output.add(2);
				break;
			case 't': output.add(1); output.add(3);
				break;
			case 'u': output.add(1); output.add(1); output.add(4);
				break;
			case 'v': output.add(2); output.add(4); output.add(4); output.add(3);
				break;
			case 'w': output.add(2); output.add(3); output.add(3);
				break;
			case 'x': output.add(2); output.add(4); output.add(2); output.add(1);
				break;
			case 'y': output.add(2); output.add(4); output.add(4); output.add(1);
				break;
			case 'z': output.add(2); output.add(4); output.add(2); output.add(4);
				break;
			case '.': output.add(2); output.add(3); output.add(4);
				break;
			default: System.out.println("ERROR: Character not recognized!!");
				break;
					
			}
		}
		return output;
	}
	
	/**
	 * @param input list of huffman-encoded integers
	 * @return decoded string
	 */
	public static String huffdecode(ArrayList<Integer> input) {
		StringBuilder output = new StringBuilder();
		int i = 0;
		while (i < input.size() - 3) {
			switch (input.get(i)) {
			case 4: output.append(' ');
				i += 1;
				break;
			case 3:
				switch (input.get(i+1)) {
				case 1: output.append('n');
					break;
				case 2: output.append('s');
					break;
				case 3: output.append('r');
					break;
				case 4: output.append('h');
					break;
				default: System.out.println("ERROR inside case 3");
					break;
				}
				i += 2;
				break;
			case 2:
				switch (input.get(i+1)) {
				case 1: output.append('o');
					i += 2;
					break;
				case 2: output.append('i');
					i += 2;
					break;
				case 3:
					switch (input.get(i+2)) {
					case 1: output.append('p');
						break;
					case 2: output.append('g');
						break;
					case 3: output.append('w');
						break;
					case 4: output.append('.');
						break;
					default: System.out.println("ERROR inside case 2,3");
						break;
					}
					i += 3;
					break;
				case 4:
					switch (input.get(i+2)) {
					case 1: output.append('m');
						i += 3;
						break;
					case 2:
						switch (input.get(i+3)) {
						case 1: output.append('x');
							break;
						case 2: output.append('j');
							break;
						case 3: output.append('q');
							break;
						case 4: output.append('z');
							break;
						default: System.out.println("ERROR inside case 2,4,2");
							break;
						}
						i += 4;
						break;
					case 3: output.append('f');
						i += 3;
						break;
					case 4:
						switch (input.get(i+3)) {
						case 1: output.append('y');
							break;
						case 2: output.append('b');
							break;
						case 3: output.append('v');
							break;
						case 4: output.append('k');
							break;
						default: System.out.println("ERROR inside case 2,4,4");
							break;
						}
						i += 4;
						break;
					default: System.out.println("ERROR inside case 2,4");
						break;
					}
					break;
				default: System.out.println("ERROR inside case 2");
					break;
				}
				break;
				
			case 1:
				switch (input.get(i+1)) {
				case 1:
					switch (input.get(i+2)) {
					case 1: output.append('l');
						break;
					case 2: output.append('d');
						break;
					case 3: output.append('c');
						break;
					case 4: output.append('u');
						break;
					default: System.out.println("ERROR inside case 1,1");
						break;
					}
					i += 3;
					break;
				case 2: output.append('e');
					i += 2;
					break;
				case 3: output.append('t');
					i += 2;
					break;
				case 4: output.append('a');
					i += 2;
					break;
				default: System.out.println("ERROR inside case 1");
					break;
				}
				break;
			default: System.out.println("ERROR: Number not recognized!!");
				break;
			}
		}
		return output.toString();
	}
}
