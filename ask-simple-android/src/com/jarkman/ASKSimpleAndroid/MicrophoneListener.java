
package com.jarkman.ASKSimpleAndroid;

/**
 * Copyright 2002 by the authors. All rights reserved.
 *
 * Author: Cristina V Lopes
 */


import java.io.*;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

/**
 * This thread puts bytes from the microphone into the StreamDecoder's buffer.
 * 
 * @author CVL
 */
public class MicrophoneListener implements Runnable {

    public static final String kThreadName = "MicrophoneListener";

    private AudioBuffer buffer = null;
    private Thread myThread = null;
    private Object runLock = new Object();
    private boolean running = false;

    /**
     * NOTE: This spawns a thread to do the listening and then returns
     * @param _buffer the AudioBuffer into which to write the microphone input
     */
    public MicrophoneListener(AudioBuffer _buffer) {
	buffer = _buffer;
	myThread = new Thread(this, kThreadName);
	myThread.start();
    }

  

    public void run() {
    	synchronized(runLock){
    	    running = true;
    	}

		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

		
    	try {
    	  /**
    	   * NOTE: we want buffSize large so that we don't loose samples when the 
    	   * StreamDecoder thread kicks in. But we want to read a small number of 
    	   * samples at a time, so that StreamDecoder can process them and they get
    	   * freed from the buffer as soon as possible.
    	   * So there's a fine balance going on here between the two threads, and
    	   * if it's not tuned, samples will be lost.
    	   */
    		
    		    		
    	    int buffSize = (int) Encoder.kSamplingFrequency 
    	    						* 2 // 2 seconds in the buffer
    	    						* 2; // recording in 16 bit
    	    
    	    int biteSize = ((int) Encoder.kSamplingFrequency ) / 8;  // move 1/8 sec of data at a time
    	    
    	    AudioRecord arec = new	AudioRecord(MediaRecorder.AudioSource.MIC,
    	    		(int) Encoder.kSamplingFrequency, //11025,
    	            AudioFormat.CHANNEL_CONFIGURATION_MONO,
    	            AudioFormat.ENCODING_PCM_16BIT, // must match PlayThread. Tried (and failed) to make ENCODING_PCM_8BIT work on Android
    	            buffSize);
    	    System.out.println(Thread.currentThread().getName() + " is recording");
    	    //ByteArrayOutputStream out  = new ByteArrayOutputStream();
     	    byte[] data_16bit = new byte[ biteSize * 2];
     	    byte[] data_8bit = new byte[ biteSize ];
     	    
     	    int numBytesRead;
    	    arec.startRecording();
    	    while(running)
    	    {
    	    	numBytesRead = arec.read(data_16bit, 0, data_16bit.length);
    	    	
    	    	// convert the 16bit we have to read to to 8bit by discarding alternate bytes - PCM is little-endian, so we discard the first byte
    	    	int i;
    	    	int j;
    		    for( i = 1, j = 0; i < numBytesRead; i += 2, j ++)
    		    	data_8bit[j] = data_16bit[i];
    		    
    		    //System.out.println(Thread.currentThread().getName() + "> bytesRead = " + numBytesRead);
    		    
     		    buffer.write(data_8bit, 0, j);   //writes into the buffer, which StreamDecoder is eagerly reading from
     		    //buffer.write(data, 0, numBytesRead);
     		    
     		    Thread.yield();
     		}
    	    
    	    arec.stop();
    	    arec.release();
    	} catch (Exception e){
    	    System.out.println(e.toString());
    	}
    }
 
    public void quit(){
	synchronized(runLock){
	    running = false;
	}
    }
}
