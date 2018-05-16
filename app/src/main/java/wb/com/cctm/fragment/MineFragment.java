package wb.com.cctm.fragment;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import wb.com.cctm.R;
import wb.com.cctm.activity.CompoundActivity;
import wb.com.cctm.activity.FinancialTransferActivity;
import wb.com.cctm.activity.InvitingFriendsActivity;
import wb.com.cctm.activity.MyorderActivity;
import wb.com.cctm.activity.ReciveCodeActivity;
import wb.com.cctm.activity.ReciverRecordActivity;
import wb.com.cctm.activity.SettingActivity;
import wb.com.cctm.activity.StepRecoderActivity;
import wb.com.cctm.activity.TransferRecoderActivity;
import wb.com.cctm.activity.UserInfoActivity;
import wb.com.cctm.activity.WalletConversionActivity;
import wb.com.cctm.base.BaseActivity;
import wb.com.cctm.base.BaseFragment;
import wb.com.cctm.commons.utils.ImageLoader;
import wb.com.cctm.commons.utils.SPUtils;
import wb.com.cctm.commons.utils.ToastUtils;
import wb.com.cctm.commons.zxing.android.CaptureActivity;
import wb.com.cctm.commons.zxing.bean.ZxingConfig;
import wb.com.cctm.commons.zxing.common.Constant;
import wb.com.cctm.net.CommonCallbackImp;
import wb.com.cctm.net.FlowAPI;
import wb.com.cctm.net.MXUtils;

import static android.app.Activity.RESULT_OK;

public class MineFragment extends BaseFragment {

    @BindView(R.id.top_right_icon)
    ImageButton top_right_icon;
    @BindView(R.id.top_left)
    ImageButton top_left;
    private Unbinder unbinder;
    @BindView(R.id.iv_head_img)
    ImageView iv_head_img;
    @BindView(R.id.tv_username)
    TextView tv_username;
    @BindView(R.id.tv_S_CURRENCY)
    TextView tv_S_CURRENCY;
    @BindView(R.id.tv_QK_CURRENCY)
    TextView tv_QK_CURRENCY;
    @BindView(R.id.tv_D_CURRENCY)
    TextView tv_D_CURRENCY;
    @BindView(R.id.tv_W_ENERGY)
    TextView tv_W_ENERGY;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        appendMainBody(this,R.layout.fragment_mine);
        appendTopBody(R.layout.activity_top_icon);
        unbinder = ButterKnife.bind(this,view);
        initview(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        homepage();
    }

    private void initview(View view) {
        top_left.setVisibility(View.INVISIBLE);
    }

    @OnClick({R.id.ll_user_info,R.id.ll_address,R.id.ll_setting})
    void viewClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.ll_user_info:
                intent = new Intent(getActivity(),UserInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_address:
                intent = new Intent(getActivity(),ReciveCodeActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_setting:
                intent = new Intent(getActivity(),SettingActivity.class);
                startActivity(intent);
                break;
        }
    }
    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    private void homepage() {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.homePage);
        requestParams.addParameter("USER_NAME", SPUtils.getString(SPUtils.username));
        MXUtils.httpPost(requestParams,new CommonCallbackImp("INDEX - 首页",requestParams, (BaseActivity) getActivity()){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    JSONObject pd_obj = JSONObject.parseObject(pd);
                    SPUtils.putString(SPUtils.wallet_address,pd_obj.getString("W_ADDRESS"));
                    SPUtils.putString(SPUtils.headimgpath,pd_obj.getString("HEAD_URL"));
                    SPUtils.putString(SPUtils.nick_name,pd_obj.getString("NICK_NAME"));
                    SPUtils.putString(SPUtils.safety,pd_obj.getString("IFPAS"));
                    SPUtils.putString(SPUtils.w_energy,pd_obj.getString("W_ENERGY"));
                    // 设置数据
                    ImageLoader.loadCircle(SPUtils.getString(SPUtils.headimgpath),iv_head_img);
                    tv_username.setText(SPUtils.getString(SPUtils.nick_name));
                    tv_D_CURRENCY.setText(pd_obj.getString("D_CURRENCY"));
                    tv_QK_CURRENCY.setText(pd_obj.getString("QK_CURRENCY"));
                    tv_S_CURRENCY.setText(pd_obj.getString("S_CURRENCY"));
                    tv_W_ENERGY.setText(pd_obj.getString("W_ENERGY"));
                } else {
                    ToastUtils.toastutils(message,getActivity());
                }
            }
        });
    }

}
