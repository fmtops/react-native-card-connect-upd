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
                    debug("error generating token: " + error.getResponseMessage());

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
