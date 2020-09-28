package xyz.penpencil.otp_capture;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

@NativePlugin(
        requestCodes={OtpCapturePlugin.REQ_USER_CONSENT} // register request code(s) for intent results
)

public class OtpCapturePlugin extends Plugin {

    protected static final int REQ_USER_CONSENT = 200;
    SmsBroadcastReceiver smsBroadcastReceiver;

    @PluginMethod()
    public void startSmsUserConsent(PluginCall call) {
        saveCall(call);
        registerBroadcastReceiver(call);

        SmsRetrieverClient client = SmsRetriever.getClient(getContext());
        //We can add sender phone number or leave it blank
        // I'm adding null here
        client.startSmsUserConsent(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                PluginCall savedCall = getSavedCall();
                if (savedCall == null) {
                    return;
                }
                savedCall.resolve();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                PluginCall savedCall = getSavedCall();
                if (savedCall == null) {
                    return;
                }
                savedCall.reject("Unable to setup listener", e);
            }
        });
    }

    @PluginMethod()
    public void stopSmsUserConsent(PluginCall call) {
        getContext().unregisterReceiver(smsBroadcastReceiver);
        call.resolve();
    }

    @Override
    protected void handleOnActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.handleOnActivityResult(requestCode, resultCode, data);
        // Get the previously saved call
        PluginCall savedCall = getSavedCall();
        if (savedCall == null) {
            return;
        }

        if (requestCode == REQ_USER_CONSENT) {
            if ((resultCode == RESULT_OK) && (data != null)) {
                //That gives all message to us.
                // We need to get the code from inside with regex
                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                getOtpFromMessage(message);
            }
        }
    }

    private void getOtpFromMessage(String message) {
        // This will match any 6 digit number in the message
        Pattern pattern = Pattern.compile("(|^)\\d{6}");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String otp = matcher.group(0);
            JSObject jsObject = new JSObject();
            jsObject.put("otp", otp);
            notifyListeners("otpListener", jsObject);
        }
    }

    private void registerBroadcastReceiver(PluginCall call) {
        smsBroadcastReceiver = new SmsBroadcastReceiver();
        smsBroadcastReceiver.smsBroadcastReceiverListener =
                new SmsBroadcastReceiver.SmsBroadcastReceiverListener() {
                    @Override
                    public void onSuccess(Intent intent) {
                        PluginCall savedCall = getSavedCall();
                        if (savedCall == null) {
                            return;
                        }
                        startActivityForResult(savedCall, intent, REQ_USER_CONSENT);
                    }
                    @Override
                    public void onFailure() {
                    }
                };
        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        getContext().registerReceiver(smsBroadcastReceiver, intentFilter);
    }
}

class SmsBroadcastReceiver extends BroadcastReceiver {
    SmsBroadcastReceiverListener smsBroadcastReceiverListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == SmsRetriever.SMS_RETRIEVED_ACTION) {
            Bundle extras = intent.getExtras();
            Status smsRetrieverStatus = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
            switch (smsRetrieverStatus.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    Intent messageIntent = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT);
                    smsBroadcastReceiverListener.onSuccess(messageIntent);
                    break;
                case CommonStatusCodes.TIMEOUT:
                    smsBroadcastReceiverListener.onFailure();
                    break;
            }
        }
    }

    public interface SmsBroadcastReceiverListener {
        void onSuccess(Intent intent);
        void onFailure();
    }
}
