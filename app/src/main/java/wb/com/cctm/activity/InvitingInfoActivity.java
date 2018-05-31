package wb.com.cctm.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.xutils.http.RequestParams;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wb.com.cctm.R;
import wb.com.cctm.base.BaseActivity;
import wb.com.cctm.bean.FrendsBean;
import wb.com.cctm.commons.utils.ImageLoader;
import wb.com.cctm.commons.utils.SPUtils;
import wb.com.cctm.commons.utils.ToastUtils;
import wb.com.cctm.net.CommonCallbackImp;
import wb.com.cctm.net.FlowAPI;
import wb.com.cctm.net.MXUtils;

public class InvitingInfoActivity extends BaseActivity {

    @BindView(R.id.tv_left_addr)
    TextView tv_left_addr;
    @BindView(R.id.tv_right_addr)
    TextView tv_right_addr;
    @BindView(R.id.tv_app_addr)
    TextView tv_app_addr;
    private String left_str = "";
    private String right_str = "";
    private String app_url = "";
    @BindView(R.id.tv_l_total)
    TextView tv_l_total;
    @BindView(R.id.tv_r_total)
    TextView tv_r_total;
    @BindView(R.id.ll_content)
    LinearLayout ll_content;
    @BindView(R.id.tv_count)
    TextView tv_count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(this,R.layout.activity_inviting_info);
        appendTopBody(R.layout.activity_top_icon);
        setTopBarTitle("邀请好友");
        setTopLeftDefultListener();
        ButterKnife.bind(this);
        initveiw();
        invitation();
        friends();
    }

    private void initveiw() {
//        tv_left_addr.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
//        tv_left_addr.getPaint().setAntiAlias(true);//抗锯齿
//
//        tv_right_addr.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
//        tv_right_addr.getPaint().setAntiAlias(true);//抗锯齿
//
        tv_app_addr.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tv_app_addr.getPaint().setAntiAlias(true);//抗锯齿
    }

    @OnClick({R.id.tv_left_addr,R.id.tv_right_addr,R.id.tv_app_addr,R.id.ll_invate})
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.tv_left_addr:
//                ClipboardManager clip_left = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
//                clip_left.setText(left_str);
//                ToastUtils.toastutils("你已复制到粘贴板",InvitingInfoActivity.this);
                break;
            case R.id.tv_right_addr:
//                ClipboardManager clip_right = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
//                clip_right.setText(right_str);
//                ToastUtils.toastutils("你已复制到粘贴板",InvitingInfoActivity.this);
                break;
            case R.id.tv_app_addr:
                ClipboardManager clip_app = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                clip_app.setText(app_url);
                ToastUtils.toastutils("你已复制到粘贴板",InvitingInfoActivity.this);
                break;
            case R.id.ll_invate:
                Intent intent = new Intent(InvitingInfoActivity.this,FrendsActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void invitation() {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.invitation);
        requestParams.addParameter("USER_NAME", SPUtils.getString(SPUtils.username));
        requestParams.addParameter("TERMINAL", "2");
        MXUtils.httpPost(requestParams,new CommonCallbackImp("INDEX - 邀请好友",requestParams,this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    JSONObject pd_obj = JSONObject.parseObject(pd);
                    left_str = pd_obj.getString("LEFT_URL");
                    right_str = pd_obj.getString("RIGHT_URL");
                    app_url = pd_obj.getString("APP_URL");
                    tv_left_addr.setText(left_str);
                    tv_right_addr.setText(right_str);
                    tv_app_addr.setText(app_url);
                    tv_l_total.setText(pd_obj.getString("L_TOTAL"));
                    tv_r_total.setText(pd_obj.getString("R_TOTAL"));
                    ll_content.setVisibility(View.VISIBLE);
                } else {
                    ToastUtils.toastutils(message,InvitingInfoActivity.this);
                }
            }
        });

    }

    private void friends() {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.friends);
        requestParams.addParameter("USER_NAME", SPUtils.getString(SPUtils.username));
        MXUtils.httpPost(requestParams,new CommonCallbackImp("INDEX - 我的好友",requestParams,this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    List<FrendsBean> beanList = JSONArray.parseArray(pd, FrendsBean.class);
                    if (beanList != null) {
                        tv_count.setText(""+beanList.size());
                    }
                }
            }
        });
    }


}
