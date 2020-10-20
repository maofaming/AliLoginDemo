package com.alilogindemo.utils;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class EventBridge {
    private final ReactApplicationContext reactContext;
    private final String eventName;
    private String type;
    private String code;
    private String result;


    public EventBridge(ReactApplicationContext context, String name) {
        eventName = name;
        reactContext = context;
    }

    public void setType(String sType) {
        type = sType;
    }

    public void setCode(String sCode) {
        code = sCode;
    }

    public void setResult(String sResult) {
        result = sResult;
    }

    public void send(String type, String code, String result) {
        setType(type);
        setCode(code);
        setResult(result);
        sendEvent();
    }

    public void sendEvent() {
        WritableMap params = Arguments.createMap();
        params.putString("type", type);
        params.putString("code", code);
        params.putString("result", result);
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

    public void sendClick(String name, String url){
        WritableMap params = Arguments.createMap();
        params.putString("type", "clickUrl");
        params.putString("name", name);
        params.putString("url", url);
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }
}
