package wb.com.cctm.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import org.xutils.http.RequestParams;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wb.com.cctm.R;
import wb.com.cctm.adapter.BBPageAdapter;
import wb.com.cctm.base.BaseActivity;
import wb.com.cctm.commons.utils.SPUtils;
import wb.com.cctm.commons.utils.ToastUtils;
import wb.com.cctm.net.CommonCallbackImp;
import wb.com.cctm.net.FlowAPI;
import wb.com.cctm.net.MXUtils;

public class GuadanActivity extends BaseActivity {
    @BindView(R.id.tv_shouxu_fei)
    TextView tv_shouxu_fei;
    @BindView(R.id.tv_sum_price)
    TextView tv_sum_price;
    @BindView(R.id.et_sell_price)
    EditText et_sell_price;
    @BindView(R.id.et_sell_number)
    EditText et_sell_number;
    @BindView(R.id.tv_zhidao_price)
    TextView tv_zhidao_price;
    @BindView(R.id.tv_gua_sell_number)
    TextView tv_gua_sell_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(this,R.layout.activity_guadan);
        appendTopBody(R.layout.activity_top_text);
        setTopBarTitle("我要挂单");
        setTopLeftDefultListener();
        ButterKnife.bind(this);
        initview();
        price();
    }

    private void initview() {
        et_sell_number.addTextChangedListener(textWatcher);
        et_sell_price.addTextChangedListener(textWatcher);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (!TextUtils.isEmpty(et_sell_number.getText().toString())&&
                    !TextUtils.isEmpty(et_sell_price.getText().toString())) {
                tv_shouxu_fei.setText(et_sell_number.getText().toString()+"能量");
                float price = Float.valueOf(et_sell_price.getText().toString());
                int number = Integer.valueOf(et_sell_number.getText().toString());
                float sum = number*price;
                DecimalFormat df = new DecimalFormat("#.00");
                tv_sum_price.setText("总价:"+df.format(sum));
            } else {
                tv_sum_price.setText("总价:"+"0.00");
            }
        }
    };

    @OnClick({R.id.btn_commit,R.id.ig_xxxx,R.id.ig_xxxx2})
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_commit:
                try {
                    if (TextUtils.isEmpty(et_sell_number.getText().toString())) {
                        ToastUtils.toastutils("请输入挂卖数量",GuadanActivity.this);
                        return;
                    }
                    if (TextUtils.isEmpty(et_sell_price.getText().toString())) {
                        ToastUtils.toastutils("请输入挂卖价格",GuadanActivity.this);
                        return;
                    }
                    int et_value = Integer.valueOf(et_sell_number.getText().toString());
                    String guaa_sell = tv_gua_sell_number.getText().toString();
                    float a_curr = Float.valueOf(guaa_sell);
                    if (et_value>a_curr) {
                        String ss =  "本次最多可转余额为"+tv_gua_sell_number.getText().toString();
                        ToastUtils.toastutils(ss,GuadanActivity.this);
                        return;
                    }
                    sell();
                } catch (Exception e) {
                    ToastUtils.toastutils("价格格式有误",GuadanActivity.this);
                    e.printStackTrace();
                }
                break;
            case R.id.ig_xxxx:
                et_sell_price.getText().clear();
                break;
            case R.id.ig_xxxx2:
                et_sell_number.getText().clear();
                break;
            default:
                break;
        }
    }

    private void price() {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.price);
        requestParams.addParameter("USER_NAME", SPUtils.getString(SPUtils.username));
        MXUtils.httpPost(requestParams,new CommonCallbackImp("MARKET - 指导价 点击挂单时候获取",requestParams,this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    JSONObject pd_obj = JSONObject.parseObject(pd);
                    tv_zhidao_price.setText(pd_obj.getString("BUSINESS_PRICE"));
                    tv_gua_sell_number.setText(pd_obj.getString("D_CURRENCY"));
                } else {
                    ToastUtils.toastutils(message,GuadanActivity.this);
                }

            }
        });
    }

    private void sell() {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.sell);
        requestParams.addParameter("USER_NAME", SPUtils.getString(SPUtils.username));
        requestParams.addParameter("PRICE",et_sell_price.getText().toString());
        requestParams.addParameter("D_CURRENCY", et_sell_number.getText().toString());
        MXUtils.httpPost(requestParams,new CommonCallbackImp("MARKET - 挂单",requestParams,this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    JSONObject pd_obj = JSONObject.parseObject(pd);
                    ToastUtils.toastutils("提交成功等待审核",GuadanActivity.this);
                    finish();
                } else {
                    ToastUtils.toastutils(message,GuadanActivity.this);
                }

            }
        });
    }
}
