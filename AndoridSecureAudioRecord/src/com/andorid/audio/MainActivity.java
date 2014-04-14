package com.andorid.audio;

import java.io.IOException;
import java.security.GeneralSecurityException;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.andoridsecureaudiorecord.R;

public class MainActivity extends ActionBarActivity {
	Button startRec, stopRec, playBack;
    Boolean recording;
    
	private PcmAudioRecorder par;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        startRec = (Button)findViewById(R.id.startrec);
        stopRec = (Button)findViewById(R.id.stoprec);
        playBack = (Button)findViewById(R.id.playback);

        startRec.setOnClickListener(startRecOnClickListener);
        stopRec.setOnClickListener(stopRecOnClickListener);
        playBack.setOnClickListener(playBackOnClickListener);
        
        enableButtons(false);
	}
	
	private void enableButtons(boolean isRecording) {
		enableButton(R.id.startrec, !isRecording);
		enableButton(R.id.stoprec, isRecording);
	}
	
	private void enableButton(int id, boolean isEnable) {
		((Button) findViewById(id)).setEnabled(isEnable);
	}
	
	OnClickListener startRecOnClickListener   = new OnClickListener() {
		@Override
	    public void onClick(View arg0) {
			enableButton(R.id.startrec, false);
			Handler handler = new Handler(); 
			par = PcmAudioRecorder.getInstanse();
			
			Thread recordThread = new Thread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(MainActivity.this,
							"Inizio registrazione", Toast.LENGTH_SHORT).show();
					recording = true;
					par.prepare();
					par.start();
				}
			
			});
			handler.postDelayed(recordThread, 100);
			enableButtons(true);
			enableButton(R.id.playback, false);
		}
	};

	OnClickListener stopRecOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			recording = false;
			enableButtons(false);
			par.stop();
			par.release();
			enableButton(R.id.playback, true);
		}
	};

	OnClickListener playBackOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Thread playRecordThread = new Thread(new Runnable() {
		  		  @Override
		  		  public void run() {
		  			  try {
						par.playRecord();
					} catch (IllegalStateException | GeneralSecurityException
							| IOException | ClassNotFoundException e) {
						e.printStackTrace();
					}
			      }
			});
			Toast.makeText(MainActivity.this,
					"Inizio riproduzione", Toast.LENGTH_SHORT).show();
			playRecordThread.start();
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
