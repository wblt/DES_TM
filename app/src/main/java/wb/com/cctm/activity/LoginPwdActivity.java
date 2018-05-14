package wb.com.cctm.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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

public class LoginPwdActivity extends BaseActivity {

    @BindView(R.id.et_username)
    EditText et_username;
    @BindView(R.id.et_old_password)
    EditText et_old_password;
    @BindView(R.id.et_new_password)
    EditText et_new_password;
    @BindView(R.id.btn_commit)
    Button btn_commit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(this,R.layout.activity_login_pwd);
        appendTopBody(R.layout.activity_top_text);
        setTopBarTitle("登录密码");
        setTopLeftDefultListener();
        ButterKnife.bind(this);
        initview();
    }

    private void initview() {

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
        if (TextUtils.isEmpty(et_username.getText().toString())) {
            ToastUtils.toastutils("用户名输入为空",LoginPwdActivity.this);
            return;
        }
        if (TextUtils.isEmpty(et_old_password.getText().toString())) {
            ToastUtils.toastutils("请输入当前登录密码",LoginPwdActivity.this);
            return;
        }
        if (TextUtils.isEmpty(et_new_password.getText().toString())) {
            ToastUtils.toastutils("请输入新的登录密码",LoginPwdActivity.this);
            return;
        }
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.changePassw);
        requestParams.addParameter("USER_NAME", et_username.getText().toString());
        requestParams.addParameter("OLD_PAS", et_old_password.getText().toString());
        requestParams.addParameter("NEW_PAS", et_new_password.getText().toString());
        MXUtils.httpPost(requestParams,new CommonCallbackImp("TOOL - 修改登录密码",requestParams,this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    ToastUtils.toastutils("修改成功",LoginPwdActivity.this);
                    SPUtils.putString(SPUtils.password,et_new_password.getText().toString());
                    finish();
                } else {
                    ToastUtils.toastutils(message,LoginPwdActivity.this);
                }

            }
        });

    }

}
