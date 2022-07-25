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

    @ReactMethod
    public void activateDevice() {
        final SwiperControllerManager swipManager = SwiperControllerManager.getInstance();
        ((CCSwiperController) swipManager.getSwiperController()).startReaders(swipManager.getSwiperCaptureMode());
    }

    @ReactMethod
    public void connectToDevice(String macAddress) {

        Log.v(TAG, "connecting to device: " + macAddress);

        final SwiperControllerManager swipManager = SwiperControllerManager.getInstance();
        
        swipManager.setContext(context);
        swipManager.setSwiperType(SwiperType.IDTech);
        swipManager.setMACAddress(macAddress);

        SwiperControllerListener mSwiperControllerListener = new SwiperControllerListener() {
            @Override
            public void onTokenGenerated(CCConsumerAccount account, CCConsumerError error) {
                Log.d(TAG, "onTokenGenerated");

                if (error == null) {
                    Log.d(TAG, "Token Generated");

                    WritableMap params = Arguments.createMap();

                    params.putString("token", account.getToken());
                    params.putString("name", account.getName());

                    sendEvent("BoltOnTokenGenerated", params);
                } else {
                    Log.d(TAG, error.getResponseMessage());

                    WritableMap params = Arguments.createMap();

                    params.putString("responseError", error.getResponseMessage());
                    params.putInt("responseCode", error.getResponseCode());

                    sendEvent("BoltOnTokenGeneratedError", params);
                }
            }

            @Override
            public void onError(SwiperError swipeError) {
                Log.d(TAG, swipeError.toString());

                WritableMap params = Arguments.createMap();
                params.putString("error", swipeError.toString());
                sendEvent("BoltOnSwipeError", params);
            }

            @Override
            public void onSwiperReadyForCard() {
                Log.d(TAG, "Swiper ready for card");
                sendEvent("BoltOnSwiperReady", null);
            }

            @Override
            public void onSwiperConnected() {
                Log.d(TAG, "Swiper connected");
                sendEvent("BoltOnSwiperConnected", null);
            }

            @Override
            public void onSwiperDisconnected() {
                Log.d(TAG, "Swiper disconnected");
                sendEvent("BoltOnSwiperDisconnected", null);
            }

            @Override
            public void onBatteryState(BatteryState batteryState) {
                Log.d(TAG, batteryState.toString());

                WritableMap params = Arguments.createMap();
                params.putString("batteryState", batteryState.toString());
                sendEvent("BoltOnBatteryState", params);
            }

            @Override
            public void onStartTokenGeneration() {
                Log.d(TAG, "Token Generation started.");
                sendEvent("BoltOnTokenGenerationStart", null);
            }

            @Override
            public void onLogUpdate(String strLogUpdate) {
                Log.d(TAG, strLogUpdate);

                WritableMap params = Arguments.createMap();
                params.putString("log", strLogUpdate);
                sendEvent("BoltOnLogUpdate", params);
            }

            @Override
            public void onDeviceConfigurationUpdate(String s) {
                Log.d(TAG, s);

                WritableMap params = Arguments.createMap();
                params.putString("configUpdate", s);
                sendEvent("BoltOnDeviceConfigurationUpdate", params);
            }

            @Override
            public void onConfigurationProgressUpdate(double v) {
                Log.d(TAG, Double.toString(v));

                WritableMap params = Arguments.createMap();
                params.putDouble("progress", v);
                sendEvent("BoltOnDeviceConfigurationProgressUpdate", params);
            }

            @Override
            public void onConfigurationComplete(boolean b) {
                Log.d(TAG, Boolean.toString(b));

                WritableMap params = Arguments.createMap();
                params.putBoolean("isComplete", b);
                sendEvent("BoltOnDeviceConfigurationUpdateComplete", params);
            }

            @Override
            public void onTimeout() {
                Log.d(TAG, "on timeout");
                sendEvent("BoltOnTimeout", null);
            }

            @Override
            public void onLCDDisplayUpdate(String str) {
            }

            @Override
            public void onRemoveCardRequested() {
                Log.d(TAG, "on card remove requested");
                sendEvent("BoltOnRemoveCardRequested", null);
            }

            @Override
            public void onCardRemoved() {
                Log.d(TAG, "on card removed");
                sendEvent("BoltOnCardRemoved", null);
            }

            @Override
            public void onDeviceBusy() {
                Log.d(TAG, "on device busy");
                sendEvent("BoltOnDeviceBusy", null);
            }
        };

        SwiperControllerManager.getInstance().connectToDevice();
        SwiperControllerManager.getInstance().setSwiperControllerListener(mSwiperControllerListener);
    }

    @ReactMethod
    public void discoverDevice() {

        Log.v(TAG, "start of discoverDevice");

        Log.v(TAG, "before checking permission");
        if (!checkPermission()) {
            Log.v(TAG, "need permission");
            requestPermission();
            return;
        }
        Log.v(TAG, "after checking permission");

        CCConsumerApi api = CCConsumer.getInstance().getApi();

        mBluetoothSearchResponseListener = new BluetoothSearchResponseListener() {
            @Override
            public void onDeviceFound(BluetoothDevice device) {
                synchronized (mapDevices) {

                    WritableMap params = Arguments.createMap();

                    params.putString("macAddress", device.getAddress());
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
                    // Log.v(TAG, new Gson().toJson(ccConsumerError));
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
