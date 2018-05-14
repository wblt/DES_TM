package wb.com.cctm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import wb.com.cctm.R;
import wb.com.cctm.base.BaseRecyclerViewAdapter;
import wb.com.cctm.base.BaseRecyclerViewHolder;
import wb.com.cctm.base.OnItemClickListener;
import wb.com.cctm.bean.NoticeBean;
import wb.com.cctm.databinding.ItemNewsBinding;

/**
 * Created by wb on 2018/4/16.
 */

public class NewsAdapter extends BaseRecyclerViewAdapter<NoticeBean> {
    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent,R.layout.item_news);
    }
    private class ViewHolder extends BaseRecyclerViewHolder<NoticeBean,ItemNewsBinding> {

        public ViewHolder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
        }

        @Override
        public void onBindViewHolder(final NoticeBean object, final int position) {
            binding.executePendingBindings();
            binding.tvTitle.setText(object.getTITLE());
            binding.tvContent.setText(object.getCONTENT());
            binding.tvTime.setText(object.getCREATE_TIME());
            binding.tvDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(object,v,position);
                    }
                }
            });

        }
    }

}
