package cn.heima.myutilslibrary.glide;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import cn.heima.myutilslibrary.R;
import cn.krvision.toolmodule.base.BaseActivity;

public class GlideActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glide);
        final ImageView iv_header = findViewById(R.id.iv_header);


        Glide.with(mContext)
                .load("http://visioncircle.image.alimmdn.com/head_image/75BFA00E-EA78-43D6-8381-F51407182BBC.png")
                .asBitmap()  //这句不能少，否则下面的方法会报错
                .centerCrop()
                .placeholder(R.drawable.person_icon) //预加载暂未图片
                .error(R.drawable.person_icon) //加载失败图片
                .into(new BitmapImageViewTarget(iv_header) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        iv_header.setImageDrawable(circularBitmapDrawable);
                    }
                });

    }
}
