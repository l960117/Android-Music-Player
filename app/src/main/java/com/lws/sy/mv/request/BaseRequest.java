package com.lws.sy.mv.request;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.lws.sy.mv.Utils.ContextUtils;
import com.lws.sy.mv.db.dao.MusicInfoDao;
import com.lws.sy.mv.view.downloadNotify;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;

import okhttp3.Call;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public abstract class BaseRequest<T>{
    public interface BaseInter<T>{
        void handleData(T data,Boolean isSuccess);
    }
    public BaseInter dataInter;
    public void setInter(BaseInter dataInter){ this.dataInter=dataInter;}
    //获取数据
    public void getData(String path){
        OkHttpUtils
                .get()
                .url(path)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        //Toast.makeText(ContextUtils.getContext(), "请求失败", Toast.LENGTH_SHORT).show();
                        if(dataInter!=null){
                            dataInter.handleData(null,false);
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
//                        Log.e("请求成功",response);
//                        Log.e("请求成功",response);
//                        Log.e("请求成功",response);
//                        Log.e("请求成功",response);
//                        Log.e("请求成功",response);
//                        Log.e("请求成功",response);
//                        Log.e("请求成功",response);
                        //Toast.makeText(ContextUtils.getContext(), "请求成功", Toast.LENGTH_SHORT).show();
                        T data=parseJson(response);
                        if(dataInter!=null){
                            dataInter.handleData(data,true);
                        }
                    }
                });
    }
    public abstract T parseJson(String json);
    //上传数据
    public void upload(String path, String url,String name){
        File file=new File(path);
        if(!file.exists()){
            Toast.makeText(ContextUtils.getContext(), "文件不存在", Toast.LENGTH_SHORT).show();
        }
        OkHttpUtils.post()
                .addFile("mFile",name+".jpg",file)
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(ContextUtils.getContext(), "上传失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
//                        Toast.makeText(ContextUtils.getContext(), "上传成功", Toast.LENGTH_SHORT).show();
////                        Log.e("上传返回",response);
////                        Log.e("上传返回",response);
////                        Log.e("上传返回",response);
////                        Log.e("上传返回",response);
////                        Log.e("上传返回",response);
////                        Log.e("上传返回",response);
                        T data=parseJson(response);
                        if(dataInter!=null){
                            dataInter.handleData(data,true);
                        }
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
//                        Log.e("上传总长度",total+"");
//                        Log.e("上传",progress+"");
                    }
                });
    }
    public void postData(String url,String filepath){
        File file = new File(filepath);
        if(!file.exists()){
            Toast.makeText(ContextUtils.getContext(), "选择的文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        String name = filepath.substring(filepath.indexOf("/"));
        OkHttpUtils.post()
                .addFile("mFile",name,file)
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        //Toast.makeText(ContextUtils.getContext(), "上传失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //Toast.makeText(ContextUtils.getContext(), "上传成功", Toast.LENGTH_SHORT).show();
                        T data=parseJson(response);
                        if(dataInter!=null){
                            dataInter.handleData(data,true);
                        }
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
//                        Log.e("上传总长度",total+"");
//                        Log.e("上传",progress+"");
                    }
                });
    }
    public void postData(String url){
        OkHttpUtils.post()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(ContextUtils.getContext(), "上传失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //Toast.makeText(ContextUtils.getContext(), "上传成功", Toast.LENGTH_SHORT).show();
                        T data=parseJson(response);
                        if(dataInter!=null){
                            dataInter.handleData(data,true);
                        }
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
//                        Log.e("上传总长度",total+"");
//                        Log.e("上传",progress+"");
                    }
                });
    }
    public void postData(String url,String filepath,String filepath1,String filepath2){
        File file = new File(filepath);
        File file1 = new File(filepath1);
        File file2 = new File(filepath2);
        if(!file.exists()||!file1.exists()||!file2.exists()){
            Toast.makeText(ContextUtils.getContext(), "选择的文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        String name = filepath.substring(filepath.indexOf("/"));
        String name1 = filepath1.substring(filepath.indexOf("/"));
        String name2 = filepath2.substring(filepath.indexOf("/"));
        OkHttpUtils.post()
                .addFile("mFile",name,file)
                .addFile("mFile",name1,file1)
                .addFile("mFile",name2,file2)
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(ContextUtils.getContext(), "上传失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //Toast.makeText(ContextUtils.getContext(), "上传成功", Toast.LENGTH_SHORT).show();
                        T data=parseJson(response);
                        if(dataInter!=null){
                            dataInter.handleData(data,true);
                        }
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
//                        Log.e("上传总长度",total+"");
//                        Log.e("上传",progress+"");
                    }
                });
    }
    public void postData(String url,String filepath,String filepath1){
        File file = new File(filepath);
        File file1 = new File(filepath1);
        if(!file.exists()||!file1.exists()){
            Toast.makeText(ContextUtils.getContext(), "选择的文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        String name = filepath.substring(filepath.indexOf("/"));
        String name1 = filepath1.substring(filepath.indexOf("/"));
        OkHttpUtils.post()
                .addFile("mFile",name,file)
                .addFile("mFile",name1,file1)
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(ContextUtils.getContext(), "上传失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //Toast.makeText(ContextUtils.getContext(), "上传成功", Toast.LENGTH_SHORT).show();
                        T data=parseJson(response);
                        if(dataInter!=null){
                            dataInter.handleData(data,true);
                        }
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
//                        Log.e("上传总长度",total+"");
//                        Log.e("上传",progress+"");
                    }
                });
    }
    //下载文件
    public void download(String url, final String music_name, final String music_singer, final int k){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/music_list");
        if(!file.exists()){
            file.mkdir();
        }
        final downloadNotify notify = new downloadNotify();
        notify.sendDownloadNotify(music_name,k+1);
        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath()+"/music_list",music_singer+"-"+music_name+".mp3") {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(ContextUtils.getContext(), "下载失败", Toast.LENGTH_SHORT).show();
                        notify.clear(k+1);
                        File file1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/music_list",music_singer+"-"+music_name+".mp3");
                        if(file1.exists()){
                            file1.delete();
                        }
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        //Toast.makeText(ContextUtils.getContext(), "下载成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        Log.e("下载总长度",total+"");
                        Log.e("下载进度",progress+"");
                        notify.changeProgress(Integer.parseInt((progress*100+"").substring(0,(progress*100+"").indexOf("."))),music_name,k+1);
                        if((progress+"").equals("1.0")){
                            Log.e("TAG","添加下载列表");
                            MusicInfoDao dao=new MusicInfoDao(ContextUtils.getContext());
                            dao.addDownload((Environment.getExternalStorageDirectory().getAbsolutePath()+"/music_list"+"/"+music_singer+"-"+music_name+".mp3"),music_name,music_singer);
                        }
                    }
                });
    }
    public void login(String path){
        OkHttpUtils.post()
                .url(path)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if(dataInter!=null){
                            dataInter.handleData(null,false);
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
//                        Log.e("请求成功",response);
//                        Log.e("请求成功",response);
//                        Log.e("请求成功",response);
//                        Log.e("请求成功",response);
//                        Log.e("请求成功",response);
//                        Log.e("请求成功",response);
//                        Log.e("请求成功",response);
                        T data=parseJson(response);
                        if(dataInter!=null){
                            dataInter.handleData(data,true);
                        }
                    }
                });
    }
}
