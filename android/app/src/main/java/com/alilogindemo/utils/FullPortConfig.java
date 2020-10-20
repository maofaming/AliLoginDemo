package com.alilogindemo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.alilogindemo.R;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.mobile.auth.gatewayauth.AuthUIConfig;
import com.mobile.auth.gatewayauth.AuthUIControlClickListener;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import com.mobile.auth.gatewayauth.ResultCode;


public class FullPortConfig extends BaseUIConfig {
    private final String TAG = "全屏竖屏样式";

    public FullPortConfig(Activity activity, PhoneNumberAuthHelper authHelper) {
        super(activity, authHelper);
    }

    @Override
    public void configAuthPage(final Context context, EventBridge eventBridge,  ReadableArray privacyArray) {
        // 设置privacy
        ReadableMap privacyOne = privacyArray.getMap(0);
        ReadableMap privacyTwo = privacyArray.getMap(1);

        mAuthHelper.setUIClickListener(new AuthUIControlClickListener() {
            @Override
            public void onClick(String code, Context context, JSONObject jsonObj) {
                switch (code) {
                    //点击授权页默认样式的返回按钮
                    case ResultCode.CODE_ERROR_USER_CANCEL:
                        Log.e(TAG, "点击了授权页默认返回按钮");
                        mAuthHelper.quitLoginPage();
                        mActivity.finish();
                        break;
                    //点击授权页默认样式的切换其他登录方式 会关闭授权页
                    //如果不希望关闭授权页那就setSwitchAccHidden(true)隐藏默认的  通过自定义view添加自己的
                    case ResultCode.CODE_ERROR_USER_SWITCH:
                        Log.e(TAG, "点击了授权页默认切换其他登录方式");
                        break;
                    //点击一键登录按钮会发出此回调
                    //当协议栏没有勾选时 点击按钮会有默认toast 如果不需要或者希望自定义内容 setLogBtnToastHidden(true)隐藏默认Toast
                    //通过此回调自己设置toast
                    case ResultCode.CODE_ERROR_USER_LOGIN_BTN:
                        if (!jsonObj.getBoolean("isChecked")) {
//                            Toast.makeText(mContext, R.string.custom_toast, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    //checkbox状态改变触发此回调
                    case ResultCode.CODE_ERROR_USER_CHECKBOX:
                        Log.e(TAG, "checkbox状态变为" + jsonObj.getBoolean("isChecked"));
                        break;
                    //点击协议栏触发此回调
                    case ResultCode.CODE_ERROR_USER_PROTOCOL_CONTROL:
                        Log.e(TAG, "点击协议，" + "name: " + jsonObj.getString("name") + ", url: " + jsonObj.getString("url"));
//                        eventBridge.sendClick(jsonObj.getString("name"), jsonObj.getString("url"));
                        break;
                    default:
                        break;

                }
            }
        });
        mAuthHelper.removeAuthRegisterXmlConfig();
        mAuthHelper.removeAuthRegisterViewConfig();

        int authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
        if (Build.VERSION.SDK_INT == 26) {
            authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_BEHIND;
        }
        mAuthHelper.setAuthUIConfig(new AuthUIConfig.Builder()
//                .setAppPrivacyOne("《隐私协议》", "https://test.h5.app.tbmao.com/user")
//                .setAppPrivacyTwo("《用户协议》", "https://www.baidu.com")
//                .setAppPrivacyColor(Color.GRAY, Color.parseColor("#002E00"))
                //隐藏默认切换其他登录方式
                .setSwitchAccHidden(true)
                //隐藏默认Toast
                .setLogBtnToastHidden(true)
                //沉浸式状态栏
                .setStatusBarColor(Color.WHITE)
                .setNavColor(Color.TRANSPARENT)
                .setNavText("")
                .setNavReturnImgPath("shanyan_back")

                .setLightColor(true)
                .setWebNavTextSize(20)
                //图片或者xml的传参方式为不包含后缀名的全称 需要文件需要放在drawable或drawable-xxx目录下 in_activity.xml, mytel_app_launcher.png
                .setAuthPageActIn("in_activity", "out_activity")
                .setAuthPageActOut("in_activity", "out_activity")
                .setVendorPrivacyPrefix("《")
                .setVendorPrivacySuffix("》")
                // 设置logo
                .setLogoImgPath("shanyan_logo")
                .setLogoWidth(108)
                .setLogoHeight(84)
                .setLogoOffsetY(85)

                //授权页号码栏：
                .setNumberColor(Color.parseColor("#FD2359"))  //设置手机号码字体颜色
                .setNumFieldOffsetY(191)    //设置号码栏相对于标题栏下边缘y偏移
                .setNumberSize(20)

                //一键登录按钮三种状态背景示例login_btn_bg.xml
                .setLogBtnBackgroundPath("shanyan_login_button")
//                .setLogBtnText("")
                .setLogBtnHeight(54)
                .setLogBtnWidth(310)
                .setLogBtnOffsetY(266)

                //授权页隐私栏：
                .setAppPrivacyOne("《" + privacyOne.getString("text") + "》", privacyOne.getString("url"))  //设置开发者隐私条款1名称和URL(名称，url)
                .setAppPrivacyTwo("《" + privacyTwo.getString("text") + "》", privacyTwo.getString("url"))  //设置开发者隐私条款2名称和URL(名称，url)
                .setAppPrivacyColor(Color.parseColor("#9E9E9E"), Color.parseColor("#000000"))    //	设置隐私条款名称颜色(基础文字颜色，协议文字颜色)
                .setPrivacyOffsetY(343)//设置隐私条款相对于屏幕下边缘y偏
                .setPrivacyTextSize(12)
                .setPrivacyMargin(40)
                .setProtocolGravity(Gravity.CENTER)


                .setSloganHidden(true)
//                .setSloganOffsetY(340)

                .setScreenOrientation(authPageOrientation)
                .create());
    }


}
