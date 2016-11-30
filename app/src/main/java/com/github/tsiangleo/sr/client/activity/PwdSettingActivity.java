package com.github.tsiangleo.sr.client.activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.tsiangleo.sr.client.R;

/**
 * Created by tsiang on 2016/11/30.
 */
public class PwdSettingActivity extends BaseActivity implements View.OnClickListener{

    private EditText pwd1EditText,pwd2EditText;
    private Button savePwdButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd_setting);

        pwd1EditText = (EditText) findViewById(R.id.pwd1EditText);
        pwd2EditText = (EditText) findViewById(R.id.pwd2EditText);
        savePwdButton = (Button) findViewById(R.id.savePwdButton);

        savePwdButton.setOnClickListener(this);
        savePwdButton.setOnTouchListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == savePwdButton){
            if(pwd1EditText.getText().toString().isEmpty() ||pwd2EditText.getText().toString().isEmpty() ){
                Toast.makeText(this,"密码不能为空",Toast.LENGTH_LONG).show();
                return;
            }
            if(!pwd1EditText.getText().toString().equals(pwd2EditText.getText().toString())){
                Toast.makeText(this,"两次输入的密码不一致",Toast.LENGTH_LONG).show();
                return;
            }
            dataAccessService.savePwd(pwd1EditText.getText().toString());
            showMsgAndCloseActivity("保存成功",this);
        }
    }
}

