package wb.com.cctm.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import org.xutils.http.RequestParams;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wb.com.cctm.App;
import wb.com.cctm.R;
import wb.com.cctm.base.BaseActivity;
import wb.com.cctm.commons.utils.Code;
import wb.com.cctm.commons.utils.SPUtils;
import wb.com.cctm.commons.utils.ToastUtils;
import wb.com.cctm.net.CommonCallbackImp;
import wb.com.cctm.net.FlowAPI;
import wb.com.cctm.net.MXUtils;

import static wb.com.cctm.R.id.et_userphone;

public class LoginActivity extends BaseActivity {
    @BindView(R.id.top_left)
    ImageButton top_left;
    @BindView(R.id.et_username)
    EditText et_username;
    @BindView(R.id.et_password)
    EditText et_password;
    @BindView(R.id.btn_login)
    Button btn_login;
    @BindView(R.id.ll_remember_password)
    LinearLayout ll_remember_password;
    @BindView(R.id.ll_auto_login)
    LinearLayout ll_auto_login;
    @BindView(R.id.tv_register)
    TextView tv_register;
    @BindView(R.id.tv_forgot_password)
    TextView tv_forgot_password;
    @BindView(R.id.iv_showCode)
    ImageView iv_showCode;
    @BindView(R.id.et_code)
    EditText et_code;
    private String realCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(this,R.layout.activity_login);
        appendTopBody(R.layout.activity_top_text);
        setTopBarTitle("登录");
        ButterKnife.bind(this);
        initview();
    }

    private void initview() {
        top_left.setVisibility(View.INVISIBLE);
        //将验证码用图片的形式显示出来
        iv_showCode.setImageBitmap(Code.getInstance().createBitmap("#111F3F"));
        realCode = Code.getInstance().getCode().toLowerCase();
        et_username.setText(SPUtils.getString(SPUtils.username));
        et_password.setText(SPUtils.getString(SPUtils.password));
    }

    @OnClick({R.id.btn_login,R.id.tv_register,R.id.tv_forgot_password,R.id.iv_showCode})
    void viewOnclick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.tv_register:
                intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_forgot_password:
                intent = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_showCode:
                iv_showCode.setImageBitmap(Code.getInstance().createBitmap("#111F3F"));
                realCode = Code.getInstance().getCode().toLowerCase();
                break;
            default:
                break;
        }
    }

    private void login() {
        final String username = et_username.getText().toString();
        final String password = et_password.getText().toString();
        String code = et_code.getText().toString();
        if (TextUtils.isEmpty(username)) {
            ToastUtils.toastutils("用户名为空",this);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtils.toastutils("密码为空",this);
            return;
        }
        if (TextUtils.isEmpty(code)) {
            ToastUtils.toastutils("验证码为空",this);
            return;
        }
        if (!realCode.equals(code)) {
            ToastUtils.toastutils("验证码输入错误",this);
            iv_showCode.setImageBitmap(Code.getInstance().createBitmap("#111F3F"));
            realCode = Code.getInstance().getCode().toLowerCase();
            return;
        }
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.login);
        requestParams.addParameter("USER_NAME", username);
        requestParams.addParameter("PASSWORD", password);
        MXUtils.httpPost(requestParams,new CommonCallbackImp("USER - 登录",requestParams,this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    SPUtils.putString(SPUtils.username,username);
                    SPUtils.putString(SPUtils.password,password);
                    SPUtils.putString(SPUtils.isLogin,"1");
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    ToastUtils.toastutils(message,LoginActivity.this);
                }
            }
        });

    }
}
