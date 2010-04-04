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


import java.io.*;

/**
 * This starts a Thread which decodes data in an AudioBuffer and writes it to an OutputStream.
 * StreamDecoder holds the buffer where the MicrophoneListener puts bytes.
 *
 * @author CVL
 */
public class StreamDecoder implements Runnable {

  public static String kThreadName = "StreamDecoder";

  private Thread myThread = null;
  private Object runLock = new Object();
  private boolean running = false;

  private AudioBuffer buffer = new AudioBuffer(); // THE buffer where bytes are being put
  private OutputStream out = null;
    

    /**
     * This creates and starts the decoding Thread
     * @param _out the OutputStream which will receive the decoded data
     */
    public StreamDecoder(OutputStream _out) {
	out = _out;
	myThread = new Thread(this, kThreadName);
	myThread.start();
    }

    public AudioBuffer getAudioBuffer(){
	return buffer;
    }

    public void run() {
	synchronized(runLock){
	    running = true;
	}

	int durationsToRead = Constants.kDurationsPerKey;
	int deletedSamples = 0;
	boolean hasKey = false;
	boolean hasEOF = false;
	double[] startSignals = new double[Constants.kBitsPerByte * Constants.kBytesPerDuration];
	boolean notEnoughSamples = true;
	byte samples[] = null;

	while(running){
	  notEnoughSamples = true;
	  while (notEnoughSamples) {
	    samples = buffer.read(Constants.kSamplesPerDuration * durationsToRead);
	    if (samples != null)
	      notEnoughSamples = false;
	    else Thread.currentThread().yield();
	  }
	  if(hasKey){ //we found the key, so decode this duration
	    byte[] decoded = Decoder.decode(startSignals, samples);
	    try {
	      buffer.delete(samples.length);
	      deletedSamples += samples.length;
		    out.write(decoded);
		    if(decoded[0] == 0){ //we are recieving no signal, so go back to key detection mode
		      //out.write("EOF\r\n".getBytes()); //this is for debugging
		      hasKey = false;
		      durationsToRead = Constants.kDurationsPerKey;
		    }
	    } catch (IOException e){
	      System.out.println("Exception while decoding:" + e);
	      break;
	    }

	    try{ 
	      //this provides the audio sampling mechanism a chance to maintain continuity
	      myThread.sleep(10); 
	    } catch(InterruptedException e){
	      System.out.println("Stream Decoding thread interrupted:" + e);
	      break;
	    }
	    continue;
	  }

	  //we don't have the key, so we are in key detection mode from this point on
	  int initialGranularity = 400;
	  int finalGranularity = 20;
	  //System.out.println("Search Start: " + deletedSamples + " End: " + (deletedSamples + samples.length));
	  //System.out.println("Search Time: " + ((float)deletedSamples / Constants.kSamplingFrequency) + " End: " 
	  //		       + ((float)(deletedSamples + samples.length) / Constants.kSamplingFrequency));
	  int startIndex = Decoder.findKeySequence(samples, startSignals, initialGranularity);
	  if(startIndex > -1){
	    System.out.println("\nRough Start Index: " + (deletedSamples + startIndex));
	    //System.out.println("Rough Start Time: " 
	    //	   + (deletedSamples + startIndex) / (float)Constants.kSamplingFrequency);

	    int shiftAmount = startIndex /* - (Constants.kSamplesPerDuration)*/; 
	    if(shiftAmount < 0){
	      shiftAmount = 0;
	    }
	    System.out.println("Shift amount: " + shiftAmount);
	    try { buffer.delete(shiftAmount);} catch (IOException e){}
	    deletedSamples += shiftAmount;
	    
	    durationsToRead = Constants.kDurationsPerKey ;
	    notEnoughSamples = true;
	    while (notEnoughSamples) {
	      samples = buffer.read(Constants.kSamplesPerDuration * durationsToRead);
	      if (samples != null)
		notEnoughSamples = false;
	      else Thread.currentThread().yield();
	    }

	    //System.out.println("Search Start: " + deletedSamples + " End: " + (deletedSamples + samples.length));
	    //System.out.println("Search Time: " + ((float)deletedSamples / Constants.kSamplesPerDuration) + " End: " 
	    //		   + ((float)(deletedSamples + samples.length) / Constants.kSamplingFrequency));
	    
	    startIndex = Decoder.findKeySequence(samples, startSignals, finalGranularity);
	    System.out.println("Refined Start Index: " + (deletedSamples + startIndex));
	    //System.out.println("Start Time: " + 
	    //	   (deletedSamples + startIndex) / (float)Constants.kSamplingFrequency);
	    try {
	      notEnoughSamples = true;
	      while (notEnoughSamples) {
		samples = buffer.read(startIndex + (Constants.kSamplesPerDuration * Constants.kDurationsPerKey));
		if (samples != null)
		  notEnoughSamples = false;
		else Thread.currentThread().yield();
	      }
		  
	      samples = ArrayUtils.subarray(samples, startIndex + Constants.kSamplesPerDuration, 
						  2 * Constants.kSamplesPerDuration);
	      Decoder.getKeySignalStrengths(samples, startSignals);
	      /*
		System.out.println(" f(0): " + startSignals[0] + " f(1): " + startSignals[1] +
		" f(2): " + startSignals[2] + " f(3): " + startSignals[3] +
		" f(4): " + startSignals[4] + " f(5): " + startSignals[5] +
		" f(6): " + startSignals[6] + " f(7): " + startSignals[7]);
	      */

	      buffer.delete(startIndex + (Constants.kSamplesPerDuration * Constants.kDurationsPerKey));
	      deletedSamples += startIndex + (Constants.kSamplesPerDuration * Constants.kDurationsPerKey);
	    } catch (IOException e){}
	    hasKey = true;
	    durationsToRead = 1;
	  } else {
	    try {
	      buffer.delete(Constants.kSamplesPerDuration);
	      deletedSamples += Constants.kSamplesPerDuration;
	    } catch (IOException e){}
	  }
	}
    }

    public void quit(){
	synchronized(runLock){
	    running = false;
	}
    }
}
