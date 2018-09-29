package com.yuanyang.xiaohu.twoscreen.presenter;

import com.yuanyang.xiaohu.twoscreen.activity.CreateParamsActivity;
import com.yuanyang.xiaohu.twoscreen.activity.OneScreenActivity;
import com.yuanyang.xiaohu.twoscreen.net.UserInfoKey;
import com.yuanyang.xiaohu.twoscreen.util.AppSharePreferenceMgr;

import java.util.Timer;
import java.util.TimerTask;
import cn.com.library.kit.ToastManager;
import cn.com.library.log.XLog;
import cn.com.library.mvp.XPresent;

public class ParamsPresent extends XPresent<CreateParamsActivity> {

    private int time = 5;

    private Timer timer = null;

    public void startTimer(){
        time = 5;
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                time--;
                XLog.e("time===" + time);
                if (time <= 0) {
                    timer.cancel();
                    toActivity();
                }
            }
        };
        timer.schedule(task, 0, 1000);

    }

    public void stopTimer(){
        if (timer != null) {
            timer.cancel();
            time = 5;
        }
    }

    private void toActivity(){
            OneScreenActivity.launch(getV());
            getV().finish();

    }

}
