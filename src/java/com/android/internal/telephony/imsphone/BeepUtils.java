package com.android.internal.telephony.imsphone;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import android.util.Log;

public class BeepUtils {

    private static final String TAG = BeepUtils.class.getSimpleName();
    private Context mContext;
    private static final long RESET_TIME = 30 * 60 * 1000;
    private static final int ALERT_HANDOVER = 1;
    private static final int RESET_ALERT_HANDOVER_TIME = 2;

    private Handler mWifiAlertHandler = new Handler(){
        private int mAlterTimes = 0;

        public void handleMessage(Message msg) {
            switch (msg.what) {
            case ALERT_HANDOVER:
                if (mAlterTimes == 3) {
                    return;
                }
                removeMessages(RESET_ALERT_HANDOVER_TIME);
                sendEmptyMessageDelayed(RESET_ALERT_HANDOVER_TIME, RESET_TIME);
                startBeepForAlert();
                mAlterTimes++;
                break;
            case RESET_ALERT_HANDOVER_TIME:
                mAlterTimes = 0;
                break;
            default:
                break;
            }
        }
    };

    public BeepUtils(Context context) {
        mContext = context;
    }

    public void startAlert(){
        mWifiAlertHandler.sendEmptyMessage(ALERT_HANDOVER);
    }

    private void startBeepForAlert(){
        new Thread() {
            public void run() {
                // Used the 40 percentage of maximum volume
                ToneGenerator mTone = new ToneGenerator(
                        AudioManager.STREAM_VOICE_CALL, 40);
                try {
                    mTone.startTone(ToneGenerator.TONE_PROP_ACK);
                    Thread.sleep(1000);
                    mTone.stopTone();
                } catch (Exception e) {
                    Log.i(TAG, "Exception caught when generator sleep " + e);
                } finally {
                    if (mTone != null) {
                        mTone.release();
                    }
                }
            };
        }.start();
        Toast.makeText(mContext,
                mContext.getResources().getString(
                        com.android.internal.R.string.config_regional_alerts_for_drop_call_toast),
                Toast.LENGTH_LONG).show();
    }
}
