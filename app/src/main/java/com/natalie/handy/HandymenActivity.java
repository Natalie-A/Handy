package com.natalie.handy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HandymenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handymen);
        //Getting the intents sent from main activity
        Intent intent = getIntent();
        final String serviceName = intent.getStringExtra(RecyclerViewAdapter.KEY_NAME);

    }
}