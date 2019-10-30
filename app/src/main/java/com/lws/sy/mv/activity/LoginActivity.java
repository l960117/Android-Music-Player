package com.lws.sy.mv.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lws.sy.mv.NetInfo.commonResponse;
import com.lws.sy.mv.R;
import com.lws.sy.mv.Utils.NET;
import com.lws.sy.mv.db.dao.MusicInfoDao;
import com.lws.sy.mv.dialog.CommonDialog;
import com.lws.sy.mv.musicUtils.TagUtils;
import com.lws.sy.mv.request.BaseRequest;
import com.lws.sy.mv.request.CommonRequest;

public class LoginActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private int id;
    private String message;
    private MusicInfoDao dao;
    private CommonRequest request;
    private Boolean isLogining = false;
    private TextView tv_login;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            CommonDialog dialog = new CommonDialog(LoginActivity.this);
            switch (msg.what){
                case 0:
                    setET(true);
                    dialog.show(message,true);
                    dialog.setInter(new CommonDialog.onDialogClickListener() {
                        @Override
                        public void OnOkClick() {

                        }

                        @Override
                        public void OnCancelClick() {

                        }
                    });
                    break;
                case 1:
                    dao.addLogin(id);
                    TagUtils.id = id;
                    dialog.show("登录成功,是否跳转至个人页？",false);
                    dialog.setInter(new CommonDialog.onDialogClickListener() {
                        @Override
                        public void OnOkClick() {
                            startActivity(new Intent(LoginActivity.this,UserActivity.class));
                            finish();
                        }

                        @Override
                        public void OnCancelClick() {
                            finish();
                        }
                    });
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dao=new MusicInfoDao(LoginActivity.this);
        username= (EditText) findViewById(R.id.username);
        password= (EditText) findViewById(R.id.password);
        tv_login= (TextView) findViewById(R.id.tv_login);
        request=new CommonRequest();
        request.setInter(new BaseRequest.BaseInter<commonResponse>() {
            @Override
            public void handleData(commonResponse data,Boolean isSuccess) {
                isLogining=false;
                if(!isSuccess){
                    Toast.makeText(LoginActivity.this, "网络连接出错，请检查网络", Toast.LENGTH_SHORT).show();
                    setET(true);
                    return;
                }
                if(data.getState().equals("ok")){
                    id= Integer.parseInt(data.getMsg());
                    mHandler.sendEmptyMessage(1);
                }else {
                    message=data.getMsg();
                    mHandler.sendEmptyMessage(0);
                }
            }
        });
    }

    public void login(View view) {
        if(isLogining) return;
        String name=username.getText().toString().trim();
        String pwd=password.getText().toString().trim();
        CommonDialog dialog=new CommonDialog(this);
        dialog.setInter(new CommonDialog.onDialogClickListener() {
            @Override
            public void OnOkClick() {

            }

            @Override
            public void OnCancelClick() {

            }
        });
        if(name.equals("")){
            dialog.show("请输入用户名",true);
            return;
        }
        if(pwd.equals("")){
            dialog.show("请输入密码",true);
            return;
        }
        isLogining = true;
        setET(false);
        String path=NET.login+"&username="+name+"&password="+pwd;
        request.login(path);
    }
    private void setET(Boolean flag){
        username.setEnabled(flag);
        password.setEnabled(flag);
        if(!flag){
            tv_login.setText("登录中...");
        }else{
            tv_login.setText("登录");
        }
    }

    public void goToRegister(View view) {
        if(isLogining) return;
        startActivity(new Intent(this,RegisterActivity.class));
    }
}
