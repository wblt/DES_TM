package wb.com.cctm.commons.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;

import wb.com.cctm.R;

/**
 * Author: Othershe
 * Time:  2016/8/11 14:47
 */
public class ImageLoader {
    public static void load(String url, ImageView iv) {
        Glide.with(iv.getContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .crossFade()
                .placeholder(R.mipmap.logo)
                .into(iv);
    }

    public static void loadCircle(String url, ImageView iv) {
        Glide.with(iv.getContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .crossFade()
                .placeholder(R.mipmap.logo)
                .transform(new CircleTransform(iv.getContext()))
                .into(iv);
    }

}
