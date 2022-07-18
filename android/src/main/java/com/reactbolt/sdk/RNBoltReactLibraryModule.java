package com.reactbolt.sdk;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.Manifest;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;

import com.bolt.consumersdk.CCConsumer;
import com.bolt.consumersdk.CCConsumerTokenCallback;
import com.bolt.consumersdk.domain.CCConsumerAccount;
import com.bolt.consumersdk.domain.CCConsumerCardInfo;
import com.bolt.consumersdk.domain.CCConsumerError;
import com.bolt.consumersdk.network.CCConsumerApi;
import com.bolt.consumersdk.utils.CCConsumerCardUtils;
import com.bolt.consumersdk.utils.LogHelper;
import com.bolt.consumersdk.utils.LogHelper.LogLevel;
import com.bolt.consumersdk.listeners.BluetoothSearchResponseListener;
import com.bolt.consumersdk.swiper.CCSwiperController;
import com.bolt.consumersdk.swiper.enums.BatteryState;
import com.bolt.consumersdk.swiper.enums.SwiperError;
import com.bolt.consumersdk.swiper.enums.SwiperType;
import com.bolt.consumersdk.swiper.SwiperControllerListener;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson; 

// IDTechSwiperController
// com.bolt.consumersdk.swiper.CCSwiperController
// Choreographer
// ConsumerApi
// ConnectionCoordinator

// 07-17 18:32:21.145 32265  9890 V NFC     : this device does not have NFC support
// 07-17 18:32:21.146 32265  6070 I NetworkScheduler.Stats: Task com.google.android.gms/com.google.android.gms.checkin.CheckinService finished executing. cause:4 result: 3 elapsed_millis: 58 uptime_millis: 58 exec_start_elapsed_seconds: 2710802 [CONTEXT service_id=218 ]
// 07-17 18:32:21.146 32265  9890 V NFC     : this device does not have NFC support
// 07-17 18:32:21.147 32265  9890 W TapAndPay: NfcAdapter is null while obtaining CardEmulation instance. [CONTEXT service_id=79 ]
// 07-17 18:32:21.147 32265  9890 W TapAndPay: java.lang.NullPointerException: NfcAdapter is null
// 07-17 18:32:21.147 32265  9890 W TapAndPay:     at android.nfc.cardemulation.CardEmulation.getInstance(CardEmulation.java:161)
// 07-17 18:32:21.147 32265  9890 W TapAndPay:     at bitm.<init>(:com.google.android.gms@222614037@22.26.14 (150400-459638804):0)
// 07-17 18:32:21.147 32265  9890 W TapAndPay:     at com.google.android.gms.tapandpay.hce.task.TapAndPayAidRegistrationTaskOperation.a(:com.google.android.gms@222614037@22.26.14 (150400-459638804):5)
// 07-17 18:32:21.147 32265  9890 W TapAndPay:     at com.google.android.gms.tapandpay.gcmtask.TapAndPayGcmTaskChimeraService.a(:com.google.android.gms@222614037@22.26.14 (150400-459638804):4)
// 07-17 18:32:21.147 32265  9890 W TapAndPay:     at anvy.call(:com.google.android.gms@222614037@22.26.14 (150400-459638804):4)
// 07-17 18:32:21.147 32265  9890 W TapAndPay:     at java.util.concurrent.FutureTask.run(FutureTask.java:266)
// 07-17 18:32:21.147 32265  9890 W TapAndPay:     at xoc.c(:com.google.android.gms@222614037@22.26.14 (150400-459638804):6)
// 07-17 18:32:21.147 32265  9890 W TapAndPay:     at xoc.run(:com.google.android.gms@222614037@22.26.14 (150400-459638804):7)
// 07-17 18:32:21.147 32265  9890 W TapAndPay:     at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167)
// 07-17 18:32:21.147 32265  9890 W TapAndPay:     at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641)
// 07-17 18:32:21.147 32265  9890 W TapAndPay:     at xth.run(:com.google.android.gms@222614037@22.26.14 (150400-459638804):0)
// 07-17 18:32:21.147 32265  9890 W TapAndPay:     at java.lang.Thread.run(Thread.java:923)

// BTLE_Controller

// 07-17 18:30:11.980 30816 30816 D IDTechSwiperController: 0x63: Ok and Have Next Command.
// 07-17 18:30:11.983 30816 30816 V BoltSDK : On swipe error: Swiper still processing commands.
// 07-17 18:30:11.984 30816 30816 I Choreographer: Skipped 297 frames!  The application may be doing too much work on its main thread.
// 07-17 18:30:11.984 30816 30816 D com.bolt.consumersdk.swiper.CCSwiperController: method: onUpdate()
// 07-17 18:30:11.984 30816 30816 D com.bolt.consumersdk.swiper.CCSwiperController: method: notifyOnLogUpdate()
// 07-17 18:30:11.984 30816 30816 D com.bolt.consumersdk.swiper.CCSwiperController: [onUpdate]
// 07-17 18:30:11.984 30816 30816 D com.bolt.consumersdk.swiper.CCSwiperController: method: onError()
// 07-17 18:30:11.985 30816 30816 D com.bolt.consumersdk.swiper.CCSwiperController: method: notifyOnTransactionError()
// 07-17 18:30:11.985 30816 30816 D com.bolt.consumersdk.swiper.CCSwiperController: [onError]::[Unknown error occurred with MPOS device.]
// 07-17 18:30:11.985 30816 30816 V BoltSDK : On swipe error: Unknown error occurred with MPOS device.
// 07-17 18:30:11.987 30816  9901 I ReactNativeJS: BoltOnDeviceMessage
// 07-17 18:30:11.989 30816  9901 I ReactNativeJS: { message: 'bad card swipe/tap/dip: ' }

// 07-17 18:30:23.589 30816 10608 I BTLE_Controller: ## Resp = 5669564F746563683200010000001253000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
// 07-17 18:30:23.590 30816 10608 D IDTechSwiperController: 0x0000: Succeeded, beginning task .
// 07-17 18:30:23.591 30816 30816 D com.bolt.consumersdk.swiper.CCSwiperController: method: onCardSwipe()
// 07-17 18:30:23.591 30816 30816 D com.bolt.consumersdk.swiper.CCSwiperController: method: notifyOnStartTokenGeneration()
// 07-17 18:30:23.591 30816 30816 D com.bolt.consumersdk.swiper.CCSwiperController: [onStartTokenGeneration]
// 07-17 18:30:23.597 30816  9901 I ReactNativeJS: token generation start
// 07-17 18:30:23.599 30816  9901 I ReactNativeJS: null
// 07-17 18:30:23.609 30816  9713 I System.out: (HTTPLog)-Static: isSBSettingEnabled false
// 07-17 18:30:23.609 30816  9713 I System.out: (HTTPLog)-Static: isSBSettingEnabled false
// 07-17 18:30:23.610   636   748 E Netd    : getNetworkForDns: getNetId from enterpriseCtrl is netid 0
// 07-17 18:30:23.677  1099  1929 D NetdEventListenerService: DNS Requested by : 656, 10302
// 07-17 18:30:23.759   842   842 E MODEMLSV: open_at_channel: open at channel fail, sleep for a while[19(No such device)]
// 07-17 18:30:23.918   797   797 E SLOGCP  : reopen_dev: open /dev/stty_lte28 error [19(No such device)]
// 07-17 18:30:23.987 30816 30816 D ConsumerApi: [Error Tokenizing data]::"com.bolt.consumersdk.domain.CCConsumerError" : {
// 07-17 18:30:23.987 30816 30816 D ConsumerApi:   "mResponseCode" : "500",
// 07-17 18:30:23.987 30816 30816 D ConsumerApi:   "mResponseMessage" : ""
// 07-17 18:30:23.987 30816 30816 D ConsumerApi: }

public class RNBoltReactLibraryModule extends ReactContextBaseJavaModule {

    private int REQUEST_PERMISSIONS = 1000;
    private static final String TAG = "BoltSDK";
    private BluetoothSearchResponseListener mBluetoothSearchResponseListener = null;
    private Map<String, BluetoothDevice> mapDevices = Collections.synchronizedMap(new HashMap<String, BluetoothDevice>());
    ReactApplicationContext context;
    private boolean enableDebugging = false;

    public RNBoltReactLibraryModule(ReactApplicationContext reactContext) {
        super(reactContext);
        context = reactContext;
    }

    @Override
    public String getName() {
        return TAG;
    }

    private void sendEvent(String eventName, @Nullable WritableMap params) {
        context
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(eventName, params);
    }

    // TODO: enable setting the timeout value
    // @ReactMethod
    // public void setCardReadTimeout(int timeoutValue) {
    // }

    @ReactMethod
    public void setDebugging(boolean shouldDebug) {

        enableDebugging = shouldDebug;

        CCConsumer.getInstance().getApi().setDebugEnabled(enableDebugging);
        LogHelper.setEnable(true);
        LogHelper.setLogLevel(LogLevel.DEBUG);
    }

    @ReactMethod
    public void activateDevice() {

        debug("activating device");
        final SwiperControllerManager swipManager = SwiperControllerManager.getInstance();
        ((CCSwiperController) swipManager.getSwiperController()).startReaders(swipManager.getSwiperCaptureMode());
        debug("after device");
    }

    @ReactMethod
    public void connectToDevice(String macAddress) {

        debug("connecting to device: " + macAddress);

        final SwiperControllerManager swipManager = SwiperControllerManager.getInstance();
        
        swipManager.setContext(context);
        swipManager.setSwiperType(SwiperType.IDTech);
        swipManager.setMACAddress(macAddress);

        SwiperControllerListener mSwiperControllerListener = new SwiperControllerListener() {
            @Override
            public void onTokenGenerated(CCConsumerAccount account, CCConsumerError error) {

                if (error == null) {
                    debug("Token Generated...???");
                    debug("this is a ... test");
                    debug(account.getToken());
                    debug(account.getName());
                    debug(account.getExpirationDate());

                    WritableMap params = Arguments.createMap();

                    debug("On token generated: " + account.getToken());

                    params.putString("token", account.getToken());
                    params.putString("name", account.getName());
                    params.putString("expiry", account.getExpirationDate());

                    sendEvent("BoltOnTokenGenerated", params);
                } else {
                    debug("error generating token: " + error.getResponseMessage() + " " + error.getResponseCode());

                    WritableMap params = Arguments.createMap();

                    params.putString("responseError", error.getResponseMessage());
                    params.putInt("responseCode", error.getResponseCode());

                    sendEvent("BoltOnTokenGeneratedError", params);
                }
            }

            @Override
            public void onError(SwiperError swipeError) {

                debug("On swipe error: " + swipeError.toString());

                WritableMap params = Arguments.createMap();
                params.putString("error", swipeError.toString());
                sendEvent("BoltOnSwipeError", params);
            }

            @Override
            public void onSwiperReadyForCard() {

                sendEvent("BoltOnSwiperReady", null);
            }

            @Override
            public void onSwiperConnected() {

                sendEvent("BoltOnSwiperConnected", null);
            }

            @Override
            public void onSwiperDisconnected() {

                sendEvent("BoltOnSwiperDisconnected", null);
            }

            @Override
            public void onBatteryState(BatteryState batteryState) {

                WritableMap params = Arguments.createMap();
                params.putString("batteryState", batteryState.toString());
                sendEvent("BoltOnBatteryState", params);
            }

            @Override
            public void onStartTokenGeneration() {

                sendEvent("BoltOnTokenGenerationStart", null);
            }

            @Override
            public void onLogUpdate(String strLogUpdate) {

                WritableMap params = Arguments.createMap();
                params.putString("message", strLogUpdate);
                sendEvent("BoltOnDeviceMessage", params);
            }

            @Override
            public void onDeviceConfigurationUpdate(String s) {

                WritableMap params = Arguments.createMap();
                params.putString("configUpdate", s);
                sendEvent("BoltOnDeviceConfigurationUpdate", params);
            }

            @Override
            public void onConfigurationProgressUpdate(double v) {

                WritableMap params = Arguments.createMap();
                params.putDouble("progress", v);
                sendEvent("BoltOnDeviceConfigurationProgressUpdate", params);
            }

            @Override
            public void onConfigurationComplete(boolean b) {

                WritableMap params = Arguments.createMap();
                params.putBoolean("isComplete", b);
                sendEvent("BoltOnDeviceConfigurationUpdateComplete", params);
            }

            @Override
            public void onTimeout() {

                sendEvent("BoltOnTimeout", null);
            }

            @Override
            public void onLCDDisplayUpdate(String str) {}

            @Override
            public void onRemoveCardRequested() {

                sendEvent("BoltOnRemoveCardRequested", null);
            }

            @Override
            public void onCardRemoved() {

                sendEvent("BoltOnCardRemoved", null);
            }

            @Override
            public void onDeviceBusy() {

                sendEvent("BoltOnDeviceBusy", null);
            }
        };

        SwiperControllerManager.getInstance().connectToDevice();
        SwiperControllerManager.getInstance().setSwiperControllerListener(mSwiperControllerListener);

        SwiperControllerManager.getInstance().getSwiperController().setDebugEnabled(enableDebugging);
    }

    @ReactMethod
    public void discoverDevice() {

        if (!checkPermission()) {
            debug("need permission");
            requestPermission();
            return;
        }

        CCConsumerApi api = CCConsumer.getInstance().getApi();

        mBluetoothSearchResponseListener = new BluetoothSearchResponseListener() {
            @Override
            public void onDeviceFound(BluetoothDevice device) {

                synchronized (mapDevices) {

                    WritableMap params = Arguments.createMap();

                    params.putString("id", device.getAddress());
                    params.putString("name", device.getName());

                    sendEvent("BoltDeviceFound", params);
                }
            }
        };

        api.startBluetoothDeviceSearch(mBluetoothSearchResponseListener, context, false);
    }

    @ReactMethod
    public void getCardToken(
      String cardNumber,
      String expiryDate,
      String cvv,
      final Promise promise
    ) {

        try {
            validateCardNumber(cardNumber);
            validateCvv(cvv);

            CCConsumerCardInfo mCCConsumerCardInfo = new CCConsumerCardInfo();
            mCCConsumerCardInfo.setCardNumber(cardNumber);
            mCCConsumerCardInfo.setExpirationDate(expiryDate);
            mCCConsumerCardInfo.setCvv(cvv);

            CCConsumer.getInstance().getApi().generateAccountForCard(mCCConsumerCardInfo, new CCConsumerTokenCallback() {
                @Override
                public void onCCConsumerTokenResponseError(CCConsumerError ccConsumerError) {
                    promise.reject(new Exception(ccConsumerError.getResponseMessage()));
                }

                @Override
                public void onCCConsumerTokenResponse(CCConsumerAccount ccConsumerAccount) {
                    promise.resolve(ccConsumerAccount.getToken());
                }
            });
        } catch (ValidateException e) {
            promise.reject(e);
        } catch (Exception e) {
            promise.reject(e);
            e.printStackTrace();
        }
    }

    private void debug(String debugMessage) {

        if (enableDebugging) {
            Log.v(TAG, debugMessage);
        }
    }

    private void validateCardNumber(String cardNumber) throws ValidateException {

        if (!CCConsumerCardUtils.validateCardNumber(cardNumber)) {
            throw new ValidateException("Invalid CardNumber");
        }
    }

    private void validateCvv(String cvv) throws ValidateException {

        if (!CCConsumerCardUtils.validateCvvNumber(cvv)) {
            throw new ValidateException("Invalid CVV");
        }
    }

    private void validateExpiryDate(String expiryDate) throws ValidateException {

        try {
            String[] array = expiryDate.split("/");
            if (!CCConsumerCardUtils.validateExpirationDate(array[0], array[1])) {
                throw new ValidateException("Invalid ExpiryDate");
            }
        } catch (Exception e) {
            throw new ValidateException("Invalid ExpiryDate");
        }
    }

    @ReactMethod
    private void setupConsumerApiEndpoint(String url) {

        CCConsumer.getInstance().getApi().setEndPoint("https://" + url);
    }

    private Boolean checkPermission() {

        return (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {

        final Activity activity = getCurrentActivity();

        String[] permissions = { Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION };
        ActivityCompat.requestPermissions(activity, permissions, REQUEST_PERMISSIONS);
    }
}
