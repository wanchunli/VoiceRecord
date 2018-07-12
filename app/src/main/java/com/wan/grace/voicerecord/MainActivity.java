package com.wan.grace.voicerecord;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    CommonSoundItemView soundItemView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        soundItemView = findViewById(R.id.item_view);
        soundItemView.setOnRecordClickListener(new CommonSoundItemView.OnRecordClickListener() {
            @Override
            public void onRecordStart(View view) {
                soundItemView.startTimeRecord();
            }

            @Override
            public void onRecordEnd(View view) {
                soundItemView.stopTimeRecord();
            }

            @Override
            public void onRecordDelete(View view) {
                soundItemView.initRecord();
            }
        });
    }
}
