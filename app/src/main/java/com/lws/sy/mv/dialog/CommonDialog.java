package com.lws.sy.mv.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.lws.sy.mv.R;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class CommonDialog {
    private Context context;
    public CommonDialog(Context context){
        this.context=context;
    }
    public void show(String msg,Boolean hideCancel){
        final AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setView(R.layout.common_dialog);
        final AlertDialog dialog = builder.create();
        dialog.show();
        TextView tv_content = (TextView) dialog.findViewById(R.id.tv_content);
        tv_content.setText(msg);
        dialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onDialogClickListener.OnOkClick();
            }
        });
        if(!hideCancel){
            dialog.findViewById(R.id.cancel).setVisibility(View.VISIBLE);
            dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    onDialogClickListener.OnCancelClick();
                }
            });
        }
    }
    public onDialogClickListener onDialogClickListener;
    public void setInter(onDialogClickListener onDialogClickListener){ this.onDialogClickListener=onDialogClickListener;}
    public interface onDialogClickListener{
        void OnOkClick();
        void OnCancelClick();
    }
}
