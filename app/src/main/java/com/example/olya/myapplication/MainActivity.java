package com.example.olya.myapplication;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    final static private String path = Environment.getExternalStorageDirectory().getAbsolutePath();
    final int REQUEST_FILE_MANAGER = 1;

    private ProgressBar progressBar;
    private EditText editText;
    private String fullFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button buttonSave = (Button) findViewById(R.id.buttonSave);
        final Button buttonLoad = (Button) findViewById(R.id.buttonLoad);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        editText = (EditText) findViewById(R.id.editText);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fullFilePath == null){
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "You need load file", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    SaveTextTask saveTextTask = new SaveTextTask();
                    saveTextTask.execute(fullFilePath);
                }
            }
        });

        buttonLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("text/plain");
                startActivityForResult(intent, REQUEST_FILE_MANAGER);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_FILE_MANAGER:
                if (resultCode == RESULT_OK) {
                    String filePath = data.getData().getPath().split(":")[1];
                    fullFilePath = path + "/" + filePath;
                    LoadTextTask loadTextTask = new LoadTextTask();
                    loadTextTask.execute(fullFilePath);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class LoadTextTask extends AsyncTask<String, Void, StringBuffer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        protected StringBuffer doInBackground(String... params) {
            String filePath = params[0];
            StringBuffer text = new StringBuffer();
            try
            {
                TimeUnit.SECONDS.sleep(2);

                BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    text.append(line);
                }
                bufferedReader.close();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            return text;
        }

        @Override
        protected void onPostExecute(StringBuffer text) {
            super.onPostExecute(text);
            editText.setText(text.toString());
            progressBar.setVisibility(ProgressBar.INVISIBLE);
        }

    }

    private class SaveTextTask extends AsyncTask<String, Void, Void> {
        String text;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(ProgressBar.VISIBLE);
            text = editText.getText().toString();
        }

        @Override
        protected Void doInBackground(String... params) {
            String filePath = params[0];
            try {
                TimeUnit.SECONDS.sleep(2);
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath));
                bufferedWriter.flush();
                bufferedWriter.write(text);
                bufferedWriter.close();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Save successful", Toast.LENGTH_SHORT);
            toast.show();
        }

    }
}
