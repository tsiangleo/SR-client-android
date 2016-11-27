package com.github.tsiangleo.sr.client.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.tsiangleo.sr.client.R;
import com.github.tsiangleo.sr.client.business.AudioRecordService;
import com.github.tsiangleo.sr.client.business.NetService;
import com.github.tsiangleo.sr.client.util.SysConfig;

import java.io.File;
import java.io.IOException;

/**
 * Created by tsiang on 2016/11/26.
 */

public class RegistVoiceActivity extends BaseActivity implements View.OnClickListener{

    private Button startButton,stopButton;
    private TextView statusTextView;
    private ProgressDialog progressDialog;

    private File rawFile;
    private File wavFile;

    private AudioRecordService audioRecordService;
    private NetService netService;

    private RecordAudioTask recordTask;
    private PlayAudioTask playTask;
    private UploadAudioTask uploadTask;
    private ConvertToWAVTask convertTask;

    /**
     * 当前的录音次数
    * */
    private int currentRecordTimes = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist_voice);

        statusTextView = (TextView) findViewById(R.id.statusTextView);
        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);

        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        startButton.setOnTouchListener(this);
        stopButton.setOnTouchListener(this);

        statusTextView.setVisibility(View.INVISIBLE);
        stopButton.setVisibility(View.GONE);

        netService = new NetService(dataAccessService.getServerIP(),dataAccessService.getServerPort());

    }

    private void initFile(String fileNamePrefix) {

        try {
            rawFile = File.createTempFile(fileNamePrefix, ".pcm",getFilesDir());
            wavFile = File.createTempFile(fileNamePrefix, ".wav",getFilesDir());
        } catch (IOException e) {
            Toast.makeText(this,"内部存储：文件创建异常："+e.getMessage(),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        if(rawFile == null || wavFile == null){
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                try {
                    File path = new File( Environment.getExternalStorageDirectory().getCanonicalPath()
                            + "/Android/data/com.github.tsiangleo.sr.client/files/");
                    path.mkdirs();
                    rawFile = File.createTempFile(fileNamePrefix, ".pcm", path);
                    wavFile = File.createTempFile(fileNamePrefix, ".wav", path);
                } catch (IOException e) {
                    Toast.makeText(this,"SD卡：文件创建异常："+e.getMessage(),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }

        if(rawFile == null || wavFile == null){
            Toast.makeText(this,"无法创建临时文件，请先确保应用具有相应的授权，再使用！",Toast.LENGTH_SHORT).show();
            //调到首页
            startActivity(new Intent(this,HomeActivity.class));
        }
    }


    @Override
    public void onClick(View v) {
        if(v == startButton){
            record();
        }else if(v == stopButton){
            stopRecord();
        }
    }

    /**
     * 文件名前缀的生成规则
     * @param order 标志第几次
     * @return
     */
    private String getFileNamePrefix(int order){
        return SysConfig.getDeviceId()+"_regist_"+order+"_";
    }

    private void record() {
        /* 每次录音都创建一个新的文件. */
        initFile(getFileNamePrefix(currentRecordTimes));
        /* audioRecordService */
        audioRecordService = new AudioRecordService(rawFile);

        startButton.setVisibility(View.GONE);
        stopButton.setVisibility(View.VISIBLE);
        statusTextView.setVisibility(View.VISIBLE);
        statusTextView.setText("正在录音...");

        recordTask = new RecordAudioTask();
        recordTask.execute();

    }
    private void stopRecord() {
        audioRecordService.stopRecording();
        createProgressDialog();
    }

    private void play() {
        playTask = new PlayAudioTask();
        playTask.execute();
    }

    private void upload() {
        uploadTask = new UploadAudioTask();
        uploadTask.execute();
    }
    private void convert() {
        convertTask = new ConvertToWAVTask();
        convertTask.execute();
    }
    private class RecordAudioTask extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                audioRecordService.startRecording();
            } catch (Exception e) {
                return e.getMessage();
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            statusTextView.setText("正在录音:"+progress[0].toString());
        }

        // 点击停止录音后由UI线程执行
        protected void onPostExecute(String result) {
            if(result == null) {
                statusTextView.setText("完成录音，文件是:" + getFilePath(rawFile));
                //停止按钮不可见
                stopButton.setVisibility(View.GONE);
                //开始按钮可见，但不出于enable状态，得等到文件上传完成后才能出于enable状态。
                startButton.setVisibility(View.VISIBLE);
                startButton.setEnabled(false);

                // 可以播放了、可以上传了,以下两个任务可以同时执行
                convert();
                play();
            }else {
//                statusTextView.setText("录音出错:"+result);
//                Toast.makeText(RegistVoiceActivity.this,"录音出错:"+result,Toast.LENGTH_LONG).show();
                //调到首页
//                startActivity(new Intent(RegistVoiceActivity.this,HomeActivity.class));

                AlertDialog.Builder builder = new AlertDialog.Builder(RegistVoiceActivity.this);
                builder.setTitle("消息提示");
                builder.setMessage(result);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //结束当前Activity
                        RegistVoiceActivity.this.finish();
                    }
                });
                builder.setCancelable(false);
                builder.create().show();
            }
        }


    }

    private class PlayAudioTask extends AsyncTask<Void, Long, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                audioRecordService.startPlaying();
            } catch (Exception e) {
                return e.getMessage();
            }
            return null;
        }

        protected void onProgressUpdate(Long... progress) {
            long total = rawFile.length() / 2;  //以short为单位，2个字节。

            if(total != 0) {
                int percent = (int)((progress[0] / (double)total) * 100);
                statusTextView.setText("正在播放录音:"+percent+"%");
            }
        }

        // 点击停止录音后由UI线程执行
        protected void onPostExecute(String result) {
            if(result != null){
                statusTextView.setText("播放录音出错:"+result);
//                Toast.makeText(RegistVoiceActivity.this,"播放录音出错:"+result,Toast.LENGTH_SHORT).show();
                return;
            }

            statusTextView.setText("录音播放完成，文件是:"+getFilePath(rawFile));
        }
    }

    private class UploadAudioTask extends AsyncTask<Void, Long, String> {

        /**
         *
         * @param params
         * @return 返回值说明：0成功; 1服务器地址和端口号不符合规范; 2文件上传出错
         */
        @Override
        protected String doInBackground(Void... params) {
            try {
                netService.upload(wavFile);
            } catch (Exception e) {
                return e.getMessage();
            }
            return null;

        }
        protected void onProgressUpdate(Long... progress) {
            long total = wavFile.length();
            if(total != 0) {
                int percent = (int)((progress[0] / (double)total) * 100);
                statusTextView.setText("正在上传文件:"+percent+"%");
            }

        }

        // 点击停止录音后由UI线程执行
        protected void onPostExecute(String result) {
            if(result != null){
                statusTextView.setText("文件上传失败："+result);
                startButton.setText("重新开始录音");
            }else {
                statusTextView.setText("文件上传完成，文件是:"+getFilePath(wavFile));
                Toast.makeText(RegistVoiceActivity.this,"声纹注册成功！",Toast.LENGTH_LONG).show();
                /* 录音成功后才自增.*/
                currentRecordTimes++;
            }
            startButton.setEnabled(true);
            //关闭进度对话框
            progressDialog.dismiss();
            //删除对应的临时录音文件
            deleteFile();
        }
    }

    private String getFilePath(File file){
        try {
           return file.getCanonicalPath();
        } catch (IOException e) {
            return null;
        }
    }

    private class ConvertToWAVTask extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                audioRecordService.rawToWavFile(wavFile);
            } catch (Exception e) {
                return e.getMessage();
            }
            return null;
        }

        // 点击停止录音后由UI线程执行
        protected void onPostExecute(String result) {
            if(result != null){
                statusTextView.setText("转换出错:"+result);
                return;
            }
            statusTextView.setText("完成转换，文件是:"+getFilePath(wavFile));
            // 可以上传了
            upload();
        }
    }

    private void createProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("声纹注册");
        progressDialog.setMessage("声纹注册中...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void deleteFile(){
        rawFile.delete();
        wavFile.delete();
    }
}
