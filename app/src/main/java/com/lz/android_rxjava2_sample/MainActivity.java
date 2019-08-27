package com.lz.android_rxjava2_sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onSimpleDemoClick(View view) {
        Intent intent = new Intent(MainActivity.this, SimpleDemoActivity.class);
        startActivity(intent);
    }

    public void onSchedulersClick(View view) {
        Intent intent = new Intent(MainActivity.this, SchedulersActivity.class);
        startActivity(intent);
    }

    public void onMapOperatorClick(View view) {
        Intent intent = new Intent(MainActivity.this, MapOperatorActivity.class);
        startActivity(intent);
    }

    public void onZipOperatorClick(View view) {
        Intent intent = new Intent(MainActivity.this, ZipOperatorActivity.class);
        startActivity(intent);
    }

    public void onBackpressureClick(View view) {
        Intent intent = new Intent(MainActivity.this, BackpressureActivity.class);
        startActivity(intent);
    }

    public void onFlowableClick(View view) {
        Intent intent = new Intent(MainActivity.this, FlowableActivity.class);
        startActivity(intent);
    }

    public void onFlowable2Click(View view) {
        Intent intent = new Intent(MainActivity.this, FlowableActivity2.class);
        startActivity(intent);
    }

    public void onFlowable3Click(View view) {
        Intent intent = new Intent(MainActivity.this, FlowableActivity3.class);
        startActivity(intent);
    }
}
