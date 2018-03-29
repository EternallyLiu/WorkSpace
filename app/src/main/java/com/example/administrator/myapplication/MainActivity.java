package com.example.administrator.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private boolean isPause = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final CustomProgressBar progressBar = findViewById(R.id.progressbar);
        final AdvancedCountdownTimer timer = new AdvancedCountdownTimer(
                (long) (10000 - progressBar.getCurrentValue() * 100), 100) {

            @Override
            public void onTick(long millisUntilFinished, int percent) {
                System.out.println("outprogress  ~~~~ " + (float) ((10000 - millisUntilFinished) * 100 / 10000));
                progressBar.setProgress((float) ((10000 - millisUntilFinished) * 100 / 10000)
                        , false);
            }

            @Override
            public void onFinish() {
                Toast.makeText(MainActivity.this, "计时器倒计时已完成", Toast.LENGTH_SHORT).show();
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
                progressBar.startValue();
            }
        });

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

        Button btn_start = findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.start();
                isPause = false;
                progressBar.startValue();
            }
        });
    }
}
