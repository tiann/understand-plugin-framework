package com.weishu.upf.service_management.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * @author weishu
 * @date 16/5/9
 */
public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        findViewById(R.id.startService1).setOnClickListener(this);
        findViewById(R.id.startService2).setOnClickListener(this);
        findViewById(R.id.stopService1).setOnClickListener(this);
        findViewById(R.id.stopService2).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startService1:
                startService(new Intent().setComponent(
                        new ComponentName("com.weishu.upf.demo.app2", "com.weishu.upf.demo.app2.TargetService1")));

                break;
            case R.id.startService2:
                startService(new Intent().setComponent(
                        new ComponentName("com.weishu.upf.demo.app2", "com.weishu.upf.demo.app2.TargetService2")));
                break;

            case R.id.stopService1:
                stopService(new Intent().setComponent(
                        new ComponentName("com.weishu.upf.demo.app2", "com.weishu.upf.demo.app2.TargetService1")));
                break;

            case R.id.stopService2:
                stopService(new Intent().setComponent(
                        new ComponentName("com.weishu.upf.demo.app2", "com.weishu.upf.demo.app2.TargetService2")));
                break;
        }
    }
}
