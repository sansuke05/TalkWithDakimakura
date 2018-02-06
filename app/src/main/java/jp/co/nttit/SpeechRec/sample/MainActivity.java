
package jp.co.nttit.SpeechRec.sample;

import jp.co.nttit.SpeechRec.sample.R;

import android.app.LoaderManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Loader;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
//import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<JSONObject>,
	Runnable, View.OnClickListener {

	private Button buttonStart;
	//private RadioButton radioHigh;
	//private RadioButton radioLow;
	private String mResultString = "";
	private String mResponse = "";

	private MediaPlayer mMediaPlayer;
	private int sound_id;

	private final String PUSHED = "1";

	/** Bluetooth関連 -------------------------------------------------------------------------- */
	/* tag */
	private static final String TAG = "Bluetooth Main";

	/* Bluetooth Adapter */
	private BluetoothAdapter mAdapter;

	/* Bluetoothデバイス */
	private BluetoothDevice mDevice;

	/* Bluetooth UUID(固定) */
	private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	/* デバイス名 環境に合わせて変更*/
	private final String DEVICE_NAME = "RNBT-2FCB";

	/* Soket */
	private BluetoothSocket mSocket;

	/* Thread */
	private Thread mThread;

	/* Threadの状態を表す */
	private boolean isRunning;

	/** 接続ボタン. */
	private Button connectButton;

	/** ステータス. */
	private TextView mStatusTextView;

	/** Action(ステータス表示). */
	private static final int VIEW_STATUS = 0;

	/** Action(取得文字列). */
	private static final int VIEW_INPUT = 1;

	/** Connect確認用フラグ */
	private boolean connectFlg = false;

	/** BluetoothのOutputStream. */
	OutputStream mmOutputStream = null;

	/** ------------------------------------------------------------------------------------- */
	/** 音声認識アクティビティのリクエストID */
	private static final int RECOGNIZE_ACTIVITY_REQUEST_ID = 1;


	//音声ファイル再生
	public void speak(){
		Resources res = getResources();
		int mediaId = res.getIdentifier(mResponse, "raw", getPackageName());

		mMediaPlayer = MediaPlayer.create(this, mediaId);
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mMediaPlayer.start();
	}

	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.main);

		mStatusTextView = (TextView)findViewById(R.id.textView);
		connectButton = (Button)findViewById(R.id.button);

		connectButton.setOnClickListener(this);

		// Bluetoothのデバイス名を取得
		// デバイス名は、RNBT-XXXXになるため、
		// DVICE_NAMEでデバイス名を定義
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mStatusTextView.setText("SearchDevice");
		Set< BluetoothDevice > devices = mAdapter.getBondedDevices();
		for ( BluetoothDevice device : devices){

			if(device.getName().equals(DEVICE_NAME)){
				mStatusTextView.setText("find: " + device.getName());
				mDevice = device;
			}
		}
		/*
		buttonStart = (Button) findViewById(R.id.button_start);
		buttonStart.setEnabled(false);
		//radioHigh = (RadioButton) findViewById(R.id.radio_high);
		//radioLow = (RadioButton) findViewById(R.id.radio_low);

		buttonStart.setEnabled(true);
		//radioHigh.setEnabled(true);
		//radioLow.setEnabled(true);
		*/
	}

	@Override
	public void onPause(){
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "onStop()");
		super.onStop();

		isRunning = false;
		connectFlg = false;

		try{
			mSocket.close();
		}
		catch(Exception e){}
	}

	@Override
	public void onDestroy(){
		if (mMediaPlayer.isPlaying()){
			mMediaPlayer.stop();
		}

		super.onDestroy();

		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// スレッド処理(connectボタン押下後に実行)
	@Override
	public void run() {
		InputStream mmInStream = null;

		Message valueMsg = new Message();
		valueMsg.what = VIEW_STATUS;
		valueMsg.obj = "connecting...";
		mHandler.sendMessage(valueMsg);

		try{

			// 取得したデバイス名を使ってBluetoothでSocket接続
			mSocket = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
			mSocket.connect();
			mmInStream = mSocket.getInputStream();
			mmOutputStream = mSocket.getOutputStream();

			// InputStreamのバッファを格納
			byte[] buffer = new byte[1024];

			// 取得したバッファのサイズを格納
			int bytes;
			valueMsg = new Message();
			valueMsg.what = VIEW_STATUS;
			valueMsg.obj = "connected.";
			mHandler.sendMessage(valueMsg);

			connectFlg = true;

			while(isRunning){

				// InputStreamの読み込み
				bytes = mmInStream.read(buffer);
				Log.i(TAG,"bytes="+bytes);
				// String型に変換
				String readMsg = new String(buffer, 0, bytes);

				// null以外なら表示
				if(readMsg.trim() != null && !readMsg.trim().equals("")){
					Log.i(TAG,"value="+readMsg.trim());

					valueMsg = new Message();
					valueMsg.what = VIEW_INPUT;
					valueMsg.obj = readMsg;
					mHandler.sendMessage(valueMsg);
				}
			}
		}
		// エラー処理
		catch(Exception e){

			valueMsg = new Message();
			valueMsg.what = VIEW_STATUS;
			valueMsg.obj = "Error1:" + e;
			mHandler.sendMessage(valueMsg);

			try{
				mSocket.close();
			}catch(Exception ee){}
			isRunning = false;
			connectFlg = false;
		}
	}

	@Override
	public void onClick(View v) {
		if(v.equals(connectButton)) {
			/*
			// 接続されていない場合のみ
			if (!connectFlg) {
				Log.d("bluetooth","connect start!");
				mStatusTextView.setText("try connect");

				mThread = new Thread(this);
				// Threadを起動し、Bluetooth接続
				isRunning = true;
				mThread.start();
			}
			*/
			Intent intent = new Intent(MainActivity.this, RecognitionActivity.class);
			// SBMモードは廃止の為、設定しない
			//int sbm_mode = radioHigh.isChecked() ? 0 : 1;
			//intent.putExtra(RecognitionActivity.KEY_SBM_MODE, sbm_mode);
			intent.putExtra(RecognitionActivity.KEY_API_KEY, "644a4b4f325168524f4a4e2f774674617351497a39636c617443396f322e4f6e7257627a7846414d444635");
			startActivityForResult(intent, RECOGNIZE_ACTIVITY_REQUEST_ID);
			//mInputTextView.setText(msgStr);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		PatternResponder patternResponder = new PatternResponder("pattern");

		switch (requestCode) {
		case RECOGNIZE_ACTIVITY_REQUEST_ID:
			mResultString = data.getStringExtra((resultCode == RESULT_OK) ? "replace_key" : "error_message");
			Log.d("main","recognized message: " + mResultString);
			Toast.makeText(this, mResultString, Toast.LENGTH_LONG).show();

			//getLoaderManager().restartLoader(1, null, MainActivity.this);
			//以下抱きまくら用に変更
			mResponse = patternResponder.response(mResultString);
			speak();

			break;
		}

		Log.d("Main", "recieved tag is " + mResponse);
	}

	@Override
	public Loader<JSONObject> onCreateLoader(int id, Bundle args) {
		String urltext = "https://king-beco.herokuapp.com/talk";

		HttpAsyncLoader httpAsyncLoader = new HttpAsyncLoader(this,urltext,mResultString);
		httpAsyncLoader.forceLoad();
		return httpAsyncLoader;
	}

	@Override
	public void onLoadFinished(Loader<JSONObject> looader, JSONObject data){
		if (data != null){
			try {
				Toast.makeText(this, data.getString("response"), Toast.LENGTH_LONG).show();
			} catch (JSONException e){
				Log.e("onLoadFinished", "JSONのパースに失敗しました。　JSONException=" + e);
			}
		} else {
			Log.e("onLoadFinished","onLoadFinished error!");
		}
	}

	@Override
	public void onLoaderReset(Loader<JSONObject> loader) {

	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int action = msg.what;
			String msgStr = (String)msg.obj;
			if(action == VIEW_INPUT && msgStr.equals(PUSHED)){
				Intent intent = new Intent(MainActivity.this, RecognitionActivity.class);
				// SBMモードは廃止の為、設定しない
				//int sbm_mode = radioHigh.isChecked() ? 0 : 1;
				//intent.putExtra(RecognitionActivity.KEY_SBM_MODE, sbm_mode);
				intent.putExtra(RecognitionActivity.KEY_API_KEY, "644a4b4f325168524f4a4e2f774674617351497a39636c617443396f322e4f6e7257627a7846414d444635");
				startActivityForResult(intent, RECOGNIZE_ACTIVITY_REQUEST_ID);
				//mInputTextView.setText(msgStr);
			}
			else if(action == VIEW_STATUS){
				mStatusTextView.setText(msgStr);
			}
		}
	};
}
