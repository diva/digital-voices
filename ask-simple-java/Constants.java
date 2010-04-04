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

public interface Constants {
    
  public static final double kLowFrequency = 600; //the lowest frequency used
  public static final double kFrequencyStep = 50; //the distance between frequencies

  public static final int kBytesPerDuration = 1; //how wide is the data stream
  public static final int kBitsPerByte = 8; //unlikely to change, I know

  // Amplitude of each frequency in a frame.
  public static final double kAmplitude = 0.125d; /* (1/8) */

  // Sampling frequency (number of sample values per second)
  public static final double kSamplingFrequency = 22050;

  // Sound duration of encoded byte (in seconds)
  public static final double kDuration = 0.1;

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
