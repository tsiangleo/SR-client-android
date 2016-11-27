package com.github.tsiangleo.sr.client.proto;


import com.github.tsiangleo.sr.client.util.TypeTransferUtil;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 *
 SR响应协议

 一、ping测试
 1.flag字段：固定为"SRRSP"，占5个字节，req[0]='S',req[1]='R',....
 2.响应id字段：标志本次响应，占36个字节。取值为对应的request的id
 3.type字段：一个字节：type取值含义：1.连通性测试，2为注册声纹，3为验证声纹，4位清除声纹，5为获取声纹模型
 4.retCode字段：一个字节；返回码。


 二、注册声纹
 同一

 三、验证声纹
 同一

 四、清除声纹
 同一

 五、获取声纹模型
 1.flag字段：固定为"SRRSP"，占5个字节，req[0]='S',req[1]='R',....
 2.响应id字段：标志本次响应，占36个字节。取值为对应的request的id
 3.type字段：一个字节：type取值含义：1.连通性测试，2为注册声纹，3为验证声纹，4位清除声纹，5为获取声纹模型
 4.retCode字段：一个字节；返回码。
 5.文件名信息：1个字节的文件名长度信息，n个字节的文件名信息。（所以文件名的长度不能超过127个字节）。
 6.语音流信息：8个字节的语音流长度信息，n个字节的语音流信息。
 *
 *
 * Created by tsiang on 2016/11/26.
 */

public class SRServerResponse {
    public static final String CHARSET = "utf-8";
    public static final String FLAG = "SRRSP";
    /* model文件在Android客户端的存放路径*/
    public static final String MODEL_FILE_CLIENT_PATH = "i:/zz/new/";

    /* 返回码的含义 */
    public static final byte RET_CODE_SUCCESS = 0x01;
    public static final byte RET_CODE_FAIL = 0x02;

    private String flag = FLAG;
    private String responseId;
    private byte responseType; //对应SRClientRequest中的请求类型
    private byte retCode;
    private File modelFile;

    /**
     * 将SRServerResponse对象write到输出流。调用者应该调用out.close()关闭输出流。
     * @param out
     * @throws Exception
     */
    public void sendResponse(OutputStream out) throws IOException {
        //写入flag字段
        out.write(flag.getBytes(CHARSET));
        //写入responseId字段
        out.write(responseId.getBytes(CHARSET));
        //写入type字段
        out.write(responseType);
        //写入retCode字段
        out.write(retCode);

        if(responseType == SRClientRequest.REQUEST_TYPE_PING ||
                responseType == SRClientRequest.REQUEST_TYPE_REGIST||
                responseType == SRClientRequest.REQUEST_TYPE_VERIFY ||
                responseType == SRClientRequest.REQUEST_TYPE_CLEAR){
            out.flush();
            return;
        }

        if(responseType == SRClientRequest.REQUEST_TYPE_GETMODEL){
            //写入文件名信息
            out.write((byte)modelFile.getName().getBytes(CHARSET).length);
            out.write(modelFile.getName().getBytes(CHARSET));

            //写入文件内容信息
            long audioLenth = modelFile.length();
            out.write(TypeTransferUtil.longToByteArray(audioLenth));
            FileInputStream fis = new FileInputStream(modelFile);
            byte[] buffer = new byte[4096];
            int len = 0;
            while((len = fis.read(buffer, 0, buffer.length)) > 0){
                out.write(buffer, 0, len);
            }
            fis.close();
            out.flush();
        }else{
            throw new RuntimeException("unsupported response type value:"+responseType);
        }

    }

    /**
     * 从输入流中读取SR响应信息。调用者应该调用in.close()关闭输入流。
     * @param in
     * @return
     * @throws IOException
     */
    public static SRServerResponse readResponse(InputStream in) throws IOException{
        SRServerResponse returnData = new SRServerResponse();
        DataInputStream dataInputStream = new DataInputStream(in);
        byte[] buf = new byte[20];
        //读取flag字段
        dataInputStream.read(buf, 0, FLAG.length());
        String flag = new String(buf,0,FLAG.length(),CHARSET);

        if(!FLAG.equals(flag)){
            throw new RuntimeException("Not a SR response");
        }

        //读取responseId字段
        byte[] ridbuf = new byte[36];
        dataInputStream.read(ridbuf, 0, 36);
        String reqId = new String(ridbuf,0,36,CHARSET);
        returnData.setResponseId(reqId);

        //读取type字段
        byte type = dataInputStream.readByte();
        returnData.setResponseType(type);

        //读取retcode字段
        byte retcode = dataInputStream.readByte();
        returnData.setRetCode(retcode);

        if(type == SRClientRequest.REQUEST_TYPE_PING ||
                type == SRClientRequest.REQUEST_TYPE_REGIST||
                type == SRClientRequest.REQUEST_TYPE_VERIFY ||
                type == SRClientRequest.REQUEST_TYPE_CLEAR){
            return returnData;
        }

        if(type == SRClientRequest.REQUEST_TYPE_GETMODEL){

            //读取文件名信息
            byte fileNameLenth = dataInputStream.readByte();
            byte[] buff2 = new byte[fileNameLenth];
            dataInputStream.read(buff2, 0, fileNameLenth);
            String fileName = new String(buff2,0,fileNameLenth,CHARSET);

            //读取文件内容信息
            long audioLenth = dataInputStream.readLong();
            File outFile  = new File(MODEL_FILE_CLIENT_PATH+"/"+fileName);
            FileOutputStream outputStream = new FileOutputStream(outFile);
            byte[] buffer = new byte[4096];
            long left = audioLenth;
            int readed = 0;
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
            returnData.setModelFile(outFile);
            return returnData;
        }else{
            throw new RuntimeException("unsupported response type value:"+type);
        }
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public void setResponseType(byte responseType) {
        this.responseType = responseType;
    }

    public void setRetCode(byte retCode) {
        this.retCode = retCode;
    }

    /**
     * 设置要传到客户端的声纹模型文件。
     * @param modelFile
     */
    public void setModelFile(File modelFile) {
        this.modelFile = modelFile;
    }

    public String getResponseId() {
        return responseId;
    }

    public byte getResponseType() {
        return responseType;
    }

    public byte getRetCode() {
        return retCode;
    }

    public File getModelFile() {
        return modelFile;
    }

    @Override
    public String toString() {
        return "SRServerResponse [flag=" + flag + ", responseId=" + responseId
                + ", responseType=" + responseType + ", retCode=" + retCode
                + ", modelFile=" + modelFile + "]";
    }

}
