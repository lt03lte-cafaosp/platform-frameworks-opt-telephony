/*
 * Copyright (C) 2015 The Android Open Source Project
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

package com.android.internal.telephony.uicc;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Wrapper class for the ISIM Service Table EF.
 * See 3GPP TS 31.103
 */
public final class IsimServiceTable extends IccServiceTable {
    public enum IsimService {
        PCSCF_ADDRESS("P-CSCF address"),
        GBA("Generic Bootstrapping Architecture"),
        HTTP_DIGEST("HTTP Digest"),
        GBA_BASED_LKEM("GBA-based Local Key Establishment Mechanism"),
        PCSCF_DISCOVERY_FOR_IMS_LBO("Support of P-CSCF discovery for IMS Local Break Out"),
        SMS("Short Message Storage"),
        SMSR("Short Message Status Reports"),
        SM_IP_SMS_PP("Support for SM-over-IP including data download via SMS-PP"),
        CC_IMS_ISIM("Communication Control for IMS by ISIM"),
        UICC_IMS("Support of UICC access to IMS"),
        URI_UICC("URI support by UICC");

        private String serviceName;

        private IsimService(String servicename){
            this.serviceName = servicename;
        }

        private String getIsimService(){
            return this.serviceName;
        }
    }

    public IsimServiceTable(byte[] table) {
        super(table);
    }

    public boolean isAvailable(IsimService service) {
        return super.isAvailable(service.ordinal());
    }

    @Override
    protected String getTag() {
        return "IsimServiceTable";
    }

    @Override
    protected Object[] getValues() {
        return IsimService.values();
    }
}

