/*
 *  Copyright(C) 2014 NTT IT CORPORATION. All rights reserved.
 */
package jp.co.nttit.SpeechRec.sample;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import jp.co.nttit.EnterVoiceSP.service.helper.SpeechRecServiceHelper;
import jp.co.nttit.EnterVoiceSP.service.helper.VoiceRecognitionEventListener;
import jp.co.nttit.EnterVoiceSP.service.util.DivideFileManager;
import speechrec.client.ModelGroup;
import speechrec.client.Nbest;
import speechrec.client.Sentence;
import speechrec.client.SpeechRecException;
import speechrec.client.Word;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 音声入力を行うアクティビティクラス。
 */
public class RecognitionActivity extends FragmentActivity implements VoiceRecognitionEventListener {

	/** Logcat用のタグ */
	private final static String TAG = RecognitionActivity.class.getSimpleName();

	/** 起動パラメータ（SBM_MODE）※SBMモードは廃止 */
	//public static final String KEY_SBM_MODE = "sbm_mode";
	/** 起動パラメータ（API_KEY） */
	public static final String KEY_API_KEY = "api_key";
	/** 起動パラメータ（VAD_MODEL） */
	public static final String KEY_VAD_MODEL = "vad_model";

	/** extraのキー(エラーメッセージ) */
	private static final String KEY_ERROR_MESSAGE = "error_message";
	/** extraのキー(音声認識結果) */
	private static final String KEY_REPLACE = "replace_key";
	/** extraのキー(音声認識結果) */
	private static final String KEY_SUPPRESS_ERROR_DIALOG = "suppress_error_dialog";

	/** レベルメータのレベル1の閾値 */
	private static final double LEVEL01_VALUE = -80.0d;
	/** レベルメータのレベル2の閾値 */
	private static final double LEVEL02_VALUE = -70.0d;
	/** レベルメータのレベル3の閾値 */
	private static final double LEVEL03_VALUE = -65.0d;
	/** レベルメータのレベル4の閾値 */
	private static final double LEVEL04_VALUE = -60.0d;
	/** レベルメータのレベル5の閾値 */
	private static final double LEVEL05_VALUE = -55.0d;
	/** レベルメータのレベル6の閾値 */
	private static final double LEVEL06_VALUE = -50.0d;
	/** レベルメータのレベル7の閾値 */
	private static final double LEVEL07_VALUE = -45.0d;
	/** レベルメータのレベル8の閾値 */
	private static final double LEVEL08_VALUE = -40.0d;
	/** レベルメータのレベル9の閾値 */
	private static final double LEVEL09_VALUE = -35.0d;
	/** レベルメータのレベル10の閾値 */
	private static final double LEVEL10_VALUE = -30.0d;
	/** レベルメータのレベル11の閾値 */
	private static final double LEVEL11_VALUE = -25.0d;
	/** レベルメータのレベル12の閾値 */
	private static final double LEVEL12_VALUE = -20.0d;
	/** レベルメータのレベル13の閾値 */
	private static final double LEVEL13_VALUE = -15.0d;
	/** レベルメータのレベル14の閾値 */
	private static final double LEVEL14_VALUE = -10.0d;
	/** レベルメータのレベル15の閾値 */
	private static final double LEVEL15_VALUE = -5.0d;
	/** レベルメータのレベル16の閾値 */
	private static final double LEVEL16_VALUE = -1.0d;

	/****************************************************************************************************/

	/** レベルメータのアニメーションを行うオブジェクト */
	private final LevelMeterDrawer levelMeterDrawer = new LevelMeterDrawer();

	/** レベルメータのレベル1の閾値 */
	private double level01Value = LEVEL01_VALUE;
	/** レベルメータのレベル2の閾値 */
	private double level02Value = LEVEL02_VALUE;
	/** レベルメータのレベル3の閾値 */
	private double level03Value = LEVEL03_VALUE;
	/** レベルメータのレベル4の閾値 */
	private double level04Value = LEVEL04_VALUE;
	/** レベルメータのレベル5の閾値 */
	private double level05Value = LEVEL05_VALUE;
	/** レベルメータのレベル6の閾値 */
	private double level06Value = LEVEL06_VALUE;
	/** レベルメータのレベル7の閾値 */
	private double level07Value = LEVEL07_VALUE;
	/** レベルメータのレベル8の閾値 */
	private double level08Value = LEVEL08_VALUE;
	/** レベルメータのレベル9の閾値 */
	private double level09Value = LEVEL09_VALUE;
	/** レベルメータのレベル10の閾値 */
	private double level10Value = LEVEL10_VALUE;
	/** レベルメータのレベル11の閾値 */
	private double level11Value = LEVEL11_VALUE;
	/** レベルメータのレベル12の閾値 */
	private double level12Value = LEVEL12_VALUE;
	/** レベルメータのレベル13の閾値 */
	private double level13Value = LEVEL13_VALUE;
	/** レベルメータのレベル14の閾値 */
	private double level14Value = LEVEL14_VALUE;
	/** レベルメータのレベル15の閾値 */
	private double level15Value = LEVEL15_VALUE;
	/** レベルメータのレベル16の閾値 */
	private double level16Value = LEVEL16_VALUE;

	/** ボタン（マイクアイコン）を包含するLinearLayout */
	private LinearLayout buttons;
	/** 状態表示を包含するLinearLayout */
	private LinearLayout progressBar;
	/** レベルメータ(レベル1)の画像 */
	private ImageView level01Image;

	/** Nベスト表示モード */
	private boolean nbest = false;
	/** 音声認識結果出力モードのセパレータ */
	private String separator = "";
	/** エラーダイアログ抑止(抑止する場合true) */
	private boolean suppressErrorDialog;

	/** 音声認識サービスのヘルパー */
	private SpeechRecServiceHelper helper = new SpeechRecServiceHelper();
	/** 音声認識サービスのパラメータ */
	private Bundle bundle;
	/** 音声認識結果のリスト */
	private LinkedList<StringBuilder> resultList = null;

	/****************************************************************************************************/

	/**
	 * 音声認識Activityの初期化を行う。
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mic_icon);

		buttons = (LinearLayout) findViewById(R.id.linearlayout_buttons);
		progressBar = (LinearLayout) findViewById(R.id.linearlayout_progressbar);

		/* レベルメータの設定 */
		level01Image = (ImageView) findViewById(R.id.imageview_level);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	/**
	 * Divide関連ファイルを端末内に展開し、音声認識サービスと接続する。
	 * 
	 * @see android.support.v4.app.FragmentActivity#onStart()
	 */
	@Override
	public void onStart() {
		Log.d(TAG, "onStart()");
		super.onStart();

		// エラーダイアログ抑止設定
		suppressErrorDialog = getIntent().getBooleanExtra(KEY_SUPPRESS_ERROR_DIALOG, false);

		// 明示的に戻りを設定しない限り、キャンセルを返却
		setResult(RESULT_CANCELED, null, getString(R.string.canceled));

		// Divide関連ファイルを端末内に展開
		DivideFileManager divideFileManager = new DivideFileManager(this);
		if (!divideFileManager.isExtracted()) {
			try {
				divideFileManager.extract();
			} catch (IOException e) {
				showErrorDialog(e.getMessage());
				return;
			}
		}

		bundle = new Bundle();
		// Bundle にインテントの値（APIキー）を追加
		Intent intent = getIntent();
		bundle.putAll(intent.getExtras());
		// Bundle に区間検出モデルファイルを追加
		bundle.putString(KEY_VAD_MODEL, divideFileManager.getDivideModelPath());

		for (String key : new TreeSet<String>(bundle.keySet())) {
			Object value = bundle.get(key);
			String name = null;
			if (value != null) {
				name = value.getClass().getSimpleName();
			}
			String s = MessageFormat.format("{0}={1} ({2})", key, value, name);
			Log.d(TAG, s);
		}

		Log.d(TAG, "helper.connect()");
		// 音声認識サービスと接続
		helper.connect(this, this);
	}

	/**
	 * アプリケーションを終了する。
	 * 
	 * @see android.support.v4.app.FragmentActivity#onPause()
	 */
	@Override
	protected void onPause() {
		Log.d(TAG, "onPause()");
		super.onPause();
		// finish中の場合、再度finishしない
		if (!isFinishing()) {
			finish();
		}
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume()");
		super.onResume();
	}

	@Override
	protected void onRestart() {
		Log.d(TAG, "onRestart()");
		super.onRestart();
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "onStop()");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy()");
		helper.close();
		super.onDestroy();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.d(TAG, "onConfigurationChanged()");
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onBackPressed() {
		Log.d(TAG, "onBackPressed()");
		super.onBackPressed();
	}

	/****************************************************************************************************/

	/**
	 * 終了ボタンを非表示に変更し、音声認識処理を終了する。
	 * 
	 * @param view
	 *            終了ボタン
	 */
	public void onClickFinishButton(View view) {
		Log.d(TAG, "onClickFinishButton()");
		// 終了ボタンを非表示、プログレスを表示に変更
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				level01Image.setImageResource(R.drawable.recognizing);
				progressBar.setVisibility(View.VISIBLE);
			}
		});
		Log.d(TAG, "helper.stopRecognition()");
		// 音声認識処理を終了
		helper.stopRecognition();
	}

	/****************************************************************************************************/

	/**
	 * 終了ボタンを活性に変更し、音声認識処理を開始を開始する。
	 * 
	 * @see jp.co.nttit.EnterVoiceSP.service.helper.VoiceRecognitionEventListener#onServiceConnected()
	 */
	@Override
	public void onServiceConnected() {
		Log.d(TAG, "onServiceConnected()");
		Log.d(TAG, "helper.startRecognition()");
		// 音声認識処理を開始
		helper.startRecognition(bundle);
	}

	/**
	 * レベルメータの画像を変更する。
	 * 
	 * @see jp.co.nttit.EnterVoiceSP.service.helper.VoiceRecognitionEventListener#onRecord(short[])
	 */
	@Override
	public void onRecord(short[] samples) {
		if (samples.length == 0) {
			return;
		}
		double avg = 0;
		int cnt = 0;
		int sum_cnt = 0;
		for (short s : samples) {
			if (s == 0) {
				s = 1;
			}
			// 処理量削減の為、間引いてdB変換
			cnt++;
			if (cnt % 16 == 0) {
				Double db = Math.log10(Math.pow(s, 2)
						/ Math.pow(Short.MAX_VALUE, 2)) * 10;
				avg += db;
				sum_cnt++;
			}
		}
		avg /= sum_cnt;

		if (avg < level01Value) {
			levelMeterDrawer.level01Id = R.drawable.speak_now_1;
		} else if (avg < level02Value) {
			levelMeterDrawer.level01Id = R.drawable.speak_now_2;
		} else if (avg < level03Value) {
			levelMeterDrawer.level01Id = R.drawable.speak_now_3;
		} else if (avg <= level04Value) {
			levelMeterDrawer.level01Id = R.drawable.speak_now_4;
		} else if (avg <= level05Value) {
			levelMeterDrawer.level01Id = R.drawable.speak_now_5;
		} else if (avg <= level06Value) {
			levelMeterDrawer.level01Id = R.drawable.speak_now_6;
		} else if (avg <= level07Value) {
			levelMeterDrawer.level01Id = R.drawable.speak_now_7;
		} else if (avg <= level08Value) {
			levelMeterDrawer.level01Id = R.drawable.speak_now_8;
		} else if (avg < level09Value) {
			levelMeterDrawer.level01Id = R.drawable.speak_now_9;
		} else if (avg < level10Value) {
			levelMeterDrawer.level01Id = R.drawable.speak_now_10;
		} else if (avg < level11Value) {
			levelMeterDrawer.level01Id = R.drawable.speak_now_11;
		} else if (avg <= level12Value) {
			levelMeterDrawer.level01Id = R.drawable.speak_now_12;
		} else if (avg <= level13Value) {
			levelMeterDrawer.level01Id = R.drawable.speak_now_13;
		} else if (avg <= level14Value) {
			levelMeterDrawer.level01Id = R.drawable.speak_now_14;
		} else if (avg <= level15Value) {
			levelMeterDrawer.level01Id = R.drawable.speak_now_15;
		} else if (avg <= level16Value) {
			levelMeterDrawer.level01Id = R.drawable.speak_now_16;
		} else {
			levelMeterDrawer.level01Id = R.drawable.none;
		}
		runOnUiThread(levelMeterDrawer);
	}

	/**
	 * レベルメータの画像を切り替えるクラス。
	 */
	private class LevelMeterDrawer implements Runnable {
		int level01Id;

		/**
		 * 画像を切り替える。
		 */
		@Override
		public void run() {
			level01Image.setImageResource(level01Id);
		}
	}

	/**
	 * 音声認識結果の値を保持する。
	 * 
	 * @see jp.co.nttit.EnterVoiceSP.service.helper.VoiceRecognitionEventListener#onResult(speechrec.client.Nbest)
	 */
	@Override
	public void onResult(Nbest result) {
		Log.d(TAG, "onResult()");

		List<Sentence> sentenceList = result.getSentenceList();

		if (resultList == null) {
			resultList = new LinkedList<StringBuilder>();
			for (int i = 0; i < sentenceList.size(); i++) {
				resultList.add(new StringBuilder());
			}
		} else {
			while (resultList.size() > sentenceList.size()) {
				resultList.removeLast();
			}
		}

		for (int i = 0; i < resultList.size(); i++) {
			StringBuilder sb = resultList.get(i);
			Sentence sentence = sentenceList.get(i);
			for (Word word : sentence.getWordList()) {
				String label = word.getLabel();
				// ラベルがnullは無視
				if (label == null) {
					continue;
				}
				// セミコロン以降、空白のみは無視
				label = label.replaceAll(";.*", "").replaceAll("[ 　]", "");
				// ラベルが空文字列は無視
				if (label.length() == 0) {
					continue;
				}
				// 音声認識結果に追加
				if ((separator != null) && (sb.length() > 0)) {
					sb.append(separator);
				}
				sb.append(label);
			}
		}
	}

	/**
	 * 終了ボタンを非表示に変更する。
	 * 
	 * @see jp.co.nttit.EnterVoiceSP.service.helper.VoiceRecognitionEventListener#onStopRecording()
	 */
	@Override
	public void onStopRecording() {
		Log.d(TAG, "onStopRecording()");
		// 終了ボタンを非表示、プログレスを表示に変更
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				level01Image.setImageResource(R.drawable.recognizing);
				progressBar.setVisibility(View.VISIBLE);
			}
		});

	}

	/**
	 * 音声認識の終了通知の結果により以下のいずれかを行う。<br/>
	 * ・エラーが発生していた場合、エラーダイアログを表示する。<br/>
	 * ・音声認識結果が０件の場合、エラーダイアログを表示する。<br/>
	 * ・Nベスト表示なし、または音声認識結果が１件の場合、処理を終了する。<br/>
	 * ・Nベスト表示あり、かつ音声認識結果が２件以上の場合、音声認識結果選択ダイアログを表示する。<br/>
	 * 
	 * @see jp.co.ntt.lab.speechrec.android.service.helper.VoiceRecognitionEventListener#onFinish(SpeechRecException)
	 */
	@Override
	public void onFinish(SpeechRecException e) {
		Log.d(TAG, "onFinish()");

		Log.d(TAG, "helper.close()");
		helper.close();

		if (e != null) {
			// エラー発生した場合
			final String message = e.getType() + " " + e.getErrno() + "\n" + e.getMessage();
			Log.e(TAG, message, e);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					showErrorDialog(message);
				}
			});

		} else {
			// 音声認識結果を詰め替えて、無効なデータを削除
			LinkedList<String> resultList = new LinkedList<String>();
			if (this.resultList != null) {
				for (StringBuilder sb : this.resultList) {
					if (sb.length() > 0) {
						resultList.add(sb.toString());
					}
				}
			}

			if (resultList.isEmpty()) {
				// 音声認識結果が０件の場合
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setResult(RESULT_OK, "", null);
						finish();
					}
				});

			} else if (!nbest || (resultList.size() == 1)) {
				// Nベスト表示なし、または音声認識結果が１件のみ
				final String replace = resultList.getFirst().toString();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setResult(RESULT_OK, replace, null);
						finish();
					}
				});

			} else {
				// Nベスト表示あり、かつ音声認識結果が２件以上
				final String[] items = new String[resultList.size()];
				for (int i = 0; i < items.length; i++) {
					items[i] = resultList.get(i).toString();
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						buttons.setVisibility(View.INVISIBLE);
						progressBar.setVisibility(View.INVISIBLE);

						showSelectDialog(items);
					}
				});
			}
		}
	}

	/****************************************************************************************************/

	/**
	 * 指定されたパラメータで結果インテントを設定し、アクティビティを終了する。<br>
	 * 
	 * @param resultCode
	 *            終了コード( -1:正常終了/0:ユーザキャンセル/1:異常終了)
	 * @param replace
	 *            音声認識結果(存在しない場合nullを指定)
	 * @param message
	 *            エラーメッセージ(存在しない場合nullを指定)
	 */
	private void setResult(int resultCode, String replace, String message) {
		Log.d(TAG, "setResult(" + resultCode + ", " + replace + ", " + message + ")");

		Intent resultIntent = new Intent();
		if (replace != null) {
			resultIntent.putExtra(KEY_REPLACE, replace);
		}
		if (message != null) {
			resultIntent.putExtra(KEY_ERROR_MESSAGE, message);
		}
		setResult(resultCode, resultIntent);
	}

	/**
	 * アクティビティを終了する。<br>
	 */
	@Override
	public void finish() {
		Log.d(TAG, "finish()");
		Log.d(TAG, "helper.close()");
		helper.close();
		super.finish();
	}

	/****************************************************************************************************/

	/**
	 * エラーダイアログを表示する。
	 * 
	 * @param message
	 *            エラーメッセージ
	 */
	private void showErrorDialog(String message) {
		Log.d(TAG, "showErrorDialog()");
		if (suppressErrorDialog) {
			setResult(RESULT_FIRST_USER, null, message);
			finish();
		} else {
			Bundle arguments = new Bundle();
			arguments.putString(ErrorDialog.ARGUMENT_KEY_MESSAGE, message);

			ErrorDialog dialog = new ErrorDialog();
			dialog.setArguments(arguments);
			dialog.show(getSupportFragmentManager(), null);
		}
	}

	/**
	 * エラーダイアログの生成を行う。
	 */
	public static class ErrorDialog extends DialogFragment {

		/** エラーメッセージの引数のキー */
		public static final String ARGUMENT_KEY_MESSAGE = "message";

		/**
		 * エラーダイアログの生成および初期化を行う。
		 * 
		 * @see android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
		 */
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			String message = getArguments().getString(ARGUMENT_KEY_MESSAGE);
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(getString(R.string.error));
			builder.setNegativeButton(getString(R.string.finish),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});

			builder.setMessage(message);
			return builder.create();
		}

		/**
		 * キャンセルボタン押下時の処理を行う。
		 * 
		 * @see android.support.v4.app.DialogFragment#onCancel(android.content.DialogInterface)
		 */
		@Override
		public void onCancel(DialogInterface dialog) {
			super.onCancel(dialog);
			RecognitionActivity activity = (RecognitionActivity) getActivity();
			String message = getArguments().getString(ARGUMENT_KEY_MESSAGE);
			activity.setResult(RESULT_FIRST_USER, null, message);
			activity.finish();
		}
	}

	/****************************************************************************************************/

	/**
	 * 音声認識結果選択ダイアログを表示する。
	 * 
	 * @param items
	 *            音声認識結果の選択肢
	 */
	private void showSelectDialog(String[] items) {
		Log.d(TAG, "showrSelectDialog()");
		Bundle arguments = new Bundle();
		arguments.putStringArray(SelectDialog.ARGUMENT_KEY_ITEMS, items);

		SelectDialog dialog = new SelectDialog();
		dialog.setArguments(arguments);
		dialog.show(getSupportFragmentManager(), null);
	}

	/**
	 * 音声認識結果選択ダイアログの生成を行う。
	 */
	public static class SelectDialog extends DialogFragment {

		/** 音声認識結果の引数のキー */
		public static final String ARGUMENT_KEY_ITEMS = "items";

		/**
		 * 音声認識結果選択ダイアログの生成および初期化を行う。
		 * 
		 * @see android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
		 */
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(getString(R.string.result_select));
			builder.setNegativeButton(getString(R.string.cancel),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});

			final CharSequence[] items = getArguments().getStringArray(
					ARGUMENT_KEY_ITEMS);
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					String replace = ((String) items[which]);
					RecognitionActivity activity = (RecognitionActivity) getActivity();
					activity.setResult(RESULT_OK, replace, null);
					activity.finish();
				}
			});
			return builder.create();
		}

		/**
		 * キャンセルボタン押下時の処理を行う。
		 * 
		 * @see android.support.v4.app.DialogFragment#onCancel(android.content.
		 *      DialogInterface)
		 */
		@Override
		public void onCancel(DialogInterface dialog) {
			super.onCancel(dialog);
			RecognitionActivity activity = (RecognitionActivity) getActivity();
			activity.finish();
		}
	}

	@Override
	public void onConnected() {
		// TODO 自動生成されたメソッド・スタブ
	}

	@Override
	public void onRecordNR(short[] arg0) {
		// TODO 自動生成されたメソッド・スタブ
	}

	@Override
	public void onResult(ModelGroup arg0) {
		// TODO 自動生成されたメソッド・スタブ
	}

	@Override
	public void onTempResult(double arg0, Nbest arg1) {
		// TODO 自動生成されたメソッド・スタブ
	}
}