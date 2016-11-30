package com.github.tsiangleo.sr.client.activity;

import android.app.AlertDialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.github.tsiangleo.sr.client.R;
import com.github.tsiangleo.sr.client.service.WatchDogService;
import com.github.tsiangleo.sr.client.util.ServiceAliveUtil;

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

    private void requestPromission() {
        new AlertDialog.Builder(this).
                setTitle("设置").
                setMessage("开启usagestats权限")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        startActivity(intent);
                        //finish();
                    }
                }).show();
    }

    private boolean hasPackageUsageStatsPermission(){
        if (Build.VERSION.SDK_INT > 20) {
            long ts = System.currentTimeMillis();
            UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            List queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, ts - 2000, ts);
            if(queryUsageStats == null || queryUsageStats.isEmpty()){
               return false;
            }
            return true;
        }
        return true;
    }
    private void checkPermission(){
        if(!hasPackageUsageStatsPermission()){
            requestPromission();
        }
    }

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
                }else if(position == 3){
                    gotoAppLock();
                }

            }
        });
        listView.setAdapter(adapter);

        //启动授权页面，需要用户授权
        checkPermission();
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
        Intent intent = new Intent(this,AppListActivity.class);
        startActivity(intent);
    }
}
