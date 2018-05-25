package wb.com.cctm.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.lmj.mypwdinputlibrary.InputPwdView;
import com.lmj.mypwdinputlibrary.MyInputPwdUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.xutils.http.RequestParams;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import wb.com.cctm.R;
import wb.com.cctm.activity.GuadanActivity;
import wb.com.cctm.activity.MarkBuyActivity;
import wb.com.cctm.activity.MarketActivity;
import wb.com.cctm.activity.MineBuyActivity;
import wb.com.cctm.activity.MineSellActivity;
import wb.com.cctm.adapter.BBPageAdapter;
import wb.com.cctm.adapter.CheckAdpter;
import wb.com.cctm.base.BaseActivity;
import wb.com.cctm.base.BaseFragment;
import wb.com.cctm.base.OnItemClickListener;
import wb.com.cctm.bean.DepthBean;
import wb.com.cctm.bean.MarkBean;
import wb.com.cctm.commons.utils.CommonUtils;
import wb.com.cctm.commons.utils.ToastUtils;
import wb.com.cctm.net.CommonCallbackImp;
import wb.com.cctm.net.FlowAPI;
import wb.com.cctm.net.MXUtils;

public class MarketFragment extends BaseFragment {
    @BindView(R.id.top_left)
    ImageButton top_left;
    @BindView(R.id.top_right_text)
    TextView top_right_text;
    private Unbinder unbinder;
    @BindView(R.id.recyc_list)
    RecyclerView recyc_list;
    private String queryId = "0";
    private CheckAdpter adpter;
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
        View view = super.onCreateView(inflater,container,savedInstanceState);
        appendMainBody(this,R.layout.fragment_market);
        appendTopBody(R.layout.activity_top_text);
        setTopBarTitle("交易");
        unbinder = ButterKnife.bind(this,view);
        initview(view);
        return view;
    }

    private void initview(View view) {
        top_left.setVisibility(View.INVISIBLE);
        top_right_text.setText("挂单");
        top_right_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),GuadanActivity.class);
                startActivity(intent);
            }
        });

        adpter = new CheckAdpter();
        adpter.setOnItemClickListener(new OnItemClickListener<MarkBean>() {
            @Override
            public void onClick(MarkBean s, View view, int position) {
                Intent intent = new Intent(getActivity(), MarkBuyActivity.class);
                intent.putExtra("TRADE_ID",s.getTRADE_ID());
                // 价格
                intent.putExtra("price",s.getBUSINESS_PRICE());
                intent.putExtra("number",s.getBUSINESS_COUNT());
                startActivity(intent);
            }
        });
        recyc_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyc_list.setAdapter(adpter);
        sm_refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(1000);
                adpter.clear();
                queryId = "0";
                marketList("1");
            }
        });
        sm_refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
                marketList("2");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        adpter.clear();
        queryId = "0";
        marketList("1");
    }


    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    private void marketList(String type) {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.marketList);
        requestParams.addParameter("QUERY_ID", queryId);
        requestParams.addParameter("TYPE", type);
        MXUtils.httpPost(requestParams,new CommonCallbackImp("MARKET - 市场列表",requestParams, (BaseActivity) getActivity()){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    List<MarkBean> beanList = JSONArray.parseArray(pd,MarkBean.class);
                    if (beanList.size()>0) {
                        queryId = beanList.get(beanList.size()-1).getTRADE_ID();
                    }
                    adpter.addAll(beanList);
                    adpter.notifyDataSetChanged();
                    if (adpter.getData().size()>0) {
                        recyc_list.setVisibility(View.VISIBLE);
                        ll_no_data.setVisibility(View.GONE);
                    } else {
                        recyc_list.setVisibility(View.GONE);
                        ll_no_data.setVisibility(View.VISIBLE);
                    }
                } else {
                    ToastUtils.toastutils(message,getActivity());
                }

            }
        });
    }

}
