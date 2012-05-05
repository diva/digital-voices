package com.jonas.CricketAndroid;

/**
 * Copyright 2012 by the authors. All rights reserved.
 *
 * Author: Cristina V Lopes
 * (Modified by Jonas Michel, 2012)
 */



import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 * 
 * @author jrm
 */

public class AudioUtils {

    public static void performData(byte[] data)
	throws IOException {

    	PlayThread p = new PlayThread( data );
	
    }

    public static void performString(String string) throws IOException {
    	ArrayList<Integer> huffencoded = Huffman.huffencode(string);
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	Encoder.encodeCricket(huffencoded, baos);
    	performData(baos.toByteArray());
    }
}
