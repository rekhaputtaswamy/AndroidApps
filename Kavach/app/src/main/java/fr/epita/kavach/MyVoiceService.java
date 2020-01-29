package fr.epita.kavach;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MyVoiceService extends Service {

    //Use audio manageer services
    static protected AudioManager mAudioManager;
    protected SpeechRecognizer mSpeechRecognizer;
    protected Intent mSpeechRecognizerIntent;
    protected final Messenger mServerMessenger = new Messenger(new IncomingHandler(this));

    //To turn screen On uitll lock phone
    protected boolean mIsListening;
    static boolean mIsStreamSolo;

    static final int MSG_RECOGNIZER_START_LISTENING = 1;
    static final int MSG_RECOGNIZER_CANCEL = 2;
    static int identify = 0, result = 0;
    String Currentdata = null, newcurrent = null;

    @Override
    public void onCreate() {
        super.onCreate();

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(new SpeechRecognitionListener());
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());

        Log.d("service", "oncreate");
    }

    protected static class IncomingHandler extends Handler {
        private WeakReference<MyVoiceService> mtarget;

        public IncomingHandler(MyVoiceService target) {
            mtarget = new WeakReference<MyVoiceService>(target);
        }


        @Override
        public void handleMessage(Message msg) {
            final MyVoiceService target = mtarget.get();

            switch (msg.what) {
                case MSG_RECOGNIZER_START_LISTENING:

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        // turn off beep sound
                        if (!mIsStreamSolo) {
                            mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                            mIsStreamSolo = true;
                        }
                    }
                    if (!target.mIsListening) {
                        target.mSpeechRecognizer.startListening(target.mSpeechRecognizerIntent);
                        target.mIsListening = true;
                    }
                    break;

                case MSG_RECOGNIZER_CANCEL:
                    if (mIsStreamSolo) {
                        mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                        mIsStreamSolo = false;
                    }
                    target.mSpeechRecognizer.cancel();
                    target.mIsListening = false;
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.destroy();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mServerMessenger.getBinder();
    }

    protected class SpeechRecognitionListener implements RecognitionListener {

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onError(int error) {
            mIsListening = false;
            Message message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
            try {
                mServerMessenger.send(message);
            } catch (RemoteException e) {

            }
        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }

        @Override
        public void onPartialResults(Bundle partialResults) {

            Log.i("spech", "onPartialResults");

            ArrayList data = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String word = (String) data.get(data.size() - 1);
            if (Currentdata == null) {
                LandingActivity.textView.setText("" + word);

            } else {
                LandingActivity.textView.setText(Currentdata + " " + word);
                LandingActivity.textView.setSelection(LandingActivity.textView.getText().length());

                checkForHelp();
            }

            newcurrent = LandingActivity.textView.getText().toString();
            identify = 1;
            Log.d("partail", "" + word);
        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.d("service", "onReadyForSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onResults(Bundle results) {

            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String word = (String) data.get(data.size() - 1);

            if (result == 0) {
                LandingActivity.textView.setText(word);
                Currentdata = LandingActivity.textView.getText().toString();

            } else if (result == 1) {
                if (Currentdata != null) {
                    LandingActivity.textView.setText(Currentdata + "\n" + word);
                    LandingActivity.textView.setSelection(LandingActivity.textView.getText().length());

                }
            }

            checkForHelp();

            Currentdata = LandingActivity.textView.getText().toString();

            Log.d("service", "" + Currentdata);

            if (mIsListening == true) {
                mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
            }
            result = 1;
        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

    }

    private void checkForHelp() {
        if(Currentdata != null) {
            if (Currentdata.toLowerCase().contains("help") && LandingActivity.messageCounter < 1) {
                LandingActivity.smsPermissions();
            }
        } else if(newcurrent != null) {
            if (newcurrent.toLowerCase().contains("help") && LandingActivity.messageCounter < 1) {
                LandingActivity.smsPermissions();
            }
        }
    }

}
