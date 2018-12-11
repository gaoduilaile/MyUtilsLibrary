package cn.heima.myutilslibrary.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.heima.myutilslibrary.R;
import cn.heima.myutilslibrary.bean.ConmentStandardBean;
import cn.krvision.toolmodule.GlideUtils;


/**
 * 适配器 一级列表菜单
 * gaoqiong
 */
public class ListAdapterProjectDetailComment extends RecyclerView.Adapter<ListAdapterProjectDetailComment.ViewHolder> {

    private List<ConmentStandardBean> mList;
    private Context mContext;
    private ListAdapterFactoryCommentFunc func;

    public ListAdapterProjectDetailComment(Context context, List<ConmentStandardBean> mList, ListAdapterFactoryCommentFunc func) {
        this.mList = mList;
        mContext = context;
        this.func = func;
    }

    public void update(List<ConmentStandardBean> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_comment_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(convertView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ListAdapterProjectDetailComment.ViewHolder holder, final int position) {
        ConmentStandardBean orderListBean = mList.get(position);

        if (orderListBean.isSecond()){
            holder.ll1.setVisibility(View.GONE);
            holder.ll2.setVisibility(View.VISIBLE);

            SpannableString spannableString1 = new SpannableString(orderListBean.getComment_parent_nickname()+orderListBean.getComment_content());
            spannableString1.setSpan(new ForegroundColorSpan(Color.parseColor("#9B9CAD")), 0, orderListBean.getComment_parent_nickname().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.tv_comment2.setText(spannableString1);

            GlideUtils.getInstance().loadImage(mContext,R.drawable.person_icon,orderListBean.getComment_image_url(),holder.iv_header2);



            holder.tv_name2.setText(orderListBean.getComment_nickname() + "");
            holder.tv_name.setContentDescription(orderListBean.getComment_nickname() + "说");
            holder.tv_title2.setText(orderListBean.getComment_title());
            holder.tv_time2.setText(orderListBean.getComment_time());

        }else {
            holder.ll1.setVisibility(View.VISIBLE);
            holder.ll2.setVisibility(View.GONE);
            holder.tv_comment.setText(orderListBean.getComment_content());

            GlideUtils.getInstance().loadImage(mContext,R.drawable.person_icon,orderListBean.getComment_image_url(),holder.iv_header);

            holder.tv_name.setText(orderListBean.getComment_nickname() + "");
            holder.tv_name.setContentDescription(orderListBean.getComment_nickname() + "说");
            holder.tv_title.setText(orderListBean.getComment_title());
            holder.tv_time.setText(orderListBean.getComment_time());
            holder.tv_num.setText("回复（" + orderListBean.getComment_number() + "）");
        }

        if (position == getItemCount() - 1) {//已经到达列表的底部
            func.loadMore();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_header;
        TextView tv_comment;
        TextView tv_name;
        TextView tv_title;
        TextView tv_time;
        TextView tv_num;
        LinearLayout ll1;

        ImageView iv_header2;
        TextView tv_comment2;
        TextView tv_name2;
        TextView tv_title2;
        TextView tv_time2;
        LinearLayout ll2;


        ViewHolder(View view) {
            super(view);
            iv_header = view.findViewById(R.id.iv_header);
            tv_comment = view.findViewById(R.id.tv_comment);
            tv_title = view.findViewById(R.id.tv_title);
            tv_name = view.findViewById(R.id.tv_name);
            tv_time = view.findViewById(R.id.tv_time);
            tv_num = view.findViewById(R.id.tv_num);
            ll1 = view.findViewById(R.id.ll1);


            iv_header2 = view.findViewById(R.id.iv_header2);
            tv_comment2 = view.findViewById(R.id.tv_comment2);
            tv_title2 = view.findViewById(R.id.tv_title2);
            tv_name2 = view.findViewById(R.id.tv_name2);
            tv_time2 = view.findViewById(R.id.tv_time2);
            ll2 = view.findViewById(R.id.ll2);


            ll1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    func.itemClick(getLayoutPosition());
                }
            });
        }
    }
}
