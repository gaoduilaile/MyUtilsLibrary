package cn.krvision.toolmodule.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

/**
 * Created by gaoqiong on 2018/12/11 10:48
 * Description:Glide加载图片
 */
public class GlideUtils {
    public static GlideUtils getInstance() {

        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final GlideUtils instance = new GlideUtils();
    }


    /**
     * Glide加载圆形头像
     * @param mContext
     * @param resourceId
     * @param imageUrl
     * @param imageView
     */
    public void loadImageCircle(final Context mContext, int resourceId, String imageUrl, final ImageView imageView){
        Glide.with(mContext)
                .load(imageUrl)
                .asBitmap()  //这句不能少，否则下面的方法会报错
                .centerCrop()
                .placeholder(resourceId) //预加载暂未图片
                .error(resourceId) //加载失败图片
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        imageView.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

    /**
     * Glide加载图片
     * @param mContext
     * @param resourceId
     * @param imageUrl
     * @param imageView
     */
    public void loadImage(final Context mContext, int resourceId, String imageUrl, final ImageView imageView){
        Glide.with(mContext)
                .load(imageUrl)
                .asBitmap()  //这句不能少，否则下面的方法会报错
                .centerCrop()
                .placeholder(resourceId) //预加载暂未图片
                .error(resourceId) //加载失败图片
                .into(imageView);
    }
}
