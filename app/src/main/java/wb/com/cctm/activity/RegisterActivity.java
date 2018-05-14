package wb.com.cctm.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import org.xutils.http.RequestParams;

import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wb.com.cctm.App;
import wb.com.cctm.R;
import wb.com.cctm.base.BaseActivity;
import wb.com.cctm.commons.utils.MD5;
import wb.com.cctm.commons.utils.RegExpValidator;
import wb.com.cctm.commons.utils.SPUtils;
import wb.com.cctm.commons.utils.StringUtil;
import wb.com.cctm.commons.utils.ToastUtils;
import wb.com.cctm.net.CommonCallbackImp;
import wb.com.cctm.net.FlowAPI;
import wb.com.cctm.net.MXUtils;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.et_username)
    EditText et_username;
    @BindView(R.id.et_password)
    EditText et_password;
    @BindView(R.id.et_re_password)
    EditText et_re_password;
    @BindView(R.id.et_code)
    EditText et_code;
    @BindView(R.id.btn_get_code)
    Button btn_get_code;
    private String codeNum;     // 验证码
    private MyCount myCount;
    @BindView(R.id.et_userphone)
    EditText et_userphone;
    @BindView(R.id.et_yaoqing_code)
    EditText et_yaoqing_code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(this,R.layout.activity_register);
        appendTopBody(R.layout.activity_top_text);
        setTopLeftDefultListener();
        setTopBarTitle("注册");
        ButterKnife.bind(this);
        initview();
    }

    private void initview() {
        myCount = new MyCount(60000, 1000);
    }

    @OnClick({R.id.btn_register,R.id.btn_get_code,R.id.tv_zhuce_xieyi})
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                register();
                break;
            case R.id.btn_get_code:
                getcode();
                break;
            case R.id.tv_zhuce_xieyi:
                ToastUtils.toastutils("开发中",RegisterActivity.this);
                break;
            default:
                break;
        }
    }

    private void getcode() {
        final String username = et_username.getText().toString();
        String userphone = et_userphone.getText().toString();
        final String password = et_password.getText().toString();
        String re_password = et_re_password.getText().toString();
        String yaoqingcode = et_yaoqing_code.getText().toString();
        if (TextUtils.isEmpty(username)) {
            ToastUtils.toastutils("用户名输入为空",RegisterActivity.this);
            return;
        }
        if (TextUtils.isEmpty(userphone)) {
            ToastUtils.toastutils("电话号码输入为空",RegisterActivity.this);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtils.toastutils("密码输入为空",RegisterActivity.this);
            return;
        }
        if (!RegExpValidator.IsHandset(userphone)) {
            ToastUtils.toastutils("电话号码格式不正确",RegisterActivity.this);
            return;
        }
        if (!StringUtil.checkpwd(password)) {
            ToastUtils.toastutils("请输入6-15位字母、数字的密码",RegisterActivity.this);
            return;
        }
        if (!password.equals(re_password)) {
            ToastUtils.toastutils("密码输入不一致",RegisterActivity.this);
            return;
        }
        if (TextUtils.isEmpty(yaoqingcode)) {
            ToastUtils.toastutils("邀请码输入为空",RegisterActivity.this);
            return;
        }
        try {
            String md5Str = MD5.MD5Encode(userphone+"shc");
            RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.register_code);
            requestParams.addParameter("ACCOUNT", et_userphone.getText().toString());
            requestParams.addParameter("digestStr", md5Str);
            MXUtils.httpPost(requestParams,new CommonCallbackImp("USER - 注册短信验证码",requestParams,this){
                @Override
                public void onSuccess(String data) {
                    super.onSuccess(data);
                    JSONObject jsonObject = JSONObject.parseObject(data);
                    String result = jsonObject.getString("code");
                    String message = jsonObject.getString("message");
                    if (result.equals(FlowAPI.SUCCEED)) {
                        ToastUtils.toastutils("验证码发送成功",RegisterActivity.this);
                        btn_get_code.setEnabled(false);
                        myCount.start();
                    } else {
                        ToastUtils.toastutils(message,RegisterActivity.this);
                    }
                }
            });
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    private void register() {
        final String username = et_username.getText().toString();
        final String password = et_password.getText().toString();
        final String userphone = et_userphone.getText().toString();
        String re_password = et_re_password.getText().toString();
        String yaoqingcode = et_yaoqing_code.getText().toString();
        String code = et_code.getText().toString();
        if (TextUtils.isEmpty(username)) {
            ToastUtils.toastutils("用户名输入为空",RegisterActivity.this);
            return;
        }
        if (TextUtils.isEmpty(userphone)) {
            ToastUtils.toastutils("电话号码输入为空",RegisterActivity.this);
            return;
        }

        if (!RegExpValidator.IsHandset(userphone)) {
            ToastUtils.toastutils("电话号码格式错误",RegisterActivity.this);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtils.toastutils("密码输入为空",RegisterActivity.this);
            return;
        }
        if (!StringUtil.checkpwd(password)) {
            ToastUtils.toastutils("密码输入为空请输入6-15位字母、数字的密码",RegisterActivity.this);
            return;
        }
        if (!password.equals(re_password)) {
            ToastUtils.toastutils("密码输入不一致",RegisterActivity.this);
            return;
        }
        if (TextUtils.isEmpty(code)) {
            ToastUtils.toastutils("验证为空",RegisterActivity.this);
            return;
        }
        if (TextUtils.isEmpty(yaoqingcode)) {
            ToastUtils.toastutils("邀请码输入为空",RegisterActivity.this);
            return;
        }
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.register);
        requestParams.addParameter("USER_NAME", username);
        requestParams.addParameter("PASSWORD", password);
        requestParams.addParameter("ACCOUNT", userphone);
        requestParams.addParameter("SJYZM", code);
        requestParams.addParameter("YQ_CODE", yaoqingcode);
        MXUtils.httpPost(requestParams,new CommonCallbackImp("USER - 用户注册",requestParams,this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    JSONObject pd_obj = JSONObject.parseObject(pd);
                    SPUtils.putString(SPUtils.username,username);
                    SPUtils.putString(SPUtils.password,password);
                    SPUtils.putString(SPUtils.phone,userphone);
                    finish();
                } else {
                    ToastUtils.toastutils(message,RegisterActivity.this);
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
            btn_get_code.setText(second + "秒");
        }

        @Override
        public void onFinish() {
            btn_get_code.setEnabled(true);
            btn_get_code.setText("获取验证码");
        }
    }


}
