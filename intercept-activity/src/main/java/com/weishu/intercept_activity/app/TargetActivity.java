package com.weishu.intercept_activity.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * 要注意的是,这个Activity并没有再Manifest中注册!!!
 *
 * 且看我们如何偷梁换柱, 成功启动它.
 *
 * Created by weishu on 16/1/7.
 */
public class TargetActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("TargetActivity 启动成功!!!");
        setContentView(tv);

    }
}
