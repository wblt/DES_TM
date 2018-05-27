package wb.com.cctm.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import org.xutils.http.RequestParams;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import wb.com.cctm.R;
import wb.com.cctm.activity.AllReleaseActivity;
import wb.com.cctm.activity.WalletRecordActivity;
import wb.com.cctm.base.BaseActivity;
import wb.com.cctm.base.BaseFragment;
import wb.com.cctm.commons.utils.SPUtils;
import wb.com.cctm.commons.utils.ToastUtils;
import wb.com.cctm.net.CommonCallbackImp;
import wb.com.cctm.net.FlowAPI;
import wb.com.cctm.net.MXUtils;


public class SuanliFragment extends BaseFragment {

    private Unbinder unbinder;
    @BindView(R.id.tv_today)
    TextView tv_today;
    @BindView(R.id.tv_big)
    TextView tv_big;
    @BindView(R.id.tv_small)
    TextView tv_small;
    @BindView(R.id.tv_smart)
    TextView tv_smart;
    @BindView(R.id.tv_jd)
    TextView tv_jd;
    @BindView(R.id.top_right_icon)
    ImageButton top_right_icon;
    @BindView(R.id.ll_all)
    LinearLayout ll_all;
    @BindView(R.id.top_left)
    ImageButton top_left;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        appendMainBody(this,R.layout.fragment_suanli);
        appendTopBody(R.layout.activity_top_icon);
        setTopBarTitle("算力");
        unbinder = ButterKnife.bind(this,view);
        initview(view);
        release();
        return view;
    }

    private void initview(View view) {
        top_left.setVisibility(View.INVISIBLE);
        ll_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),AllReleaseActivity.class);
                startActivity(intent);
            }
        });
    }

    private void release() {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.release);
        requestParams.addParameter("USER_NAME", SPUtils.getString(SPUtils.username));
        MXUtils.httpPost(requestParams,new CommonCallbackImp("INDEX - 释放记录",requestParams, (BaseActivity) getActivity()){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    JSONObject pd_obj = JSONObject.parseObject(pd);
                    tv_today.setText(pd_obj.getString("CALCULATE_MONEY"));
                    tv_big.setText(pd_obj.getString("BIG_CURRENCY"));
                    tv_small.setText(pd_obj.getString("SMALL_CURRENCY"));
                    tv_smart.setText(pd_obj.getString("STATIC_CURRENCY"));
                    tv_jd.setText(pd_obj.getString("JD_CURRENCY"));
                } else {
                    ToastUtils.toastutils(message,getActivity());
                }

            }
        });
    }
}
