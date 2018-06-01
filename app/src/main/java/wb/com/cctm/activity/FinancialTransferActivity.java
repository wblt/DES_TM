package wb.com.cctm.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.lmj.mypwdinputlibrary.InputPwdView;
import com.lmj.mypwdinputlibrary.MyInputPwdUtil;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.ui.ImageGridActivity;

import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wb.com.cctm.R;
import wb.com.cctm.adapter.BBPageAdapter;
import wb.com.cctm.base.BaseActivity;
import wb.com.cctm.commons.utils.ImageLoader;
import wb.com.cctm.commons.utils.SPUtils;
import wb.com.cctm.commons.utils.ToastUtils;
import wb.com.cctm.commons.widget.ActionSheet;
import wb.com.cctm.net.CommonCallbackImp;
import wb.com.cctm.net.FlowAPI;
import wb.com.cctm.net.MXUtils;

public class FinancialTransferActivity extends BaseActivity {

    @BindView(R.id.tv_d_curr)
    TextView tv_d_curr;
    @BindView(R.id.tv_qk_money)
    TextView tv_qk_money;
    @BindView(R.id.tv_w_energy)
    TextView tv_w_energy;
    @BindView(R.id.et_number)
    EditText et_number;
    @BindView(R.id.et_wallet_address)
    EditText et_wallet_address;
    @BindView(R.id.btn_commit)
    Button btn_commit;
    private MyInputPwdUtil myInputPwdUtil;
    @BindView(R.id.ll_send_type)
    LinearLayout ll_send_type;
    @BindView(R.id.tv_send_type)
    TextView tv_send_type;
    private Dialog dialog;
    private String send_type = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(this,R.layout.activity_financial_transfer);
        appendTopBody(R.layout.activity_top_text);
        setTopLeftDefultListener();
        setTopBarTitle("财务转账");
        ButterKnife.bind(this);
        initview();
        sendMes();
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
                ToastUtils.toastutils("忘记密码",FinancialTransferActivity.this);
            }

            @Override
            public void finishPwd(String pwd) {
                myInputPwdUtil.hide();
                send(pwd);
            }
        });
        String address = getIntent().getStringExtra("address");
        if (address != null &&!TextUtils.isEmpty(address)) {
            et_wallet_address.setText(address);
        }
        dialog = ActionSheet.showSheet(this,R.layout.actionsheet_send_type);
        TextView cancel = dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();

            }
        });
        TextView take_charge = dialog.findViewById(R.id.take_charge);
        take_charge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
                send_type = "0";
                tv_send_type.setText("发送零钱");
            }
        });
        TextView choose_qk = dialog.findViewById(R.id.choose_qk);
        choose_qk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
                send_type = "1";
                tv_send_type.setText("发送区块DEC");
            }
        });
    }

    @OnClick({R.id.btn_commit,R.id.ll_send_type})
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_commit:
                if (TextUtils.isEmpty(et_wallet_address.getText().toString())) {
                    ToastUtils.toastutils("请输入转入的钱包地址",FinancialTransferActivity.this);
                    return;
                }
                if (TextUtils.isEmpty(et_number.getText().toString())) {
                    ToastUtils.toastutils("请输入转入的数量",FinancialTransferActivity.this);
                    return;
                }
                if (et_wallet_address.getText().toString().length() != 64) {
                    ToastUtils.toastutils("钱包地址长度不正确",FinancialTransferActivity.this);
                    return;
                }
                if (SPUtils.getString(SPUtils.safety).equals("0")) {
                    Intent intent = new Intent(FinancialTransferActivity.this,SafetyPwdActivity.class);
                    startActivity(intent);
                } else {
                    myInputPwdUtil.show();
                }
                break;
            case R.id.ll_send_type:
                dialog.show();
                break;
            default:
                break;
        }
    }

    private void sendMes() {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.sendMes);
        requestParams.addParameter("USER_NAME", SPUtils.getString(SPUtils.username));
        MXUtils.httpPost(requestParams,new CommonCallbackImp("MY - 可发送内容",requestParams,this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    JSONObject pd_obj = JSONObject.parseObject(pd);
                    tv_qk_money.setText(pd_obj.getString("QK_CURRENCY"));
                    tv_w_energy.setText(pd_obj.getString("W_ENERGY"));
                    tv_d_curr.setText(pd_obj.getString("D_CURRENCY"));
                } else {
                    ToastUtils.toastutils(message,FinancialTransferActivity.this);
                }

            }
        });
    }

    private void send(String pwd) {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.send);
        requestParams.addParameter("USER_NAME", SPUtils.getString(SPUtils.username));
        requestParams.addParameter("W_ADDRESS", et_wallet_address.getText().toString());
        requestParams.addParameter("S_MONEY", et_number.getText().toString());
        requestParams.addParameter("CURRENCY_TYPE", send_type);
        requestParams.addParameter("PASSW", pwd);
        MXUtils.httpPost(requestParams,new CommonCallbackImp("MY - 发送",requestParams,this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    JSONObject pd_obj = JSONObject.parseObject(pd);
                    ToastUtils.toastutils("转账成功",FinancialTransferActivity.this);
                    finish();
                } else {
                    ToastUtils.toastutils(message,FinancialTransferActivity.this);
                }
            }
        });
    }
}
