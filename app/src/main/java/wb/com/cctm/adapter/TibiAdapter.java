package wb.com.cctm.adapter;

import android.view.View;
import android.view.ViewGroup;

import wb.com.cctm.R;
import wb.com.cctm.base.BaseRecyclerViewAdapter;
import wb.com.cctm.base.BaseRecyclerViewHolder;
import wb.com.cctm.bean.TibiBean;
import wb.com.cctm.commons.utils.StringUtil;
import wb.com.cctm.databinding.ItemTibiJiluBinding;

/**
 * Created by wb on 2018/6/1.
 */

public class TibiAdapter extends BaseRecyclerViewAdapter<TibiBean> {

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(viewGroup, R.layout.item_tibi_jilu);
    }

    private class ViewHolder extends BaseRecyclerViewHolder<TibiBean,ItemTibiJiluBinding> {
        public ViewHolder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
        }

        @Override
        public void onBindViewHolder(final TibiBean object, final int position) {
            binding.executePendingBindings();
            binding.tvName.setText(object.getUSER_NAME());
            binding.tvAddress.setText(object.getW_ADDRESS());
            binding.tvNumber.setText(object.getS_CURRENCY());
            String time = StringUtil.formatUnixTime(Long.parseLong(object.getCREATE_TIME()));
            binding.tvTime.setText(time);
            if (object.getSTATUS().equals("0")) {
                binding.tvStatus.setText("待审核");
            } else if (object.getSTATUS().equals("1")) {
                binding.tvStatus.setText("提币成功");
            } else if (object.getSTATUS().equals("2")) {
                binding.tvStatus.setText("提币失败");
            } else if (object.getSTATUS().equals("9")) {
                binding.tvStatus.setText("已取消");
            } else {
                binding.tvStatus.setText("未知");
            }
            binding.tvCancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(object,v,position);
                    }
                }
            });
            if (object.getSTATUS().equals("0")) {
                binding.tvCancle.setVisibility(View.VISIBLE);
            } else {
                binding.tvCancle.setVisibility(View.GONE);
            }
        }
    }
}
