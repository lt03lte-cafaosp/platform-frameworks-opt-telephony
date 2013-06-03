/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.internal.telephony;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * {@hide}
 */
public class OperatorInfo implements Parcelable {
    public enum State {
        UNKNOWN,
        AVAILABLE,
        CURRENT,
        FORBIDDEN;
    }

    private String operatorAlphaLong;
    private String operatorAlphaShort;
    private String operatorNumeric;

    private State state = State.UNKNOWN;

    private String radioTech;
    private String radioTechDes; // displayed in UI, "2G" or "3G"

    public String
    getOperatorAlphaLong() {
        return operatorAlphaLong;
    }

    public String
    getOperatorAlphaShort() {
        return operatorAlphaShort;
    }

    public String
    getOperatorNumeric() {
        return operatorNumeric;
    }

    public State
    getState() {
        return state;
    }

    public String
    getRadioTech() {
        return radioTech;
    }

    public String
    getRadioTechDes() {
        return radioTechDes;
    }

    public OperatorInfo(String operatorAlphaLong,
                String operatorAlphaShort,
                String operatorNumeric,
                State state,
                String radioTech) {

        this.operatorAlphaLong = operatorAlphaLong;
        this.operatorAlphaShort = operatorAlphaShort;
        this.operatorNumeric = operatorNumeric;

        this.state = state;

        this.radioTech = radioTech;
        this.radioTechDes = getRadioTechDes(radioTech);
    }

    public OperatorInfo(String operatorAlphaLong,
                String operatorAlphaShort,
                String operatorNumeric,
                State state) {
        this (operatorAlphaLong, operatorAlphaShort,
                operatorNumeric, state,"");
    }

    public OperatorInfo(String operatorAlphaLong,
                String operatorAlphaShort,
                String operatorNumeric,
                String stateString, String radioTech) {
        this (operatorAlphaLong, operatorAlphaShort,
                operatorNumeric, rilStateToState(stateString),radioTech );
    }

    /**
     * See state strings defined in ril.h RIL_REQUEST_QUERY_AVAILABLE_NETWORKS
     */
    private static State rilStateToState(String s) {
        if (s.equals("unknown")) {
            return State.UNKNOWN;
        } else if (s.equals("available")) {
            return State.AVAILABLE;
        } else if (s.equals("current")) {
            return State.CURRENT;
        } else if (s.equals("forbidden")) {
            return State.FORBIDDEN;
        } else {
            throw new RuntimeException(
                "RIL impl error: Invalid network state '" + s + "'");
        }
    }

    private static String getRadioTechDes(String s) {
   /*
    RADIO_TECH_UNKNOWN = 0,
    RADIO_TECH_GPRS = 1,
    RADIO_TECH_EDGE = 2,
    RADIO_TECH_UMTS = 3,
    RADIO_TECH_IS95A = 4,
    RADIO_TECH_IS95B = 5,
    RADIO_TECH_1xRTT =  6,
    RADIO_TECH_EVDO_0 = 7,
    RADIO_TECH_EVDO_A = 8,
    RADIO_TECH_HSDPA = 9,
    RADIO_TECH_HSUPA = 10,
    RADIO_TECH_HSPA = 11,
    RADIO_TECH_EVDO_B = 12,
    RADIO_TECH_EHRPD = 13,
    RADIO_TECH_LTE = 14,
    RADIO_TECH_HSPAP = 15, // HSPA+
    RADIO_TECH_GSM = 16, // Only supports voice
    RADIO_TECH_TD_SCDMA = 17
   */
        if (s == null)
            return "";
        if ( s.equals("1")
                || s.equals("2")
                || s.equals("4")
                || s.equals("5")
                || s.equals("6")
                || s.equals("16")){
            return "2G";
        } else if (s.equals("3")
            ||s.equals("17")
            ||s.equals("7")
            ||s.equals("8")
            ||s.equals("9")
            ||s.equals("10")
            ||s.equals("11")
            ||s.equals("12")
            ||s.equals("13")
            ||s.equals("15")) {
            return "3G";
        }else if(s.equals("14")){
            return "4G";
        }else{
            return "";
        }
    }

    public String toString() {
        return "OperatorInfo " + operatorAlphaLong
                + "/" + operatorAlphaShort
                + "/" + operatorNumeric
                + "/" + state
                +"/"  + radioTechDes;
    }

    /**
     * Parcelable interface implemented below.
     * This is a simple effort to make OperatorInfo parcelable rather than
     * trying to make the conventional containing object (AsyncResult),
     * implement parcelable.  This functionality is needed for the
     * NetworkQueryService to fix 1128695.
     */

    public int describeContents() {
        return 0;
    }

    /**
     * Implement the Parcelable interface.
     * Method to serialize a OperatorInfo object.
     */
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(operatorAlphaLong);
        dest.writeString(operatorAlphaShort);
        dest.writeString(operatorNumeric);
        dest.writeSerializable(state);
        dest.writeString(radioTech);
    }

    /**
     * Implement the Parcelable interface
     * Method to deserialize a OperatorInfo object, or an array thereof.
     */
    public static final Creator<OperatorInfo> CREATOR =
        new Creator<OperatorInfo>() {
            public OperatorInfo createFromParcel(Parcel in) {
                OperatorInfo opInfo = new OperatorInfo(
                        in.readString(), /*operatorAlphaLong*/
                        in.readString(), /*operatorAlphaShort*/
                        in.readString(), /*operatorNumeric*/
                        (State) in.readSerializable(), /*state*/
                        in.readString());/*radioTech*/
                return opInfo;
            }

            public OperatorInfo[] newArray(int size) {
                return new OperatorInfo[size];
            }
        };
}
