package wb.com.cctm.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

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

public class PayInfoActivity extends BaseActivity {
    @BindView(R.id.et_bank_name)
    EditText et_bank_name;
    @BindView(R.id.et_bank_number)
    EditText et_bank_number;
    @BindView(R.id.et_bank_user_name)
    EditText et_bank_user_name;
    @BindView(R.id.et_bank_addr_name)
    EditText et_bank_addr_name;
    @BindView(R.id.et_wechat_number)
    EditText et_wechat_number;
    @BindView(R.id.et_zhifuba_number)
    EditText et_zhifuba_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(this,R.layout.activity_pay_info);
        appendTopBody(R.layout.activity_top_text);
        setTopBarTitle("支付信息");
        setTopLeftDefultListener();
        ButterKnife.bind(this);
        payMes();
    }

    @OnClick({R.id.btn_commit})
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_commit:
                String wechat = et_wechat_number.getText().toString();
                String zhifubao = et_zhifuba_number.getText().toString();
                String bankname = et_bank_name.getText().toString();
                String banknumber = et_bank_number.getText().toString();
                String bankusername = et_bank_user_name.getText().toString();
                String bankaddrname = et_bank_addr_name.getText().toString();
                if (TextUtils.isEmpty(bankname)
                &&TextUtils.isEmpty(banknumber)&&TextUtils.isEmpty(bankusername)&&TextUtils.isEmpty(bankaddrname)
                &&TextUtils.isEmpty(wechat)&&TextUtils.isEmpty(zhifubao)) {
                    ToastUtils.toastutils("银行卡，微信，支付三者必须填一个",PayInfoActivity.this);
                    return;
                }
                if (!TextUtils.isEmpty(wechat)) {
                    // 微信
                    commit();
                } else if (!TextUtils.isEmpty(zhifubao)) {
                    // 支付宝
                    commit();
                } else {
                    // 银行卡
                    if (TextUtils.isEmpty(bankname)) {
                        ToastUtils.toastutils("请输入银行名称",PayInfoActivity.this);
                        return;
                    }
                    if (TextUtils.isEmpty(banknumber)) {
                        ToastUtils.toastutils("请输入银行账号",PayInfoActivity.this);
                        return;
                    }
                    if (TextUtils.isEmpty(bankusername)) {
                        ToastUtils.toastutils("请输入开户名",PayInfoActivity.this);
                        return;
                    }
                    if (TextUtils.isEmpty(bankaddrname)) {
                        ToastUtils.toastutils("请输入开户支行名称",PayInfoActivity.this);
                        return;
                    }
                    commit();
                }
                break;
            default:
                break;
        }
    }

    private void commit() {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.cgPayMes);
        requestParams.addParameter("USER_NAME", SPUtils.getString(SPUtils.username));
        requestParams.addParameter("BANK_NAME", et_bank_name.getText().toString());
        requestParams.addParameter("BANK_NO", et_bank_number.getText().toString());
        requestParams.addParameter("BANK_USERNAME",et_bank_user_name.getText().toString());
        requestParams.addParameter("BANK_ADDR", et_bank_addr_name.getText().toString());
        requestParams.addParameter("WCHAT", et_wechat_number.getText().toString());
        requestParams.addParameter("ALIPAY", et_zhifuba_number.getText().toString());
        MXUtils.httpPost(requestParams,new CommonCallbackImp("TOOL - 修改支付信息",requestParams,this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    JSONObject pd_obj = JSONObject.parseObject(pd);
                    et_bank_name.setText(pd_obj.getString("BANK_NAME"));
                    et_bank_user_name.setText(pd_obj.getString("BANK_USERNAME"));
                    et_bank_number.setText(pd_obj.getString("BANK_NO"));
                    et_bank_addr_name.setText(pd_obj.getString("BANK_ADDR"));
                    et_wechat_number.setText(pd_obj.getString("WCHAT"));
                    et_zhifuba_number.setText(pd_obj.getString("ALIPAY"));
                    ToastUtils.toastutils("提交成功",PayInfoActivity.this);
                    finish();
                } else {
                    ToastUtils.toastutils(message,PayInfoActivity.this);
                }

            }
        });
    }

    private void payMes() {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.payMes);
        requestParams.addParameter("USER_NAME", SPUtils.getString(SPUtils.username));
        MXUtils.httpPost(requestParams,new CommonCallbackImp("TOOL - 支付信息",requestParams,this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    JSONObject pd_obj = JSONObject.parseObject(pd);
                    et_bank_name.setText(pd_obj.getString("BANK_NAME"));
                    et_bank_user_name.setText(pd_obj.getString("BANK_USERNAME"));
                    et_bank_number.setText(pd_obj.getString("BANK_NO"));
                    et_bank_addr_name.setText(pd_obj.getString("BANK_ADDR"));
                    et_wechat_number.setText(pd_obj.getString("WCHAT"));
                    et_zhifuba_number.setText(pd_obj.getString("ALIPAY"));
                } else {
                    ToastUtils.toastutils(message,PayInfoActivity.this);
                }

            }
        });
    }
}
