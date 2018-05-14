package wb.com.cctm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import wb.com.cctm.R;
import wb.com.cctm.base.BaseRecyclerViewAdapter;
import wb.com.cctm.base.BaseRecyclerViewHolder;
import wb.com.cctm.bean.AllReleaseBean;
import wb.com.cctm.bean.TransferRecoderBean;
import wb.com.cctm.databinding.ItemTransfeRecoderBinding;

/**
 * Created by wb on 2018/4/24.
 */

public class TransferRecoderAdapter extends BaseRecyclerViewAdapter<TransferRecoderBean> {
    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(viewGroup,R.layout.item_transfe_recoder);
    }

    private class ViewHolder extends BaseRecyclerViewHolder<TransferRecoderBean,ItemTransfeRecoderBinding> {
        public ViewHolder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
        }
        @Override
        public void onBindViewHolder(TransferRecoderBean object, int position) {
            binding.executePendingBindings();
            binding.tvAddress.setText(object.getW_ADDRESS());
            binding.tvNumber.setText("-"+object.getSEND_MONEY());
            binding.tvTime.setText(object.getCREATE_TIME());
            binding.tvType.setText(object.getCURRENCY_TYPE());
        }
    }
}
