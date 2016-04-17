package proj.colorpicker;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import proj.colorpicker.ble.BlunoLibrary;

public class MainActivity extends BlunoLibrary implements ColorPickerListener{

  public static final String APP_CODE = "ColorPicker";



  private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

  int red, green, blue;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    onCreateProcess();
    serialBegin(115200);

    setContentView(R.layout.activity_main);

    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
      if( ContextCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission( this,
        Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
        ActivityCompat.requestPermissions(this,
          new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
            , Manifest.permission.ACCESS_FINE_LOCATION
            , Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
      }
    }

    ColorPicker colorPicker = (ColorPicker) findViewById(R.id.colorPicker);
    colorPicker.setListener(this);

  }

  @Override
  public void newColorPicked(int red, int green, int blue) {
    Log.d(MainActivity.APP_CODE, "newColorPicked");

    if(this.red != red && this.green != green && this.blue != blue) {

      this.red = red;
      this.green = green;
      this.blue = blue;

      StringBuilder strData = new StringBuilder();
      strData.append(red)
        .append(",")
        .append(green)
        .append(",")
        .append(blue)
        .append("\r\n");

      Log.d(MainActivity.APP_CODE, "serial send: "+strData.toString());
      serialSend(strData.toString());
      Log.d(APP_CODE, strData.toString());
    }
  }

  @Override
  public boolean onCreateOptionsMenu( Menu menu ) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main_nav, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected( MenuItem item ) {
    int id = item.getItemId();

    if( id == R.id.action_bluetooth_connect ) {
      buttonScanOnClickProcess();
      return true;
    }

    return super.onOptionsItemSelected( item );
  }

  @Override
  public void onSerialReceived(String theString) {

  }

  protected void onResume() {
    super.onResume();
    onResumeProcess();
  }

  @Override
  protected void onPause() {
    super.onPause();
    onPauseProcess();
  }

  protected void onStop() {
    super.onStop();
    onStopProcess();                                                        //onStop Process by BlunoLibrary
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    onDestroyProcess();                                                        //onDestroy Process by BlunoLibrary
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
  @Override
  public void onRequestPermissionsResult( int requestCode,
                                          String permissions[], int[] grantResults ) {
    switch( requestCode ) {
      case PERMISSION_REQUEST_COARSE_LOCATION: {
        if( grantResults[ 0 ] == PackageManager.PERMISSION_GRANTED ) {
          Log.d( APP_CODE, "coarse location permission granted" );
        } else {
          final AlertDialog.Builder builder = new AlertDialog.Builder( this );
          builder.setTitle( "Functionality limited" );
          builder.setMessage( "Since location access has not been granted, this app will not be able to discover beacons when in the background." );
          builder.setPositiveButton( android.R.string.ok, null );
          builder.setOnDismissListener( new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss( DialogInterface dialog ) {
            }

          } );
          builder.show();
        }
        return;
      }
    }
  }


    @Override
    public void onConectionStateChange( connectionStateEnum theConnectionState ) {//Once connection state changes, this function will be called
        switch( theConnectionState ) {                                            //Four connection state
            case isConnected:
                showSnackBarToast(this,"Bluetooth connected");
                Log.d(APP_CODE, "Connected");
                break;
            case isConnecting:
                showSnackBarToast(this,"Bluetooth Connecting");
                Log.d( APP_CODE, "Connecting" );
                break;
            case isToScan:
                showSnackBarToast(this,"Bluetooth to scan");
                Log.d( APP_CODE, "Scan" );
                break;
            case isScanning:
                showSnackBarToast(this,"Bluetooth scanning");
                Log.d( APP_CODE, "Scanning" );
                break;
            case isDisconnecting:
                showSnackBarToast(this,"Bluetooth is disconnecting");
                Log.d( APP_CODE, "isDisconnecting" );
                break;
            default:
                break;
        }
    }

    public void showSnackBarToast(Context context, String message){
      int currentapiVersion = android.os.Build.VERSION.SDK_INT;
      if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
        // Do something for lollipop and above versions
        Snackbar.make(((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
      } else{
        // do something for phones running an SDK before lollipop
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
      }
    }
}
