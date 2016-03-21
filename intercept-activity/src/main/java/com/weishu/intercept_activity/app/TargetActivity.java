package com.weishu.intercept_activity.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * 要注意的是,这个Activity并没有再Manifest中注册!!!
 * <p/>
 * 且看我们如何瞒天过海, 成功启动它.
 * <p/>
 * Created by weishu on 16/1/7.
 */
public class TargetActivity extends Activity {

    private static final String TAG = "TargetActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called with " + "savedInstanceState = [" + savedInstanceState + "]");
        TextView tv = new TextView(this);
        tv.setText("TargetActivity 启动成功!!!");
        setContentView(tv);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called with " + "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called with " + "");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called with " + "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called with " + "");
    }
}
