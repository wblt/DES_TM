package wb.com.cctm.commons.utils;

import android.content.Context;
import android.widget.Toast;

/*--------------------------------------------------------------------
  文 件 名：ToastUtils
  作 　 者：HuangXiJun (黄夕君)
  创建日期：V1.0,  2018/3/16(版本号+逗号＋日期，注：日期格式：YYYY－MMM－DD，即月用英文表示，尽量减少异意)
  模块功能：Toast的工具类，主要是接口访问操作是弹出的提示语
---------------------------------------------------------------------*/
public class ToastUtils {
    public static void toastutils(String msg, Context context){
        if(msg!=null&&!msg.isEmpty())
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
}
