package wb.com.cctm.commons.utils;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import java.math.BigDecimal;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/8/23.
 */
public class StringUtil {

    public static int initRoomNum(){
        String time = String.valueOf(System.currentTimeMillis());
        Random random = new Random();
        String s = "";
        for(int i=0;i<3;i++){
            s += String.valueOf(random.nextInt(8) + 1);
        }
        time = s + time.substring(3,9);
        return Integer.valueOf(time);
    }

    static Random random=new Random();
    private static char[] chars = new char[] { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
            'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
            'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z' };
    /**
     * 取随机的字符串
     * @return
     */
    public static String getRandomString(int length) {
        char[] r = new char[length];
        for (int i = 0; i < r.length; i++) {
            r[i] = chars[random.nextInt(chars.length)];
        }
        return String.valueOf(r);
    }

    public static String subZeroAndDot(String s){
        if(s != null){
            if(s.indexOf(".") > 0){
                s = s.replaceAll("0+?$", "");//去掉多余的0
                s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
            }
        }
        return s;
    }

    //字符串减法
    public static String jian(String a,String b){
        BigDecimal b1 = new BigDecimal(a);
        BigDecimal b2 = new BigDecimal(b);
        return subZeroAndDot(b1.subtract(b2).toString());
    }



    /**
     * 判断是否全部为中文字符
     * @param s
     * @return
     */
    public static boolean checkfilename(String s){
        s=new String(s.getBytes());//用GBK编码
        String pattern="[\u4e00-\u9fa5]+";
        Pattern p= Pattern.compile(pattern);
        Matcher result=p.matcher(s);
        return result.matches(); //是否含有中文字符
    }

    public static boolean checkpwd(String pwd) {
        String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,}$";
        if (pwd.matches(regex)){
            return true;
        } else {
            return false;
        }
    }


    /**
     * 设置字体颜色
     */
    public static SpannableStringBuilder textAddColor(String content, int start, int end, int color){
        /// 改变字体hint颜色， 也可改变大小....
        if(content==null||content.equals("")){
            content="";
        }
        if(start<0){
            start=0;
        }
        if(end<0){
            end=content.length();
        }
        SpannableStringBuilder ssb = new SpannableStringBuilder(content);
        ForegroundColorSpan fcs=new ForegroundColorSpan(color);
        ssb.setSpan(fcs, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置hint<br />
        return ssb;
    }

    public static String getRandom5(){
        int n=0;
        n=(int)(Math.random()*100000);
        while(n<10000 || !handle(n)){
            n=(int)(Math.random()*100000);
        }
        return n+"";
    }

    public static boolean handle(int n){
        int[] list=new int[5];
        for(int i=0;i<5;i++){
            list[i]=n%10;
            n=n/10;
        }
        for(int i=0;i<5;i++){
            for(int j=i+1;j<5;j++){
                if(list[i]==list[j]) return false;
            }
        }
        return true;
    }
}
