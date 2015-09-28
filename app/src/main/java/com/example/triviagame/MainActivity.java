package com.example.triviagame;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    final static int CREATE_CODE = 201;
    final static int DELETE_CODE = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void changeToStartTriviaAct(View view) {
        if (isConnectedOnline()) {
            Intent intent = new Intent(MainActivity.this, TriviaActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "You're not connected :(", Toast.LENGTH_LONG).show();
        }
    }

    public void changeToCreateQuestionAct(View view) {
        if (isConnectedOnline()) {
            Intent intent = new Intent(MainActivity.this, CreateQuestionActivity.class);
            startActivityForResult(intent, CREATE_CODE);
        } else {
            Toast.makeText(this, "You're not connected :(", Toast.LENGTH_LONG).show();
        }
    }

    public void changeToDeleteAllAct(View view) {
        if (isConnectedOnline()) {
            Log.d("demo", "clicked delete");
        } else {
            Toast.makeText(this, "You're not connected :(", Toast.LENGTH_LONG).show();
        }
        // http request for delete all
    }

    public void changeToExitAct(View view) {
        finish();
    }

    public boolean isConnectedOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo(); // returns null for no network
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
}
