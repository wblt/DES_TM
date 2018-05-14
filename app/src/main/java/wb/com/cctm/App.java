package wb.com.cctm;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.tencent.bugly.crashreport.CrashReport;

import org.xutils.x;

import java.util.LinkedList;
import java.util.List;

import wb.com.cctm.activity.MainActivity;

/**
 * Created by wb on 2018/4/12.
 */

public class App extends Application {
    private List<Activity> mList = new LinkedList<>();
    private static App instance;
    private MainActivity mainActivity;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // 腾讯bugly
        CrashReport.initCrashReport(getApplicationContext(), "a92ccabdd7", true);
        initExt();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static App getInstance() {
        return instance;
    }

    public void addActivity(Activity activity){
        mList.add(activity);
    }

    public void closeActivitys() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
            mList.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initExt(){
        x.Ext.init(this);
        x.Ext.setDebug(true);
    }

    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

}
