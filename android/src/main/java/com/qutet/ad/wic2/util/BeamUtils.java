/*
 * Copyright 2014 Google Inc. All rights reserved.
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

package com.qutet.ad.wic2.util;

import com.qutet.ad.wic2.provider.ScheduleContract;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Parcelable;

/**
 * Android Beam helper methods.
 */
public class BeamUtils {

    /**
     * Sets this activity's Android Beam message to one representing the given session.
     */
    public static void setBeamSessionUri(Activity activity, Uri sessionUri) {
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        if (nfcAdapter == null) {
            // No NFC :-(
            return;
        }

        nfcAdapter.setNdefPushMessage(new NdefMessage(
                new NdefRecord[]{
                        new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                                ScheduleContract.makeContentItemType(
                                        ScheduleContract.Sessions.CONTENT_TYPE_ID).getBytes(),
                                new byte[0],
                                sessionUri.toString().getBytes())
                }), activity);
    }

    /**
     * Checks to see if the activity's intent ({@link android.app.Activity#getIntent()}) is
     * an NFC intent that the app recognizes. If it is, then parse the NFC message and set the
     * activity's intent (using {@link Activity#setIntent(android.content.Intent)}) to something
     * the app can recognize (i.e. a normal {@link Intent#ACTION_VIEW} intent).
     */
    public static void tryUpdateIntentFromBeam(Activity activity) {
        Intent originalIntent = activity.getIntent();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(originalIntent.getAction())) {
            Parcelable[] rawMsgs = originalIntent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage msg = (NdefMessage) rawMsgs[0];
            // Record 0 contains the MIME type, record 1 is the AAR, if present.
            // In wic2, AARs are not present.
            NdefRecord mimeRecord = msg.getRecords()[0];
            if (ScheduleContract.makeContentItemType(
                    ScheduleContract.Sessions.CONTENT_TYPE_ID).equals(
                    new String(mimeRecord.getType()))) {
                // Re-set the activity's intent to one that represents session details.
                Intent sessionDetailIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(new String(mimeRecord.getPayload())));
                activity.setIntent(sessionDetailIntent);
            }
        }
    }
}
