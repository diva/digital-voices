package com.jarkman.ASKSimpleAndroid;


import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class PlayThread extends Thread{

	public boolean isPlaying = true;
	private byte[] buffer;
	
	public PlayThread( byte[] b )
	{
		buffer = new byte[b.length * 2];
		
		// convert from 8 bit per sample to little-endian 16 bit per sample, IOW 16-bit PCM
		int i, j;
		for(i=0, j =0; i < b.length; i++, j += 2)
		{
		    buffer[j] = 0;
		    buffer[j+1] = b[i];
		}

		
		start();
	}
	
	public void run()
	{

		isPlaying = true;
		
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
	  
	    AudioTrack atrack = new AudioTrack(AudioManager.STREAM_MUSIC,
	    							(int) Encoder.kSamplingFrequency,
	                                AudioFormat.CHANNEL_CONFIGURATION_MONO,
	                                AudioFormat.ENCODING_PCM_16BIT,  // ENCODING_PCM_8BIT sounds very scratchy, so we use 16 bit and double up the data
	                                buffer.length, 
	                                AudioTrack.MODE_STREAM);

	    atrack.setPlaybackRate((int) Encoder.kSamplingFrequency);

       
        
        atrack.play();
      
        try {
                atrack.write(buffer, 0, buffer.length);
        } catch (Exception e) {

                e.printStackTrace();
        }

        
        atrack.stop();
	}
	
	public void stopLoop()
	{
		isPlaying = false;
	}
	
}
