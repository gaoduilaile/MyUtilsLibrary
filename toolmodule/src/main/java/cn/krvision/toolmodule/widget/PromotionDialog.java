package cn.krvision.toolmodule.widget;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import cn.krvision.toolmodule.R;

/**
 * 2019/1/4  GaoQiong
 * 自定义弹窗dialog
 */
public class PromotionDialog extends Dialog implements View.OnClickListener {

    private Activity context;
    private Button tv_assemble_friend;
    private ImageView tvNO;

    public PromotionDialog(Activity context) {
        super(context);
    }

    public PromotionDialog(Activity context, int theme) {
        super(context, theme);
        this.context = context;
    }

    protected PromotionDialog(Activity context, boolean cancelable,
                              OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.promotion_dialog);

        tv_assemble_friend = findViewById(R.id.tv_assemble_friend);
        tvNO = findViewById(R.id.tv_no);
        tvNO.requestFocus();
        tv_assemble_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirmClickListener.yesClick();
            }
        });
        tvNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirmClickListener.noClick();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }


    public interface OnConfirmClickListener {
        void yesClick();

        void noClick();
    }

    private OnConfirmClickListener onConfirmClickListener;

    public void setOnConfirmClickListener(OnConfirmClickListener onConfirmClickListener) {
        this.onConfirmClickListener = onConfirmClickListener;
    }


    /*   final PromotionDialog dialog = new PromotionDialog(mContext,R.style.dialog);
                        dialog.setCancelable(true);
                        dialog.setOnConfirmClickListener(new PromotionDialog.OnConfirmClickListener() {
                            @Override
                            public void yesClick() {
                                dialog.dismiss();
                            }

                            @Override
                            public void noClick() {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();


                        */

}
