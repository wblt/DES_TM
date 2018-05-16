package wb.com.cctm.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import org.xutils.http.RequestParams;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wb.com.cctm.R;
import wb.com.cctm.base.BaseActivity;
import wb.com.cctm.commons.utils.SPUtils;
import wb.com.cctm.commons.utils.ToastUtils;
import wb.com.cctm.net.CommonCallbackImp;
import wb.com.cctm.net.FlowAPI;
import wb.com.cctm.net.MXUtils;

public class WalletRecordActivity extends BaseActivity {

    @BindView(R.id.tv_today)
    TextView tv_today;
    @BindView(R.id.tv_big)
    TextView tv_big;
    @BindView(R.id.tv_small)
    TextView tv_small;
    @BindView(R.id.tv_smart)
    TextView tv_smart;
    @BindView(R.id.tv_jd)
    TextView tv_jd;
    @BindView(R.id.top_right_icon)
    ImageButton top_right_icon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(this,R.layout.activity_wallet_record);
        appendTopBody(R.layout.activity_top_icon);
        setTopBarTitle("释放记录");
        setTopLeftDefultListener();
        ButterKnife.bind(this);
        initview();
    }

    private void initview() {
        top_right_icon.setImageResource(R.mipmap.recode_icon);
        top_right_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalletRecordActivity.this,AllReleaseActivity.class);
                startActivity(intent);
            }
        });
        release();
    }


    private void release() {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.release);
        requestParams.addParameter("USER_NAME", SPUtils.getString(SPUtils.username));
        MXUtils.httpPost(requestParams,new CommonCallbackImp("INDEX - 释放记录",requestParams,this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    JSONObject pd_obj = JSONObject.parseObject(pd);
                    tv_today.setText(pd_obj.getString("CALCULATE_MONEY"));
                    tv_big.setText(pd_obj.getString("BIG_CURRENCY"));
                    tv_small.setText(pd_obj.getString("SMALL_CURRENCY"));
                    tv_smart.setText(pd_obj.getString("STATIC_CURRENCY"));
                    tv_jd.setText(pd_obj.getString("JD_CURRENCY"));
                } else {
                    ToastUtils.toastutils(message,WalletRecordActivity.this);
                }

            }
        });
    }
}

