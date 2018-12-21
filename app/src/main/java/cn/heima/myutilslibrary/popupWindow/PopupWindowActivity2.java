package cn.heima.myutilslibrary.popupWindow;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.heima.myutilslibrary.R;
import cn.heima.myutilslibrary.singleInstance.Resource;
import cn.heima.myutilslibrary.singleInstance.Singleton;
import cn.heima.myutilslibrary.singleInstance.SingletonEnum;


public class PopupWindowActivity2 extends AppCompatActivity {
    private TextView mTv;
    private TextView mMainView;
    private RelativeLayout mRl_parent;
    private PopupWindow mPopupWindow;
    private int mWidth;
    private boolean mIsClick5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_window);
        ButterKnife.bind(this);
        Resource resource = SingletonEnum.INSTANCE.getInstance();//通过枚举类方式 获取Resource的单例
        Singleton singleton = Singleton.getInstance();//通过静态内部类方式 获取Singleton的单例
        mTv = (TextView) findViewById(R.id.tv);
        mMainView = (TextView) findViewById(R.id.tv_mainView);
        mRl_parent = (RelativeLayout) findViewById(R.id.rl_parent);
    }

    @OnClick({R.id.tv, R.id.tv_mainView, R.id.button01, R.id.button02, R.id.button03, R.id.button04, R.id.button05, R.id.rl_parent})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv:
                startActivity(new Intent(PopupWindowActivity2.this,PopupWindowActivity.class));
                break;
            case R.id.tv_mainView:
                break;
            case R.id.button01:
                initPopupWindow(1);
                mPopupWindow.showAsDropDown(mTv);
                break;
            case R.id.button02:
                initPopupWindow(2);
                //以下为分步介绍控件获取中间位置偏移量方式：(对应控件宽度-popup宽度)/2
                int tv_width = mTv.getWidth();//获取对应的控件view宽度px值
                int popup_width = dip2px(120);//将popupWindow宽度转为px值(这里的popup宽度是写死了的)
                int width = (tv_width - mWidth) / 2;//获取x轴偏移量px
                mPopupWindow.showAsDropDown(mTv, width, 0);//设置x轴偏移量：注意单位为px
                break;
            case R.id.button03:
                initPopupWindow(3);
                //int[] locaitons = new int[2];//存放相应控件在屏幕的xy轴坐标点；单位px
                //mTv.getLocationOnScreen(locaitons);//locaitons[0]为x轴 locaitons[1]为y轴
                // X、Y方向偏移量:设置x轴偏移量为相应控件中心;y轴无偏移
                mPopupWindow.showAtLocation(mRl_parent, Gravity.CENTER, 0, 0);
                break;
            case R.id.button04:
                initPopupWindow(4);
                mPopupWindow.showAtLocation(mRl_parent, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.button05:
                mIsClick5 = !mIsClick5;
                initPopupWindow(5);
                break;
            case R.id.rl_parent:
                break;
        }
    }


    private void initPopupWindow(int type) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View popupWindow = layoutInflater.inflate(R.layout.popup_window2, null);
        //mode共有三种情况，取值分别为MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY, MeasureSpec.AT_MOST。
        // MeasureSpec.EXACTLY是（完全）精确尺寸，父元素决定自元素的确切大小，子元素将被限定在给定的边界里而忽略它本身大小；
        //      当我们将控件的layout_width或layout_height指定为具体数值时
        //      如andorid:layout_width="50dip"，或者为FILL_PARENT是，都是控件大小已经确定的情况，都是精确尺寸。
        // MeasureSpec.AT_MOST是（至多）最大尺寸，当控件的layout_width或layout_height指定为WRAP_CONTENT时，
        //      控件大小一般随着控件的子空间或内容进行变化，此时控件尺寸只要不超过父控件允许的最大尺寸即可。
        //      因此，此时的mode是AT_MOST，size给出了父控件允许的最大尺寸。
        // MeasureSpec.UNSPECIFIED是未指定尺寸，父元素不对子元素施加任何束缚，子元素可以得到任意想要的大小；
        //      这种情况不多，一般都是父控件是AdapterView，通过measure方法传入的模式。
        //以下方式是为了在popupWindow还没有弹出显示之前就测量获取其宽高（单位是px相熟）
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        TextView mTextView = (TextView) popupWindow.findViewById(R.id.tv_popupTv);
        mTextView.measure(w, h);
        mWidth = mTextView.getMeasuredWidth();//获取测量宽度px
        int mHeight = mTextView.getMeasuredHeight();//获取测量高度px

        //设置点击popupWindow里面文本可以dismiss该popupWindow
        popupWindow.findViewById(R.id.tv_popupTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
            }
        });
        // 创建一个PopupWindow
        // 参数1：contentView 指定PopupWindow的显示View
        // 参数2：width 指定PopupWindow的width可以固定死某些数值：
        //       如果不想固定死可以设置为ViewGroup.LayoutParams.WRAP_CONTENT/MATCH_CONTENT
        // 参数3：height 指定PopupWindow的height
        mPopupWindow = new PopupWindow(popupWindow, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //设置动画两种方式：动画效果可以参考该网址 http://blog.csdn.net/xiaanming/article/details/8997260
        //方式1：xml配置文件
        //        mPopupWindow.setAnimationStyle(R.anim.popupwindow_enter);
        //方式2：直接设置该popupWindow中的View的动画
        //        setPopupAnimation(popupWindow);

        mPopupWindow.setFocusable(true); //这里很重要，设置该popupWindow可以获取焦点，不然无法响应点击事件

        switch (type) {
            case 1:
                //方式2：直接设置该popupWindow中的View的动画
                setPopupAnimation(popupWindow);
                //6.0无效
                mPopupWindow.setOutsideTouchable(false);//设置点击外面不可以消失~注意该效果在设置背景的情况下是无效的
                break;
            case 2:
                //方式1：xml配置文件
                mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
                mPopupWindow.setOutsideTouchable(true);//设置点击外面可以消失~注意则必须要设置该popupWindow背景才有效
                break;
            case 3:
                //方式2：直接设置该popupWindow中的View的动画
                setPopupAnimation(popupWindow);
                //6.0无效
                mPopupWindow.setOutsideTouchable(false);//设置点击外面不可以消失~注意该效果在设置背景的情况下是无效的
                break;
            case 4:
                //方式1：xml配置文件
                mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
                mPopupWindow.setOutsideTouchable(true);//设置点击外面可以消失~注意则必须要设置该popupWindow背景才有效
                break;
            case 5:
                mMainView.setVisibility(mIsClick5 ? View.VISIBLE : View.GONE);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, dip2px(48));
                mMainView.measure(w, h);
                int mMainViewWidth = mMainView.getMeasuredWidth();//获取测量宽度px
                int width = (mTv.getWidth() - mMainViewWidth) / 2;//获取x轴偏移量px
                params.setMargins(mTv.getLeft() + width, mTv.getHeight(), 0, 0);
                mMainView.setLayoutParams(params);//设置位置
                if (mIsClick5)
                    break;
            default:
                //方式1：xml配置文件
                mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
                mPopupWindow.setOutsideTouchable(true);//设置点击外面可以消失~注意则必须要设置该popupWindow背景才有效
                break;

        }
    }



    /**
     * 设置组合动画
     *
     * @param paramView
     */
    private void setPopupAnimation(View paramView) {
        //透明度动画
        AlphaAnimation localAlphaAnimation = new AlphaAnimation(0.0F, 1.0F);
        localAlphaAnimation.setInterpolator(new Interpolator() {
            public float getInterpolation(float paramFloat) {
                return 10.0F * paramFloat;
            }
        });
        localAlphaAnimation.setDuration(800L);//动画持续时长
        //缩放动画:
        // 参数：
        // 1.为x轴起始缩放度 2.为x结束缩放度
        // 3.为y起始缩放度 4.为y结束缩放度
        // 5.为相对x轴类型为顶部 6.为该类型上起始度（0.5f为中间位置）
        // 7.为相对y轴类型 8.为该类型起始位置（0F为原位置）
        ScaleAnimation localScaleAnimation = new ScaleAnimation(0F, 1.0F, 0F, 1.0F, Animation.ZORDER_TOP, 0.5F, Animation.ZORDER_TOP, 0F);
        localScaleAnimation.setDuration(500L);//动画持续时长
        AnimationSet localAnimationSet = new AnimationSet(true);
        localAnimationSet.addAnimation(localScaleAnimation);
        localAnimationSet.addAnimation(localAlphaAnimation);
        paramView.startAnimation(localAnimationSet);
    }

    /**
     * dip与px之间转换
     *
     * @param dipValue
     * @return
     */
    private int dip2px(float dipValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
