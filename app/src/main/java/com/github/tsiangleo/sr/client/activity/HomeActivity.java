package com.github.tsiangleo.sr.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.tsiangleo.sr.client.R;

/**
 * Created by tsiang on 2016/11/26.
 */

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    private Button registButton,verifyButton,clearButton,getModelButton,settingButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        registButton = (Button) findViewById(R.id.registButton);
        verifyButton = (Button) findViewById(R.id.verifyButton);
        clearButton = (Button) findViewById(R.id.clearButton);
        getModelButton = (Button) findViewById(R.id.getModelButton);
        settingButton = (Button) findViewById(R.id.settingButton);

        registButton.setOnClickListener(this);
        verifyButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        getModelButton.setOnClickListener(this);
        settingButton.setOnClickListener(this);

        registButton.setOnTouchListener(this);
        verifyButton.setOnTouchListener(this);
        clearButton.setOnTouchListener(this);
        getModelButton.setOnTouchListener(this);
        settingButton.setOnTouchListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == registButton){
            gotoRegist();
        }else if(v == verifyButton){
            gotoVerify();
        }else if(v == clearButton){
            gotoClear();
        }else if(v == getModelButton){
            gotoGetModel();
        }else if(v == settingButton){
            gotoSetting();
        }
    }

    private void gotoSetting() {
        Intent intent = new Intent(this,SettingActivity.class);
        startActivity(intent);
    }

    private void gotoGetModel() {
        // TODO: 2016/11/26
    }

    private void gotoClear() {
        // TODO: 2016/11/26
    }

    private void gotoVerify() {
        Intent intent = new Intent(this,VerifyVoiceActivity.class);
        startActivity(intent);
    }

    private void gotoRegist() {
        Intent intent = new Intent(this,RegistVoiceActivity.class);
        startActivity(intent);
    }


}
