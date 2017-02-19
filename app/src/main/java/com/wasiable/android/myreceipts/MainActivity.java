package com.wasiable.android.myreceipts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.samples.vision.barcodereader.BarcodeActivity;
import com.google.android.gms.samples.vision.barcodereader.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, BarcodeActivity.class);
        startActivity(intent);
    }
}
