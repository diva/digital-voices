package com.jonas.CricketAndroid;

/*
 * This is an Android port of the Digital Voices cricket codec,
 * https://github.com/diva/digital-voices/tree/master/cricket
 *  
 * See also http://github.com/diva/digital-voices
 * 
 * Ported April 2012 by jonasrmichel@mail.utexas.edu
 * borrowing heavily from Richard Jarkman's port of the
 * Digital Voices B-ASK pentatonic codec.
 * 
 * Usage notes:
 * 
 *  - Type something in and hit Chirp to have it encoded and chirped
 *  
 *  - Hit Listen to start the listen process
 *  	A status message will appear below the Listen button
 *  	When input is decoded, it'll show up below that
 *  
 */


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Main extends Activity {
    /** Called when the activity is first created. */
	 //Loopback l = null;
	 
	MicrophoneListener microphoneListener = null;
	StreamDecoder sDecoder = null;
	ByteArrayOutputStream decodedStream = new ByteArrayOutputStream();
	Timer refreshTimer = null;
	
	Handler mHandler = new Handler();
	
	TextView textListen;
	TextView textStatus;
	
    Uri mCreateDataUri = null;
    String mCreateDataType = null;
    String mCreateDataExtraText = null;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        textStatus = (TextView) findViewById(R.id.TextStatus);
        textListen = (TextView) findViewById(R.id.TextListen);
               
        Button t = (Button) findViewById(R.id.ButtonPlay);
    	t.setOnClickListener(mPlayListener);
    	
        t = (Button) findViewById(R.id.ButtonListen);
    	t.setOnClickListener(mListenListener);
    	
        final Intent intent = getIntent();
        final String action = intent.getAction();
        if (Intent.ACTION_SEND.equals(action)) 
        {
          
            mCreateDataUri = intent.getData();
            mCreateDataType = intent.getType();

            if( mCreateDataUri == null )
            {
            	mCreateDataUri = intent.getParcelableExtra( Intent.EXTRA_STREAM );
            
            	
            }

            mCreateDataExtraText = intent.getStringExtra( Intent.EXTRA_TEXT );
            
            if( mCreateDataUri == null )
            	mCreateDataType = null;
            
            // The new entry was created, so assume all will end well and
            // set the result to be returned.
            setResult(RESULT_OK, (new Intent()).setAction(null));
        }
    	
    }

    
	View.OnClickListener mPlayListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            EditText e = (EditText) findViewById(R.id.EditTextToPlay);
            String s = e.getText().toString().toLowerCase(); // just use lower case
            
            
            
            
            perform( s );
            
        }
    };
    
    
	View.OnClickListener mListenListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
        	
        	
        	if( microphoneListener == null )
            {
        		
        		listen();
        		((Button)v).setText("Stop listening");
            }
        	else
        	{
        		
        		
        		stopListening();
        		((Button)v).setText("Listen");
        	}
            
        }
    };
    
   
    
    
	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		
		//if( l != null )
		//	l.stopLoop();
		
		super.onPause();
		
		if( refreshTimer != null )
		{
			refreshTimer.cancel();
			refreshTimer = null;
		}
		
		stopListening();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() 
	{
		super.onResume();
		
		String sent = null;
        
        if( mCreateDataExtraText != null )
        {
        	sent = mCreateDataExtraText;
        }
        else if( mCreateDataType != null && mCreateDataType.startsWith("text/") )
        {
        	//read the URI into a string
        	
        	byte[] b = readDataFromUri( this.mCreateDataUri );
        	if( b != null )
        		sent = new String( b );
        	 
        		
        }	
        
        if( sent != null )
        {
        	EditText e = (EditText) findViewById(R.id.EditTextToPlay);
        	e.setText(sent);
        }
        
		
		refreshTimer = new Timer();

		
		
		refreshTimer.schedule(
	    		new TimerTask()
		        {
		            @Override
		            public void run()
		            {
	
		        		mHandler.post(new Runnable() // have to do this on the UI thread
		        		{
		        		      public void run()
		        		      {
		        		    	  updateResults();
	
	
		        		      }
		        		    });
		                 
		            }
						}, 500, 500);
	
	}
    
	private void updateResults()
	{
		if( microphoneListener != null ) 
	    { 	
	    	textListen.setText(decodedStream.toString());
	    	textStatus.setText(sDecoder.getStatusString());	
	    }
		else
		{
			textStatus.setText("");
		}
		
	}
	private void listen()
	{
		stopListening();
		
		decodedStream.reset();
		
	  //the StreamDecoder uses the Decoder to decode samples put in its AudioBuffer
	  // StreamDecoder starts a thread
	  sDecoder = new StreamDecoder( decodedStream ); 

	  //the MicrophoneListener feeds the microphone samples into the AudioBuffer
	  // MicrophoneListener starts a thread
	  
	  microphoneListener = new MicrophoneListener(sDecoder.getAudioBuffer());
	  System.out.println("Listening");
	}  
	  
	private void stopListening()
	{
		if( microphoneListener != null )
			microphoneListener.quit();
		
		microphoneListener = null;
		
		if( sDecoder != null )
			sDecoder.quit();
		
		sDecoder = null;
	}
	
	private void perform( String input )
	{
		  
		try 
		{
		    
		      //try to play the file
		      System.out.println("Performing " + input);
//		      AudioUtils.performArray(input.getBytes());
		      AudioUtils.performString(input);
		} 
		    
		catch (Exception e){
		    System.out.println("Could not encode " + input + " because of " + e);
		  }
		  
	}
	
	/*
	private void encode( String inputFile, String outputFile )
	{
	
	  try 
	  {
 
	      //There was an output file specified, so we should write the wav
	      System.out.println("Encoding " + inputFile);
	      AudioUtils.encodeFileToWav(new File(inputFile), new File(outputFile));
	    
	  } 
	  catch (Exception e)
	  {
	    System.out.println("Could not encode " + inputFile + " because of " + e);
	  }
		
	}
	*/
	
	 private byte[] readDataFromUri( Uri uri )
	    {
	    	byte[] buffer = null;
	    	
		    try 
			{
				InputStream stream = getContentResolver().openInputStream(uri) ;
								
		        int bytesAvailable = stream.available();
		        //int maxBufferSize = 1024;
		        int bufferSize = bytesAvailable; //Math.min(bytesAvailable, maxBufferSize);
		        int totalRead = 0;
		        buffer = new byte[bufferSize];
		
		        // read file and write it into form...
		        int bytesRead = stream.read(buffer, 0, bufferSize);
		        while (bytesRead > 0)
		        {
		                bytesRead = stream.read(buffer, totalRead, bufferSize);
		                totalRead += bytesRead;
		        } 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
		
		return buffer;
	    }
    
}