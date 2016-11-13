package me.cfanguo.smarger;

    import android.app.Activity;
    import android.bluetooth.BluetoothAdapter;
    import android.bluetooth.BluetoothDevice;
    import android.content.BroadcastReceiver;
    import android.content.Context;
    import android.content.Intent;
    import android.content.IntentFilter;
    import android.os.BatteryManager;
    import android.os.Bundle;
    import android.support.v7.app.AppCompatActivity;
    import android.text.TextUtils;
    import android.view.View;
    import android.widget.EditText;
    import android.widget.SeekBar;
    import android.widget.TextView;
    import android.view.Menu;
    import android.view.MenuItem;
    import android.view.MenuInflater;
    import android.widget.Toast;

    import java.util.*;

public class BatterySettings extends AppCompatActivity {
  SeekBar skStart, skStop;
  EditText edStart, edStop;
  TextView confirmMsg;
  int startVal, stopVal;
  public static final int sendON = 1, sendOFF = 0;

  // Intent request codes
  private static final int REQUEST_CONNECT_DEVICE = 1;
  private static final int REQUEST_ENABLE_BT = 2;

  private BluetoothAdapter mBluetoothAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_battery_settings);

    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (mBluetoothAdapter != null){
      Toast.makeText(getApplicationContext(), "Bluetooth available", Toast.LENGTH_LONG).show();
      if (!mBluetoothAdapter.isEnabled()) {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
      }
      else {
        setUpCharger();
      }
    }
    else {
      Toast.makeText(getApplicationContext(), "Bluetooth unavailable", Toast.LENGTH_LONG).show();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu items for use in the action bar
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_activity_battery_settings, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle item selection
    switch (item.getItemId()) {
      case R.id.select_bluetooth:
        bluetoothPopup(item.getActionView());
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void setUpCharger() {
    skStart=(SeekBar) findViewById(R.id.startseek);
    skStop=(SeekBar) findViewById(R.id.stopseek);
    edStart=(EditText) findViewById(R.id.startinput);
    edStop=(EditText) findViewById(R.id.stopinput);
    confirmMsg=(TextView) findViewById(R.id.confirmmsg);

    this.registerReceiver(this.batteryInfoReceiver,	new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

    skStart.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // TODO Auto-generated method stub
        edStart.setText(String.valueOf(progress));
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
      }
    });
    skStop.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // TODO Auto-generated method stub
        edStop.setText(String.valueOf(progress));
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
      }
    });
    edStart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        String valCheck = edStart.getText().toString();
        if (!TextUtils.isEmpty(valCheck) && TextUtils.isDigitsOnly(valCheck)){
          skStart.setProgress(Integer.parseInt(valCheck));
        }
      }
    });
    edStop.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        String valCheck = edStop.getText().toString();
        if (!TextUtils.isEmpty(valCheck) && TextUtils.isDigitsOnly(valCheck)){
          skStop.setProgress(Integer.parseInt(valCheck));
        }
      }
    });
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_ENABLE_BT){
      // When the request to enable Bluetooth returns
      if (resultCode == Activity.RESULT_OK) {
        // Bluetooth is now enabled, setup the charger
        setUpCharger();
      } else {
        // User did not enable Bluetooth or an error occurred
        Toast.makeText(getApplicationContext(), "Error: no Bluetooth enabled", Toast.LENGTH_SHORT).show();
      }
    }
  }

  public void bluetoothPopup(View view) {
    Intent intent = new Intent(this, DeviceList.class);
    startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
  }

  /** Called when the user clicks the Confirm button */
  public void confirmValues(View view) {
    String startCheck = edStart.getText().toString();
    String stopCheck = edStop.getText().toString();

    if (!TextUtils.isEmpty(startCheck) &&
        !TextUtils.isEmpty(stopCheck) &&
        TextUtils.isDigitsOnly(stopCheck) &&
        TextUtils.isDigitsOnly(startCheck) &&
        (Integer.parseInt(stopCheck) > Integer.parseInt(startCheck)) &&
        (Integer.parseInt(stopCheck) <= 100)) {
      startVal = Integer.parseInt(startCheck);
      stopVal = Integer.parseInt(stopCheck);
      confirmMsg.setText("Your phone will start charging once it drops below "
          + Integer.toString(startVal)
          +"% and until it reaches "
          + Integer.toString(stopVal)
          +"%");
    }
    else {
      confirmMsg.setText("Please ensure you have entered valid values");
    }
  }

  /** Checks the level vs the suggested value, and sends a signal via bluetooth*/
  private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {

      int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
      int plugged= intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,0);
      TextView testText=(TextView) findViewById(R.id.confirmmsg);

      if (level >= stopVal && plugged != 0) {
        testText.setText(
            "Level: " + level + "\n" +
                "OFF: "+ stopVal + "\n");
      }
      else if (level <= startVal && plugged == 0) {
        testText.setText(
            "Level: " + level + "\n" +
                "ON: "+ startVal + "\n");
      }
    }
  };
}

