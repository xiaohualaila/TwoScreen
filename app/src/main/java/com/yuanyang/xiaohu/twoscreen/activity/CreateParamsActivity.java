package com.yuanyang.xiaohu.twoscreen.activity;

import android.app.Activity;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.yuanyang.xiaohu.twoscreen.R;
import com.yuanyang.xiaohu.twoscreen.net.UserInfoKey;
import com.yuanyang.xiaohu.twoscreen.presenter.ParamsPresent;
import com.yuanyang.xiaohu.twoscreen.util.AppSharePreferenceMgr;
import butterknife.BindView;
import butterknife.OnClick;
import cn.com.library.kit.ToastManager;
import cn.com.library.log.XLog;
import cn.com.library.mvp.XActivity;
import cn.com.library.router.Router;

public class CreateParamsActivity extends XActivity<ParamsPresent> {


    @BindView(R.id.params_view)
    View paramsView;
    @BindView(R.id.et_ip_main)
    EditText mainIp;
    @BindView(R.id.tv_content)
    TextView tipContent;

    @Override
    public void initData(Bundle savedInstanceState) {

        initOneView();

    }

    /**初始化大屏*/
    private void initOneView() {
        XLog.e("执行大屏代码");
        mainIp.setHint("请输入大屏IP");

        if (TextUtils.isEmpty(AppSharePreferenceMgr.get(context, UserInfoKey.BIG_SCREEN_IP, "").toString())){
            setAppendContent("请设置大屏IP");
            return;
        }
        mainIp.setText(AppSharePreferenceMgr.get(context, UserInfoKey.BIG_SCREEN_IP, "").toString());
        setAppendContent("大屏IP:" + AppSharePreferenceMgr.get(context, UserInfoKey.BIG_SCREEN_IP, "").toString());
        setAppendContent("参数设置完成，系统将在五秒后执行");
        getP().startTimer();
    }


    @OnClick({R.id.bt_set, R.id.bt_stop_auto, R.id.bt_change_device_type})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_set:
                if (checkIsEmpty()){
                    getP().startTimer();
                } else {
                    ToastManager.showShort(context, "设置失败，请重试");
                }
                break;
            case R.id.bt_stop_auto:
                //停止倒计时
                getP().stopTimer();
                setAppendContent("停止启动");
                break;
            case R.id.bt_change_device_type:
                // 改变设备类型
                AppSharePreferenceMgr.put(context, UserInfoKey.SCREEN_NUM, -1);
                ToastManager.showShort(context, "请重启软件选择屏幕");
                System.exit(0);
                break;
        }
    }
    /**校验输入是否为空、添加提示*/
    private boolean checkIsEmpty() {
            if (TextUtils.isEmpty(mainIp.getText().toString())){
                return false;
            } else {
                AppSharePreferenceMgr.put(context, UserInfoKey.BIG_SCREEN_IP, mainIp.getText().toString());
                setAppendContent("参数设置成功");
                setAppendContent("大屏IP" + mainIp.getText().toString());
                setAppendContent("参数设置完成，系统将在五秒后执行");
            }
        return true;
    }
    /**添加提示*/
    private void setAppendContent(String content) {
        if (!TextUtils.isEmpty(content)){
            tipContent.append("\n" + content);
        } else {
            tipContent.append(content);
        }
    }

    public static void launch(Activity activity) {
        Router.newIntent(activity)
                .to(CreateParamsActivity.class)
                .launch();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_params;
    }

    @Override
    public ParamsPresent newP() {
        return new ParamsPresent();
    }
}
