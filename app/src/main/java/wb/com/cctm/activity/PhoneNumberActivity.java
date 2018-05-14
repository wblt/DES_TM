package wb.com.cctm.activity;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;

import org.xutils.http.RequestParams;

import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wb.com.cctm.R;
import wb.com.cctm.base.BaseActivity;
import wb.com.cctm.commons.utils.MD5;
import wb.com.cctm.commons.utils.RegExpValidator;
import wb.com.cctm.commons.utils.SPUtils;
import wb.com.cctm.commons.utils.ToastUtils;
import wb.com.cctm.net.CommonCallbackImp;
import wb.com.cctm.net.FlowAPI;
import wb.com.cctm.net.MXUtils;

public class PhoneNumberActivity extends BaseActivity {


    @BindView(R.id.et_username)
    EditText et_username;
    @BindView(R.id.et_phone)
    EditText et_phone;
    @BindView(R.id.et_code)
    EditText et_code;
    @BindView(R.id.btn_code)
    Button btn_code;
    private String codeNum;     // 验证码
    private MyCount myCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(this,R.layout.activity_phone_number);
        appendTopBody(R.layout.activity_top_text);
        setTopLeftDefultListener();
        setTopBarTitle("手机号码");
        ButterKnife.bind(this);
        initview();
    }

    private void initview() {
        myCount = new MyCount(60000, 1000);
    }

    @OnClick({R.id.btn_code,R.id.btn_commit})
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_code:
                getcode();
                break;
            case R.id.btn_commit:
                commit();
                break;
            default:
                break;

        }
    }

    private void getcode() {
        String phone = et_phone.getText().toString();
        String username = et_username.getText().toString();
        if(TextUtils.isEmpty(username)) {
            ToastUtils.toastutils("用户名输入为空",PhoneNumberActivity.this);
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.toastutils("电话号码输入为空",PhoneNumberActivity.this);
            return;
        }
        if (!RegExpValidator.IsHandset(phone)) {
            ToastUtils.toastutils("电话号码格式错误",PhoneNumberActivity.this);
            return;
        }
        try {
            String md5_str = MD5.MD5Encode(phone+"shc");
            RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.register_code);
            requestParams.addParameter("ACCOUNT",phone);
            requestParams.addParameter("digestStr", md5_str);
            MXUtils.httpPost(requestParams,new CommonCallbackImp("USER - 注册短信验证码",requestParams,this){
                @Override
                public void onSuccess(String data) {
                    super.onSuccess(data);
                    JSONObject jsonObject = JSONObject.parseObject(data);
                    String result = jsonObject.getString("code");
                    String message = jsonObject.getString("message");
                    if (result.equals(FlowAPI.SUCCEED)) {
                        ToastUtils.toastutils("验证码发送成功",PhoneNumberActivity.this);
                        btn_code.setEnabled(false);
                        myCount.start();
                    } else {
                        ToastUtils.toastutils(message,PhoneNumberActivity.this);
                    }
                }
            });
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


    }

    private void commit() {
        final String phone = et_phone.getText().toString();
        final String username = et_username.getText().toString();
        String code = et_code.getText().toString();
        if (TextUtils.isEmpty(username)) {
            ToastUtils.toastutils("用户名输入为空",PhoneNumberActivity.this);
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.toastutils("电话号码输入为空",PhoneNumberActivity.this);
            return;
        }
        if (!RegExpValidator.IsHandset(phone)) {
            ToastUtils.toastutils("电话号码格式错误",PhoneNumberActivity.this);
            return;
        }
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.phoneCg);
        requestParams.addParameter("USER_NAME", username);
        requestParams.addParameter("TEL", phone);
        requestParams.addParameter("SJYZM", code);
        MXUtils.httpPost(requestParams,new CommonCallbackImp("TOOL - 修改手机号码",requestParams,this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    SPUtils.putString(SPUtils.phone,phone);
                    ToastUtils.toastutils("修改成功",PhoneNumberActivity.this);
                    finish();
                } else {
                    ToastUtils.toastutils(message,PhoneNumberActivity.this);
                }
            }
        });

    }

    public class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval){
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            int second = (int) (l / 1000);
            btn_code.setText(second + "秒");
        }

        @Override
        public void onFinish() {
            btn_code.setEnabled(true);
            btn_code.setText("获取验证码");
        }
    }

}
