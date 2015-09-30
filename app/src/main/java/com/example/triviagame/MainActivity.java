package com.example.triviagame;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;

public class MainActivity extends AppCompatActivity {
    final static String GROUP_ID = "9022f64581646f253ecb15501ef68c93";
    final static int CREATE_CODE = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button delete_all_button = (Button) findViewById(R.id.delete_all_btn);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Questions")
            .setMessage("Are you sure you want to delete all your questions?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    RequestParams params = new RequestParams("POST", "http://dev.theappsdr.com/apis/trivia_fall15/deleteAll.php");
                    params.addParam("gid", GROUP_ID);
                    new DeleteAllWithParams().execute(params);
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d("demo", "No");
                }
            })
            .setCancelable(false);
        final AlertDialog alert = builder.create();

        delete_all_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.show();
            }
        });
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

    private class DeleteAllWithParams extends AsyncTask<RequestParams, Integer, String> {
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(RequestParams... params) {
            BufferedReader reader;
            try {
                HttpURLConnection con = params[0].setupConnection();
                publishProgress();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                if (reader.readLine() != null) {
                    return reader.readLine();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            progressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //will execute before dointhe background method
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Deleting Questions");
            progressDialog.setMessage("Deleting ...");
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //gets executed when publish progress gets executed
            super.onProgressUpdate(values);
        }
    }
}
