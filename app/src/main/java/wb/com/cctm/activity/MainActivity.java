package wb.com.cctm.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.alibaba.fastjson.JSONObject;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.security.NoSuchAlgorithmException;

import wb.com.cctm.App;
import wb.com.cctm.R;
import wb.com.cctm.base.BaseActivity;
import wb.com.cctm.bean.VersionBean;
import wb.com.cctm.commons.utils.BBConfig;
import wb.com.cctm.commons.utils.MD5;
import wb.com.cctm.commons.utils.ToastUtils;
import wb.com.cctm.commons.utils.VersionUtil;
import wb.com.cctm.fragment.DeliverFragment;
import wb.com.cctm.fragment.MarketFragment;
import wb.com.cctm.fragment.MineFragment;
import wb.com.cctm.fragment.RecoderFragment;
import wb.com.cctm.fragment.SuanliFragment;
import wb.com.cctm.net.CommonCallbackImp;
import wb.com.cctm.net.FlowAPI;
import wb.com.cctm.net.MXUtils;

public class MainActivity extends BaseActivity {
    private Button[] mTabs;
    private Fragment[] fragments;
    private int index;
    private int currentTabIndex;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };
    private long exitTime = 0;
    private ProgressDialog pBar;
    private VersionBean versionBean;
    private boolean isDown = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(this,R.layout.activity_main);
        App.getInstance().addActivity(this);
        initView();
    }

    private void initView() {
        DeliverFragment deliverFragment = new DeliverFragment();
        MarketFragment marketFragment = new MarketFragment();
        MineFragment mineFragment = new MineFragment();
        RecoderFragment recoderFragment = new RecoderFragment();
        SuanliFragment suanliFragment = new SuanliFragment();
        fragments = new Fragment[] {deliverFragment,suanliFragment,recoderFragment,mineFragment};
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, deliverFragment)
                .add(R.id.fragment_container, suanliFragment)
                .add(R.id.fragment_container, recoderFragment)
                .add(R.id.fragment_container, mineFragment)
                .hide(deliverFragment)
                .hide(suanliFragment)
                .hide(recoderFragment)
                .hide(mineFragment)
                .show(deliverFragment)
                .commit();
        mTabs = new Button[4];
        mTabs[0] = (Button) findViewById(R.id.btn_deliver);
        mTabs[1] = (Button) findViewById(R.id.btn_market);
        mTabs[2] = (Button) findViewById(R.id.btn_recoder);
        mTabs[3] = (Button) findViewById(R.id.btn_mine);
        mTabs[0].setSelected(true);
    }
    /**
     * on tab clicked
     *
     * @param view
     */
    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_deliver:
                index = 0;
                break;
            case R.id.btn_market:
                index = 1;
                break;
            case R.id.btn_recoder:
                index = 2;
                break;
            case R.id.btn_mine:
                index = 3;
                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        mTabs[currentTabIndex].setSelected(false);
        // set current tab selected
        mTabs[index].setSelected(true);
        currentTabIndex = index;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        } else {
            initTools();
            if (!isDown) {
                // 获取版本信息
                getNewVersion();
            }
        }
    }


    private void initTools(){
        String out_file_path = BBConfig.YYW_FILE_PATH;
        File dir = new File(out_file_path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if((System.currentTimeMillis()-exitTime) > 2000){
            Toast.makeText(this,"再次点击退出应用",Toast.LENGTH_LONG).show();
            exitTime = System.currentTimeMillis();
        } else {
            App.getInstance().exit();
        }
    }

    //双击后退按钮关闭应用
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("","KKKKKKKKKKKKKKKKKKKKKKK=" + keyCode);
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(this,"再次点击退出应用",Toast.LENGTH_LONG).show();
                exitTime = System.currentTimeMillis();
            } else {
                App.getInstance().exit();
            }
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_HOME){
            // 不退出程序，进入后台
            moveTaskToBack(true);
        }
        return super.onKeyDown(keyCode, event);
    }

    private void getNewVersion() {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.version);
        MXUtils.httpPost(requestParams,new CommonCallbackImp("INDEX - 版本号",requestParams,this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    versionBean = JSONObject.parseObject(pd,VersionBean.class);
                    int code = Integer.valueOf(versionBean.getV_VERSION_CODE());
                    if (code>VersionUtil.getAppVersionCode(MainActivity.this)) {
                        showUpdateDialog(versionBean.getV_NUMBER(),versionBean.getV_ADDR());
                    }
                } else {
                    ToastUtils.toastutils(message,MainActivity.this);
                }

            }
        });
    }

    private void showUpdateDialog(String newVersion, final String url) {
        String verName = VersionUtil.getAppVersionName(MainActivity.this);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("软件更新")
                .setCancelable(false)
                .setMessage("当前版本"+verName+",发现有新版本"+newVersion+"请及时更新")
                .setNegativeButton(
                        "确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                int permission = ActivityCompat.checkSelfPermission(MainActivity.this,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                if (permission != PackageManager.PERMISSION_GRANTED) {
                                    // We don't have permission so prompt the user
                                    ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE,
                                            REQUEST_EXTERNAL_STORAGE);
                                } else {
                                    initTools();
                                    isDown = true;
                                    downFile(url);
                                }
                            }
                        }).show();
    }

    private void downFile(String downurl) {
        pBar = new ProgressDialog(MainActivity.this);    //进度条，在下载的时候实时更新进度，提高用户友好度
        pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pBar.setTitle("正在下载");
        pBar.setMessage("请稍候...");
        pBar.setProgress(0);
        pBar.setCancelable(false);
        pBar.show();
        String apkName = downurl.substring(downurl.lastIndexOf("/") + 1);// 接口名称
        final String filepath = BBConfig.YYW_FILE_PATH + apkName;
        RequestParams requestParams = new RequestParams(downurl);
        requestParams.setSaveFilePath(filepath);
        x.http().get(requestParams, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(File result) {
                Toast.makeText(MainActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
                installApk(filepath);
                pBar.dismiss();
                pBar = null;
                isDown = false;
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                Toast.makeText(MainActivity.this, "下载失败，请检查网络和SD卡", Toast.LENGTH_SHORT).show();
                pBar.dismiss();
                pBar = null;
                isDown = false;
            }
            @Override
            public void onCancelled(CancelledException cex) {

            }
            @Override
            public void onFinished() {

            }
            @Override
            public void onWaiting() {

            }
            @Override
            public void onStarted() {

            }
            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                System.out.println("downloadProgress -- " + total + "  " + current);
                int p = (int) (((float) current / total) * 100);
                pBar.setProgress(p);
            }
        });
    }

    private void installApk(String apkpath) {
        Log.i("","FFFFFFFFFFFFFFFFF=" + apkpath);
        File file = new File(apkpath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if(Build.VERSION.SDK_INT>=24) { //判读版本是否在7.0以上
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            String auth =  getApplication().getPackageName()+".fileProvider";
            Uri apkUri = FileProvider.getUriForFile(MainActivity.this, auth, file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        }else{
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted准许
                Toast.makeText(MainActivity.this,"已获得授权！",Toast.LENGTH_SHORT).show();
                initTools();
                if (versionBean != null) {
                    int code = Integer.valueOf(versionBean.getV_VERSION_CODE());
                    if (code>VersionUtil.getAppVersionCode(MainActivity.this)) {
                        isDown = true;
                        downFile(versionBean.getV_ADDR());
                    }
                }
            } else {
                // Permission Denied拒绝
                Toast.makeText(MainActivity.this,"未获得授权！",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
