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
import wb.com.cctm.commons.utils.RegExpValidator;
import wb.com.cctm.commons.utils.SPUtils;
import wb.com.cctm.commons.utils.StringUtil;
import wb.com.cctm.commons.utils.ToastUtils;
import wb.com.cctm.net.CommonCallbackImp;
import wb.com.cctm.net.FlowAPI;
import wb.com.cctm.net.MXUtils;

public class ForgotPassword2Activity extends BaseActivity {

    @BindView(R.id.et_password)
    EditText et_password;
    @BindView(R.id.et_re_password)
    EditText et_re_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(this,R.layout.activity_forgot_password2);
        appendTopBody(R.layout.activity_top_icon);
        setTopLeftDefultListener();
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_commit})
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_commit:
                commit();
                break;
        }
    }

    private void commit() {
        final String phone = getIntent().getStringExtra("phone");;
        final String username = getIntent().getStringExtra("username");;
        String code = getIntent().getStringExtra("code");
        final String password = et_password.getText().toString();
        String re_password = et_re_password.getText().toString();
        if (TextUtils.isEmpty(password)) {
            ToastUtils.toastutils("密码输入为空",ForgotPassword2Activity.this);
            return;
        }
        if (!StringUtil.checkpwd(password)) {
            ToastUtils.toastutils("请输入6-15位字母、数字的密码",ForgotPassword2Activity.this);
            return;
        }
        if (!re_password.equals(password)) {
            ToastUtils.toastutils("两次密码输入不一致",ForgotPassword2Activity.this);
            return;
        }
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.forgotpwd);
        requestParams.addParameter("USER_NAME", username);
        requestParams.addParameter("SJYZM", code);
        requestParams.addParameter("PASSWORD", password);
        MXUtils.httpPost(requestParams,new CommonCallbackImp("USER - 忘记密码",requestParams,this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    SPUtils.putString(SPUtils.phone,phone);
                    SPUtils.putString(SPUtils.username,username);
                    SPUtils.putString(SPUtils.password,password);
                    ToastUtils.toastutils("修改成功",ForgotPassword2Activity.this);
                    finish();
                } else {
                    ToastUtils.toastutils(message,ForgotPassword2Activity.this);
                }
            }
        });

    }

}
