package com.jonas.CricketAndroid;


/**
 * Copyright 2012 by the authors. All rights reserved.
 *
 * Author: Jonas Michel
 */


import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Encoder.
 * @author jrm
 */
public class Encoder implements Constants {

    /**
     * encodeCricket is the public function of class Encoder.
     * @param input the ArrayList of integers to encode
     * @param output the stream of audio samples representing the input, 
     *               pre- and post-fixed with an hail signal
     */
    public static void encodeCricket(ArrayList<Integer> input, OutputStream output) throws IOException {
    	System.out.println("encodeCricket starts");
    	
    	byte[] zeros = new byte[kSamplesPerDuration];
    	
    	// write out the initial hail sequence
    	output.write(zeros);
    	output.write(Encoder.getHailSequence());
    	
    	// now write the data
    	int info[] = new int[4];
    	for (int i = 0; i < input.size() - 3; i+=4) {
    		for (int j = 0; j < 4; j++)
    			info[j] = input.get(i+j);
    		output.write(Encoder.createPacket(info));
    	}
    	
    	// write out the closing hail sequence
    	output.write(Encoder.getHailSequence());
    	
    	System.out.println("encodeCricket ends");
    }

    /**
     * @return audio samples for a duration of the hail frequency, Constants.kHailFrequency
     */
    private static byte[] getHailSequence(){
	double[] signal = new double[kSamplesPerDuration];
	//add a sinusoid of the hail frequency, amplitude kAmplitude and duration kDuration
	double innerMultiplier = Constants.kHailFrequency * (1 / kSamplingFrequency) * 2 * Math.PI;
	for(int l = 0; l < signal.length; l++){
	  signal[l] = /*kAmplitude **/ Math.cos(innerMultiplier * l); 
	}
	return ArrayUtils.getByteArrayFromDoubleArray(smoothWindow(signal, 0.3));
    }

     /** About smoothwindow.
      * This is a data set in with the following form:
      *
      *   |
      * 1 |  +-------------------+
      *   | /                     \
      *   |/                       \
      *   +--|-------------------|--+---
      *     0.01              0.09  0.1  time
      * 
      * It is used to smooth the edges of the signal in each duration 
      *
      */
     private static double[] smoothWindow(double[] input, double magicScalingNumber){
	 double[] smoothWindow = new double[input.length];
	 double minVal = 0;
	 double maxVal = 0;
	 int peaks = (int)(input.length * 0.1);
	 double steppingValue = 1 / (double)peaks;
	 for (int i=0; i<smoothWindow.length; i++){
	     if (i < peaks){
	       smoothWindow[i] = input[i] * (steppingValue * i) /* / magicScalingNumber*/;
	     } else if ( i > input.length - peaks){
	       smoothWindow[i] = input[i] * (steppingValue * (input.length - i - 1)) /* / magicScalingNumber */;
	     } else {
		 //don't touch the middle values
	       smoothWindow[i] = input[i] /* / magicScalingNumber */;
	     }
 	    if (smoothWindow[i] < minVal){
 		minVal = smoothWindow[i];
 	    }
 	    if(smoothWindow[i] > maxVal){
 		maxVal = smoothWindow[i];
 	    }
	 }
	 return smoothWindow;
     }

    private static double[] smoothWindow(double[] input){
	double magicScalingNumber = 0.8;
	return smoothWindow(input, magicScalingNumber);
    }

    /**
     * This isn't used at the moment, but it does sound nice
     */
    private static double[] blackmanSmoothWindow(double[] input){
 	double magicScalingNumber = 3.5;
 	double[] smoothWindow = new double[input.length];
 	double steppingValue = 2*Math.PI/(input.length -1);
 	double maxVal = 0;
 	double minVal = 0;
 	for(int i=0; i<smoothWindow.length; i++){
 	    smoothWindow[i] = (input[i] * (0.42 - 0.5*Math.cos(steppingValue*i) +
					   0.08*Math.cos(steppingValue*i))) * 3.5;
 	    if(smoothWindow[i] < minVal){
 		minVal = smoothWindow[i];
 	    }
 	    if(smoothWindow[i] > maxVal){
 		maxVal = smoothWindow[i];
 	    }
 	}
 	return smoothWindow;
     }
    
    /**
     * Creates a three-chirp byte array of audio samples.
     * @param info array of four integers to encode to a chirp frame
     * @return byte array of audio samples representing the info integers
     */
    private static byte[] createPacket(int[] info) {
    	ArrayList<Double> packet = new ArrayList<Double>();
    	int packetlength, silencelength, packetsamples, silencesamples;
    	double amplitude;
    	
    	// info[0] determines the phase of this packet
    	int zeros_prefix = (int) Math.round(Constants.kSamplingFrequency * (info[0]-1) * (2 * Constants.kShortSize + 1.5 * Constants.kLongSize)/1000);
    	for (int i = 0; i < zeros_prefix; i++) {
    		packet.add(0.0);
    	}
    	
    	// info[1,2,3] are encoded to chirp (amplitude,length) pairs
    	for (int k = 1; k < 4; k++) {
    		switch (info[k]) {
    		case 1:
    			packetlength = Constants.kShortSize;
    			silencelength = Constants.kLongSize;
    			amplitude = Constants.kBigAmplitude;
    			break;
    		case 2:
    			packetlength = Constants.kShortSize;
    			silencelength = Constants.kLongSize;
    			amplitude = Constants.kSmallAmplitude;
    			break;
    		case 3:
    			packetlength = Constants.kLongSize;
    			silencelength = Constants.kShortSize;
    			amplitude = Constants.kBigAmplitude;
    			break;
    		case 4:
    			packetlength = Constants.kLongSize;
    			silencelength = Constants.kShortSize;
    			amplitude = Constants.kSmallAmplitude;
    			break;
    		default:
    			packetlength = 0;
    			silencelength = 0;
    			amplitude = 0;
    			break;
    		}
    		packetsamples = (int) Math.round((Constants.kSamplingFrequency * packetlength) / 1000);
    		silencesamples = (int) Math.round((Constants.kSamplingFrequency * silencelength) / 1000);
    		
    		double time[] = new double[packetsamples];
    		for (int t = 0; t < packetsamples; t++) {
    			time[t] = t * (((double) packetlength) / (packetsamples*1000));
    		}
    		
    		double smallpacket[] = new double[packetsamples];
    		for (int t = 0; t < packetsamples; t++) {
    			smallpacket[t] = amplitude * Math.sin(2 * Math.PI * time[t] * Constants.kFrequency);
    		}
    		
    		for (int i = 0; i < silencesamples; i++) {
    			packet.add(0.0);
    		}
    		
    		double smoothsmallpacket[] = smoothWindow(smallpacket);
    		for (int i = 0; i < smoothsmallpacket.length; i++) {
    			packet.add(smoothsmallpacket[i]);
    		}
    	}
    	
    	int zeros_suffix = (Constants.kSamplesPerFrame - packet.size());
    	for (int i = 0; i < zeros_suffix; i++) {
    		packet.add(0.0);
    	}
    	
    	double signal[] = new double[packet.size()];
    	for (int i = 0; i < signal.length; i++) {
    		signal[i] = packet.get(i);
    	}
    	
    	// convert to bytes
    	return ArrayUtils.getByteArrayFromDoubleArray(signal);
    }
   
}
