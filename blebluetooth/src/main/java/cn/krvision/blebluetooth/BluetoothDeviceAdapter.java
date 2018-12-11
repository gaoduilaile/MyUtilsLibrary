package cn.krvision.blebluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * 创建日期：2018/8/22 on 上午10:56
 * 描述: 扫描到的蓝牙设备
 * 作者: gaoqiong
 */
public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.ViewHolder> {

    private List<BluetoothDevice> mList;
    private Context mContext;
    private BluetoothDeviceAdapterFunc func;

    public BluetoothDeviceAdapter(Context context, List<BluetoothDevice> mList, BluetoothDeviceAdapterFunc func) {
        this.mList = mList;
        mContext = context;
        this.func = func;
    }

    public void update(List<BluetoothDevice> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.rv_ble_device_item, null);
        ViewHolder viewHolder = new ViewHolder(convertView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final int adapterPosition = holder.getAdapterPosition();
        BluetoothDevice device = mList.get(adapterPosition);
        holder.tvName.setText(device.getName());
        holder.tvAddress.setText(device.getAddress());
        holder.llAdapterItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                func.itemClick(adapterPosition);
            }
        });
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
        TextView tvName;
        TextView tvAddress;
        LinearLayout llAdapterItem;

        ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tv_name);
            tvAddress = view.findViewById(R.id.tv_address);
            llAdapterItem = view.findViewById(R.id.ll_adapter_item);
        }
    }

    public interface BluetoothDeviceAdapterFunc {
        void itemClick(int position);
    }

}
