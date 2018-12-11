package cn.krvision.blebluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class PrimaryDeviceActivity extends AppCompatActivity implements BleServiceInterface {
    private RecyclerView recyclerView;
    private ArrayList<BluetoothDevice> mList = new ArrayList<>();
    private BluetoothDeviceAdapter adapter;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary_device);
        BleService.getInstance().initBleServiceInterface(this);
        context = this;
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new BluetoothDeviceAdapter(this, mList,
                new BluetoothDeviceAdapter.BluetoothDeviceAdapterFunc() {
                    @Override
                    public void itemClick(int position) {
                        BluetoothDevice device = mList.get(position);

                        SendBroadcastDataUtils.sendData(context, Constants.ACTION_CONNECTED, device.getName(), device.getAddress());

                    }
                });
    }

    public void startScan(View view) {
        SendBroadcastDataUtils.sendData(context, Constants.ACTION_SCAN);
    }

    @Override
    public void OnCharacteristicWriteSuccess() {

    }

    @Override
    public void onLeScanSuccess(BluetoothDevice device) {
        mList.add(device);
        adapter.update(mList);
    }

}
