package com.github.tsiangleo.sr.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.github.tsiangleo.sr.client.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tsiang on 2016/11/26.
 */

public class HomeActivity extends BaseActivity {

    private int[] imageIds = new int[]{R.drawable.regist,R.drawable.verify,R.drawable.setting,R.drawable.lock};
    private String[] titles = new String[]{"注册声纹","验证声纹","系统设置","应用锁"};
    private int arrowId = R.drawable.arrow;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SimpleAdapter adapter = new SimpleAdapter(this,getData(),R.layout.activity_home_listview,
                new String[]{"header","title","arrow"},
                new int[]{R.id.header,R.id.title,R.id.arrow});

        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0 ){
                    gotoRegist();
                }else if(position == 1){
                    gotoVerify();
                }else if(position == 2){
                    gotoSetting();
                }else if(position == 2){
                    gotoAppLock();
                }

            }
        });
        listView.setAdapter(adapter);
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        for(int i = 0;i<imageIds.length;i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("header", imageIds[i]);
            item.put("title", titles[i]);
            item.put("arrow",arrowId);
            listItems.add(item);
        }
        return listItems;
    }

    private void gotoSetting() {
        Intent intent = new Intent(this,SettingActivity.class);
        startActivity(intent);
    }

    private void gotoVerify() {
        Intent intent = new Intent(this,VerifyVoiceActivity.class);
        startActivity(intent);
    }

    private void gotoRegist() {
        Intent intent = new Intent(this,RegistVoiceActivity.class);
        startActivity(intent);
    }

    private void gotoAppLock(){
        // TODO: 2016/11/28  
    }
}
