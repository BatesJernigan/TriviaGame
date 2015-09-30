package com.example.triviagame;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        TextView progressBarTextView = (TextView) findViewById(R.id.progress_bar_text_view);
        Log.d("demo", "this intent " + this.getIntent().getExtras().getInt(TriviaActivity.PERCENT_CORRECT));
        int integerProgress = this.getIntent().getExtras().getInt(TriviaActivity.PERCENT_CORRECT);
        progressBar.setProgress(integerProgress);
        progressBarTextView.setText(integerProgress+"%");

        Button try_again_btn = (Button) findViewById(R.id.try_again_btn);
        Button quit_btn = (Button) findViewById(R.id.results_quit_btn);

        try_again_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(ResultActivity.this, TriviaActivity.class);
                startActivity(intent);
            }
        });

        quit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
