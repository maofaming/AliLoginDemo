package com.alilogindemo.utils;

import android.app.Activity;
import android.content.Context;

import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;


public abstract class BaseUIConfig implements AuthPageConfig {
    public Activity mActivity;
    public Context mContext;
    public PhoneNumberAuthHelper mAuthHelper;

    public static AuthPageConfig init(Constant.UI_TYPE type, Activity activity, PhoneNumberAuthHelper authHelper) {
        switch (type) {
            case FULL_PORT:
                return new FullPortConfig(activity, authHelper);
//            case FULL_LAND:
//                return new FullLandConfig(activity, authHelper);
            default:
                return null;
        }
    }

    public BaseUIConfig(Activity activity, PhoneNumberAuthHelper authHelper) {
        mActivity = activity;
        mContext = activity.getApplicationContext();
        mAuthHelper = authHelper;
    }

    /**
     *  在横屏APP弹竖屏一键登录页面或者竖屏APP弹横屏授权页时处理特殊逻辑
     *  Android8.0只能启动SCREEN_ORIENTATION_BEHIND模式的Activity
     */
    public void onResume() {

    }

    public void release() {
        mAuthHelper.setAuthListener(null);
        mAuthHelper.setUIClickListener(null);
        mAuthHelper.removeAuthRegisterViewConfig();
        mAuthHelper.removeAuthRegisterXmlConfig();
    }
}
