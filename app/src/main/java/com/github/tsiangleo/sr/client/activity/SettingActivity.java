package com.github.tsiangleo.sr.client.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.github.tsiangleo.sr.client.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tsiang on 2016/11/28.
 */

public class SettingActivity extends BaseActivity {

    private String[] voiceSettingTitles = new String[]{"采样频率设置","声道数设置"};
    private String[] serverSettingTitles = new String[]{"服务器地址设置"};

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setVoiceListView();
        setServerListView();
    }

    private void setVoiceListView() {
        SimpleAdapter voiceSettingAdapter = new SimpleAdapter(this,getVoiceSettingData(), R.layout.activity_setting_listview,
                new String[]{"title"},
                new int[]{R.id.title});

        ListView voiceSetting = (ListView)findViewById(R.id.voiceSetting);
        voiceSetting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0 ){
                    gotoSetSapmleRate();
                }else if(position == 1){
                    gotoSetChannel();
                }
            }
        });
        voiceSetting.setAdapter(voiceSettingAdapter);
    }

    private void setServerListView() {
        SimpleAdapter voiceSettingAdapter = new SimpleAdapter(this,getServerSettingData(), R.layout.activity_setting_listview,
                new String[]{"title"},
                new int[]{R.id.title});

        ListView serverSetting = (ListView)findViewById(R.id.serverSetting);
        serverSetting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0 ){
                    gotoServerSetting();
                }

            }
        });
        serverSetting.setAdapter(voiceSettingAdapter);
    }

    private List<Map<String, Object>> getVoiceSettingData() {
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        for(int i = 0;i<voiceSettingTitles.length;i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("title", voiceSettingTitles[i]);
            listItems.add(item);
        }
        return listItems;
    }

    private List<Map<String, Object>> getServerSettingData() {
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        for(int i = 0;i<serverSettingTitles.length;i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("title", serverSettingTitles[i]);
            listItems.add(item);
        }
        return listItems;
    }

    private void gotoServerSetting() {
        // 将一个layout布局文件转为一个view对象。
//        LayoutInflater inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.activity_server_setting, null);
//
//        new AlertDialog.Builder(this)
//                .setTitle("服务器地址设置")
//                .setIcon(android.R.drawable.ic_dialog_info)
//                .setCancelable(false)
//                .setView(view)
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        dialog.dismiss();
//                    }
//                }).setNegativeButton("取消", null).create().show();


        Intent intent = new Intent(this,ServerSettingActivity.class);
        startActivity(intent);
    }

    private void gotoSetChannel() {
        new AlertDialog.Builder(this)
                .setTitle("请选择")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setSingleChoiceItems(new String[] {"单声道(Mono)","双声道(Stereo)"}, 0,
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                )
                .setNegativeButton("取消", null)
                .show();
    }

    private void gotoSetSapmleRate(){
        new AlertDialog.Builder(this)
                .setTitle("请选择")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setSingleChoiceItems(new String[] {"44100HZ","22050HZ","11025HZ","8000HZ"}, 0,
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                )
                .setNegativeButton("取消", null)
                .show();
    }
}

