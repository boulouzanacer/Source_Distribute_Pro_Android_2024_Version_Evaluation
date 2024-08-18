package com.safesoft.proapp.distribute.adapters.proximityAdapter;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.safesoft.proapp.distribute.R;

import java.util.List;

/**
 * Created by Administrator on 2015/6/9.
 */
public class BluetoothDeviceAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<BluetoothDevice> mList;
    private final LayoutInflater mInflater;
    private final Activity mActivity;

    public BluetoothDeviceAdapter(Context context, Activity activity, List<BluetoothDevice> list) {
        this.mContext = context;
        this.mActivity = activity;
        this.mList = list;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView tvName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.bluetooth_device_item, null);
            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.tv_bluetooth_device_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BluetoothDevice bluetoothDevice = mList.get(position);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                return convertView;
            }
        }

        if (TextUtils.isEmpty(bluetoothDevice.getName())) {
            holder.tvName.setText(bluetoothDevice.getAddress());
        } else {

            holder.tvName.setText(mList.get(position).getName());
        }

        return convertView;
    }
}
