package cn.krvision.toolmodule.utils;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import cn.krvision.toolmodule.R;

/**
 * 创建日期：2018/7/9 on 下午1:28
 * 描述: 所有弹窗类
 * 作者: liangyang
 */
public class DialogUtils {


    private DialogUtils() {
    }

    public static DialogUtils getInstance() {
        return SingletonHolder.sInstance;
    }

    /**
     * 静态内部类
     */
    private static class SingletonHolder {
        private static final DialogUtils sInstance = new DialogUtils();
    }


    /**
     * 修改用户名称
     */
    public void reviseNickName(final Activity context, final int type, final ReviseNickNameInterface dialogInterface) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = context.getLayoutInflater().inflate(R.layout.dialog_edit_nick_name, null);
        TextView tvTitle = dialogView.findViewById(R.id.tv_title);
        TextView tvNo = dialogView.findViewById(R.id.tv_no);
        TextView tvYes = dialogView.findViewById(R.id.tv_yes);
        final EditText etHistoryCustom = dialogView.findViewById(R.id.et_history_custom);

        tvTitle.setText("请输入昵称");
        etHistoryCustom.setHint("16个字以内");

        etHistoryCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etHistoryCustom.setText("");
            }
        });
        builder.setView(dialogView);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                dialogInterface.reviseNickNameInterfaceNo();
            }
        });

        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = etHistoryCustom.getText().toString();

                if (newName.length() > 0) {
                    if (!MyUtils.containsEmoji(newName)) {
                        if (newName.length() <= 16) {
                            alertDialog.dismiss();
                            //修改用户昵称
                            dialogInterface.reviseNickNameInterfaceYes(etHistoryCustom, newName, type);
                        } else {
                            MyUtils.Toast(context,"用户名长度不能超过16个字");
                            etHistoryCustom.setText("");
                            etHistoryCustom.requestFocus();
                        }
                    } else {
                        MyUtils.Toast(context,"视氪导航暂不支持输入表情");
                        etHistoryCustom.setText("");
                        etHistoryCustom.requestFocus();
                    }
                } else {
                    MyUtils.Toast(context,"用户名不能为空");
                    etHistoryCustom.setText("");
                    etHistoryCustom.requestFocus();
                }
            }
        });

        alertDialog.show();
    }


    /**
     * 从指定位置弹出
     */
    private PopupWindow popupWindow;

    public void showSelectPicPop(View viewParent, Activity context, int dpValue, final FactorySortInterface factorySortInterface) {
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        LinearLayout layout = (LinearLayout) context.getLayoutInflater().inflate(R.layout.custom_spnnier_layout, null);
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
//        popupWindow.showAtLocation(viewParent, Gravity.TOP | Gravity.START, location[0], location[1] + MyUtils.dp2px(context,dpValue));
        popupWindow.showAtLocation(viewParent, Gravity.TOP | Gravity.START, location[0], location[1] + MyUtils.dp2px(context, dpValue));

        //添加按键事件监听
        TextView tv_spinner1 = layout.findViewById(R.id.tv_spinner1);
        TextView tv_spinner2 = layout.findViewById(R.id.tv_spinner2);
        TextView tv_spinner3 = layout.findViewById(R.id.tv_spinner3);

        tv_spinner1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                factorySortInterface.success("全部内容", 0);
            }
        });

        tv_spinner2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                factorySortInterface.success("所有需求", 1);
            }
        });

        tv_spinner3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                factorySortInterface.success("所有项目", 2);
            }
        });
    }


    public interface ReviseNickNameInterface {
        void reviseNickNameInterfaceNo();

        void reviseNickNameInterfaceYes(EditText et_history_custom, String show_name, int type);
    }

    public interface FactorySortInterface {
        void success(String s, int position);
    }





}
