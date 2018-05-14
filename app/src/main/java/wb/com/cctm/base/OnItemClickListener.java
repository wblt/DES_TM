package wb.com.cctm.base;

import android.view.View;

/**
 * Created by wb on 2018/3/28.
 */
public interface OnItemClickListener<T> {
    public void onClick(T t, View view, int position);
}
