package com.github.tsiangleo.sr.client;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by tsiang on 2016/11/23.
 */

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText hostEditText;
    private EditText portEditText;
    private EditText frequencyEditText;

    private Button nextButton;

    private String host;
    private int port;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        hostEditText = (EditText) findViewById(R.id.hostEditText);
        portEditText = (EditText) findViewById(R.id.portEditText);
        frequencyEditText = (EditText) findViewById(R.id.frequencyEditText);

        nextButton = (Button) findViewById(R.id.nextButton);

        nextButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == nextButton){
            Toast.makeText(this,"连接...",Toast.LENGTH_SHORT).show();

            if(hostEditText.getText().toString().isEmpty()){
                Toast.makeText(this,"服务器地址不能为空",Toast.LENGTH_SHORT).show();
                return;
            }
            if(portEditText.getText().toString().isEmpty()){
                Toast.makeText(this,"服务器端口号不能为空",Toast.LENGTH_SHORT).show();
                return;
            }
            new NetCheckTask().execute(hostEditText.getText().toString(),portEditText.getText().toString());
        }
    }

    private class NetCheckTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            String h = params[0];
            int p = Integer.parseInt(params[1]);

            boolean isOk = true;
            Socket s = null;
            try {
                s = new Socket(h, p);
            } catch (Exception e) {
                isOk = false;
            } finally {
                if (s != null) {
                    try {
                        s.close();
                    } catch (IOException e) {
                    }
                }
                if (isOk) {
                    host = h;
                    port = p;
                }
            }
            return isOk;
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(SettingActivity.this, "连接到服务器成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SettingActivity.this, "无法连接到服务器", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
