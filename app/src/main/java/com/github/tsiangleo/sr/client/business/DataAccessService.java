package com.github.tsiangleo.sr.client.business;

import android.content.SharedPreferences;

/**
 * 数据访问的业务对象
 * Created by tsiang on 2016/11/26.
 */
public class DataAccessService {
    public static final String KEY_SERVER_IP = "com.github.tisnagleo.sr.server.ip";
    public static final String KEY_SERVER_PORT = "com.github.tisnagleo.sr.server.port";

    private SharedPreferences  preferences;
    private SharedPreferences.Editor editor;

   public DataAccessService(SharedPreferences  preferences){
       this.preferences =  preferences;
       this.editor = preferences.edit();
   }

    /**
     * 保存服务器的地址到本地
     * @param ip
     * @param port
     */
    public void saveServerAddr(String ip, int port) {
        editor.putString(KEY_SERVER_IP,ip);
        editor.putInt(KEY_SERVER_PORT,port);
        editor.commit();
    }

    /**
     * 获取服务的ip地址
     * @return
     */
    public String getServerIP(){
        return preferences.getString(KEY_SERVER_IP,null);
    }
    /**
     * 获取服务的ip地址
     * @return
     */
    public int getServerPort(){
        return preferences.getInt(KEY_SERVER_PORT,0);
    }


}
