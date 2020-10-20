package com.alilogindemo.utils;

import android.content.Context;

import com.facebook.react.bridge.ReadableArray;

public interface AuthPageConfig {

    /**
     * 配置授权页样式
     */
    void configAuthPage(final Context context, EventBridge eventBridge, ReadableArray privacyArray);

    /**
     * android8.0兼容
     */
    void onResume();

    /**
     * 释放sdk内部引用，防止内存泄漏
     */
    void release();
}
