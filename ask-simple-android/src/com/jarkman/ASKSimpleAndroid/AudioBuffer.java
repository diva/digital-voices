package com.jarkman.ASKSimpleAndroid;

/**
 * Copyright 2002 by the authors. All rights reserved.
 *
 * Author: Cristina V Lopes
 */

import java.util.*;
import java.io.*;

/**
 * A thread safe buffer for audio samples
 * NOTE: This has no hard limits for memory usage
 *
 * @author CVL
 */
public class AudioBuffer {
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private Object lock = new Object();

    public AudioBuffer(){};

    /**
     * @param input an array to write to the end of the buffer
     */
    public synchronized void write(byte[] input)
      throws IOException {
      baos.write(input);
    }

    /**
     * @param input the source array
     * @param offset the offset into the array from which to start copying
     * @param length the length to copy
     */
    public synchronized void write(byte[] input, int offset, int length)
      throws IOException {
      baos.write(input, offset, length);
    }
    
    /**
     * @param n the number of bytes to try to read (nondestructively)
     * @return if the buffer.size >= n, return the requested byte array, otherwise null
     *
     * NOTE: THIS DOES NOT REMOVE BYTES FROM THE BUFFER
     */
    public synchronized byte[] read(int n){
      if(baos.size() < n){
	return null;
      }
      byte[] result = ArrayUtils.subarray(baos.toByteArray(), 0, n);
      return result;
    }

    /**
     * @param n the number of bytes to remove from the buffer. 
     * If n > buffer.size, it has the same effect as n = buffer.size.
     */
    public synchronized void delete(int n)
      throws IOException {
      if(n <= 0){
	return;
      }
      if(baos.size() < n){
	baos.reset();
	return;
      }
      byte[] buff = ArrayUtils.subarray(baos.toByteArray(), n - 1, baos.size() - n);
      baos.reset();
      baos.write(buff);
    }

    /**
     * @return the current size of the buffer
     */
    public synchronized int size(){
      int size = 0;
      size = baos.size();
      return size;
    }
}
