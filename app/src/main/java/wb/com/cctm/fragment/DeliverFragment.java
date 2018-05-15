package wb.com.cctm.fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.lzy.imagepicker.ImagePicker;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.xutils.http.RequestParams;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import wb.com.cctm.R;
import wb.com.cctm.activity.CompoundActivity;
import wb.com.cctm.activity.FinancialTransferActivity;
import wb.com.cctm.activity.FrendsActivity;
import wb.com.cctm.activity.InvitingFriendsActivity;
import wb.com.cctm.activity.MoveWalletActivity;
import wb.com.cctm.activity.MyorderActivity;
import wb.com.cctm.activity.NewsActivity;
import wb.com.cctm.activity.NewsDetailActivity;
import wb.com.cctm.activity.ReciveCodeActivity;
import wb.com.cctm.activity.ReciverRecordActivity;
import wb.com.cctm.activity.ShengHeActivity;
import wb.com.cctm.activity.StepRecoderActivity;
import wb.com.cctm.activity.TestActivity;
import wb.com.cctm.activity.TransferRecoderActivity;
import wb.com.cctm.activity.UserInfoActivity;
import wb.com.cctm.activity.WalletConversionActivity;
import wb.com.cctm.activity.WalletRecordActivity;
import wb.com.cctm.adapter.NewsAdapter;
import wb.com.cctm.base.BaseActivity;
import wb.com.cctm.base.BaseFragment;
import wb.com.cctm.base.OnItemClickListener;
import wb.com.cctm.bean.NoticeBean;
import wb.com.cctm.commons.step.UpdateUiCallBack;
import wb.com.cctm.commons.step.service.StepService;
import wb.com.cctm.commons.step.utils.SharedPreferencesUtils;
import wb.com.cctm.commons.step.view.StepArcView;
import wb.com.cctm.commons.utils.CommonUtils;
import wb.com.cctm.commons.utils.ImageLoader;
import wb.com.cctm.commons.utils.SPUtils;
import wb.com.cctm.commons.utils.ToastUtils;
import wb.com.cctm.commons.widget.MyGridView;
import wb.com.cctm.net.CommonCallbackImp;
import wb.com.cctm.net.FlowAPI;
import wb.com.cctm.net.MXUtils;

public class DeliverFragment extends BaseFragment {
    Unbinder unbinder;
    @BindView(R.id.top_left)
    ImageButton top_left;
    @BindView(R.id.top_right_icon)
    ImageButton top_right_icon;
    @BindView(R.id.recyc_list)
    RecyclerView recyc_list;
    private NewsAdapter newsAdapter;
    @BindView(R.id.ll_content)
    LinearLayout ll_content;
    @BindView(R.id.ll_no_data)
    LinearLayout ll_no_data;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        appendMainBody(this,R.layout.fragment_deliver);
        appendTopBody(R.layout.activity_top_icon);
        setTopBarTitle("首页");
        unbinder = ButterKnife.bind(this,view);
        initview(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        notice();
    }

    private void initview(View view) {
        top_left.setVisibility(View.INVISIBLE);
        top_right_icon.setImageResource(R.mipmap.scan);
        newsAdapter = new NewsAdapter();
        newsAdapter.setOnItemClickListener(new OnItemClickListener<NoticeBean>() {
            @Override
            public void onClick(NoticeBean noticeBean, View view, int position) {
                Intent intent = new Intent(getActivity(),NewsDetailActivity.class);
                intent.putExtra("title",noticeBean.getTITLE());
                intent.putExtra("content",noticeBean.getCONTENT());
                intent.putExtra("time",noticeBean.getCREATE_TIME());
                startActivity(intent);
            }
        });
        recyc_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyc_list.setAdapter(newsAdapter);
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    private void notice() {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.notice);
        MXUtils.httpPost(requestParams,new CommonCallbackImp("USER - 注册短信验证码",requestParams, (BaseActivity) getActivity()){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    List<NoticeBean> beanList = JSONArray.parseArray(pd,NoticeBean.class);
                    newsAdapter.clear();
                    newsAdapter.addAll(beanList);
                    newsAdapter.notifyDataSetChanged();
                    if (newsAdapter.getData().size()>0) {
                        ll_content.setVisibility(View.VISIBLE);
                        ll_no_data.setVisibility(View.GONE);
                    } else {
                        ll_content.setVisibility(View.GONE);
                        ll_no_data.setVisibility(View.VISIBLE);
                    }
                } else {
                    ToastUtils.toastutils(message,getActivity());
                }

            }
        });
    }

}
