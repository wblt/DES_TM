package wb.com.cctm.net;

import org.xutils.http.RequestParams;

/**
 * Created by wb on 2018/4/19.
 */

public class FlowAPI {
    // 服务器返回成功标志
    public static final String SUCCEED="1000";
    public static RequestParams getRequestParams(String uri){
        RequestParams requestParams=new RequestParams(uri);
        requestParams.setConnectTimeout(150000);
        return requestParams;
    }
    public static final String TX_FILE_ID = "1254340937";
    public static final String TX_PRES_ID = null;

//    public static String SERVER_IP = "http://139.196.225.206:8082";
    public static String SERVER_IP = "http://decapp.top";

    // USER - 注册短信验证码
    public static String register_code = SERVER_IP + "/app/user/sysendMess";

    // USER - 用户注册
    public static String register = SERVER_IP + "/app/user/syreg";

    // USER - 用户登录
    public static String login = SERVER_IP + "/app/user/login";

    // USER - 忘记密码短信验证码
    public static String forgotpwd_code = SERVER_IP + "/app/user/sysendMessFG";

    // USER - 忘记密码
    public static String forgotpwd = SERVER_IP + "/app/user/forget";

    // INDEX - 公告
    public static String notice = SERVER_IP + "/app/index/notice";

    // INDEX - 我的好友
    public static String friends = SERVER_IP + "/app/index/friends";

    // INDEX - 记录步数
    public static String dayStep = SERVER_IP + "/app/index/dayStep";

    // INDEX - 邀请好友
    public static String invitation = SERVER_IP + "/app/index/invitation";

    // INDEX - 零钱钱包转入算力钱包
    public static String transferred = SERVER_IP + "/app/index/transferred";

    // INDEX - 首页
    public static String homePage = SERVER_IP + "/app/index/homePage";

    // TOOL - 修改个人信息
    public static String cgPersonMes = SERVER_IP + "/app/tool/cgPersonMes";

    // TOOL - 修改安全密码
    public static String aqPassw = SERVER_IP + "/app/tool/aqPassw";

    // TOOL - 修改手机号码
    public static String phoneCg = SERVER_IP + "/app/tool/phoneCg";

    // TOOL - 修改登录密码
    public static String changePassw = SERVER_IP + "/app/tool/changePassw";

    // INDEX - 释放记录
    public static String release = SERVER_IP + "/app/index/release";

    // INDEX - 释放详情
    public static String releaseDetaiil = SERVER_IP + "/app/index/releaseDetaiil";

    // MY - 复利状态
    public static String ifFl = SERVER_IP + "/app/my/ifFl";

    // MY - 修改复利状态
    public static String cgFl = SERVER_IP + "/app/my/cgFl";

    // MY - 兑换能量
    public static String changeEnergy = SERVER_IP + "/app/my/changeEnergy";

    // MY - 兑换能量页面
    public static String cgEnergyMes = SERVER_IP + "/app/my/cgEnergyMes";

    // MY - 发送
    public static String send = SERVER_IP + "/app/my/send";

    // MY - 可发送内容
    public static String sendMes = SERVER_IP + "/app/my/sendMes";

    // MY - 接收记录
    public static String receiveDetail = SERVER_IP + "/app/my/receiveDetail";

    // MY - 转账记录
    public static String sendDetail = SERVER_IP + "/app/my/sendDetail";

    // MY - 运动记录
    public static String stepDetail = SERVER_IP + "/app/my/stepDetail";

    // USER - 安全密码短信验证码
    public static String sysendMessAQ = SERVER_IP + "/app/user/sysendMessAQ";

    // K线图
    public static String depth = SERVER_IP + "/app/market/depth";

    // TOOL - 修改支付信息
    public static String cgPayMes = SERVER_IP + "/app/tool/cgPayMes";

    // TOOL - 修改支付信息
    public static String payMes = SERVER_IP + "/app/tool/payMes";

    // MARKET - 买单
    public static String buy = SERVER_IP + "/app/market/buy";

    // MARKET - 买单列表
    public static String buyList = SERVER_IP + "/app/market/buyList";

    // MARKET - 卖单列表
    public static String sellList = SERVER_IP + "/app/market/sellList";

    // MARKET - 市场列表
    public static String marketList = SERVER_IP + "/app/market/marketList";

    // MARKET - 挂单
    public static String sell = SERVER_IP + "/app/market/sell";

    // MARKET - 指导价 点击挂单时候获取
    public static String price = SERVER_IP + "/app/market/price";

    // MARKET - 订单取消
    public static String orderCancle = SERVER_IP + "/app/market/orderCancle";

    // MARKET - 订单已付款(测试过审核也可用此接口)
    public static String pay = SERVER_IP + "/app/market/pay";

    // MARKET - 订单确认收款
    public static String surePay = SERVER_IP + "/app/market/surePay";

    // MARKET - 订单详情
    public static String orderDetail = SERVER_IP + "/app/market/orderDetail";

    // INDEX - 版本号
    public static String version = SERVER_IP + "/app/index/version";

    // MARKET - 释放K线
    public static String releaseDepth = SERVER_IP + "/app/market/releaseDepth";

}
