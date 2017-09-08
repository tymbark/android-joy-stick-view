package com.damianmichalak.joystick;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.damianmichalak.joy_stick.JoyStickView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final JoyStickView joyStickView = (JoyStickView) findViewById(R.id.joystick);
        final AppCompatSeekBar seekAngle = (AppCompatSeekBar) findViewById(R.id.seek_angle);
        final AppCompatSeekBar seekItems = (AppCompatSeekBar) findViewById(R.id.seek_items);
        final AppCompatSeekBar seekSpread = (AppCompatSeekBar) findViewById(R.id.seek_spread);
        final TextView angle = (TextView) findViewById(R.id.angle);
        final TextView items = (TextView) findViewById(R.id.items);
        final TextView spread = (TextView) findViewById(R.id.spread);

        findViewById(R.id.mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joyStickView.setOpenedFixedMode(!joyStickView.getMode());
            }
        });

        seekAngle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                angle.setText("angle: " + progress);
                joyStickView.setAngle(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekItems.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                items.setText("count: " + progress);
                joyStickView.setItemsCount(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekSpread.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                final float x = (float) progress / 100;
                spread.setText("spread: " + x);
                joyStickView.setSpread(x);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
}
