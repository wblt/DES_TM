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
    private List<Fragment> mFragmentList;
    private List<String> mTitleList;
    @BindView(R.id.lineChart)
    LineChart lineChart;
    @BindView(R.id.tv_day)
    TextView tv_day;
    @BindView(R.id.tv_week)
    TextView tv_week;
    private List<DepthBean> depthBeanList;
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
        setTopBarTitle("卖单市场");
        unbinder = ButterKnife.bind(this,view);
        initview(view);
        return view;
    }

    private void initview(View view) {
        top_right_text.setText("挂单");
        top_right_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),GuadanActivity.class);
                startActivity(intent);
            }
        });
        top_left.setVisibility(View.INVISIBLE);
        initLineChart();
        depth("0","日线走势图");
        adpter = new CheckAdpter();
        adpter.setOnItemClickListener(new OnItemClickListener<MarkBean>() {
            @Override
            public void onClick(MarkBean s, View view, int position) {
                switch (view.getId()) {
                    case R.id.ll_pipei:
                        Intent intent = new Intent(getActivity(), MarkBuyActivity.class);
                        intent.putExtra("TRADE_ID",s.getTRADE_ID());
                        // 价格
                        intent.putExtra("price",s.getBUSINESS_PRICE());
                        intent.putExtra("number",s.getBUSINESS_COUNT());
                        startActivity(intent);
                        break;
                }
            }
        });
        recyc_list.setLayoutManager(new LinearLayoutManager(getContext()));
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

    @OnClick({R.id.tv_day,R.id.tv_week})
    void viewClick(View view) {
        List<Entry> entries;
        switch (view.getId()) {
            case R.id.tv_day:
                depth("0","日线走势图");
                tv_day.setTextColor(getActivity().getResources().getColor(R.color.yellow));
                tv_week.setTextColor(getActivity().getResources().getColor(R.color.white));
                break;
            case R.id.tv_week:
                depth("1","周线走势图");
                tv_day.setTextColor(getActivity().getResources().getColor(R.color.white));
                tv_week.setTextColor(getActivity().getResources().getColor(R.color.yellow));
                break;
            default:
                break;
        }
    }


    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    private void initLineChart() {
        final List<String> mList = new ArrayList<>();
        mList.add("04-22");
        mList.add("04-23");
        mList.add("04-24");
        mList.add("04-25");
        mList.add("04-26");
        mList.add("04-27");
        mList.add("04-28");
        //显示边界
        lineChart.setDrawBorders(true);
        // 轴
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(6,false);
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(6);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mList.get((int) value); //mList为存有月份的集合
            }
        });
        // 左右y轴
        YAxis leftYAxis = lineChart.getAxisLeft();
        YAxis rightYAxis = lineChart.getAxisRight();
        leftYAxis.setAxisMinimum(0f);
        leftYAxis.setAxisMaximum(10f);
        rightYAxis.setAxisMinimum(0f);
        rightYAxis.setAxisMaximum(10f);
        rightYAxis.setTextColor(Color.WHITE); //文字颜色
        leftYAxis.setTextColor(Color.WHITE); //文字颜色

        // 描述
        Description description = new Description();
        description.setEnabled(false);
        lineChart.setDescription(description);

        // 手势
        lineChart.setDragEnabled(false);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setScaleEnabled(false);
        lineChart.setDragXEnabled(false);
        lineChart.setDragYEnabled(false);


        // 标签
        Legend legend = lineChart.getLegend();
        legend.setTextColor(Color.WHITE);

        //设置数据
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            entries.add(new Entry(i, (float) (Math.random()) * 10));
        }
        //一个LineDataSet就是一条线
        LineDataSet lineDataSet = new LineDataSet(entries, "日线走势图");
        LineData data = new LineData(lineDataSet);
        lineDataSet.setColor(Color.GREEN);
        lineDataSet.setValueTextColor(Color.GREEN);
        lineChart.setData(data);
    }

    /**
     * 更新图表
     *
     */
    public void notifyDataSetChanged(LineChart chart, List<Entry> values, String lable, final List<String> xList) {
        if (xList == null) {
            return;
        }
        if (values == null) {
            return;
        }
        chart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xList.get((int) value); //mList为存有月份的集合
            }
        });
        chart.invalidate();
        LineDataSet lineDataSet = (LineDataSet) chart.getData().getDataSetByIndex(0);
        lineDataSet.setValues(values);
        lineDataSet.setLabel(lable);
        chart.getData().notifyDataChanged();
        chart.notifyDataSetChanged();
    }

    private void depth(final String type, final String title) {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.depth);
        requestParams.addParameter("TYPE", type);
        requestParams.addParameter("NUM", "7");
        MXUtils.httpPost(requestParams,new CommonCallbackImp("USER - 获取k线数据",requestParams, (BaseActivity) getActivity()){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    depthBeanList = JSONArray.parseArray(pd,DepthBean.class);
                    if (depthBeanList != null) {
                        // 值
                        Collections.reverse(depthBeanList); // 倒序排列
                        List<String> xlist = new ArrayList<String>();
                        List<Entry>  entries = new ArrayList<>();
                        for (int i = 0; i < depthBeanList.size(); i++) {
                            DepthBean bean = depthBeanList.get(i);
                            String sub = bean.getDEAL_TIME().substring(5);
                            entries.add(new Entry(i, Float.valueOf(bean.getBUSINESS_PRICE())));
                            xlist.add(sub);
                        }
                        String lastStr = depthBeanList.get(depthBeanList.size()-1).getDEAL_TIME();
                        for (int i = 0;i<7-depthBeanList.size();i++) {
                            String rtime = "00-00";
                            if (type.equals("0")) {
                                rtime = CommonUtils.getOldDate(lastStr,i*1+1);
                                xlist.add(rtime.substring(5));
                            } else {
                                rtime =  CommonUtils.getOldDate(lastStr,i*7+7);
                                xlist.add(rtime.substring(5));
                            }

                        }
                        notifyDataSetChanged(lineChart,entries,title,xlist);
                    }
                } else {
                    ToastUtils.toastutils(message,getActivity());
                }

            }
        });
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
                    ToastUtils.toastutils(message,getContext());
                }

            }
        });
    }

}
