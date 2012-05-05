package com.jonas.CricketAndroid;

/**
 * Copyright 2012 by the authors. All rights reserved.
 *
 * Author: Jonas Michel
 */

public interface Constants {
    
  public static final int kBitsPerByte = 8; //unlikely to change, I know

  // Sampling frequency (number of sample values per second)
  public static final double kSamplingFrequency = 22050;  

  // Sound duration of encoded byte (in seconds)
  public static final double kDuration = 0.2; // increased from 0.1 to improve reliability on Android 

  // Number of samples per duration
  public static final int kSamplesPerDuration = (int)(kSamplingFrequency * kDuration);

  //This is used to convert the floats of the encoding to the bytes of the audio
  public static final int kFloatToByteShift = 128;

  // The length, in durations, of the key sequence
  public static final int kDurationsPerKey = 3; 

  //The frequency used in the initial hail of the key
  public static final int kHailFrequency = 3000;

  // The detection threshold for a hail key
  public static final double kKeyDetectionThreshold = 0.20;
  
  // Hail detection granularities
  public static final int kKeyDetectionGranularityCoarse = 400;
  public static final int kKeyDetectionGranularityFine = 20;
  
  // The chirping frequency
  public static final int kFrequency = 4184;
  
  // The length in milliseconds of a frame
  public static final int kFrameLength = 420; // ms
  
  // The number of samples in a frame
  public static final int kSamplesPerFrame = (int) Math.round((kSamplingFrequency * kFrameLength) / 1000);
  
  // The length (width) in milliseconds of "short" and "long" chirps
  public static final int kShortSize = (int) Math.round(15); // ms
  public static final int kLongSize = (int) Math.round(24*1.5); // ms
  
  // Aliases for chirp lengths
  public static final int kPacketLength = kShortSize; // ms
  public static final int kSilenceLength = kLongSize; // ms
  
  // The amplitudes of "big" and "small" chirps
  public static final double kBigAmplitude = 1.0;
  public static final double kSmallAmplitude = 0.25;
  
  // Some heavily used convenience constants
  public static final int kSilencePacketLength = (kShortSize + kLongSize);
  public static final int kSilencePacketSamples = (int) Math.round((kSamplingFrequency * kSilencePacketLength) / 1000);
  public static final int kThreePacketSamples = (int) Math.round((3 * kSamplingFrequency * kSilencePacketLength) / 1000);
  
  // Chirp decoding thresholds (these may need to be tuned to fit your smartphone)
  public static final double kThreshWidth = 16.0;
  public static final double kThreshAmplitude = 1.50;
}
