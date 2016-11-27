package com.github.tsiangleo.sr.client.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.tsiangleo.sr.client.R;
import com.github.tsiangleo.sr.client.proto.SRClientRequest;
import com.github.tsiangleo.sr.client.proto.SRServerResponse;
import com.github.tsiangleo.sr.client.util.SysConfig;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

/**
 * Created by tsiang on 2016/11/23.
 *
 */

public class SettingActivity extends BaseActivity implements View.OnClickListener{

    private EditText ipEditText;
    private EditText portEditText;
    private Button saveButton;
    private ProgressDialog progressDialog;

    private String host;
    private int port;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Toast.makeText(this,"SerialNumber:"+ SysConfig.getDeviceId(),Toast.LENGTH_SHORT).show();

        ipEditText = (EditText) findViewById(R.id.ipEditText);
        portEditText = (EditText) findViewById(R.id.portEditText);
        saveButton = (Button) findViewById(R.id.saveButton);

        saveButton.setOnClickListener(this);
        saveButton.setOnTouchListener(this);

        if(dataAccessService.getServerIP() != null){
            ipEditText.setText(dataAccessService.getServerIP());
        }
        if(dataAccessService.getServerPort() > 0){
            //注意：""
            portEditText.setText(""+dataAccessService.getServerPort());
        }
    }


    @Override
    public void onClick(View v) {
        if (v == saveButton){
            if(ipEditText.getText().toString().isEmpty()){
                Toast.makeText(this,"服务器地址不能为空",Toast.LENGTH_LONG).show();
                return;
            }
            if(portEditText.getText().toString().isEmpty()){
                Toast.makeText(this,"服务器端口号不能为空",Toast.LENGTH_LONG).show();
                return;
            }
            new NetCheckTask().execute(ipEditText.getText().toString(),portEditText.getText().toString());
            createProgressDialog();
        }
    }

    private class NetCheckTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            String h = params[0];
            int p = Integer.parseInt(params[1]);

            boolean isOk = true;

            SRClientRequest req = new SRClientRequest();
            req.setRequestId(UUID.randomUUID().toString());
            req.setRequestType(SRClientRequest.REQUEST_TYPE_PING);
            try {
                Socket client = new Socket(h, p);
                OutputStream outputStream = client.getOutputStream();
                InputStream inputStream = client.getInputStream();
                //发送请求
                req.sendRequest(outputStream);
                SRServerResponse response = SRServerResponse.readResponse(inputStream);
                inputStream.close();
                outputStream.close();
                client.close();
             }catch (Exception e){
                isOk = false;
            }finally {
                if(isOk){
                    host = h;
                    port = p;
                }
            }
            return isOk;
        }

        protected void onPostExecute(Boolean result) {
            //关闭进度框
            progressDialog.dismiss();
            if (result) {
                Toast.makeText(SettingActivity.this, "保存成功", Toast.LENGTH_LONG).show();
                dataAccessService.saveServerAddr(host,port);
                // TODO: 2016/11/26

            } else {
                Toast.makeText(SettingActivity.this, "无法ping通服务器，请输入正确的地址和端口号！", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void createProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("服务器设置");
        progressDialog.setMessage("服务器连通性测试中...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
}
