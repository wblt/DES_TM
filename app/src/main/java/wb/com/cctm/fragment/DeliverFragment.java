package wb.com.cctm.fragment;
import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
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
import com.lzy.imagepicker.ImagePicker;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.xutils.http.RequestParams;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
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
import wb.com.cctm.bean.DepthBean;
import wb.com.cctm.bean.NoticeBean;
import wb.com.cctm.bean.ReleaseDepthBean;
import wb.com.cctm.commons.step.UpdateUiCallBack;
import wb.com.cctm.commons.step.service.StepService;
import wb.com.cctm.commons.step.utils.SharedPreferencesUtils;
import wb.com.cctm.commons.step.view.StepArcView;
import wb.com.cctm.commons.utils.CommonUtils;
import wb.com.cctm.commons.utils.ImageLoader;
import wb.com.cctm.commons.utils.SPUtils;
import wb.com.cctm.commons.utils.ToastUtils;
import wb.com.cctm.commons.widget.MyGridView;
import wb.com.cctm.commons.zxing.android.CaptureActivity;
import wb.com.cctm.commons.zxing.bean.ZxingConfig;
import wb.com.cctm.commons.zxing.common.Constant;
import wb.com.cctm.net.CommonCallbackImp;
import wb.com.cctm.net.FlowAPI;
import wb.com.cctm.net.MXUtils;

public class DeliverFragment extends BaseFragment {
    Unbinder unbinder;
    @BindView(R.id.top_left)
    ImageButton top_left;
    @BindView(R.id.top_right_icon)
    ImageButton top_right_icon;
    @BindView(R.id.lineChart)
    LineChart lineChart;
    @BindView(R.id.lineChart2)
    LineChart lineChart2;
    @BindView(R.id.tv_day)
    TextView tv_day;
    @BindView(R.id.tv_week)
    TextView tv_week;
    private List<DepthBean> depthBeanList;
    private List<ReleaseDepthBean> releaseDepthBeanList;

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
    }

    @OnClick({R.id.ll_dui,R.id.ll_address,R.id.ll_caiwu,R.id.tv_day,R.id.tv_week})
    void viewClick(View view) {
        List<Entry> entries;
        Intent intent;
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
            case R.id.ll_caiwu:
                intent = new Intent(getActivity(),FinancialTransferActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_address:
                intent = new Intent(getActivity(), MoveWalletActivity.class);
                intent.putExtra("D_CURRENCY",SPUtils.getString("D_CURRENCY"));
                intent.putExtra("QK_CURRENCY",SPUtils.getString("QK_CURRENCY"));
                intent.putExtra("A_CURRENCY",SPUtils.getString("A_CURRENCY"));
                startActivity(intent);
                break;
            case R.id.ll_dui:
                intent = new Intent(getActivity(),WalletConversionActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }


    private void initview(View view) {
        top_left.setVisibility(View.INVISIBLE);
        initLineChart();
        initLineChart2();
        depth("0","日线走势图");
        releaseDepth("0","算力释放报表");
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
        xAxis.setTextColor(getResources().getColor(R.color.white));
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
        rightYAxis.setTextColor(getResources().getColor(R.color.white)); //文字颜色
        leftYAxis.setTextColor(getResources().getColor(R.color.white)); //文字颜色

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
        legend.setTextColor(getResources().getColor(R.color.white));

        //设置数据
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            entries.add(new Entry(i, (float) (Math.random()) * 10));
        }
        //一个LineDataSet就是一条线
        LineDataSet lineDataSet = new LineDataSet(entries, "日线走势图");
        LineData data = new LineData(lineDataSet);
        lineDataSet.setColor(getResources().getColor(R.color.white));
        lineDataSet.setValueTextColor(getResources().getColor(R.color.white));
        lineChart.setData(data);
    }

    private void initLineChart2() {
        final List<String> mList = new ArrayList<>();
        mList.add("04-22");
        mList.add("04-23");
        mList.add("04-24");
        mList.add("04-25");
        mList.add("04-26");
        mList.add("04-27");
        mList.add("04-28");
        //显示边界
        lineChart2.setDrawBorders(true);
        // 轴
        XAxis xAxis = lineChart2.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(6,false);
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(6);
        xAxis.setTextColor(getResources().getColor(R.color.white));
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mList.get((int) value); //mList为存有月份的集合
            }
        });
        // 左右y轴
        YAxis leftYAxis = lineChart2.getAxisLeft();
        YAxis rightYAxis = lineChart2.getAxisRight();
        leftYAxis.setAxisMinimum(0f);
        leftYAxis.setAxisMaximum(1000f);
        rightYAxis.setAxisMinimum(0f);
        rightYAxis.setAxisMaximum(1000f);
        rightYAxis.setTextColor(getResources().getColor(R.color.white)); //文字颜色
        leftYAxis.setTextColor(getResources().getColor(R.color.white)); //文字颜色

        // 描述
        Description description = new Description();
        description.setEnabled(false);
        lineChart2.setDescription(description);

        // 手势
        lineChart2.setDragEnabled(false);
        lineChart2.setDoubleTapToZoomEnabled(false);
        lineChart2.setPinchZoom(false);
        lineChart2.setScaleEnabled(false);
        lineChart2.setDragXEnabled(false);
        lineChart2.setDragYEnabled(false);

        // 标签
        Legend legend = lineChart2.getLegend();
        legend.setTextColor(getResources().getColor(R.color.white));

        //设置数据
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            entries.add(new Entry(i, (float) (Math.random()) * 10));
        }
        //一个LineDataSet就是一条线
        LineDataSet lineDataSet = new LineDataSet(entries, "算力释放报表");
        LineData data = new LineData(lineDataSet);
        lineDataSet.setColor(getResources().getColor(R.color.white));
        lineDataSet.setValueTextColor(getResources().getColor(R.color.white));
        lineChart2.setData(data);
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

    private void releaseDepth(final String type, final String title) {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.releaseDepth);
        requestParams.addParameter("USER_NAME", SPUtils.getString(SPUtils.username));
        MXUtils.httpPost(requestParams,new CommonCallbackImp("USER - 获取释放k线数据",requestParams, (BaseActivity) getActivity()){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    releaseDepthBeanList = JSONArray.parseArray(pd,ReleaseDepthBean.class);
                    if (releaseDepthBeanList != null && releaseDepthBeanList.size()>0) {
                        // 值
                        Collections.reverse(releaseDepthBeanList); // 倒序排列
                        List<String> xlist = new ArrayList<String>();
                        List<Entry>  entries = new ArrayList<>();
                        for (int i = 0; i < releaseDepthBeanList.size(); i++) {
                            ReleaseDepthBean bean = releaseDepthBeanList.get(i);
                            String sub = bean.getCREATE_TIME().substring(5);
                            entries.add(new Entry(i, Float.valueOf(bean.getCALCULATE_MONEY())));
                            xlist.add(sub);
                        }
                        String lastStr = releaseDepthBeanList.get(releaseDepthBeanList.size()-1).getCREATE_TIME();
                        for (int i = 0;i<7-releaseDepthBeanList.size();i++) {
                            String rtime = "00-00";
                            if (type.equals("0")) {
                                rtime = CommonUtils.getOldDate(lastStr,i*1+1);
                                xlist.add(rtime.substring(5));
                            } else {
                                rtime =  CommonUtils.getOldDate(lastStr,i*7+7);
                                xlist.add(rtime.substring(5));
                            }
                        }
                        notifyDataSetChanged(lineChart2,entries,title,xlist);
                    }
                } else {
                    ToastUtils.toastutils(message,getActivity());
                }

            }
        });
    }

}
