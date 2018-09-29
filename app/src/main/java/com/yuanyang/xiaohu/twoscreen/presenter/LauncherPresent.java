package com.yuanyang.xiaohu.twoscreen.presenter;

import android.Manifest;
import android.app.ProgressDialog;

import com.blankj.utilcode.util.PermissionUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yuanyang.xiaohu.twoscreen.activity.CreateParamsActivity;
import com.yuanyang.xiaohu.twoscreen.activity.LauncherActivity;
import com.yuanyang.xiaohu.twoscreen.model.BaseBean;
import com.yuanyang.xiaohu.twoscreen.model.VersionModel;
import com.yuanyang.xiaohu.twoscreen.net.BillboardApi;
import com.yuanyang.xiaohu.twoscreen.util.APKVersionCodeUtils;
import com.yuanyang.xiaohu.twoscreen.util.AppSharePreferenceMgr;
import com.yuanyang.xiaohu.twoscreen.util.PermissionsUtil;
import com.yuanyang.xiaohu.twoscreen.util.SDCardUtil;

import cn.com.library.kit.ToastManager;
import cn.com.library.log.XLog;
import cn.com.library.mvp.XPresent;
import cn.com.library.net.ApiSubscriber;
import cn.com.library.net.NetError;
import cn.com.library.net.XApi;

public class LauncherPresent extends XPresent<LauncherActivity> {

    /**权限申请*/
    public void checkPermissions(){
        PermissionsUtil.requestPermission(mPermission, new RxPermissions(getV()),
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO);
    }
    /**权限申请回调*/
    private PermissionsUtil.RequestPermission mPermission = new PermissionsUtil.RequestPermission() {
        @Override
        public void onRequestPermissionSuccess() {
            getV().nextAction();

        }

        @Override
        public void onRequestPermissionFailure() {
            ToastManager.showShort(getV(), "请授权后重启软件");
            PermissionUtils.launchAppDetailsSettings();
            getV().finish();
        }

        @Override
        public void onRequestPermissionFailureWithAskNeverAgain() {
            ToastManager.showShort(getV(), "请授权后重启软件");
            PermissionUtils.launchAppDetailsSettings();
            getV().finish();
        }
    };
    /**获取数据*/
    public void loadData(int version) {
        BillboardApi.getDataService().checkVersion(version)
                .compose(XApi.<BaseBean<VersionModel>>getApiTransformer())
                .compose(XApi.<BaseBean<VersionModel>>getScheduler())
                .compose(getV().<BaseBean<VersionModel>>bindToLifecycle())
                .subscribe(new ApiSubscriber<BaseBean<VersionModel>>() {
                    @Override
                    protected void onFail(NetError error) {
                        getV().showError(error);
                    }

                    @Override
                    public void onNext(BaseBean<VersionModel> model) {
                        getV().dialog.dismiss();
                        if (model.isSuccess()) {
//                            XLog.e("model========" + new Gson().toJson(model));
//                            getV().showData(model.getMessageBody());
                            VersionModel model1 = model.getMessageBody();
                            int v_no = APKVersionCodeUtils.getVersionCode(getV());
                            if(model1.getBuild()> v_no){
                                   getV().updateVersion(model1);
                            }else {
                                CreateParamsActivity.launch(getV());
                                getV().finish();
                            }
                        } else {
                            CreateParamsActivity.launch(getV());
                            getV().finish();
                        }
                    }
                });
    }


}
