/**
 * Copyright 2002 by the authors. All rights reserved.
 *
 * Author: Cristina V Lopes (crista at tagide dot com)
 *

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
 * <p>This class gives example uses of the digital voices code.</p>
 * <ul>
 * <li> usage: dv.Main -hardware : displays audio hardware info
 * <li> usage: dv.Main -decode <file.wav> : decodes the wav file
 * <li> usage: dv.Main -listen : listens on the microphone for audio bits
 * <li> usage: dv.Main inputFile.txt : plays inputFile
 * <li> usage: dv.Main input.txt output.wav : encodes input.txt into output.wav
 * </ul>
 *
 * @author CVL
 */
public class Main {

    public static void main(String[] args){
	//if there are no arguments or -h or -?, print usage and exit
	if(args.length < 1  
	   || "-h".equals(args[0]) 
	   || "-?".equals(args[0])){
	    printUsage();
	    System.exit(0);
	}

	//reads the microphone input in real time and decodes it to System.out
 	if("-listen".equals(args[0])){
	  //the StreamDecoder uses the codec.Decoder to decode samples put in its AudioBuffer
	  // StreamDecoder starts a thread
	  StreamDecoder sDecoder = new StreamDecoder(System.out);

	  //the MicrophoneListener feeds the microphone samples into the AudioBuffer
	  // MicrophoneListener starts a thread
	  MicrophoneListener listener = new MicrophoneListener(sDecoder.getAudioBuffer());
	  System.out.println("Listening");

	  // The main thread does nothing more, just waits for the others to die
	}
	else if("-decode".equals(args[0])) {

	  //decodes the file named in args[1] and prints it to System.out
	  
	  //we must have a file name to decode
	  if(args.length < 2){
	    printUsage();
	    System.exit(0);
	  }
	    
	  try {
	    File inputFile = new File(args[1]);
	    if(inputFile.exists()){
	      //now decode the inputFile to System.out
	      AudioUtils.decodeWavFile(inputFile, System.out);
	    } else {
	      System.out.println("Cannot find file " + args[1]);
	    }
	  } catch (javax.sound.sampled.UnsupportedAudioFileException e){
	    System.out.println("Error reading " + args[1] + ":" + e);
	  } catch (IOException e){
	    System.out.println("IO Error reading " + args[1] + ":" + e);
	  }
	}
	else if("-hardware".equals(args[0])) {
	  //this is a little utility to show what hardware the JVM finds
	  
	  AudioUtils.displayMixerInfo();
	  System.exit(0);
	}
	  
	else if("-record".equals(args[0])) {
	  AudioUtils.recordToFile(new File(args[1]), 100);
	  System.exit(0);
	}

	else {
	  //Try to perform or encode the file named in args[0]
	  String inputFile = args[0];
	  String outputFile = null;
	  if(args.length > 1 && args[1].length() > 0){
	    //the existence of a second argument indicates that we should encode to args[1]
	    outputFile = args[1];
	  }
	  try {
	    if(outputFile == null){ 
	      //try to play the file
	      System.out.println("Performing " + args[0]);
	      AudioUtils.performFile(new File(inputFile));
	    } else { 
	      //There was an output file specified, so we should write the wav
	      System.out.println("Encoding " + args[0]);
	      AudioUtils.encodeFileToWav(new File(inputFile), new File(outputFile));
	    }
	  } catch (Exception e){
	    System.out.println("Could not encode " + inputFile + " because of " + e);
	  }
	  System.exit(0);
	}
    }

    /**
     * Prints the help text to System.out
     */
    public static void printUsage(){
	System.out.println("usage: dv.Main -hardware : displays audio hardware info");
	System.out.println("usage: dv.Main -decode <file.wav> : decodes the wav file");
	System.out.println("usage: dv.Main -listen : listens on the microphone for audio bits");
	System.out.println("usage: dv.Main <inputFile.txt> : plays the encoded file");
	System.out.println("usage: dv.Main <input.txt> <output.wav> : encodes input.txt into output.wav");
    }

}
