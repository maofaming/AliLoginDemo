package com.alilogindemo.aliphone;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alilogindemo.utils.AuthPageConfig;
import com.alilogindemo.utils.BaseUIConfig;
import com.alilogindemo.utils.Constant;
import com.alilogindemo.utils.EventBridge;
import com.facebook.react.BuildConfig;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import com.mobile.auth.gatewayauth.PreLoginResultListener;
import com.mobile.auth.gatewayauth.ResultCode;
import com.mobile.auth.gatewayauth.TokenResultListener;
import com.mobile.auth.gatewayauth.model.TokenRet;


public class AliPhoneLoginModule extends ReactContextBaseJavaModule {
    private static final String TAG = AliPhoneLoginModule.class.getSimpleName();
    private final ReactApplicationContext reactContext;
    private PhoneNumberAuthHelper mPhoneNumberAuthHelper;
    private TokenResultListener mCheckListener;
    private TokenResultListener mTokenResultListener;
    private Constant.UI_TYPE mUIType = Constant.UI_TYPE.FULL_PORT;
    private AuthPageConfig mUIConfig;

    public AliPhoneLoginModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "AliPhoneLogin";
    }

    @ReactMethod
    public void init(String loginKey, Promise promise) {
        Log.i(TAG, "AliPhoneLogin: " + loginKey);
        mCheckListener = new TokenResultListener() {
            @Override
            public void onTokenSuccess(String s) {
                try {
                    Log.i(TAG, "checkEnvAvailable success：" + s);
                    TokenRet pTokenRet = JSON.parseObject(s, TokenRet.class);
                    if (ResultCode.CODE_ERROR_ENV_CHECK_SUCCESS.equals(pTokenRet.getCode())) {
                        accelerateLoginPage(5000, promise);
                    }
                } catch (Exception e) {
                    promise.reject(e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onTokenFailed(String s) {
                Log.e(TAG, "checkEnvAvailable fail：" + s);
                promise.reject(new Error(s));
                //终端环境检查失败之后 跳转到其他号码校验方式
            }
        };
        mPhoneNumberAuthHelper = PhoneNumberAuthHelper.getInstance(reactContext, mCheckListener);
        mPhoneNumberAuthHelper.setAuthSDKInfo(loginKey);
        mPhoneNumberAuthHelper.checkEnvAvailable(PhoneNumberAuthHelper.SERVICE_TYPE_LOGIN);
        mUIConfig = BaseUIConfig.init(mUIType, getCurrentActivity(), mPhoneNumberAuthHelper);
    }

    /**
     * 在不是一进app就需要登录的场景 建议调用此接口 加速拉起一键登录页面
     * 等到用户点击登录的时候 授权页可以秒拉
     * 预取号的成功与否不影响一键登录功能，所以不需要等待预取号的返回。
     *
     * @param timeout
     */
    public void accelerateLoginPage(int timeout, Promise promise) {
        mPhoneNumberAuthHelper.accelerateLoginPage(timeout, new PreLoginResultListener() {
            @Override
            public void onTokenSuccess(String s) {
                Log.e(TAG, "预取号成功: " + s);
                promise.resolve(s);
            }

            @Override
            public void onTokenFailed(String s, String s1) {
                Log.e(TAG, "预取号失败：" + ", " + s1);
                promise.reject(new Error(s1));
            }
        });
    }

    /**
     * 拉起授权页
     *
     * @param timeout 超时时间
     */
    @ReactMethod
    public void getLoginToken(final String eventName, int timeout, ReadableArray privacyArray) {
        final EventBridge eventBridge = new EventBridge(reactContext, eventName);
        mUIConfig.configAuthPage(getReactApplicationContext(), eventBridge, privacyArray);
        mTokenResultListener = new TokenResultListener() {
            @Override
            public void onTokenSuccess(String s) {
                TokenRet tokenRet = null;
                try {
                    tokenRet = JSON.parseObject(s, TokenRet.class);
                    if (ResultCode.CODE_START_AUTHPAGE_SUCCESS.equals(tokenRet.getCode())) {
                        Log.i(TAG, "唤起授权页成功：" + s);
                        eventBridge.send("openLogin", tokenRet.getCode(), s);
                    }

                    if (ResultCode.CODE_GET_TOKEN_SUCCESS.equals(tokenRet.getCode())) {
                        Log.i(TAG, "获取token成功：" + s);
                        mPhoneNumberAuthHelper.quitLoginPage();
                        mUIConfig.release();
                        eventBridge.send("loginResult", tokenRet.getCode(), tokenRet.getToken());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTokenFailed(String s) {
                Log.e(TAG, "获取token失败：" + s);
                //如果环境检查失败 使用其他登录方式
                TokenRet tokenRet = null;
                WritableMap params = Arguments.createMap();
                params.putString("type", "loginResult");
                try {
                    tokenRet = JSON.parseObject(s, TokenRet.class);
                    eventBridge.send("loginResult", tokenRet.getCode(),tokenRet.getMsg());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mPhoneNumberAuthHelper.quitLoginPage();
                mUIConfig.release();
            }
        };
        mPhoneNumberAuthHelper.setAuthListener(mTokenResultListener);
        mPhoneNumberAuthHelper.getLoginToken(reactContext, timeout);
    }
}

