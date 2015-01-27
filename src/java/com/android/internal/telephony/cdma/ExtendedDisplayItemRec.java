/*
 * Copyright (c) 2013, The Linux Foundation. All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.
 *   * Neither the name of The Linux Foundation nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.android.internal.telephony.cdma;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import android.util.Log;

public class ExtendedDisplayItemRec {
    // According to ANSI TI.610-1998, the info in these records should be
    // encoded as US-ASCII
    private final String INFO_CHARSET = "US-ASCII";

    public ExtendedDisplayItemRec(ExtendedDisplayTag tag, byte len, byte[] data) {
        init(tag, len, data);
    }

    public ExtendedDisplayItemRec(ExtendedDisplayTag tag, byte len) {
        init(tag, len, null);
    }

    private void init(ExtendedDisplayTag tag, byte len, byte[] data) {
        this.displayTag = tag;
        this.mLen = len;
        this.mData = data;
    }

    public byte[] getData() {
        return mData;
    }

    public String getDataAsString() {
        String ret = null;
        Log.d("CdmaDisplayInfoRec", "getDataAsString()");
        if (mData == null)
            return "";
        Charset chs = Charset.forName(INFO_CHARSET);
        CharsetDecoder d = chs.newDecoder();
        ByteBuffer b = ByteBuffer.wrap(mData);
        try {
            ret = d.decode(b).toString();
        } catch (CharacterCodingException e) {
            Log.e("CdmaDisplayInfoRec", "Error decoding", e);
        }
        return ret;
    }

    @Override
    public String toString() {
        String ret = "";
        ret += displayTag.toString() +
                "(" + mLen + "): " +
                getDataAsString();
        return ret;
    }

    public ExtendedDisplayTag displayTag;
    private byte mLen;
    private byte[] mData;

    public enum ExtendedDisplayTag {
            X_DISPLAY_TAG_BLANK((byte) 0x80),
            X_DISPLAY_TAG_SKIP((byte) 0x81),
            X_DISPLAY_TAG_CONTINUATION((byte) 0x82),
            X_DISPLAY_TAG_CALLED_ADDRESS((byte) 0x83),
            X_DISPLAY_TAG_CAUSE((byte) 0x84),
            X_DISPLAY_TAG_PROGRESS_INDICATOR((byte) 0x85),
            X_DISPLAY_TAG_NOTIFICATION_INDICATOR((byte) 0x86),
            X_DISPLAY_TAG_PROMPT((byte) 0x87),
            X_DISPLAY_TAG_ACCUMULATED_DIGITS((byte) 0x88),
            X_DISPLAY_TAG_STATUS((byte) 0x89),
            X_DISPLAY_TAG_INBAND((byte) 0x8a),
            X_DISPLAY_TAG_CALLING_ADDRESS((byte) 0x8b),
            X_DISPLAY_TAG_REASON((byte) 0x8c),
            X_DISPLAY_TAG_CALLING_PARTY_NAME((byte) 0x8d),
            X_DISPLAY_TAG_CALLED_PARTY_NAME((byte) 0x8e),
            X_DISPLAY_TAG_ORIGINAL_CALLED_NAME((byte) 0x8f),
            X_DISPLAY_TAG_REDIRECTING_NAME((byte) 0x90),
            X_DISPLAY_TAG_CONNECTED_NAME((byte) 0x91),
            X_DISPLAY_TAG_ORIGINATING_RESTRICTIONS((byte) 0x92),
            X_DISPLAY_TAG_DATETIME((byte) 0x93),
            X_DISPLAY_TAG_CALL_APPEARANCE_ID((byte) 0x94),
            X_DISPLAY_TAG_FEATURE_ADDRESS((byte) 0x95),
            X_DISPLAY_TAG_REDIRECTION_NAME((byte) 0x96),
            X_DISPLAY_TAG_REDIRECTION_NUMBER((byte) 0x97),
            X_DISPLAY_TAG_REDIRECTING_NUMBER((byte) 0x98),
            X_DISPLAY_TAG_ORIGINAL_CALLED_NUMBER((byte) 0x99),
            X_DISPLAY_TAG_CONNECTED_NUMBER((byte) 0x9a),
            X_DISPLAY_TAG_TEXT((byte) 0x9e), ;

        private final byte mValue;

        ExtendedDisplayTag(byte value) {
            this.mValue = value;
        }

        public byte value() {
            return mValue;
        }

        /**
         * Return a 0-based ordinal number corresponding to the tag (e.g. 0 for
         * X_DISPLAY_TAG_BLANK, 1 for X_DISPLAY_TAG_SKIP, etc)
         */
        public int asIndex() {
            int ret = (mValue & 0xff) & ~0x80;
            Log.d("DisplayTag", toString() + " as index: " + ret);
            return ret;
        }

        /**
         * Create a new ExtendedDisplayTag from the wire byte it represents
         * (e.g. 0x80 for X_DISPLAY_TAG_BLANK, 0x81 for X_DISPLAY_TAG_SKIP, etc)
         */
        public static ExtendedDisplayTag fromByte(byte value) {
            Log.d("DisplayTag", "DisplayTag.fromByte(" + value + ")");
            ExtendedDisplayTag ret = null;
            for (ExtendedDisplayTag tag : ExtendedDisplayTag.values()) {
                if (tag.mValue == value)
                    ret = tag;
            }
            Log.d("DisplayTag", "Tag for byte " + value + ": " + ret);
            return ret;
        }
    }
}
