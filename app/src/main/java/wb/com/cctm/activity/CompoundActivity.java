package wb.com.cctm.activity;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

public class CompoundActivity extends BaseActivity {

    @BindView(R.id.ll_fuli)
    LinearLayout ll_fuli;
    @BindView(R.id.ll_stop_fuli)
    LinearLayout ll_stop_fuli;
    @BindView(R.id.img_fuli_gou)
    ImageView img_fuli_gou;
    @BindView(R.id.img_stop_gou)
    ImageView img_stop_gou;
    private String flag = "0";
    private String lastflag = "0";
    @BindView(R.id.btn_commit)
    Button btn_commit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(this,R.layout.activity_compound);
        appendTopBody(R.layout.activity_top_text);
        setTopLeftDefultListener();
        setTopBarTitle("复利");
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        ifFl();
    }

    @OnClick({R.id.ll_fuli,R.id.ll_stop_fuli,R.id.btn_commit})
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.ll_fuli:
                flag = "1";
                updatastatus();
                break;
            case R.id.ll_stop_fuli:
                flag = "0";
                updatastatus();
                break;
            case R.id.btn_commit:
                showLoadding("请稍候...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoadding();
                        lastflag = flag;
                        btn_commit.setBackgroundResource(0);
                        btn_commit.setBackgroundResource(R.drawable.buttom_bg_gray);
                        cgFl();
                    }
                },2000);
                break;
            default:
                break;
        }
    }

    private void updatastatus() {
        if (flag.equals("1")) {
            img_fuli_gou.setVisibility(View.VISIBLE);
            img_stop_gou.setVisibility(View.INVISIBLE);
        } else {
            img_fuli_gou.setVisibility(View.INVISIBLE);
            img_stop_gou.setVisibility(View.VISIBLE);
        }
        if (lastflag.equals(flag)) {
            btn_commit.setBackgroundResource(0);
            btn_commit.setBackgroundResource(R.drawable.buttom_bg_gray);
        } else {
            btn_commit.setBackgroundResource(0);
            btn_commit.setBackgroundResource(R.drawable.buttom_bg_gray);
        }
    }


    private void ifFl() {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.ifFl);
        requestParams.addParameter("USER_NAME", SPUtils.getString(SPUtils.username));
        MXUtils.httpPost(requestParams,new CommonCallbackImp("MY - 复利状态",requestParams,this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    JSONObject pd_obj = JSONObject.parseObject(pd);
                    if (pd_obj.getString("IFFL").equals("0")) {
                        flag = "0";
                    } else {
                        flag = "1";
                    }
                    updatastatus();
                } else {
                    ToastUtils.toastutils(message,CompoundActivity.this);
                }

            }
        });
    }

    private void cgFl() {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.cgFl);
        requestParams.addParameter("USER_NAME", SPUtils.getString(SPUtils.username));
        requestParams.addParameter("IFFL", flag);
        MXUtils.httpPost(requestParams,new CommonCallbackImp("USER - 注册短信验证码",requestParams,this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    JSONObject pd_obj = JSONObject.parseObject(pd);
                    if (pd_obj.getString("IFFL").equals("0")) {
                        flag = "0";
                    } else {
                        flag = "1";
                    }
                    ToastUtils.toastutils("修改成功",CompoundActivity.this);
                    updatastatus();
                } else {
                    ToastUtils.toastutils(message,CompoundActivity.this);
                }

            }
        });
    }




}
