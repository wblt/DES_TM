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
                .diskCacheStrategy(DiskCacheStrategy.ALL)//让Glide既缓存全尺寸图片，下次在任何ImageView中加载图片的时候，全尺寸的图片将从缓存中取出，重新调整大小，然后缓存
                .crossFade()
                .placeholder(R.mipmap.error_picture)
                .transform(new CircleTransform(iv.getContext()))
                .into(iv);
    }

}
