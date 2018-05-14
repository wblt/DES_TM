package wb.com.cctm.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import wb.com.cctm.R;
import wb.com.cctm.activity.OrderDetailActivity;
import wb.com.cctm.adapter.MybuyAdapter;
import wb.com.cctm.adapter.MychecAdapter;
import wb.com.cctm.base.BaseActivity;
import wb.com.cctm.base.BaseFragment;
import wb.com.cctm.base.OnItemClickListener;
import wb.com.cctm.bean.MybuyBean;
import wb.com.cctm.commons.utils.SPUtils;
import wb.com.cctm.commons.utils.ToastUtils;
import wb.com.cctm.net.CommonCallbackImp;
import wb.com.cctm.net.FlowAPI;
import wb.com.cctm.net.MXUtils;


public class MybuyFragment extends BaseFragment {

    private Unbinder unbinder;
    @BindView(R.id.recyc_list)
    RecyclerView recyc_list;
    private String queryid = "0";
    private MybuyAdapter adpter;
    @BindView(R.id.ll_no_data)
    LinearLayout ll_no_data;
    @BindView(R.id.sm_refreshLayout)
    SmartRefreshLayout sm_refreshLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mybuy,container,false);
        unbinder = ButterKnife.bind(this,view);
        initview(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        queryid = "0";
        adpter.clear();
        buyList("1");
    }

    private void initview(View view) {
        adpter = new MybuyAdapter();
        recyc_list.setLayoutManager(new LinearLayoutManager(getContext()));
        recyc_list.setAdapter(adpter);
        adpter.setOnItemClickListener(new OnItemClickListener<MybuyBean>() {
            @Override
            public void onClick(MybuyBean mybuyBean, View view, int position) {
                Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
                intent.putExtra("TRADE_ID",mybuyBean.getTRADE_ID());
                intent.putExtra("action","买单");
                startActivity(intent);
            }
        });
        sm_refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(1000);
                queryid = "0";
                adpter.clear();
                buyList("1");
            }
        });
        sm_refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
                buyList("2");
            }
        });
    }

    private void buyList(String type) {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.buyList);
        requestParams.addParameter("QUERY_ID", queryid);
        requestParams.addParameter("USER_NAME", SPUtils.getString(SPUtils.username));
        requestParams.addParameter("TYPE", type);
        MXUtils.httpPost(requestParams,new CommonCallbackImp("MARKET - 买单列表",requestParams, (BaseActivity) getActivity()){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    List<MybuyBean> beanList = JSONArray.parseArray(pd,MybuyBean.class);
                    if (beanList.size()>0) {
                        queryid = beanList.get(beanList.size()-1).getTRADE_ID();
                    }
                    adpter.addAll(beanList);
                    adpter.notifyDataSetChanged();
                    if (adpter.getData().size()>0) {
                        ll_no_data.setVisibility(View.GONE);
                        recyc_list.setVisibility(View.VISIBLE);
                    } else {
                        ll_no_data.setVisibility(View.VISIBLE);
                        recyc_list.setVisibility(View.GONE);
                    }
                } else {
                    ToastUtils.toastutils(message,getActivity());
                }

            }
        });

    }




}
