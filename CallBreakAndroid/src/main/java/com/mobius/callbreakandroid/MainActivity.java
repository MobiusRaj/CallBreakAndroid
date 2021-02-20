package com.mobius.callbreakandroid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.mobius.callbreakandroid.game_activity.Activity_CallBreakList;
import com.mobius.callbreakandroid.utility_base.GlobalLoader_new;
import com.mobius.callbreakandroid.utility_base.ParentActivity;

public class MainActivity extends ParentActivity {

    private GlobalLoader_new loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        loader = new GlobalLoader_new(MainActivity.this);
//        GlobalLoaderSHOW(getResources().getString(R.string.PleaseWait));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(getApplicationContext(), Activity_CallBreakList.class);
                startActivity(i);
                finish();
            }
        },1000);
    }
}