/**
 * Copyright 2002 by the authors. All rights reserved.
 *
 * Author: Cristina V Lopes (crista at tagide dot com)

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  THE SOFTWARE.

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
