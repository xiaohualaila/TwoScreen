package com.yuanyang.xiaohu.twoscreen.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.view.View;

import com.bigkoo.alertview.AlertView;
import com.yuanyang.xiaohu.twoscreen.R;
import com.yuanyang.xiaohu.twoscreen.dialog.DownloadAPKDialog;
import com.yuanyang.xiaohu.twoscreen.dialog.LoadingDialog;
import com.yuanyang.xiaohu.twoscreen.model.VersionModel;
import com.yuanyang.xiaohu.twoscreen.net.UserInfoKey;
import com.yuanyang.xiaohu.twoscreen.presenter.LauncherPresent;
import com.yuanyang.xiaohu.twoscreen.util.AppDownload;
import com.yuanyang.xiaohu.twoscreen.util.AppSharePreferenceMgr;

import java.io.File;
import cn.com.library.kit.ToastManager;
import cn.com.library.mvp.XActivity;
import cn.com.library.net.NetError;

public class LauncherActivity extends XActivity<LauncherPresent> implements AppDownload.Callback{

    public LoadingDialog dialog;
    public DownloadAPKDialog dialog_app;

    @Override
    public void initData(Bundle savedInstanceState) {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        dialog = new LoadingDialog(context, "请稍后···");
//        XLog.e("getAllDevices====" + new Gson().toJson(new SerialPortFinder().getAllDevices()));
//        XLog.e("getAllDevicesPath====" + new Gson().toJson(new SerialPortFinder().getAllDevicesPath()));
//        LocationUtil.getInstance().startLocation(context);
        getP().checkPermissions();
    }

    /**
     * 判断是否选择过屏
     */
    public void nextAction() {
//        if (((int) AppSharePreferenceMgr.get(context, UserInfoKey.SCREEN_NUM, -1)) == -1)
//            selectScreenNum();
//        else {
            dialog.show();
            getP().loadData(1);
//        }
    }

    /**
     * 选择屏幕
     */
//    private void selectScreenNum() {
//        new AlertView("选择屏幕", null, null, null, new String[]{"室内双屏", "室外大屏",  "拍照", "视频"}, this, AlertView.Style.ActionSheet,
//                (o, position) -> {
//                    dialog.show();
//                    ToastManager.showShort(context, position == 0 ? "室内双屏" : position == 1 ? "室外大屏" : position == 2 ?  "拍照" : "视频");
//                    AppSharePreferenceMgr.put(context, UserInfoKey.SCREEN_NUM, position);
//                    getP().loadData(position);
//                }).show();
//    }

    /**
     * 请求返回错误
     */
    public void showError(NetError error) {
        dialog.dismiss();
        if (error != null) {
            switch (error.getType()) {
                case NetError.ParseError:
                    ToastManager.showShort(context, "数据解析异常");
                    break;

                case NetError.AuthError:
                    ToastManager.showShort(context, "身份验证异常");
                    break;

                case NetError.BusinessError:
                    ToastManager.showShort(context, "业务异常");
                    break;

                case NetError.NoConnectError:
                    ToastManager.showShort(context, "网络无连接");
                    break;

                case NetError.NoDataError:
                    ToastManager.showShort(context, "数据为空");
                    break;

                case NetError.OtherError:
                    ToastManager.showShort(context, "其他异常");
                    break;
            }

                CreateParamsActivity.launch(context);
                finish();
        }
    }

    public void updateVersion(VersionModel model){
        dialog_app = new DownloadAPKDialog(this);
        dialog_app.show();
        dialog_app.setCancelable(false);
        dialog_app.getFile_name().setText(model.getVdetails());
        dialog_app.getFile_num().setText(model.getVnumber());
        AppDownload appDownload = new AppDownload();
        appDownload.setProgressInterface(this);
        appDownload.downApk(model.getDownload(),this);
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_launcher;
    }

    @Override
    public LauncherPresent newP() {
        return new LauncherPresent();
    }

    @Override
    public void callProgress(int progress) {
        if (progress >= 100) {
            runOnUiThread(() -> {
                dialog_app.dismiss();
               // String path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS )+ "zhsq.apk";
                 String path = "/storage/emulated/0/download/"+"zhsq.apk";
                install(path);
            });

        }else {
            runOnUiThread(() -> {
                dialog_app.getSeekBar().setProgress( progress );
                dialog_app.getNum_progress().setText(progress+"%");
            });
        }
    }

    /**
     * 开启安装过程
     * @param fileName
     */
    private void install(String fileName) {
        //承接我的代码，filename指获取到了我的文件相应路径
         if (fileName != null) {
             if (fileName.endsWith(".apk")) {
                 if(Build.VERSION.SDK_INT>=24) {//判读版本是否在7.0以上
                       File file= new File(fileName);
                       Uri apkUri = FileProvider.getUriForFile(context, "cn.com.billboard.fileprovider", file);
                       //在AndroidManifest中的android:authorities值
                        Intent install = new Intent(Intent.ACTION_VIEW);
                        install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                              install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                              context.startActivity(install);
                 } else{
                     Intent install = new Intent(Intent.ACTION_VIEW);
                     install.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
                     install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                     context.startActivity(install);
                 }
             }
         }


    }



}
