package com.lws.sy.mv.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lws.sy.mv.R;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class EditDialog {
    private Context context;
    public EditDialog(Context context){
        this.context=context;
    }
    public void show(final int type){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setView(R.layout.edit_dialog);
        final AlertDialog dialog = builder.create();
        dialog.show();
        TextView tv_dialog_title = (TextView) dialog.findViewById(R.id.tv_dialog_title);
        if(type==1){
            tv_dialog_title.setText("请输入昵称");
        }else{
            tv_dialog_title.setText("请输入个性签名");
        }
        dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onDialogClickListener.OnCancelClick();
            }
        });
        dialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) dialog.findViewById(R.id.et_content);
                String content = editText.getText().toString().trim();
                dialog.dismiss();
                onDialogClickListener.OnOkClick(content,type);
            }
        });
    }
    public onDialogClickListener onDialogClickListener;
    public void setInter(onDialogClickListener onDialogClickListener){ this.onDialogClickListener=onDialogClickListener;}



    public interface onDialogClickListener{
        void OnOkClick(String content,int type);
        void OnCancelClick();
    }
}
