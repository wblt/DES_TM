package wb.com.cctm.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.xutils.http.RequestParams;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import wb.com.cctm.R;
import wb.com.cctm.adapter.CheckAdpter;
import wb.com.cctm.base.BaseActivity;
import wb.com.cctm.base.OnItemClickListener;
import wb.com.cctm.bean.MarkBean;
import wb.com.cctm.commons.utils.ToastUtils;
import wb.com.cctm.net.CommonCallbackImp;
import wb.com.cctm.net.FlowAPI;
import wb.com.cctm.net.MXUtils;

public class MarketActivity extends BaseActivity {
    @BindView(R.id.recyc_list)
    RecyclerView recyc_list;
    private String queryId = "0";
    private CheckAdpter adpter;
    @BindView(R.id.ll_no_data)
    LinearLayout ll_no_data;
    @BindView(R.id.sm_refreshLayout)
    SmartRefreshLayout sm_refreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(this,R.layout.activity_market);
        appendTopBody(R.layout.activity_top_icon);
        setTopLeftDefultListener();
        setTopBarTitle("市场交易");
        ButterKnife.bind(this);
        initview();
        adpter.clear();
        queryId = "0";
        marketList("1");
    }


    private void initview() {
        adpter = new CheckAdpter();
        adpter.setOnItemClickListener(new OnItemClickListener<MarkBean>() {
            @Override
            public void onClick(MarkBean s, View view, int position) {
                Intent intent = new Intent(MarketActivity.this, MarkBuyActivity.class);
                intent.putExtra("TRADE_ID",s.getTRADE_ID());
                // 价格
                intent.putExtra("price",s.getBUSINESS_PRICE());
                intent.putExtra("number",s.getBUSINESS_COUNT());
                startActivity(intent);
            }
        });
        recyc_list.setLayoutManager(new LinearLayoutManager(MarketActivity.this));
        recyc_list.setAdapter(adpter);
        sm_refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(1000);
                adpter.clear();
                queryId = "0";
                //marketList("1");
            }
        });
        sm_refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
                //marketList("2");
            }
        });
    }

    private void marketList(String type) {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.marketList);
        requestParams.addParameter("QUERY_ID", queryId);
        requestParams.addParameter("TYPE", type);
        MXUtils.httpPost(requestParams,new CommonCallbackImp("MARKET - 市场列表",requestParams, this){
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
                    ToastUtils.toastutils(message,MarketActivity.this);
                }

            }
        });
    }

}
