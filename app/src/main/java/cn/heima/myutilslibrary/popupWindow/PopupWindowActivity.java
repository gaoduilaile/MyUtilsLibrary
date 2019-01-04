package cn.heima.myutilslibrary.popupWindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;

import cn.heima.myutilslibrary.R;
import cn.krvision.toolmodule.base.ARouterPath;
import cn.krvision.toolmodule.base.BaseActivity;
import cn.krvision.toolmodule.utils.MyUtils;
@Route(path= ARouterPath.PopupWindowActivity)
public class PopupWindowActivity extends BaseActivity {

    private PopupWindow popupWindow;
    private TextView tvSort;
    private LinearLayout ll1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_window);
        tvSort = findViewById(R.id.tv_sort);
        ll1 = findViewById(R.id.ll1);
    }

    public void sortClick(View view) {
        showSelectPicPop2(tvSort);
    }

    public void bottomClick(View view) {
        showSelectPicPop(ll1);
    }

    /**
     * 从页面底部弹出
     *
     * @param view
     */

    private void showSelectPicPop(View view) {
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.feedback_search_layout, null);
        popupWindow = new PopupWindow(layout,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);


        //点击空白处时，隐藏掉pop窗口
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //添加弹出、弹入的动画
        popupWindow.setAnimationStyle(R.style.Popupwindow);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        popupWindow.showAtLocation(view, Gravity.LEFT | Gravity.BOTTOM, 0, -location[1]);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            // 在dismiss中恢复透明度
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow()
                        .getAttributes();
                lp.alpha = 1f;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
            }
        });

        //添加按键事件监听
        TextView tv_send = layout.findViewById(R.id.tv_send);
        final EditText etHistoryCustom = layout.findViewById(R.id.et_search);

        tv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = etHistoryCustom.getText().toString();
                if (message.length() > 0) {

                } else {

                }
            }
        });
    }


    /**
     * 从指定位置弹出
     */
    private void showSelectPicPop2(View viewParent) {
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.custom_spnnier_layout, null);
        popupWindow = new PopupWindow(layout,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        //点击空白处时，隐藏掉pop窗口
        popupWindow.setFocusable(true);
        // 如果不设置PopupWindow的背景，有些版本就会出现一个问题：无论是点击外部区域还是Back键都无法dismiss弹框
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        //添加弹出、弹入的动画
        popupWindow.setAnimationStyle(R.style.Popupwindow);

        //获取tvsort 在屏幕的坐标
        int[] location = new int[2];
        viewParent.getLocationOnScreen(location);

        //设置好参数之后再show
        popupWindow.showAtLocation(viewParent, Gravity.TOP | Gravity.START, location[0], location[1] + MyUtils.dp2px(PopupWindowActivity.this, 49));

        //添加按键事件监听
        TextView tv_spinner1 = layout.findViewById(R.id.tv_spinner1);
        TextView tv_spinner2 = layout.findViewById(R.id.tv_spinner2);
        TextView tv_spinner3 = layout.findViewById(R.id.tv_spinner3);

        tv_spinner1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tv_spinner2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tv_spinner3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    /**
     * 计算出来的位置，y方向就在anchorView的上面和下面对齐显示，x方向就是与屏幕右边对齐显示
     * 如果anchorView的位置有变化，就可以适当自己额外加入偏移来修正
     *
     * @param anchorView
     * @param contentView
     * @return window显示的左上角的xOff, yOff坐标
     */
    private static int[] calculatePopWindowPos(final View anchorView, final View contentView) {
        final int windowPos[] = new int[2];
        final int anchorLoc[] = new int[2];
        // 获取锚点View在屏幕上的左上角坐标位置
        anchorView.getLocationOnScreen(anchorLoc);
        final int anchorHeight = anchorView.getHeight();
        // 获取屏幕的高宽
        final int screenHeight = getScreenHeight(anchorView.getContext());
        final int screenWidth = getScreenWidth(anchorView.getContext());
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        // 计算contentView的高宽
        final int windowHeight = contentView.getMeasuredHeight();
        final int windowWidth = contentView.getMeasuredWidth();
        // 判断需要向上弹出还是向下弹出显示
        final boolean isNeedShowUp = (screenHeight - anchorLoc[1] - anchorHeight < windowHeight);
        if (isNeedShowUp) {
            windowPos[0] = screenWidth - windowWidth;
            windowPos[1] = anchorLoc[1] - windowHeight;
        } else {
            windowPos[0] = screenWidth - windowWidth;
            windowPos[1] = anchorLoc[1] + anchorHeight;
        }
        return windowPos;
    }

    /**
     * 获取屏幕高度(px)
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕宽度(px)
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

}
