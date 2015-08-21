/*
     Copyright (c) 2015, The Linux Foundation. All Rights Reserved.
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      disclaimer in the documentation and/or other materials provided
      with the distribution.
    * Neither the name of The Linux Foundation nor the names of its
      contributors may be used to endorse or promote products derived
      from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT
ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

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
