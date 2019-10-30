package com.lws.sy.mv.activity;

import android.app.Instrumentation;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lws.sy.mv.MainActivity;
import com.lws.sy.mv.NetInfo.userInfo;
import com.lws.sy.mv.R;
import com.lws.sy.mv.Utils.NET;
import com.lws.sy.mv.db.dao.MusicInfoDao;
import com.lws.sy.mv.dialog.CommonDialog;
import com.lws.sy.mv.dialog.EditDialog;
import com.lws.sy.mv.musicUtils.TagUtils;
import com.lws.sy.mv.request.BaseRequest;
import com.lws.sy.mv.request.CommonRequest;
import com.lws.sy.mv.request.userInfoRequest;

public class UserSettingActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView goBack;
    private int id=0;
    private userInfoRequest request;
    private userInfo userinfo;
    private CommonRequest request1;
    private TextView nickname;
    private TextView loginOut;
    private TextView sign;
    private LinearLayout ll_nickname;
    private LinearLayout ll_sign;
    private MusicInfoDao dao;
    private EditDialog dialog;
    private CommonDialog dialog1;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                if(!userinfo.getNickname().equals("")){
                    nickname.setText(userinfo.getNickname());
                }
                if(!userinfo.getSign().equals("")){
                    sign.setText(userinfo.getSign());
                }else{
                    sign.setText("这个人很懒,什么都没写~");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);
        sign= (TextView) findViewById(R.id.sign);
        nickname= (TextView) findViewById(R.id.nickname);
        loginOut= (TextView) findViewById(R.id.loginOut);
        ll_nickname= (LinearLayout) findViewById(R.id.ll_nickname);
        ll_sign= (LinearLayout) findViewById(R.id.ll_sign);
        ll_sign.setOnClickListener(this);
        ll_nickname.setOnClickListener(this);
        loginOut.setOnClickListener(this);
        request1=new CommonRequest();
        dialog=new EditDialog(UserSettingActivity.this);
        dialog.setInter(new EditDialog.onDialogClickListener() {

            @Override
            public void OnOkClick(String content, int type) {
                if(type==1){
                    request1.login(NET.update+"&nickname="+content+"&id="+id+"&type="+type);
                    nickname.setText(content);
                }else if(type==2){
                    request1.login(NET.update+"&sign="+content+"&id="+id+"&type="+type);
                    sign.setText(content);
                }
            }

            @Override
            public void OnCancelClick() {

            }
        });
        dialog1=new CommonDialog(this);
        dialog1.setInter(new CommonDialog.onDialogClickListener() {
            @Override
            public void OnOkClick() {
                dao.deleteLogin();
                TagUtils.id = -1;
                startActivity(new Intent(UserSettingActivity.this,MainActivity.class));
                finish();
            }

            @Override
            public void OnCancelClick() {

            }
        });
        initID();
        initData();
        goBack= (ImageView) findViewById(R.id.goBack);
        goBack.setOnClickListener(this);
    }

    private void initData() {
        request=new userInfoRequest();
        request.getData(NET.getInfo+"&id="+id);
        request.setInter(new BaseRequest.BaseInter<userInfo>() {
            @Override
            public void handleData(userInfo data, Boolean isSuccess) {
                if(!isSuccess){
                    return;
                }
                userinfo=data;
                mHandler.sendEmptyMessage(1);
            }
        });
    }

    private void initID() {
        dao=new MusicInfoDao(this);
        Cursor cursor = dao.queryLogin();
        while(cursor.moveToNext()){
            id=cursor.getInt(0);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.goBack:
                new Thread() {
                    public void run() {
                        try {
                            Instrumentation inst = new Instrumentation();
                            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                        } catch (Exception e) {
                        }
                    }
                }.start();
                break;
            case R.id.ll_nickname:
                dialog.show(1);
                break;
            case R.id.ll_sign:
                dialog.show(2);
                break;
            case R.id.ll_more:
                break;
            case R.id.loginOut:
                dialog1.show("即将退出，是否继续？",false);
                break;
        }
    }
}
