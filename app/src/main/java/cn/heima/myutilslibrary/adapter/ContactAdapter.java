package cn.heima.myutilslibrary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.heima.myutilslibrary.R;
import cn.heima.myutilslibrary.contacts.LinphoneContact;

/**
 * Created by gaoqiong on 2018/12/20 13:51
 * Description:$value$
 */
public class ContactAdapter extends BaseAdapter {
    private List<LinphoneContact> LinphoneContacts = new ArrayList<>();
    private Context context;

    public ContactAdapter(Context context, List<LinphoneContact> LinphoneContacts) {
        this.LinphoneContacts = LinphoneContacts;
        this.context = context;
    }

    @Override
    public int getCount() {
        return LinphoneContacts.size();
    }

    @Override
    public LinphoneContact getItem(int position) {
        return LinphoneContacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.contact_item_layout, null);
            viewHolder.tvName = convertView.findViewById(R.id.tv_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        LinphoneContact contact = (LinphoneContact) getItem(position);

        viewHolder.tvName.setText(contact.getFullName());

        return convertView;
    }

    private class ViewHolder {
        private TextView tvName;
    }
}
