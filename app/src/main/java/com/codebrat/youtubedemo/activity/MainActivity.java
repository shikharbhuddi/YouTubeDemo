package com.codebrat.youtubedemo.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codebrat.youtubedemo.R;
import com.codebrat.youtubedemo.adaptor.ModelAdaptor;
import com.codebrat.youtubedemo.model.Model;
import com.codebrat.youtubedemo.utils.DatabaseHelper;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener{
	private static final String TAG = MainActivity.class.getSimpleName();
	private final int REQ_CODE_SPEECH_INPUT = 9;
	public static final String YOUTUBE_URL = "www.youtube.com/watch?v=";
	public final String API_KEY = "AIzaSyDMGP-4XmUXb0n6VvS7jOxO6v-Ihmxnkgg";
	public String videoId = "5u4G23_OohI";
	public int time = 0;
	public String comment;

	private YouTubePlayerSupportFragment fragment;
	private YouTubePlayer youTubePlayer;
	private MyPlaybackEventListener myPlaybackEventListener;
	private MyUtteranceProgressListener myUtteranceProgressListener;

	private Toolbar toolbar;
	private EditText searchUrlText;
	private ImageButton searchBtn;
	private Button addCommentBtn;
	private TextView commentsHead;
	private TextView errorMsg;
	private TextView clearComments;

	private ListView modelContainer;
	private ModelAdaptor modelAdaptor;
 	private ArrayList<Model> modelArrayList;
	private Model model;
	private DatabaseHelper databaseHelper;

	private TextToSpeech tts;
	private HashMap<String, String> ttsMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		myPlaybackEventListener = new MyPlaybackEventListener();
		myUtteranceProgressListener = new MyUtteranceProgressListener();
		databaseHelper = new DatabaseHelper(MainActivity.this);

		ttsMap = new HashMap<String, String>();
		ttsMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UID");

		initUI();
	}

	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean b) {
		if(null== player) return;
		youTubePlayer = player;
		youTubePlayer.setPlaybackEventListener(myPlaybackEventListener);
		if (!b) {
			player.cueVideo(videoId, time);
		}
	}

	@Override
	public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
		Toast.makeText(this, "Failed to initialize.", Toast.LENGTH_LONG).show();
	}

	private void initUI(){
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragment = new YouTubePlayerSupportFragment();
		fragmentTransaction.add(R.id.youtube_fragment, fragment,
			YouTubePlayerSupportFragment.class.getSimpleName());
		fragmentTransaction.commit();

		if(toolbar!=null){
			searchUrlText = (EditText) toolbar.findViewById(R.id.search_text);
			searchBtn = (ImageButton) toolbar.findViewById(R.id.search_btn);
		}

		searchBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!searchUrlText.getText().toString().trim().isEmpty()) {
					time = 0;
					String url = searchUrlText.getText().toString().trim();
					videoId = url.substring(url.indexOf("?v=") + 3, url.length());
					createYouTubeFragment();

					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(searchUrlText.getWindowToken(), 0);
				}
			}
		});

		addCommentBtn = (Button) findViewById(R.id.add_comment);
		addCommentBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(youTubePlayer!= null && youTubePlayer.isPlaying()) {
					youTubePlayer.pause();
					model = new Model();
					model.setVideoId(videoId);
					model.setTime(youTubePlayer.getCurrentTimeMillis());
					speechToText();
				}
			}
		});

		commentsHead = (TextView) findViewById(R.id.comments_head);
		errorMsg = (TextView) findViewById(R.id.err_msg);
		clearComments = (TextView) findViewById(R.id.clear_comments);

		clearComments.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				databaseHelper.resetModelsTables();
				modelArrayList.clear();
				modelAdaptor.notifyDataSetChanged();
				clearComments.setVisibility(View.GONE);
				errorMsg.setVisibility(View.VISIBLE);
			}
		});

		Typeface face = Typeface.createFromAsset(getAssets(),
			"fonts/Cabin-Regular.ttf");
		searchUrlText.setTypeface(face);
		addCommentBtn.setTypeface(face);
		commentsHead.setTypeface(face);
		clearComments.setTypeface(face);

		face = Typeface.createFromAsset(getAssets(),
			"fonts/Quicksand-Regular.ttf");
		errorMsg.setTypeface(face);

		modelContainer = (ListView) findViewById(R.id.modelContainer);
		modelArrayList = new ArrayList<Model>();
		if(databaseHelper!=null){
			modelArrayList = databaseHelper.getAllModels();
		}
		if(modelArrayList.isEmpty()) {
			errorMsg.setVisibility(View.VISIBLE);
			clearComments.setVisibility(View.GONE);
		}
		modelAdaptor = new ModelAdaptor(MainActivity.this, modelArrayList);
		modelContainer.setAdapter(modelAdaptor);

		modelContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				model = modelArrayList.get(position);
				videoId = model.getVideoId();
				time = model.getTime();
				comment = model.getComment();
				searchUrlText.setText(YOUTUBE_URL + videoId);
				textToSpeech();
			}
		});
	}

	private void createYouTubeFragment(){
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragment = new YouTubePlayerSupportFragment();
		fragmentTransaction.replace(R.id.youtube_fragment, fragment,
			YouTubePlayerSupportFragment.class.getSimpleName());
		fragmentTransaction.commit();
		fragment.initialize(videoId, MainActivity.this);
	}

	private void speechToText(){
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
			RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
			getString(R.string.speech_prompt));
		try {
			startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
		} catch (ActivityNotFoundException a) {
			Toast.makeText(getApplicationContext(),
				getString(R.string.speech_not_supported),
				Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case REQ_CODE_SPEECH_INPUT: {
				if (resultCode == RESULT_OK && null != data) {
					ArrayList<String> result = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
					model.setComment(result.get(0));
					modelArrayList.add(model);
					if(!modelArrayList.isEmpty()) {
						errorMsg.setVisibility(View.GONE);
						clearComments.setVisibility(View.VISIBLE);
					}
					modelAdaptor.notifyDataSetChanged();
					databaseHelper.addModel(model);
				}
				break;
			}
		}
	}

	private void textToSpeech(){
		tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				if (status == TextToSpeech.SUCCESS) {
					int result = tts.setLanguage(Locale.US);
					if (result == TextToSpeech.LANG_MISSING_DATA
						|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
						Log.e("TTS", "This Language is not supported");
					} else {
						if(model!=null)
							tts.speak(model.getComment(), TextToSpeech.QUEUE_FLUSH, ttsMap);
					}
				} else {
					Log.e("TTS", "Initilization Failed!");
				}
			}
		});
		tts.setOnUtteranceProgressListener(myUtteranceProgressListener);
	}

	private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {

		@Override
		public void onBuffering(boolean arg0) {
			Log.d(TAG, "Buffering");
		}

		@Override
		public void onPaused() {
			Log.d(TAG, "Paused");
		}

		@Override
		public void onPlaying() {
			Log.d(TAG, "Playing");
		}

		@Override
		public void onSeekTo(int arg0) {
			Log.d(TAG, "Seeking");
		}

		@Override
		public void onStopped() {
			Log.d(TAG, "Stopped");
		}
	}

	private final class MyUtteranceProgressListener extends UtteranceProgressListener {
		@Override
		public void onStart(String utteranceId) {
			Log.d(TAG, "Start TTS");
		}

		@Override
		public void onDone(String utteranceId) {
			Log.d(TAG, "Stop TTS");
			createYouTubeFragment();
		}

		@Override
		public void onError(String utteranceId) {
			Log.e(TAG, utteranceId);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (tts != null) {
			tts.shutdown();
		}
	}
}
