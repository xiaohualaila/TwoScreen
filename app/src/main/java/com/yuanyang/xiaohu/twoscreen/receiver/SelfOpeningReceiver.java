package com.yuanyang.xiaohu.twoscreen.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yuanyang.xiaohu.twoscreen.activity.CreateParamsActivity;
import cn.com.library.kit.ToastManager;

public class SelfOpeningReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED") ) {
            Intent intent2 = new Intent(context, CreateParamsActivity.class);
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);
        }
        ToastManager.showShort(context, "开机启动成功");
    }
}
