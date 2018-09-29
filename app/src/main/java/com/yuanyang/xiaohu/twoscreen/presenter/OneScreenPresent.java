package com.yuanyang.xiaohu.twoscreen.presenter;


import com.google.gson.Gson;
import com.yuanyang.xiaohu.twoscreen.activity.OneScreenActivity;
import com.yuanyang.xiaohu.twoscreen.model.BaseBean;
import com.yuanyang.xiaohu.twoscreen.model.ScreenDataModel;
import com.yuanyang.xiaohu.twoscreen.model.ScreenShowModel;
import com.yuanyang.xiaohu.twoscreen.net.BillboardApi;
import com.yuanyang.xiaohu.twoscreen.net.UserInfoKey;
import com.yuanyang.xiaohu.twoscreen.service.UpdateService;
import com.yuanyang.xiaohu.twoscreen.util.AppSharePreferenceMgr;
import com.yuanyang.xiaohu.twoscreen.util.DownloadFileUtil;
import com.yuanyang.xiaohu.twoscreen.util.ReaderJsonUtil;

import java.util.List;

import cn.com.library.log.XLog;
import cn.com.library.mvp.XPresent;
import cn.com.library.net.ApiSubscriber;
import cn.com.library.net.NetError;
import cn.com.library.net.XApi;


public class OneScreenPresent extends XPresent<OneScreenActivity> {

    /**
     * 回调 展示数据、启动及时服务、上报状态
     */
    CallBack callBack = new CallBack() {
        @Override
        public void onChangeUI() {
            getV().dialog.dismiss();
            getV().showData();
            UpdateService.getInstance().startTimer();
            updateState();
        }

        @Override
        public void onErrorChangeUI(String message) {
            getV().showToastManger(message);
        }
    };

    /**
     * 获取数据
     */
    public void getScreenData(String ipAddress, boolean isRefresh) {
        BillboardApi.getDataService().getData(ipAddress)
                .compose(XApi.<BaseBean<ScreenDataModel>>getApiTransformer())
                .compose(XApi.<BaseBean<ScreenDataModel>>getScheduler())
                .compose(getV().<BaseBean<ScreenDataModel>>bindToLifecycle())
                .subscribe(new ApiSubscriber<BaseBean<ScreenDataModel>>() {
                    @Override
                    protected void onFail(NetError error) {
                        getV().dialog.dismiss();
                        if (isRefresh)
                            callBack.onChangeUI();
                        else
                            UpdateService.getInstance().startTimer();
                        getV().showError(error);
                    }

                    @Override
                    public void onNext(BaseBean<ScreenDataModel> model) {
                        if (model.isSuccess()) {
                            downloadAndSaveData(model.getMessageBody());
                        } else {
                            if (isRefresh)
                                callBack.onChangeUI();
                            else
                                UpdateService.getInstance().startTimer();
                        }
                    }
                });
    }

    /**
     * 下载并保存数据
     */
    private void downloadAndSaveData(ScreenDataModel model) {
        AppSharePreferenceMgr.put(getV(), UserInfoKey.BIG_SCREEN_ID, model.getSid());
        List[] lists = ReaderJsonUtil.getInstance().splitsData(new Gson().fromJson(model.getMessage(), ScreenShowModel.class));
        DownloadFileUtil.getInstance().downBigLoadPicture(getV(), lists[0], lists[1], callBack);
    }

    /**
     * 上报状态
     */
    private void updateState() {
        String bigId = AppSharePreferenceMgr.get(getV(), UserInfoKey.BIG_SCREEN_ID, "").toString();
        BillboardApi.getDataService().upState(bigId)
                .compose(XApi.<BaseBean>getApiTransformer())
                .compose(XApi.<BaseBean>getScheduler())
                .compose(getV().<BaseBean>bindToLifecycle())
                .subscribe(new ApiSubscriber<BaseBean>() {
                    @Override
                    protected void onFail(NetError error) {
                        getV().showError(error);
                    }

                    @Override
                    public void onNext(BaseBean model) {
                        if (model.isSuccess()) {
                            XLog.e("状态上报成功");
                        } else {
                            XLog.e("状态上报失败");
                        }
                    }
                });
    }

    /**
     * 回调
     */
    public interface CallBack {
        void onChangeUI();

        void onErrorChangeUI(String message);
    }

}
