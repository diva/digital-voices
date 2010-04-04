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


/**
 * A set of handy array manipulation utilities
 * @author CVL
 */
public class ArrayUtils {

    /**
     * Create a new array of length 'length' and fill it from array 'array'
     * @param array the array from which to take the subsection
     * @param start the index from which to start the subsection copy
     * @param length the length of the returned array.
     * @return byte[] of length 'length', padded with zeros if array.length is shorter than 'start' + 'length'
     *
     * NOTE! if start + length goes beyond the end of array.length, the returned value will be padded with 0s.
     */
    public static byte[] subarray(byte[] array, int start, int length){
	byte[] result = new byte[length];
	for(int i=0; (i < length) && (i + start < array.length); i++){
	    result[i] = array[i + start];
	}
	return result;
    }

    /**
     * Converts the input matrix into a single dimensional array by transposing and concatenating the columns
     * @param input a 2D array whose columns will be concatenated
     * @return the concatenated array
     */
    public static byte[] concatenate(byte[][] input){
	//sum the lengths of the columns
	int totalLength = 0;
	for(int i = 0; i < input.length; i++){
	    totalLength += input[i].length;
	}
	//create the result array
	byte[] result = new byte[totalLength];

	//populate the result array
	int currentIndex = 0;
	for(int i=0; i < input.length; i++){
	    for(int j = 0; j < input[i].length; j++){
		result[currentIndex++] = input[i][j];
	    }
	}
	return result;
    }

    /**
     * @param sequence the array of floats to return as a shifted and clipped array of bytes
     * @return byte[i] = sequence[i] * Constants.kFloatToByteShift cast to a byte
     * Note!: This doesn't handle cast/conversion issues, so don't use this unless you understand the code
     */
    public static byte[] getByteArrayFromDoubleArray(double[] sequence){
	byte[] result = new byte[sequence.length];
	for(int i=0; i < result.length; i++){
	    result[i] = (byte)((sequence[i] * Constants.kFloatToByteShift) - 1);
	}
	return result;
    }
}
