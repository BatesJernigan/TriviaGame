package com.example.triviagame;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

public class TriviaActivity extends AppCompatActivity {
    private int globalIndex = 0;
    private LinkedList<Question> questionLinkedList = new LinkedList<>();
    private double totalCorrect = 0.0;
    private double correctPercentage = 0.0;
    static final String PERCENT_CORRECT = "percent_correct";
    RequestParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia);
        params = new RequestParams("POST", "http://dev.theappsdr.com/apis/trivia_fall15/checkAnswer.php");
        params.addParam("gid", MainActivity.GROUP_ID);
        if(isConnectedOnline()) {
            new GetQAs().execute("http://dev.theappsdr.com/apis/trivia_fall15/getAll.php");
        } else {
            Toast.makeText(TriviaActivity.this, "Whoops, you're not connected!",
                Toast.LENGTH_LONG).show();
        }

        findViewById(R.id.quit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.next_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnectedOnline()) {
                    int asyncSafeIndex = globalIndex;
                    RadioGroup radioGroup = (RadioGroup) findViewById(R.id.answer_radio_group);

                    if(radioGroup.getCheckedRadioButtonId() != -1) {
                        radioGroup.removeAllViews();
                        params.addParam("a", "" + (radioGroup.getCheckedRadioButtonId()));
                        params.addParam("qid", questionLinkedList.get(globalIndex).getQuestionId() + "");
                        new CheckAnswer().execute(params);
                    } else {
                        globalIndex++;
                        if(globalIndex == (double)questionLinkedList.size()) {
                            Intent intent = new Intent(TriviaActivity.this, ResultActivity.class);
                            correctPercentage = (totalCorrect / (double)questionLinkedList.size()) * 100.0;
                            intent.putExtra(PERCENT_CORRECT, (int) correctPercentage);
                            startActivity(intent);
                            finish();
                        } else {
                            setViewFields();
                        }
                    }
                } else {
                    Toast.makeText(TriviaActivity.this, "Whoops, you're not connected!",
                        Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setViewFields() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.answer_radio_group);
        TextView questionText = (TextView) findViewById(R.id.question_text_view);
        TextView questionId = (TextView) findViewById(R.id.question_number_text_view);
        for(int i = 0; i < questionLinkedList.get(globalIndex).getAnswers().size(); i++) {
            String answer = questionLinkedList.get(globalIndex).getAnswers().get(i);
            RadioButton radioButton = new RadioButton(TriviaActivity.this);
            radioButton.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            radioButton.setText(answer);
            radioButton.setId(i);
            radioGroup.addView(radioButton);
        }
        questionText.setText(questionLinkedList.get(globalIndex).getQuestion());
        questionId.setText("Q " + questionLinkedList.get(globalIndex).getQuestionId());

        if(questionLinkedList.get(globalIndex).getPictureUrl() != null) {
            new GetImage().execute(questionLinkedList.get(globalIndex).getPictureUrl());
        }
    }

    private class GetQAs extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            BufferedReader reader;
            try {
                URL url = new URL(params[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                publishProgress();
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                return sb.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            if (result != null) {
                String[] resultArray = result.split("\n");
                for(String line : resultArray) {
                    Question newQuestion = makeQuestion(line);

                    if (newQuestion.getQuestionId() != -1)
                        questionLinkedList.add(newQuestion);
                }
                if (questionLinkedList.get(globalIndex).getQuestionId() != -1) {
                    setViewFields();
                }
            } else {
                Log.d("demo", "Null data");
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(TriviaActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading Questions");
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        private Question makeQuestion(String resultLine) {
            Integer questionId;
            String url = "";
            LinkedList<String> answers = new LinkedList<>();
            int number_semicolons = resultLine.length() - resultLine.replace(";", "").length();

            String[] lineArray = resultLine.split(";");
            if(resultLine.contains("http://dev.theappsdr.com/apis/trivia_fall15/imgs/")) {
                try {
                    url = lineArray[lineArray.length-1];
                } catch (IndexOutOfBoundsException e) {
                    url = null;
                }
            }
            try {
                 questionId = Integer.parseInt(lineArray[0]);
            } catch (NumberFormatException e) {
                return new Question(answers, null, null, -1);
            }
            String question = lineArray[1];

            for(int i = 2; i < (number_semicolons - 1); i++) {
                answers.add(lineArray[i]);
            }
            return new Question(answers, url, question, questionId);
        }
    }

    private class CheckAnswer extends AsyncTask<RequestParams, Integer, String> {
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(RequestParams... params) {
            BufferedReader reader;
            String line;
            try {
                HttpURLConnection con = params[0].setupConnection();
                con.setRequestMethod("POST");
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                publishProgress();

                if ((line = reader.readLine()) != null) {
                    return line;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            if (result != null) {
                globalIndex++;
                if(Integer.parseInt(result) == 1) {
                    totalCorrect += 1.0;
                    Toast.makeText(TriviaActivity.this, "Great Job!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(TriviaActivity.this, "Ouch, Not Quite.", Toast.LENGTH_LONG).show();
                }
                if(globalIndex == (questionLinkedList.size()-1)) {
                    Intent intent = new Intent(TriviaActivity.this, ResultActivity.class);
                    correctPercentage = (totalCorrect / (double)questionLinkedList.size()) * 100.0;
                    intent.putExtra(PERCENT_CORRECT, (int) correctPercentage);
                    startActivity(intent);
                    finish();
                } else {
                    setViewFields();
                }
            } else {
                Log.d("demo", "Null data");
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(TriviaActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Checking Answer");
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

    private class GetImage extends AsyncTask<String, Void, Bitmap> {
        ProgressDialog progressDialog;

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                publishProgress();
                Bitmap image = new BitmapFactory().decodeStream(con.getInputStream());
                return image;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

            @Override
        protected void onPostExecute(Bitmap result) {
            ImageView imageView = (ImageView) findViewById(R.id.start_trivia_image_view);
            if (result != null) {
                imageView.setImageBitmap(result);
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(TriviaActivity.this, R.drawable.no_image));
            }
            progressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(TriviaActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading Image");
            progressDialog.show();
        }
    }

    public boolean isConnectedOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
}
