package jp.co.nttit.SpeechRec.sample;

import android.app.AlertDialog;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;

import jp.ne.docomo.smt.dev.aitalk.AiTalkTextToSpeech;
import jp.ne.docomo.smt.dev.aitalk.data.AiTalkSsml;
import jp.ne.docomo.smt.dev.common.exception.SdkException;
import jp.ne.docomo.smt.dev.common.exception.ServerException;
import jp.ne.docomo.smt.dev.common.http.AuthApiKey;

/**
 * Created by sansuke05 on 2016/08/20.
 */
public class HttpAsyncLoader extends AsyncTaskLoader<JSONObject> {
    //AIサーバーへデータ送受信,音声合成

    // 警報ダイアログ
    private AlertDialog.Builder _dlg;
    // 変換タイプ
    private int _henkan;
    public static final int henkan_ssml_sound = 1;
    public static final int henkan_ssml_aikana = 2;
    public static final int henkan_ssml_jeitakana = 3;
    public static final int henkan_aikana_sound = 11;
    public static final int henkan_aikana_jeitakana = 13;
    public static final int henkan_jeitakana_sound = 21;
    // エラーフラグ
    private boolean isSdkException = false;
    private String exceptionMessage = null;

    //APIキー
    static final String KEY = "4865796d634e44466b3572336d65357869797538356f73725a495a4d527a5562682e536c396d7542562f33";

    //AIサーバのURL
    private URL url = null;
    String inputPhrase = "";

    public HttpAsyncLoader(Context context,String urltext,String inputPhrase){
        super(context);
        this.inputPhrase = inputPhrase;
        try {
            this.url = new URL(urltext);
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
    }

    @Override
    public JSONObject loadInBackground() {

        HttpURLConnection con = null;
        DataOutputStream os = null;
        BufferedWriter writer = null;
        byte[] resultData = null;

        AuthApiKey.initializeAuth(KEY);

        try {
            con = (HttpURLConnection)url.openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);

            con.setUseCaches(false);

            con.setChunkedStreamingMode(0);

            con.setRequestMethod("POST");
            con.setRequestProperty("Connection", "Keep-Alive");

            String postData = "message=" + inputPhrase;
            Log.d("kingbeko POST", postData);
            writer = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
            writer.write(postData);
            writer.flush();
            writer.close();

            //os = new DataOutputStream(con.getOutputStream());
            //os.writeBytes(postData);

            int status = con.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK){
                //通信に成功

                //JSONを取得
                BufferedInputStream in = new BufferedInputStream(con.getInputStream());
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) != -1){
                    if (length > 0){
                        outputStream.write(buffer, 0, length);
                    }
                }

                JSONObject json = new JSONObject(new String(outputStream.toByteArray()));

                in.close();

                //音声合成
                AiTalkSsml ssml = new AiTalkSsml();
                ssml.startVoice("nozomi");
                ssml.addText(json.getString("response"));
                ssml.endVoice();

                try {
                    AiTalkTextToSpeech search = new AiTalkTextToSpeech();
                    resultData = search.requestAiTalkSsmlToSound(ssml.makeSsml());

                    // 音声出力用バッファ作成
                    int bufSize = AudioTrack.getMinBufferSize(16000, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
                    // ビッグエディアンをリトルエディアンに変換
                    search.convertByteOrder16(resultData);
                    // 音声出力
                    AudioTrack at = new AudioTrack(AudioManager.STREAM_NOTIFICATION, 16000, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, bufSize, AudioTrack.MODE_STREAM);
                    at.play();
                    at.write(resultData, 0, resultData.length);
                    // 音声出力待ち
                    Thread.sleep(resultData.length / 32);
                } catch (SdkException e){
                    e.printStackTrace();
                } catch (ServerException e){
                    e.printStackTrace();
                } catch (InterruptedException e){}

                return json;
            } else {
                throw new ConnectException(String.valueOf(status));
            }

            //Thread.sleep(1000);
        } catch (ConnectException e1){
            e1.printStackTrace();
        } catch (JSONException e1){
            e1.printStackTrace();
        } catch (MalformedURLException e1){
            e1.printStackTrace();
        } catch (IOException e1){
            e1.printStackTrace();
        } //catch (InterruptedException e){}
        finally {
            if (con != null){
                con.disconnect();
            }
        }

        return null;
    }
}

class ConnectException extends Exception {
    public ConnectException(String msg) {
        super(msg);
    }
}
