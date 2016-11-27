package com.github.tsiangleo.sr.client.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.github.tsiangleo.sr.client.business.DataAccessService;

/**
 * Created by tsiang on 2016/11/26.
 */

public class BaseActivity extends AppCompatActivity implements View.OnTouchListener{
    //正常情况下的按钮颜色
    public final static String BUTTON_COLOR_NORMAL = "#1aad19";
    //按下按钮之后的按钮颜色
    public final static String BUTTON_COLOR_DOWN = "#179b16";

    protected DataAccessService dataAccessService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataAccessService = new DataAccessService(getSharedPreferences("com.github.tisnagleo.sr",MODE_PRIVATE));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v instanceof Button){
            //按下按钮
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                v.setBackgroundColor(Color.parseColor(BUTTON_COLOR_DOWN));

            }
            //抬起手指
            if(event.getAction() == MotionEvent.ACTION_UP){
                v.setBackgroundColor(Color.parseColor(BUTTON_COLOR_NORMAL));
            }
        }
        // return false保证还可以处理onclick事件
        return false;
    }


}
