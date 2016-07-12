package com.example.weishu.contentprovider_management;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * @author weishu
 * @date 16/7/8.
 */
public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    // demo ContentProvider 的URI
    private static Uri URI = Uri.parse("content://com.example.weishu.testcontentprovider.TestContentProvider");

    static int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button query = new Button(this);
        query.setText("query");
        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = getContentResolver().query(URI,
                        null, null, null, null);
                assert cursor != null;
                while (cursor.moveToNext()) {
                    int count = cursor.getColumnCount();
                    StringBuilder sb = new StringBuilder("column: ");
                    for (int i = 0; i < count; i++) {
                        sb.append(cursor.getString(i) + ", ");
                    }
                    Log.d(TAG, sb.toString());
                }
            }
        });

        Button insert = new Button(this);
        insert.setText("insert");
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put("name", "name" + count++);
                getContentResolver().insert(URI, values);
            }
        });

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(query);
        layout.addView(insert);

        setContentView(layout);
    }
}
