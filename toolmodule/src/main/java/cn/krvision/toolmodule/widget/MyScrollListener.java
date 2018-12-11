package cn.krvision.toolmodule.widget;

import android.util.Log;
import android.widget.AbsListView;

/**
 * 创建日期：2018/7/23 on 下午3:41
 * 描述: 自定义View-MyScrollListener  listview滑动监听
 * 作者: gaoqiong
 */
public class MyScrollListener implements AbsListView.OnScrollListener {
    private LoadCallBack callBack;
    private boolean onScrollStateChanged;

    public MyScrollListener(LoadCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        Log.e("onScroll=", " onScrollStateChanged");
        onScrollStateChanged = true;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int lastItemid = view.getLastVisiblePosition(); // 获取当前屏幕最后Item的ID


        if ((lastItemid + 1) == totalItemCount && onScrollStateChanged) { // 达到数据的最后一条记录
            Log.e("onScroll=", lastItemid + " " + totalItemCount);
            if (totalItemCount > 0) {
                if (callBack != null) {
                    callBack.load();
                }
            }
        }
    }

    public interface LoadCallBack {
        void load();
    }
}
