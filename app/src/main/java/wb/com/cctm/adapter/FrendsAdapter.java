package wb.com.cctm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import wb.com.cctm.R;
import wb.com.cctm.base.BaseRecyclerViewAdapter;
import wb.com.cctm.base.BaseRecyclerViewHolder;
import wb.com.cctm.base.OnItemClickListener;
import wb.com.cctm.bean.FrendsBean;
import wb.com.cctm.bean.NoticeBean;
import wb.com.cctm.commons.utils.ImageLoader;
import wb.com.cctm.databinding.ItemFrendsBinding;

/**
 * Created by wb on 2018/4/20.
 */

public class FrendsAdapter extends BaseRecyclerViewAdapter<FrendsBean> {
    // 模板复制即可
    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 传你的item布局文件
        return new ViewHolder(parent, R.layout.item_frends);
    }

    // 模板复制即可
    private class ViewHolder extends BaseRecyclerViewHolder<FrendsBean,ItemFrendsBinding> {
        public ViewHolder(ViewGroup parent, int item_android) {
            super(parent, item_android);
        }
        @Override
        public void onBindViewHolder(FrendsBean object, int position) {
            binding.executePendingBindings();
            // 接下来真正开始您的业务逻辑
            binding.tvUsername.setText(object.getNICK_NAME());
            ImageLoader.load(object.getHEAD_URL(),binding.ivImg);
            binding.tvTel.setText(object.getTEL());
        }
    }
}
