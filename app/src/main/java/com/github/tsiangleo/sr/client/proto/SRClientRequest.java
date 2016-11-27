package com.github.tsiangleo.sr.client.proto;

import com.github.tsiangleo.sr.client.util.TypeTransferUtil;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 *
 SR请求协议

 一、ping测试
 1.flag字段：固定为"SRREQ"，占5个字节，req[0]='S',req[1]='R',....
 2.请求id字段：标志本次请求，占36个字节。
 3.type字段：一个字节：type取值含义：1.连通性测试，2为注册声纹，3为验证声纹，4位清除声纹，5为获取声纹模型

 二、注册声纹
 1.flag字段：固定为"SRREQ"，占5个字节，req[0]='S',req[1]='R',....
 2.请求id字段：标志本次请求，占36个字节。
 3.type字段：一个字节：type取值含义：1.连通性测试，2为注册声纹，3为验证声纹，4位清除声纹，5为获取声纹模型
 4.deviceId信息：一个字节的deviceId长度，n个字节的deviceId信息。
 5.文件名信息：1个字节的文件名长度信息，n个字节的文件名信息。（所以文件名的长度不能超过127个字节）。
 6.语音流信息：8个字节的语音流长度信息，n个字节的语音流信息。

 三、验证声纹
 同注册声纹，只是type字段不同。

 四、清除声纹
 1.flag字段：固定为"SRREQ"，占5个字节，req[0]='S',req[1]='R',....
 2.请求id字段：标志本次请求，占36个字节。
 3.type字段：一个字节：type取值含义：1.连通性测试，2为注册声纹，3为验证声纹，4位清除声纹，5为获取声纹模型
 4.deviceId信息：一个字节的deviceId长度，n个字节的deviceId信息。

 五、获取声纹模型
 同清除声纹，只是type字段不同。
 *
 *
 *  Created by tsiang on 2016/11/26.
 */
public class SRClientRequest {
    public static final String CHARSET = "utf-8";
    public static final String FLAG = "SRREQ";
    public static final String UPLOAD_PATH = "i:/zz/new/";

    public static final byte REQUEST_TYPE_PING = 0x01;
    public static final byte REQUEST_TYPE_REGIST = 0x02;
    public static final byte REQUEST_TYPE_VERIFY = 0x03;
    public static final byte REQUEST_TYPE_CLEAR = 0x04;
    public static final byte REQUEST_TYPE_GETMODEL = 0x05;

    private String flag = FLAG;
    private byte requestType;
    private String requestId;
    private String deviceId;
    private File audioFile;

    /**
     * 将SRClientRequest对象write到输出流。调用者应该调用out.close()关闭输出流。
     * @param out
     * @throws Exception
     */
    public void sendRequest(OutputStream out) throws IOException {
        //写入flag字段
        out.write(flag.getBytes(CHARSET));
        //写入requestId字段

        out.write(requestId.getBytes(CHARSET));
        //写入type字段
        out.write(requestType);

        if(requestType == REQUEST_TYPE_PING){
            out.flush();
            return;
        }

        //写入deviceId信息
        out.write((byte)deviceId.getBytes(CHARSET).length);
        out.write(deviceId.getBytes(CHARSET));

        if(requestType == REQUEST_TYPE_CLEAR || requestType == REQUEST_TYPE_GETMODEL){
            out.flush();
            return;
        }

        if(requestType == REQUEST_TYPE_REGIST || requestType == REQUEST_TYPE_VERIFY){
            //写入文件名信息
            out.write((byte)audioFile.getName().getBytes(CHARSET).length);
            out.write(audioFile.getName().getBytes(CHARSET));

            //写入文件内容信息
            long audioLenth = audioFile.length();
            out.write(TypeTransferUtil.longToByteArray(audioLenth));
            FileInputStream fis = new FileInputStream(audioFile);
            byte[] buffer = new byte[4096];
            int len = 0;
            while((len = fis.read(buffer, 0, buffer.length)) > 0){
                out.write(buffer, 0, len);
            }
            fis.close();
            out.flush();
        }else{
            throw new RuntimeException("unsupported request type value:"+requestType);
        }
    }


    /**
     * 从输入流中读取SR请求信息。调用者应该调用in.close()关闭输入流。
     * TODO 这种不close的方式一旦方法体抛出异常会导致不能正确关闭输入流。
     *
     * @param in
     * @return
     * @throws IOException
     */
    public static SRClientRequest readRequest(InputStream in) throws IOException{
        SRClientRequest returnData = new SRClientRequest();
        DataInputStream dataInputStream = new DataInputStream(in);
        byte[] buf = new byte[20];
        //读取flag字段
        dataInputStream.read(buf, 0, FLAG.length());
        String flag = new String(buf,0,FLAG.length(),CHARSET);

        if(!FLAG.equals(flag)){
            throw new RuntimeException("Not a SR request");
        }

        //读取requestId字段
        byte[] ridbuf = new byte[36];
        dataInputStream.read(ridbuf, 0, 36);
        String reqId = new String(ridbuf,0,36,CHARSET);
        returnData.setRequestId(reqId);

        //读取type字段
        byte type = dataInputStream.readByte();
        returnData.setRequestType(type);

        if(type == REQUEST_TYPE_PING){
            return returnData;
        }

        //读取deviceId信息
        byte deiviceIdLenth = dataInputStream.readByte();
        byte[] buff = new byte[deiviceIdLenth];
        dataInputStream.read(buff, 0, deiviceIdLenth);
        String deiviceId = new String(buff,0,deiviceIdLenth,CHARSET);
        returnData.setDeviceId(deiviceId);

        if(type == REQUEST_TYPE_CLEAR || type == REQUEST_TYPE_GETMODEL){
            return returnData;
        }

        if(type == REQUEST_TYPE_REGIST || type == REQUEST_TYPE_VERIFY){

            //读取文件名信息
            byte fileNameLenth = dataInputStream.readByte();
            byte[] buff2 = new byte[fileNameLenth];
            dataInputStream.read(buff2, 0, fileNameLenth);
            String fileName = new String(buff2,0,fileNameLenth,CHARSET);

            //读取文件内容信息
            long audioLenth = dataInputStream.readLong();
            /**
             * 文件命名规则：timestamp_fileName
             */
            File outFile  = new File(UPLOAD_PATH+"/"+System.currentTimeMillis()+"_"+fileName);
            FileOutputStream outputStream = new FileOutputStream(outFile);
            byte[] buffer = new byte[4096];
            long left = audioLenth;
            int readed = 0;
            /**
             * 陷阱:这里dataInputStream.read()容易阻塞，一旦读取的字节数不足buffer.length，且没有读到EOF。
             *
             */
            while(left > 0){
                int len = buffer.length;
                while((readed = dataInputStream.read(buffer,0,len)) > 0){
                    outputStream.write(buffer, 0, readed);
                    left -= readed;
                    if(left < buffer.length){
                        len = (int) left;
                    }
                }
            }

            outputStream.close();
            returnData.setAudioFile(outFile);
            return returnData;
        }else{
            throw new RuntimeException("unsupported request type value:"+type);
        }
    }

    public byte getRequestType() {
        return requestType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public File getAudioFile() {
        return audioFile;
    }

    public String getRequestId() {
        return requestId;
    }

    /**
     * 设置请求的类型，取值参考SRClientRequest中的FILE。
     * @param requestType
     */
    public void setRequestType(byte requestType) {
        this.requestType = requestType;
    }

    /**
     * 设置本次请求id，建议id为UUID字符串。
     * @param requestId
     * @exception requestId必须为36个字节，否则抛异常
     */
    public void setRequestId(String requestId) {
        try {
            if(requestId != null && requestId.getBytes(CHARSET).length != 36){
                throw new RuntimeException("requestId必须为36个字节");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.requestId = requestId;
    }
    /**
     * 设置设备id
     * @param deviceId
     * @exception deviceId不能超过127个字节
     */
    public void setDeviceId(String deviceId) {
        try {
            if(deviceId != null && deviceId.getBytes(CHARSET).length > Byte.MAX_VALUE){
                throw new RuntimeException("deviceId不能超过127个字节");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        this.deviceId = deviceId;
    }
    /**
     * 设置要上传的语音文件
     * @param audioFile
     * @exception 文件名不能超过127个字节
     */
    public void setAudioFile(File audioFile) {
        try {
            if(audioFile.getName() != null &&
                    audioFile.getName().getBytes(CHARSET).length > Byte.MAX_VALUE){
                throw new RuntimeException("文件名不能超过127个字节");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        this.audioFile = audioFile;
    }
    @Override
    public String toString() {
        return "SRClientRequest [flag=" + flag + ", requestType=" + requestType
                + ", requestId=" + requestId + ", deviceId=" + deviceId
                + ", audioFile=" + audioFile + "]";
    }




}
