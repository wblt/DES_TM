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
    @BindView(R.id.et_code)
    EditText et_code;
    @BindView(R.id.btn_get_code)
    Button btn_get_code;
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
        ButterKnife.bind(this);
        initview();
    }

    private void initview() {
        myCount = new MyCount(60000, 1000);
    }

    @OnClick({R.id.btn_next,R.id.btn_get_code,R.id.tv_zhuce_xieyi})
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                next();
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
        if (TextUtils.isEmpty(username)) {
            ToastUtils.toastutils("用户名输入为空",RegisterActivity.this);
            return;
        }
        if (TextUtils.isEmpty(userphone)) {
            ToastUtils.toastutils("电话号码输入为空",RegisterActivity.this);
            return;
        }
        if (!RegExpValidator.IsHandset(userphone)) {
            ToastUtils.toastutils("电话号码格式不正确",RegisterActivity.this);
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

    private void next() {
        final String username = et_username.getText().toString();
        String userphone = et_userphone.getText().toString();
        String yaoqingcode = et_yaoqing_code.getText().toString();
        String code = et_code.getText().toString();
        if (TextUtils.isEmpty(username)) {
            ToastUtils.toastutils("用户名输入为空",RegisterActivity.this);
            return;
        }
        if (TextUtils.isEmpty(code)) {
            ToastUtils.toastutils("验证码输入为空",RegisterActivity.this);
            return;
        }
        if (TextUtils.isEmpty(userphone)) {
            ToastUtils.toastutils("电话号码输入为空",RegisterActivity.this);
            return;
        }
        if (!RegExpValidator.IsHandset(userphone)) {
            ToastUtils.toastutils("电话号码格式不正确",RegisterActivity.this);
            return;
        }
        if (TextUtils.isEmpty(yaoqingcode)) {
            ToastUtils.toastutils("邀请码输入为空",RegisterActivity.this);
            return;
        }

        // 然后进入下一步
        Intent intent = new Intent(RegisterActivity.this,Register2Activity.class);
        intent.putExtra("username",username);
        intent.putExtra("yaoqingcode",yaoqingcode);
        intent.putExtra("userphone",userphone);
        intent.putExtra("code",code);
        startActivity(intent);
    }


}
