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

public class SafetyPwdActivity extends BaseActivity {

    @BindView(R.id.et_username)
    EditText et_username;
    @BindView(R.id.et_password)
    EditText et_password;
    @BindView(R.id.et_code)
    EditText et_code;
    @BindView(R.id.btn_code)
    Button btn_code;
    @BindView(R.id.et_re_password)
    EditText et_re_password;
    private String codeNum;     // 验证码
    private MyCount myCount;
    @BindView(R.id.et_phone)
    EditText et_phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(this,R.layout.activity_safety_pwd);
        appendTopBody(R.layout.activity_top_text);
        setTopBarTitle("安全密码");
        setTopLeftDefultListener();
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
            ToastUtils.toastutils("用户名输入为空",SafetyPwdActivity.this);
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.toastutils("电话号码输入为空",SafetyPwdActivity.this);
            return;
        }
        if (!RegExpValidator.IsHandset(phone)) {
            ToastUtils.toastutils("电话号码格式错误",SafetyPwdActivity.this);
            return;
        }
        try {
            String md5_str = MD5.MD5Encode(phone+"shc");
            RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.sysendMessAQ);
            requestParams.addParameter("USER_NAME",username);
            requestParams.addParameter("ACCOUNT",phone);
            requestParams.addParameter("digestStr", md5_str);
            MXUtils.httpPost(requestParams,new CommonCallbackImp("USER - 安全密码短信验证码",requestParams,this){
                @Override
                public void onSuccess(String data) {
                    super.onSuccess(data);
                    JSONObject jsonObject = JSONObject.parseObject(data);
                    String result = jsonObject.getString("code");
                    String message = jsonObject.getString("message");
                    if (result.equals(FlowAPI.SUCCEED)) {
                        ToastUtils.toastutils("验证码发送成功",SafetyPwdActivity.this);
                        btn_code.setEnabled(false);
                        myCount.start();
                    } else {
                        ToastUtils.toastutils(message,SafetyPwdActivity.this);
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
        String password = et_password.getText().toString();
        String re_password = et_re_password.getText().toString();
        if (TextUtils.isEmpty(username)) {
            ToastUtils.toastutils("用户名输入为空",SafetyPwdActivity.this);
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.toastutils("电话号码输入为空",SafetyPwdActivity.this);
            return;
        }
        if (!RegExpValidator.IsHandset(phone)) {
            ToastUtils.toastutils("电话号码格式错误",SafetyPwdActivity.this);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtils.toastutils("密码输入为空",SafetyPwdActivity.this);
            return;
        }
        if (!re_password.equals(password)) {
            ToastUtils.toastutils("两次密码输入不一致",SafetyPwdActivity.this);
            return;
        }
        if (password.length() != 6) {
            ToastUtils.toastutils("请输入6位数字密码",SafetyPwdActivity.this);
            return;
        }
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.aqPassw);
        requestParams.addParameter("USER_NAME", username);
        requestParams.addParameter("SJYZM", code);
        requestParams.addParameter("NEW_PAS", password);
        MXUtils.httpPost(requestParams,new CommonCallbackImp("TOOL - 修改安全密码",requestParams,this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    SPUtils.putString(SPUtils.phone,phone);
                    ToastUtils.toastutils("修改成功",SafetyPwdActivity.this);
                    SPUtils.putString(SPUtils.safety,"1");
                    finish();
                } else {
                    ToastUtils.toastutils(message,SafetyPwdActivity.this);
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
