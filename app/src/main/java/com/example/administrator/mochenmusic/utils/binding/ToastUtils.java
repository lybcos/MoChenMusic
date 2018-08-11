package com.example.administrator.mochenmusic.utils.binding;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    private static Toast mToast;
    private static Context mContext;

    public static void init(Context context) {
        mContext=context.getApplicationContext();
    }

    public static void show(int resId) {
        show(mContext.getString(resId));
    }

    public static void show(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, text, Toast.LENGTH_LONG);
        }else{
            mToast.setText(text);
        }
        mToast.show();
    }
}
