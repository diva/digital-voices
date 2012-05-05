package com.jonas.CricketAndroid;

/**
 * Copyright 2012 by the authors. All rights reserved.
 *
 * Author: Cristina V Lopes
 * (Modified by Jonas Michel, 2012)
 */

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;


/**
 * This starts a Thread which decodes data in an AudioBuffer and writes it to an
 * OutputStream. StreamDecoder holds the buffer where the MicrophoneListener
 * puts bytes.
 * 
 * @author CVL
 */
public class StreamDecoder implements Runnable {

	public static String kThreadName = "StreamDecoder";

	private Thread myThread = null;
	private Object runLock = new Object();
	private boolean running = false;

	private AudioBuffer buffer = new AudioBuffer(); // THE buffer where bytes
													// are being put
	private OutputStream out = null;
	boolean hasKey = false;
	
	private ArrayList<Integer> huffsequence;

	/**
	 * This creates and starts the decoding Thread
	 * 
	 * @param _out the OutputStream which will receive the decoded data
	 */
	public StreamDecoder(OutputStream _out) {
		out = _out;
		huffsequence = new ArrayList<Integer>();
		myThread = new Thread(this, kThreadName);
		myThread.start();
	}

	public String getStatusString() {
		String s = "";

		int backlog = (int) ((1000 * buffer.size()) / Constants.kSamplingFrequency);

		if (backlog > 0)
			s += "Backlog: " + backlog + " mS ";

		if (hasKey)
			s += "Found key sequence ";

		return s;
	}

	public AudioBuffer getAudioBuffer() {
		return buffer;
	}

	public void run() {
		synchronized (runLock) {
			running = true;
		}

		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

		//int durationsToRead = Constants.kDurationsPerKey;
		int deletedSamples = 0;

		double signalStrength_begin[] = new double[1];
		double signalStrength_end[] = new double[1];
		boolean notEnoughSamples = true;
		//byte samples[] = null;
		
		byte samples[] = null;

		hasKey = false;

		while (running) {
			notEnoughSamples = true;
			while (notEnoughSamples) {
				// grab enough samples to make a key
				samples = buffer.read(Constants.kSamplesPerFrame);
				
				if (samples != null)
					notEnoughSamples = false;
				else
					Thread.yield();
			}

			/* START DECODING */
			if (hasKey) {
				int eot = Decoder.findKeySequence(ArrayUtils.subarray(samples, 0, Constants.kSamplesPerDuration), signalStrength_end, Constants.kKeyDetectionGranularityFine);
				if (eot > -1) {
					try{
						// delete these samples
						buffer.delete(eot + Constants.kSamplesPerDuration);
						deletedSamples += eot + Constants.kSamplesPerDuration;
						
						// decode the huffman sequence
						String decoded = Huffman.huffdecode(huffsequence) + "\n";
						huffsequence.clear();
						
						// display the text
						out.write(decoded.getBytes());
					} catch (IOException e) { 
					}
					
					// reset
					hasKey = false;
					continue;
				}
				
				// grab enough samples to make a frame
				samples = buffer.read(Constants.kSamplesPerFrame);
				huffsequence.addAll(Decoder.decodeFrame(signalStrength_begin[0], samples));
				
				// delete these samples
				try {
					buffer.delete(Constants.kSamplesPerFrame);
					deletedSamples += Constants.kSamplesPerFrame;
				} catch (IOException e) { 
				}

				try {
					// this provides the audio sampling mechanism a chance to maintain continuity
					Thread.sleep(10);
				} catch (InterruptedException e) {
					System.out.println("Stream Decoding thread interrupted:" + e);
					break;
				}
				continue;
			}
			/* END DECODING */

			// we don't have the key, so we are in key detection mode from this point on

			// System.out.println("Search Start: " + deletedSamples + " End: " +
			// (deletedSamples + samples.length));
			// System.out.println("Search Time: " + ((float)deletedSamples /
			// Constants.kSamplingFrequency) + " End: "
			// + ((float)(deletedSamples + samples.length) /
			// Constants.kSamplingFrequency));
			int startIndex = Decoder.findKeySequence(samples, signalStrength_begin, Constants.kKeyDetectionGranularityCoarse);
			if (startIndex > -1) {
				// found key using coarse detection
				try {
					buffer.delete(startIndex);
				} catch (IOException e) {
				}
				deletedSamples += startIndex;
				
				// now find key using fine detection
				notEnoughSamples = true;
				while (notEnoughSamples) {
					samples= buffer.read(Constants.kSamplesPerFrame);
					if (samples != null)
						notEnoughSamples = false;
					else
						Thread.yield();
				}
				startIndex = Decoder.findKeySequence(samples, signalStrength_begin, Constants.kKeyDetectionGranularityFine);
				
				// found key using fine detection
				try {
					buffer.delete(startIndex + Constants.kSamplesPerDuration);
				} catch (IOException e) {
				}
				deletedSamples += startIndex;
				
				hasKey = true;
			} else {
				try {
					buffer.delete(Constants.kSamplesPerDuration);
					deletedSamples += (Constants.kSamplesPerDuration);
				} catch (IOException e) { 
				}
			}
		}
	}

	public void quit() {
		synchronized (runLock) {
			running = false;
		}
	}
}
