package com.lws.sy.mv.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lws.sy.mv.NetInfo.commonResponse;
import com.lws.sy.mv.R;
import com.lws.sy.mv.Utils.NET;
import com.lws.sy.mv.dialog.CommonDialog;
import com.lws.sy.mv.request.BaseRequest;
import com.lws.sy.mv.request.CommonRequest;

public class RegisterActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private EditText password1;
    private CommonRequest request;
    private Boolean isRegister = false;
    private TextView tv_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        request.setInter(new BaseRequest.BaseInter<commonResponse>() {
            @Override
            public void handleData(commonResponse data,Boolean isSuccess) {
                CommonDialog dialog = new CommonDialog(RegisterActivity.this);
                CommonDialog dialog1 = new CommonDialog(RegisterActivity.this);
                dialog.setInter(new CommonDialog.onDialogClickListener() {
                    @Override
                    public void OnOkClick() {
                        finish();
                    }

                    @Override
                    public void OnCancelClick() {

                    }
                });
                dialog1.setInter(new CommonDialog.onDialogClickListener() {
                    @Override
                    public void OnOkClick() {

                    }

                    @Override
                    public void OnCancelClick() {

                    }
                });
                setET(true);
                isRegister=false;
                if(!isSuccess){
                    dialog1.show("网络连接出错,请检查网络连接",true);
                    return;
                }
                if(data.getState().equals("ok")){
                    dialog.show("注册成功，是否前往登录？",false);
                }else{
                    dialog1.show("注册失败，请重试？",true);
                }
            }
        });
    }

    private void initView() {
        username= (EditText) findViewById(R.id.username);
        password= (EditText) findViewById(R.id.password);
        password1= (EditText) findViewById(R.id.password1);
        tv_register = (TextView) findViewById(R.id.tv_register);
        request=new CommonRequest();
    }

    public void register(View view) {
        if(isRegister) return;
        String name =username.getText().toString().trim();
        String pwd=password.getText().toString().trim();
        String pwd1=password1.getText().toString().trim();
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
            dialog.show("用户名不能为空",true);
            return;
        }
        if(pwd.equals("")||pwd1.equals("")){
            dialog.show("密码不能为空",true);
            return;
        }
        if(!pwd.equals(pwd1)){
            dialog.show("两次输入的密码不一致",true);
            return;
        }
        isRegister=true;
        setET(false);
        String url=NET.register+"&username="+name+"&password="+pwd;
        request.login(url);
    }

    public void setET(boolean flag) {
        username.setEnabled(flag);
        password.setEnabled(flag);
        password1.setEnabled(flag);
        if(!flag){
            tv_register.setText("注册中...");
        }else{
            tv_register.setText("注册");
        }
    }
}
