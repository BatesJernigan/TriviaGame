package com.example.triviagame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    final static int CREATE_CODE = 201;
    final static int DELETE_CODE = 204;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void changeToStartTriviaAct(View view) {
        Intent intent = new Intent(MainActivity.this, TriviaActivity.class);
        startActivity(intent);
    }

    public void changeToCreateQuestionAct(View view) {
        Intent intent = new Intent(MainActivity.this, CreateQuestionActivity.class);
        startActivityForResult(intent, CREATE_CODE);
    }

    public void changeToDeleteAllAct(View view) {
        Log.d("demo", "clicked delete");
        // http request for delete all
    }

    public void changeToExitAct(View view) {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
