package com.example.administrator.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private boolean isPause = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final CustomProgressBar progressBar = findViewById(R.id.progressbar);
        progressBar.setCustomProgressListener(new CustomProgressBar.CustomProgressListener() {
            @Override
            public void onPause() {
                Toast.makeText(MainActivity.this, "暂停", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onContinue() {
                Toast.makeText(MainActivity.this, "开启", Toast.LENGTH_SHORT).show();
            }
        });

        final AdvancedCountdownTimer timer = new AdvancedCountdownTimer(
                (long) (10000 - progressBar.getCurrentValue() * 100), 100) {

            @Override
            public void onTick(long millisUntilFinished, int percent) {
                progressBar.setProgress((float) ((10000 - millisUntilFinished) * 100 / 10000)
                        , false);
            }

            @Override
            public void onFinish() {
                progressBar.setProgress(100, false);
            }
        };

        timer.setStateListener(new AdvancedCountdownTimer.StateListenr() {
            @Override
            public void pause() {
                progressBar.pauseValue();
            }

            @Override
            public void resume() {
                progressBar.continueValue();
            }
        });
        timer.start();

        progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPause) {
                    isPause = true;
                    timer.pause();

                } else {
                    isPause = false;
                    timer.resume();
                }
            }
        });

        Button btn_delete = findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.deleteLastValue(progressBar.getListSize() - 1);
            }
        });
    }
}
