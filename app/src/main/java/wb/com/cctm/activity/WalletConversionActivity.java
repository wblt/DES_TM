package wb.com.cctm.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.lmj.mypwdinputlibrary.InputPwdView;
import com.lmj.mypwdinputlibrary.MyInputPwdUtil;

import org.xutils.http.RequestParams;

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

public class WalletConversionActivity extends BaseActivity {

    @BindView(R.id.tv_w_energy)
    TextView tv_w_energy;
    @BindView(R.id.tv_t_money)
    TextView tv_t_money;
    @BindView(R.id.tv_d_curr)
    TextView tv_d_curr;
    @BindView(R.id.tv_w_bl)
    TextView tv_w_bl;
    @BindView(R.id.et_number)
    EditText et_number;
    private MyInputPwdUtil myInputPwdUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(this,R.layout.activity_wallet_conversion);
        appendTopBody(R.layout.activity_top_text);
        setTopLeftDefultListener();
        setTopBarTitle("能量兑换");
        ButterKnife.bind(this);
        initview();
        cgEnergyMes();
    }

    private void initview() {
        myInputPwdUtil = new MyInputPwdUtil(this);
        myInputPwdUtil.getMyInputDialogBuilder().setAnimStyle(R.style.dialog_anim);
        myInputPwdUtil.setListener(new InputPwdView.InputPwdListener() {
            @Override
            public void hide() {
                myInputPwdUtil.hide();
            }
            @Override
            public void forgetPwd() {
                ToastUtils.toastutils("忘记密码",WalletConversionActivity.this);
            }

            @Override
            public void finishPwd(String pwd) {
                myInputPwdUtil.hide();
                changeEnergy(pwd);
            }
        });
    }

    @OnClick({R.id.btn_commit})
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_commit:
                if (TextUtils.isEmpty(et_number.getText().toString())) {
                    ToastUtils.toastutils("请输入兑换数量",WalletConversionActivity.this);
                    return;
                }
                if (SPUtils.getString(SPUtils.safety).equals("0")) {
                    Intent intent = new Intent(WalletConversionActivity.this,SafetyPwdActivity.class);
                    startActivity(intent);
                } else {
                    myInputPwdUtil.show();
                }
                break;
            default:
                break;
        }
    }

    private void cgEnergyMes() {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.cgEnergyMes);
        requestParams.addParameter("USER_NAME", SPUtils.getString(SPUtils.username));
        MXUtils.httpPost(requestParams,new CommonCallbackImp("MY - 兑换能量页面",requestParams,this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    JSONObject pd_obj = JSONObject.parseObject(pd);
                    tv_t_money.setText(pd_obj.getString("T_MONEY"));
                    tv_w_energy.setText(pd_obj.getString("W_ENERGY"));
                    tv_d_curr.setText(pd_obj.getString("D_CURRENCY"));
                    tv_w_bl.setText(pd_obj.getString("W_BL"));
                    SPUtils.putString(SPUtils.w_energy,pd_obj.getString("W_ENERGY"));
                } else {
                    ToastUtils.toastutils(message,WalletConversionActivity.this);
                }

            }
        });
    }

    private void changeEnergy(String pwd) {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.changeEnergy);
        requestParams.addParameter("USER_NAME", SPUtils.getString(SPUtils.username));
        requestParams.addParameter("PASSW", pwd);
        requestParams.addParameter("S_NUM", et_number.getText().toString());
        MXUtils.httpPost(requestParams,new CommonCallbackImp("MY - 兑换能量",requestParams,this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    JSONObject pd_obj = JSONObject.parseObject(pd);
                    ToastUtils.toastutils("兑换成功",WalletConversionActivity.this);
                    cgEnergyMes();
                } else {
                    ToastUtils.toastutils(message,WalletConversionActivity.this);
                }

            }
        });
    }


}
