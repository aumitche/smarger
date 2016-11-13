package me.cfanguo.smarger;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


/**
 * Created by fanguo on 2016-11-12.
 */

public class DeviceList extends Activity {
  // Return Intent extra
  public static String EXTRA_DEVICE_ADDRESS = "device_axxxddress";

  // Member fields
  private BluetoothAdapter mBtAdapter;
  private ArrayAdapter<String> mPairedDevicesArrayAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Setup the window
    setContentView(R.layout.device_list);

    // Initialize array adapters. One for already paired devices and
    // one for newly discovered devices
    mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.devices);

    // Find and set up the ListView for paired devices
    ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
    pairedListView.setAdapter(mPairedDevicesArrayAdapter);
    pairedListView.setOnItemClickListener(mDeviceClickListener);

    // Get the local Bluetooth adapter
    mBtAdapter = BluetoothAdapter.getDefaultAdapter();

    // Get a set of currently paired devices
    Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

    // If there are paired devices, add each one to the ArrayAdapter
    if (pairedDevices.size() > 0) {
      findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
      for (BluetoothDevice device : pairedDevices) {
        mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
      }
    } else {
      String noDevices = getResources().getText(R.string.none_paired).toString();
      mPairedDevicesArrayAdapter.add(noDevices);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    // Make sure we're not doing discovery anymore
    if (mBtAdapter != null) {
      mBtAdapter.cancelDiscovery();
    }
  }


  // The on-click listener for all devices in the ListViews
  private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
    public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
      String noDevicesPaired = getResources().getText(R.string.none_paired).toString();
      String noDevicesFound = getResources().getText(R.string.none_found).toString();

      // Cancel discovery because it's costly and we're about to connect
      mBtAdapter.cancelDiscovery();

      String info = ((TextView) v).getText().toString();

      if ( (info != noDevicesPaired)  &&  (info != noDevicesFound) ){

        if (info.length() >= 17) {
          // Get the device MAC address, which is the last 17 chars in the View
          String address = info.substring(info.length() - 17);

          // Create the result Intent and include the MAC address
          Intent intent = new Intent();
          intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

          // Set result and finish this Activity
          setResult(Activity.RESULT_OK, intent);
        }
        else {
          setResult(Activity.RESULT_CANCELED);
        }

        finish();
      }
    }
  };
}


