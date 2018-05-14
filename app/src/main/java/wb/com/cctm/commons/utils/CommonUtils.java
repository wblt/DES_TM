package wb.com.cctm.commons.utils;

import android.content.Context;
import android.view.View;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jingbin on 2016/11/22.
 * 获取原生资源
 */
public class CommonUtils {
    public static String getCurrentTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(System.currentTimeMillis());
    }

    /**
     * 获取前n天日期、后n天日期
     *
     * @param distanceDay 前几天 如获取前7天日期则传-7即可；如果后7天则传7
     * @return
     */
    public static String getOldDate(String beginstr,int distanceDay){
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date beginDate = df.parse(beginstr);
            SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
            Calendar date = Calendar.getInstance();
            date.setTime(beginDate);
            date.set(Calendar.DATE, date.get(Calendar.DATE) + distanceDay);
            Date endDate = dft.parse(dft.format(date.getTime()));
            return dft.format(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
