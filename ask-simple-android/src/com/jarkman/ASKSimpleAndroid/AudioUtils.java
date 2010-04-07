package com.jarkman.ASKSimpleAndroid;

/**
 * Copyright 2002 by the authors. All rights reserved.
 *
 * Author: Cristina V Lopes
 */



import java.io.*;
import java.util.Date;


/**
 * 
 * @author CVL
 */

public class AudioUtils {

	/*
    //the default format for reading and writing audio information
    public static AudioFormat kDefaultFormat = new AudioFormat((float) Encoder.kSamplingFrequency,
							       (int) 8, (int) 1, true, false);
	 */
    
	/*
    public static void decodeWavFile(File inputFile, OutputStream out)
	throws UnsupportedAudioFileException,
	IOException {
	StreamDecoder sDecoder = new StreamDecoder(out);
	AudioBuffer aBuffer = sDecoder.getAudioBuffer();

	AudioInputStream audioInputStream = 
	    AudioSystem.getAudioInputStream(kDefaultFormat, 
					    AudioSystem.getAudioInputStream(inputFile));
	int bytesPerFrame = audioInputStream.getFormat().getFrameSize();
	// Set an arbitrary buffer size of 1024 frames.
	int numBytes = 1024 * bytesPerFrame; 
	byte[] audioBytes = new byte[numBytes];
	int numBytesRead = 0;
	// Try to read numBytes bytes from the file and write it to the buffer
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	while ((numBytesRead = audioInputStream.read(audioBytes)) != -1) {
	  
	    aBuffer.write(audioBytes, 0, numBytesRead);
	}
    }
	 */
	
	/*
    public static void writeWav(File file, byte[] data, AudioFormat format)
	throws IllegalArgumentException,
	IOException {
	ByteArrayInputStream bais = new ByteArrayInputStream(data);
	AudioInputStream ais = new AudioInputStream(bais, format, data.length);
	AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
    }
    */
	
    /*
    public static void displayMixerInfo(){
	Mixer.Info[] mInfos = AudioSystem.getMixerInfo();
	if(mInfos == null){
	    System.out.println("No Mixers found");
	    return;
	}

	for(int i=0; i < mInfos.length; i++){
	    System.out.println("Mixer Info: " + mInfos[i]);
	    Mixer mixer = AudioSystem.getMixer(mInfos[i]);
	    Line.Info[] lines = mixer.getSourceLineInfo();
	    for(int j = 0; j < lines.length; j++){
		System.out.println("\tSource: " + lines[j]);
	    }
	    lines = mixer.getTargetLineInfo();
	    for(int j = 0; j < lines.length; j++){
		System.out.println("\tTarget: " + lines[j]);
	    }
	}
    }
	*/
	
	/*
    public static void displayAudioFileTypes(){
	AudioFileFormat.Type[] types = AudioSystem.getAudioFileTypes();
	for(int i=0; i < types.length; i++){
	    System.out.println("Audio File Type:" + types[i].toString());
	}
    }
	*/
	
	/*
    //This never returns, which is kind of lame.
    // NOT USED!! - replaced by MicrophoneListener.run()
    public static void listenToMicrophone(AudioBuffer buff){
	try {
	    int buffSize = 4096;
	    TargetDataLine line = getTargetDataLine(kDefaultFormat);
	    line.open(kDefaultFormat, buffSize);

	    ByteArrayOutputStream out  = new ByteArrayOutputStream();
	    int numBytesRead;
	    byte[] data = new byte[line.getBufferSize() / 5];
	    line.start();
	    while(true){
		numBytesRead =  line.read(data, 0, data.length);
		buff.write(data, 0, numBytesRead);
	    }
	    
	} catch (Exception e){
	    System.out.println(e.toString());
	}
    }
	*/
	
	/*
    public static void recordToFile(File file, int length){
	try {
	    int buffSize = 4096;
	    TargetDataLine line = getTargetDataLine(kDefaultFormat);
	    line.open(kDefaultFormat, buffSize);

	    ByteArrayOutputStream out  = new ByteArrayOutputStream();
	    int numBytesRead;
	    byte[] data = new byte[line.getBufferSize() / 5];
	    line.start();
	    for(int i=0; i < length; i++) {
		numBytesRead =  line.read(data, 0, data.length);
		out.write(data, 0, numBytesRead);
	    }
	    line.drain();
	    line.stop();
	    line.close();
	    
	    writeWav(file, out.toByteArray(), kDefaultFormat);

	} catch (Exception e){
	    System.out.println(e.toString());
	}
    }
	*/
	
	/*
    public static TargetDataLine getTargetDataLine(AudioFormat format)
	throws LineUnavailableException {
	DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
	if (!AudioSystem.isLineSupported(info)) {
	    throw new LineUnavailableException();
	}	
	return (TargetDataLine) AudioSystem.getLine(info);
    }

    public static SourceDataLine getSourceDataLine(AudioFormat format)
	throws LineUnavailableException {
	DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
	if (!AudioSystem.isLineSupported(info)) {
	    throw new LineUnavailableException();
	}
	return (SourceDataLine) AudioSystem.getLine(info);
    }
	*/
	
	
    public static void encodeFileToWav(File inputFile, File outputFile)
	throws IOException {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	Encoder.encodeStream(new FileInputStream(inputFile), baos);
	// patched out for Android
	// writeWav(outputFile, baos.toByteArray());
    }
    
	

    
    public static void performData(byte[] data)
	throws IOException {

    	PlayThread p = new PlayThread( data );
	
	
    }

    public static void performFile(File file) 
	throws IOException {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	Encoder.encodeStream(new FileInputStream(file), baos);
	performData(baos.toByteArray());
    }
    
    public static void performArray(byte[] array) 
	throws IOException {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	Encoder.encodeStream(new ByteArrayInputStream(array), baos);
	performData(baos.toByteArray());
    }
}
