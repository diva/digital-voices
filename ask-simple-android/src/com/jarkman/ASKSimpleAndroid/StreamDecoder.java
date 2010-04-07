package com.jarkman.ASKSimpleAndroid;

/**
 * Copyright 2002 by the authors. All rights reserved.
 *
 * Author: Cristina V Lopes
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
  boolean hasKey = false;
    

    /**
     * This creates and starts the decoding Thread
     * @param _out the OutputStream which will receive the decoded data
     */
    public StreamDecoder(OutputStream _out) {
	out = _out;
	myThread = new Thread(this, kThreadName);
	myThread.start();
    }

    public String getStatusString()
    {
    	String s = "";
    	
    	int backlog = (int) ((1000 * buffer.size()) / Constants.kSamplingFrequency);
    	
    	if( backlog > 0 )
    		s += "Backlog: " + backlog + " mS ";

    	if( hasKey )
    		s += "Found key sequence ";

    	return s;
    }
    
    public AudioBuffer getAudioBuffer(){
	return buffer;
    }

    public void run() {
	synchronized(runLock){
	    running = true;
	}

	android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

	
	int durationsToRead = Constants.kDurationsPerKey;
	int deletedSamples = 0;
	
	boolean hasEOF = false;
	double[] startSignals = new double[Constants.kBitsPerByte * Constants.kBytesPerDuration];
	boolean notEnoughSamples = true;
	byte samples[] = null;

	hasKey = false;
	
	while(running)
	{
	  notEnoughSamples = true;
	  while (notEnoughSamples) 
	  {
	    samples = buffer.read(Constants.kSamplesPerDuration * durationsToRead);
	    if (samples != null)
	    	notEnoughSamples = false;
	    else 
	    	Thread.yield();
	  }
	  
	  if(hasKey)
	  { //we found the key, so decode this duration
	    byte[] decoded = Decoder.decode(startSignals, samples);
	    try {
	      buffer.delete(samples.length);
	      deletedSamples += samples.length;
		  out.write(decoded);
		  
		  System.out.println("decoded " + decoded.length + " bytes");
		  
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
	      Thread.sleep(10); 
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
	  if(startIndex > -1)
	  {
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
	      else Thread.yield();
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
		else Thread.yield();
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
	    
	    System.out.println(">>>>>>>>>>>>>>>>>>>>>    found key <<<<<<<<<<<<<<<<<<<<<");
	    
	    
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
