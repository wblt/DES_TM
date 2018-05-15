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
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import wb.com.cctm.R;
import wb.com.cctm.adapter.NewsAdapter;
import wb.com.cctm.base.BaseActivity;
import wb.com.cctm.base.OnItemClickListener;
import wb.com.cctm.bean.NoticeBean;
import wb.com.cctm.commons.utils.ToastUtils;
import wb.com.cctm.net.CommonCallbackImp;
import wb.com.cctm.net.FlowAPI;
import wb.com.cctm.net.MXUtils;

public class NewsActivity extends BaseActivity {
    @BindView(R.id.recyc_list)
    RecyclerView recyc_list;
    private NewsAdapter newsAdapter;
    @BindView(R.id.ll_content)
    LinearLayout ll_content;
    @BindView(R.id.ll_no_data)
    LinearLayout ll_no_data;
    @BindView(R.id.sm_refreshLayout)
    SmartRefreshLayout sm_refreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(this,R.layout.activity_news);
        appendTopBody(R.layout.activity_top_text);
        setTopBarTitle("公告");
        setTopLeftDefultListener();
        ButterKnife.bind(this);
        initview();
        notice();
    }

    private void initview() {
        newsAdapter = new NewsAdapter();
        recyc_list.setLayoutManager(new LinearLayoutManager(this));
        recyc_list.setAdapter(newsAdapter);
        sm_refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(1000);
                newsAdapter.clear();
                notice();
            }
        });
    }
    private void notice() {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.notice);
        MXUtils.httpPost(requestParams,new CommonCallbackImp("USER - 注册短信验证码",requestParams,this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    List<NoticeBean> beanList = JSONArray.parseArray(pd,NoticeBean.class);
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
                    ToastUtils.toastutils(message,NewsActivity.this);
                }

            }
        });
    }
}
