package com.github.tsiangleo.sr.client.business;

import com.github.tsiangleo.sr.client.proto.SRClientRequest;
import com.github.tsiangleo.sr.client.proto.SRServerResponse;
import com.github.tsiangleo.sr.client.util.SysConfig;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

/**
 * Created by tsiang on 2016/11/26.
 */

public class NetService {

    private final String ip;
    private final int port  ;

    public NetService(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    public void upload(File wavFile) throws Exception{
        if(wavFile  == null){
            throw new IllegalArgumentException("待上传的文件不存在");
        }

        //不能只有文件头
        if(wavFile.length() <= 44){
            throw new IllegalArgumentException("待上传的文件内容不能为空");
        }

        SRClientRequest req = new SRClientRequest();
        req.setRequestId(UUID.randomUUID().toString());
        req.setRequestType(SRClientRequest.REQUEST_TYPE_REGIST);
        req.setAudioFile(wavFile);
        req.setDeviceId(SysConfig.getDeviceId());

        try {
            Socket socket = new Socket(ip, port);
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            //发送请求
            req.sendRequest(outputStream);
            SRServerResponse response = SRServerResponse.readResponse(inputStream);
            if (response.getRetCode() != SRServerResponse.RET_CODE_SUCCESS) {
                throw new RuntimeException("文件上传失败，返回码是："+response.getRetCode());
            }
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败,异常信息："+e.getMessage(),e);
        }
    }
}
