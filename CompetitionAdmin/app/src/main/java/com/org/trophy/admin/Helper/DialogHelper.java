package com.org.trophy.admin.Helper;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogHelper {

    private Context context;
    private ProgressDialog dialog;

    public interface MyDialogListener{
        void onClickOk();
        void onClickNo();
    }

    public DialogHelper(Context context){
        this.context = context;
        dialog = new ProgressDialog(context);
    }

    public void setDialogInfo(String title, String message){
        dialog.setCancelable(false);

        dialog.setTitle(title);
        dialog.setMessage(message);

    }

    public void showProgressDialog(){
        if(dialog != null){
            if(dialog.isShowing()) return;
            dialog.setCancelable(false);
            dialog.setMessage("Please wait...");
            dialog.show();
        }
    }

    public void closeDialog(){
        if(dialog != null){
            dialog.dismiss();
        }
    }

    public static void showMessageDialog(Context context, String title, String message, final MyDialogListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(listener != null){
                    listener.onClickOk();
                }
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(listener != null){
                    listener.onClickNo();
                }
            }
        });
        builder.create().show();
    }

    public static void showNotificationDialog(final Context context, String title, String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create().show();
    }
}
