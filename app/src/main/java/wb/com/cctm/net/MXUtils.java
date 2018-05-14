package wb.com.cctm.net;


import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by TianHongChun on 2016/5/14.
 * 网络工具,下载图片,下载文件，网络请求,同步、异步、UI线程任务,统一管理xutils
 */
public class MXUtils {
    /**
     * http
     * @param requestParams
     * @param callback
     */
    public static void httpPost(RequestParams requestParams, Callback.CommonCallback<String> callback){
            x.http().post(requestParams,callback);
    }
    public static void httpGet(RequestParams requestParams, Callback.CommonCallback<String> callback){
        x.http().get(requestParams,callback);
    }

}
