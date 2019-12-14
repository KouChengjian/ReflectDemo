package com.xj.hookdemo.activityhook;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.xj.hookdemo.R;

public class TestClickActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_click);

        View v1 = findViewById(R.id.v1);
        View v2 = findViewById(R.id.v2);
        View v3 = findViewById(R.id.v3);
        v3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("msg","v3-onTouch = " + event.getAction());
                return false;
            }
        });
        v3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("msg","v3-OnClick");
            }
        });
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.e("msg", "EventActivity-dispatch");
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e("msg", "EventActivity-onTouch = "  + event.getAction());
        return super.onTouchEvent(event);
    }

}
