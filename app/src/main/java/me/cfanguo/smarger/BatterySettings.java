package me.cfanguo.smarger;

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

public class BatterySettings extends AppCompatActivity {
  SeekBar skStart, skStop;
  EditText edStart, edStop;
  TextView confirmMsg;
  int startVal, stopVal;
  public static final int sendON = 1, sendOFF = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_battery_settings);

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

  /** Called when the user clicks the Confirm button */
  public void sendMessage(View view) {
    String startCheck = edStart.getText().toString();
    String stopCheck = edStop.getText().toString();

    if (!TextUtils.isEmpty(startCheck) &&
        !TextUtils.isEmpty(stopCheck) &&
        TextUtils.isDigitsOnly(stopCheck) &&
        TextUtils.isDigitsOnly(startCheck)) {
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
      TextView testText=(TextView) findViewById(R.id.confirmmsg);

      if (level >= stopVal) {
        testText.setText(
            "Level: " + level + "\n" +
            "OFF: "+ sendOFF + "\n");
      }
      else if (level <= startVal) {}
      testText.setText(
              "Level: " + level + "\n" +
              "ON: "+ sendON + "\n");
    }
  };
}
