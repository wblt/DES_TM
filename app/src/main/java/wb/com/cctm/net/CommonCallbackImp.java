package wb.com.cctm.net;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;


import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import wb.com.cctm.base.BaseActivity;

/**
 * Created by TianHongChun on 2016/6/1.
 * 网络请求,结果监听, 可以对大数据进行进度监听
 *  比如表单参数带文件
 */
public class CommonCallbackImp implements Callback.CommonCallback<String>,Callback.ProgressCallback<String> {
    private String tip;
    private RequestParams requestParams;
    private BaseActivity activity;
    private int type = 0;

    public CommonCallbackImp(String tip, RequestParams requestParams, BaseActivity activity){
        this.tip = tip;
        this.requestParams = requestParams;
        this.activity = activity;
        type = 1;
    }

    public CommonCallbackImp(String tip, RequestParams requestParams){
        this.tip = tip;
        this.requestParams = requestParams;
    }

    private void show(String pMsg){
        if(activity == null){
            return;
        }
        Toast toast =  Toast.makeText(activity,pMsg,Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onSuccess(String result) {
        Log.i("",tip + "返回："+result);
        if(type == 1){
            activity.dismissLoadding();
        }
    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
        if (ex instanceof ConnectException || ex instanceof SocketTimeoutException) {
            show("连接超时,请重新尝试");
        } else if (ex instanceof HttpException){
            show("网络错误");
        }else {
            show("服务器异常,请联系管理员");
            ex.printStackTrace();
        }
        if(type == 1) {
            activity.dismissLoadding();
        }
    }

    @Override
    public void onCancelled(CancelledException cex) {

    }

    @Override
    public void onFinished() {
    }

    //////////////////// 上传带文件可以监听进度 //////
    @Override
    public void onWaiting() {
    }

    @Override
    public void onStarted() {
        Log.i("",tip+"参数：" + requestParams.getStringParams() + requestParams.getFileParams());
        if(type == 1){
            activity.showLoadding("请稍候...");
        }
    }

    @Override
    public void onLoading(long total, long current, boolean isDownloading) {
        Log.i("","onLoading:"+total+"="+current+"="+isDownloading);
    }
}
