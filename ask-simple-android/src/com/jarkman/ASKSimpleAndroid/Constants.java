package com.jarkman.ASKSimpleAndroid;


/**
 * Copyright 2002 by the authors. All rights reserved.
 *
 * Authir: Cristina V Lopes
 */

public interface Constants {
    
  public static final double kLowFrequency = 600; //the lowest frequency used
  public static final double kFrequencyStep = 50; //the distance between frequencies

  public static final int kBytesPerDuration = 1; //how wide is the data stream 
  								// (rps - that is, the pre-encoding data stream, not the audio)
  								// (rps - kFrequencies must be this long)
  
  public static final int kBitsPerByte = 8; //unlikely to change, I know

  // Amplitude of each frequency in a frame.
  public static final double kAmplitude = 0.125d; /* (1/8) */

  // Sampling frequency (number of sample values per second)
  public static final double kSamplingFrequency = 11025; //rps - reduced to 11025 from 22050 
  		// to enable the decoder to keep up with the audio on Motorola CLIQ  

  // Sound duration of encoded byte (in seconds)
  public static final double kDuration = 0.2; // rps - increased from 0.1 to improve reliability on Android 

  // Number of samples per duration
  public static final int kSamplesPerDuration = (int)(kSamplingFrequency * kDuration);

  //This is used to convert the floats of the encoding to the bytes of the audio
  public static final int kFloatToByteShift = 128;

  // The length, in durations, of the key sequence
  public static final int kDurationsPerKey = 3; 

  //The frequency used in the initial hail of the key
  public static final int kHailFrequency = 3000;

  //The frequencies we use for each of the 8 bits
  public static final int[] kFrequencies = {1000,                       //1000
					    (int)(1000 * (float)27/24), //1125
					    (int)(1000 * (float)30/24), //1250
					    (int)(1000 * (float)36/24), //1500 
					    (int)(1000 * (float)40/24), //1666
					    (int)(1000 * (float)48/24), //2000
					    (int)(1000 * (float)54/24), //2250
					    (int)(1000 * (float)60/24)};//2500
}
