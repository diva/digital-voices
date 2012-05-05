
package com.jonas.CricketAndroid;

/**
 * Copyright 2012 by the authors. All rights reserved.
 *
 * Author: Jonas Michel
 */


import java.util.ArrayList;

/**
 * This class contains the signal processing functions.
 *
 * @author jrm
 */
public class Decoder implements Constants {

    /**
     * @param signal the audio samples to search
     * @param signalStrengths this will be filled in with the strengths for each frequency (NOTE THIS SIDE EFFECT)
     * @param granularity a correlation will be determined every granularity samples (lower is slower)
     * @return the index in signal of the key sequence, or -1 if it wasn't found (in which case signalStrengths is trashed)
     */
    public static int findKeySequence(byte[] signal, double[] signalStrength, int granularity){
	int maxCorrelationIndex = -1;
	double maxCorrelation = -1;
	double minSignal = 0.003;
	//double acceptedSignal = 0.01;
	int i=0;
	for(i = 0; i <= signal.length - kSamplesPerDuration; i += granularity){ 
	    //test the correlation
	    byte[] partialSignal = ArrayUtils.subarray(signal, i, kSamplesPerDuration);
	    double corr = complexDetect(partialSignal, Constants.kHailFrequency) /* * 4 */;
	    //	    System.out.println("Correlation at " + i + ":" + corr);
	    if (corr > maxCorrelation){
		maxCorrelation = corr;
		maxCorrelationIndex = i;
	    }
	    if(granularity <= 0){
		break;
	    }
	}

	//System.out.println("Searched to index:" + i);
	if (maxCorrelation < Constants.kKeyDetectionThreshold && maxCorrelation > -1){
	    //System.out.println("Best Correlation:" + maxCorrelation);
	    maxCorrelationIndex = -1;
	}
	
	if (maxCorrelation >= Constants.kKeyDetectionThreshold)
		System.out.println("gotkey");
	//if(maxCorrelationIndex >= 0){
	    //System.out.println("\r\nMax Correlation:" + maxCorrelation + " index:" + maxCorrelationIndex);
	    //System.out.println("signal.length:" + signal.length);
	    //getKeySignalStrengths(ArrayUtils.subarray(signal, maxCorrelationIndex + kSamplesPerDuration, 
	    //					      kSamplesPerDuration * 2),
	    //		  signalStrengths);
	//}

	signalStrength[0] = maxCorrelation;
	
	return maxCorrelationIndex;
    }

    /**
     * 
     * @param signalStrength the signal strength of the chirp frequency
     * @param samples the audio samples to be decoded
     * @return array list of decoded integers
     */
    public static ArrayList<Integer> decodeFrame(double signalStrength, byte[] samples) {
    	ArrayList<Integer> output = new ArrayList<Integer>();
    	int winner = 0;
    	double maxpower = 0;
    	double signal[] = ArrayUtils.getDoubleArrayfromByteArray(samples);
    	
    	// normalize samples <wrt> signalStrength
    	Decoder.normalize(signal, signalStrength);
    	
    	// first symbol
    	for (int k = 1; k <=4; k++) {
    		int beginning = (int) Math.round(Constants.kSamplingFrequency * (k-1) * (2*Constants.kPacketLength + 1.5*Constants.kSilenceLength) / 1000);
    		if (beginning + Constants.kThreePacketSamples > signal.length) {
    			// pad with enough zeros to make a frame
    			double[] signal_padded = new double[beginning + Constants.kThreePacketSamples];
    			// copy samples into padded array
    			System.arraycopy(signal, 0, signal_padded, 0, signal.length);
    			signal = signal_padded;
    		}
    		double power = Decoder.norm(ArrayUtils.getSlice(signal, beginning, (beginning + Constants.kThreePacketSamples)));
    		if (power > maxpower) {
    			maxpower = power;
    			winner = k;
    		}
    	}
    	output.add(winner);
    	
    	// second symbol, we need the position of the three packets
    	double amplitude, width;
    	int beginning = (int) Math.round(Constants.kSamplingFrequency * (winner-1) * (2*Constants.kPacketLength + 1.5*Constants.kSilenceLength) / 1000);
    	for (int jj = 1; jj < 4; jj++) {
    		double[] currframe = ArrayUtils.getSlice(signal, (beginning + Constants.kSilencePacketSamples*(jj-1)), (beginning + Constants.kSilencePacketSamples*jj));
    		amplitude = Decoder.absMax(currframe);
    		Decoder.normalize(currframe, amplitude);
    		width = Decoder.norm(currframe);
    		if (width < Constants.kThreshWidth) {
    			if (amplitude < Constants.kThreshAmplitude)
    				output.add(2);
    			else
    				output.add(1);
    		} else {
    			if (amplitude < Constants.kThreshAmplitude)
    				output.add(4);
    			else
    				output.add(3);
    		}
    	}
    	return output;
    }

    // original implementation from ask-simple-java :
    private static double complexDetect(byte[] signal, double frequency){
    	double realSum = 0;
    	double imaginarySum = 0;
    	double u = 2 * Math.PI * frequency / kSamplingFrequency;
    	// y = e^(ju) = cos(u) + j * sin(u) 

    	for(int i = 0; i < signal.length; i++){
    	  //System.out.println("signal[" +i +"]: " +signal[i] + "; convert: " + (signal[i])/(float)Constants.kFloatToByteShift);
    	    realSum = realSum + (Math.cos(i * u) * (signal[i]/(float)Constants.kFloatToByteShift));
    	    imaginarySum = imaginarySum + (Math.sin(i * u) * (signal[i]/(float)Constants.kFloatToByteShift));
    	}
    	//System.out.println("realSum=" + realSum + "; imSum=" + imaginarySum);
     	double realAve = realSum/signal.length;
     	double imaginaryAve = imaginarySum/signal.length;
//       	System.out.println("u:" + u + " realAve:" + realAve + " imaginaryAve:" + imaginaryAve 
//       			   + " \r\nfrequency:" + frequency + " signal.length:" + signal.length
//       			   + " realSum:" + realSum + " imaginarySum:" + imaginarySum 
//       			   + "signal[100]:" + (signal[100]/(float)Constants.kFloatToByteShift));
    	// return the abs ( realAve + imaginaryAve * i ) which equals sqrt( realAve^2 + imaginaryAve^2)
    	return Math.sqrt( (realAve * realAve) + (imaginaryAve * imaginaryAve) );
        }
    
    private static void normalize(double[] signal, double value) {
    	// normalize samples <wrt> value
    	for (int i = 0; i < signal.length; i++) {
    		signal[i] = signal[i] / value;
    	}
    }
    
    private static double norm(double[] signal) {
    	double sum = 0;
    	// calculate 2-norm
    	for (int i = 0; i < signal.length; i++) {
    		//sum += Math.pow(signal[i], 2);
    		sum += Math.pow(signal[i], 2);
    	}
    	return Math.sqrt(sum);
    }
    
    private static double absMax(double[] signal) {
    	double abs, maxvalue = 0;
    	for (int i = 0; i < signal.length; i++) {
    		abs = Math.abs(signal[i]);
    		if (abs > maxvalue)
    			maxvalue = abs;
    	}
    	return maxvalue;
    }
    
}
