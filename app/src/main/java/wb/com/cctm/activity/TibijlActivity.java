package wb.com.cctm.activity;

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
import wb.com.cctm.adapter.TibiAdapter;
import wb.com.cctm.base.BaseActivity;
import wb.com.cctm.base.OnItemClickListener;
import wb.com.cctm.bean.AllReleaseBean;
import wb.com.cctm.bean.TibiBean;
import wb.com.cctm.commons.utils.SPUtils;
import wb.com.cctm.commons.utils.ToastUtils;
import wb.com.cctm.net.CommonCallbackImp;
import wb.com.cctm.net.FlowAPI;
import wb.com.cctm.net.MXUtils;


public class TibijlActivity extends BaseActivity {

    @BindView(R.id.recyc_list)
    RecyclerView recyc_list;
    @BindView(R.id.ll_no_data)
    LinearLayout ll_no_data;
    TibiAdapter adapter;
    @BindView(R.id.sm_refreshLayout)
    SmartRefreshLayout sm_refreshLayout;
    private String queryId = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(this,R.layout.activity_tibijl);
        appendTopBody(R.layout.activity_top_text);
        setTopLeftDefultListener();
        setTopBarTitle("提币记录");
        ButterKnife.bind(this);
        initview();
        tequilaDetail("1");
    }

    private void initview() {
        adapter = new TibiAdapter();
        recyc_list.setLayoutManager(new LinearLayoutManager(this));
        recyc_list.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener<TibiBean>() {
            @Override
            public void onClick(TibiBean tibiBean, View view, int position) {
                tequilaCancle(tibiBean.getID());
            }
        });
        sm_refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(1000);
                queryId = "0";
                adapter.clear();
                tequilaDetail("1");
            }
        });
        sm_refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
                tequilaDetail("2");
            }
        });
    }

    private void tequilaDetail(String type) {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.tequilaDetail);
        requestParams.addParameter("USER_NAME", SPUtils.getString(SPUtils.username));
        requestParams.addParameter("QUERY_ID", queryId);
        requestParams.addParameter("TYPE", type);
        MXUtils.httpPost(requestParams,new CommonCallbackImp("MY - 提币申请",requestParams,this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    List<TibiBean> beanList = JSONArray.parseArray(pd,TibiBean.class);
                    if (beanList.size() > 0) {
                        queryId = beanList.get(beanList.size() -1).getID();
                    }
                    adapter.addAll(beanList);
                    adapter.notifyDataSetChanged();
                    if (adapter.getData().size()>0) {
                        recyc_list.setVisibility(View.VISIBLE);
                        ll_no_data.setVisibility(View.GONE);
                    } else {
                        recyc_list.setVisibility(View.GONE);
                        ll_no_data.setVisibility(View.VISIBLE);
                    }
                } else {
                    ToastUtils.toastutils(message,TibijlActivity.this);
                }

            }
        });
    }

    private void tequilaCancle(String id) {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.tequilaCancle);
        requestParams.addParameter("ID", id);
        MXUtils.httpPost(requestParams,new CommonCallbackImp("MY - 提币取消",requestParams,this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    queryId = "0";
                    adapter.clear();
                    tequilaDetail("1");
                    ToastUtils.toastutils("操作成功",TibijlActivity.this);
                } else {
                    ToastUtils.toastutils(message,TibijlActivity.this);
                }

            }
        });
    }

}
