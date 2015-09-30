package com.example.triviagame;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class CreateQuestionActivity extends Activity {

    String attachmentName = "bitmap";
    String attachmentFileName = "bitmap.bmp";
    String crlf = "\r\n";
    String twoHyphens = "--";
    int count = 0;
    int name_count = -1;
    int answer = 0;
    String qFinal = "";
    String group_id = "9022f64581646f253ecb15501ef68c93";
    ProgressDialog progressDialog;
    private static final int SELECT_PICTURE = 1;
    Uri uri;
    ArrayList<String> nlist = new ArrayList<>();
    final StringBuilder listString = new StringBuilder();
    Bitmap bitmap = null;
    String boundary =  "*****";

    EditText question;
    Button plus;
    Button submit;
    EditText option;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);

        option = (EditText) findViewById(R.id.editTextOption);

        plus = (Button) findViewById(R.id.plusbtn);
        submit = (Button) findViewById(R.id.SubmitBtn);
        Button select_img = (Button) findViewById(R.id.SelectImgBtn);


        select_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
            }
        });


        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String optname = option.getText().toString();
                if (optname.isEmpty()) {
                    Toast.makeText(CreateQuestionActivity.this, "Please enter an option name", Toast.LENGTH_SHORT).show();

                } else {
                    DisplayRadioButton(optname);
                    option.setText("");
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bitmap != null) {
                    RequestParams params = new RequestParams("POST", "http://dev.theappsdr.com/apis/trivia_fall15/uploadPhoto.php");
                    Log.d("demo", "bitmap string value of" + String.valueOf(bitmap));
                    Log.d("demo", "uri to string" + uri.toString());
                    params.addParam("uploaded_file", uri.getPath());//String.valueOf(bitmap));
                    new UploadImageWithParams().execute(params);
                } else {
                    question = (EditText) findViewById(R.id.editTextQuestion);

                    if(question.getText().toString().isEmpty()) {
                        Toast.makeText(CreateQuestionActivity.this, "Please type a question",
                            Toast.LENGTH_SHORT).show();
                    }
                    else if(count < 2) {
                        Toast.makeText(CreateQuestionActivity.this, "Number of options should be more"+
                            " than 2.\n Please add more options", Toast.LENGTH_LONG).show();
                    }
                    else {
                        if(bitmap != null) {
                            for(int i =0; i < nlist.size(); i++) {
                                listString.append(nlist.get(i)+";");
                            }
                            qFinal = question.getText().toString() + ";" + listString + ";"+ answer +";";
                        } else {
                            for(int i =0; i < nlist.size(); i++) {
                                listString.append(nlist.get(i)+";");
                            }
                            Log.d("demo", "list string create question\n" + listString.toString());
                            qFinal = question.getText().toString() + ";" + listString +";" + answer +";";
                            Log.d("demo", "question final\n" + qFinal);
                            RequestParams params = new RequestParams("POST", "http://dev.theappsdr.com/apis/trivia_fall15/saveNew.php");
                            params.addParam("gid", group_id);
                            params.addParam("q", qFinal);

                            Log.d("demo", "params in create question\n" + params.toString());
                            new SaveQueswithParams().execute(params);
                        }
                    }
                }

            }
        });
    }

    public void DisplayRadioButton(String name) {
        count = count+1;
        name_count = name_count +1;
        RadioGroup radiogroup = (RadioGroup) findViewById(R.id.radioGroup);
        RadioButton rdbtn = new RadioButton(this);
        rdbtn.setId(count);
        rdbtn.setText(name);
        nlist.add(name_count,name);
        rdbtn.setChecked(true);
        radiogroup.addView(rdbtn);

        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                answer = checkedId - 1;
            }
        });
    }

    class UploadImageWithParams extends AsyncTask<RequestParams, Void, String> {

        @Override
        protected String doInBackground(RequestParams... params) {
            BufferedReader reader;
            try {
                HttpURLConnection con = params[0].setupConnection();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                con.setDoOutput(true);

                con.setRequestProperty("Connection", "Keep-Alive");
                con.setRequestProperty("Cache-Control", "no-cache");
                con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                DataOutputStream request = new DataOutputStream(con.getOutputStream());

                request.writeBytes(twoHyphens + boundary + crlf);
                request.writeBytes("Content-Disposition: form-data; name=\"" + attachmentName + "\";filename=\"" + attachmentFileName + "\"" + crlf);
                request.writeBytes(crlf);

                request.writeBytes(crlf);
                request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);

                request.flush();
                request.close();

                InputStream responseStream = new BufferedInputStream(con.getInputStream());

                BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = responseStreamReader.readLine()) != null)
                {
                    stringBuilder.append(line).append("\n");
                }
                responseStreamReader.close();

                String response = stringBuilder.toString();

                responseStream.close();

                con.disconnect();

                return response;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            Log.d("demo", "result from image upload " + result);
            if (result != null) {
                question = (EditText) findViewById(R.id.editTextQuestion);
                Log.d("demo", "result present " + result);

                if(question.getText().toString().isEmpty()) {
                    Toast.makeText(CreateQuestionActivity.this, "Please type a question",
                        Toast.LENGTH_SHORT).show();
                }
                else if(count < 2) {
                    Toast.makeText(CreateQuestionActivity.this, "Number of options should be more"+
                        " than 2.\n Please add more options", Toast.LENGTH_LONG).show();
                }
                else {
                    if(bitmap != null) {
                        for(int i =0; i < nlist.size(); i++) {
                            listString.append(nlist.get(i)+";");
                        }
                        qFinal = question.getText().toString() + ";" + listString + ";"+ answer +";";
                    } else {
                        for(int i =0; i < nlist.size(); i++) {
                            listString.append(nlist.get(i)+";");
                        }
                        Log.d("demo", "list string create question\n" + listString.toString());
                        qFinal = question.getText().toString() + ";" + listString +";" + answer +";";
                        Log.d("demo", "question final\n" + qFinal);
                        RequestParams params = new RequestParams("POST", "http://dev.theappsdr.com/apis/trivia_fall15/saveNew.php");
                        params.addParam("gid", group_id);
                        params.addParam("q", qFinal);

                        Log.d("demo", "params in create question\n" + params.toString());
                        new SaveQueswithParams().execute(params);
                    }
                }
            } else {
                Log.d("demo", "Null data");
            }
        }
    }

    class SaveQueswithParams extends AsyncTask<RequestParams, Void, String> {

        @Override
        protected String doInBackground(RequestParams... params) {

            BufferedReader reader = null;
            String line;
            try {
                HttpURLConnection con = params[0].setupConnection();
                publishProgress();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();//arraylist of strings
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
                Log.d("demo", "result present " + result);
            } else {
                Log.d("demo", "Null data");
            }
            finish();
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //will execute before dointhe background method
            progressDialog = new ProgressDialog(CreateQuestionActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Saving Questions");
            progressDialog.setMessage("Saving ...");
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //gets executed when publish progress gets executed
            super.onProgressUpdate(values);
            //progressDialog.setProgress(0);
        }
    }
}
