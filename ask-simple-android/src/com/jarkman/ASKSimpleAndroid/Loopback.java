package com.jarkman.ASKSimpleAndroid;

import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

// Simple microphone/speaker loopback test to check Andoid audio APIs are working

public class Loopback extends Thread{

	public boolean isRecording = true;
	
	public void run()
	{
		// from http://www.mail-archive.com/android-developers@googlegroups.com/msg76498.html
		
		isRecording = true;
		
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

        int buffersize = AudioRecord.getMinBufferSize(11025,
        		AudioFormat.CHANNEL_CONFIGURATION_MONO,
        		AudioFormat.ENCODING_PCM_16BIT);

        if( buffersize < 1 )
        {
        	// parameters not supported by hardware, probably
        	isRecording = false;
        	return;
        }
        	
	    AudioRecord arec = new	AudioRecord(MediaRecorder.AudioSource.MIC,
	                                11025,
	                                AudioFormat.CHANNEL_CONFIGURATION_MONO,
	                                AudioFormat.ENCODING_PCM_16BIT,
	                                buffersize);

	    AudioTrack atrack = new AudioTrack(AudioManager.STREAM_MUSIC,
	                                11025,
	                                AudioFormat.CHANNEL_CONFIGURATION_MONO,
	                                AudioFormat.ENCODING_PCM_16BIT,
	                                buffersize, //ba.size(),
	                                AudioTrack.MODE_STREAM);

	    atrack.setPlaybackRate(11025);

        byte[] buffer = new byte[buffersize];
        
        arec.startRecording();
        atrack.play();

        while(isRecording) {
                arec.read(buffer, 0, buffersize);
                try {
                        atrack.write(buffer, 0, buffer.length);
                } catch (Exception e) {

                        e.printStackTrace();
                }
        }

        arec.stop();
        atrack.stop();
	}
	
	public void stopLoop()
	{
		isRecording = false;
	}
	
}
