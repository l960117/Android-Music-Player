package com.lws.sy.mv.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import com.lws.sy.mv.R;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class ProgressDialog {
    private Context context;
    AlertDialog.Builder builder;
    AlertDialog dialog;
    public ProgressDialog(Context context){
        this.context=context;
    }
    public void show(String msg){
        builder=new AlertDialog.Builder(context);
        builder.setView(R.layout.progress_dialog);
        dialog = builder.create();
        dialog.show();
        TextView tv_msg= (TextView) dialog.findViewById(R.id.tv_msg);
        tv_msg.setText(msg);
    }
    public void hidden(){
        dialog.dismiss();
    }
}
