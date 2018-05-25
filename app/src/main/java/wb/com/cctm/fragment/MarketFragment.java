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

    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }






}
