package com.reactbolt.sdk;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.bolt.consumersdk.CCConsumer;
import com.bolt.consumersdk.CCConsumerTokenCallback;
import com.bolt.consumersdk.domain.CCConsumerAccount;
import com.bolt.consumersdk.domain.CCConsumerCardInfo;
import com.bolt.consumersdk.domain.CCConsumerError;
import com.bolt.consumersdk.network.CCConsumerApi;
import com.bolt.consumersdk.utils.CCConsumerCardUtils;
import com.bolt.consumersdk.listeners.BluetoothSearchResponseListener;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RNBoltReactLibraryModule extends ReactContextBaseJavaModule {

    private static final String TAG = "CardConnect";
    private BluetoothSearchResponseListener mBluetoothSearchResponseListener = null;
    private Map<String, BluetoothDevice> mapDevices = Collections.synchronizedMap(new HashMap<String, BluetoothDevice>());
    ReactApplicationContext context;

    public RNCardConnectReactLibraryModule(ReactApplicationContext reactContext) {
        super(reactContext);
        context = reactContext;
    }

    @Override
    public String getName() {
        return "CardConnect";
    }

    @ReactMethod
    public void discoverDevice() {

        Log.v(TAG, "start of discoverDevice");

        CCConsumerApi api = CCConsumer.getInstance().getApi();
        api.startBluetoothDeviceSearch(mBluetoothSearchResponseListener, context, false);

        mBluetoothSearchResponseListener = new BluetoothSearchResponseListener() {
            @Override
            public void onDeviceFound(BluetoothDevice device) {
                synchronized (mapDevices) {

                    Log.v(TAG, "on device found");
                    Log.v(TAG, device.getAddress());

                    // mapDevices.put(device.getAddress(), device);

                    // deviceListAdapter.clear();

                    // for (BluetoothDevice dev : mapDevices.values()) {
                    //     if (TextUtils.isEmpty(dev.getName())) {
                    //         deviceListAdapter.add(dev.getAddress());
                    //     } else {
                    //         deviceListAdapter.add(dev.getName());
                    //     }
                    // }

                    // deviceListAdapter.notifyDataSetChanged();
                }
            }
        };
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
        }catch (Exception e){
            promise.resolve(e);
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
        CCConsumer.getInstance().getApi().setEndPoint(url);
    }
}
