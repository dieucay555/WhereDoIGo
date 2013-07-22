package com.archerabi.wheredoigo;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.LinearLayout;

public class MainActivity extends Activity implements SensorEventListener,LocationListener{

	private SensorManager mSensorManager;
	private Sensor mMagneticSensor;
	private Sensor mAccelSensor;
	private LocationManager mLocationManager;
	
	private float[] latestAccelValues = null;
	private float[] latestMagValues = null;
	
	private long lastTimeStamp;
	
	private DirectionPointerCanvas directionCanvas ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.root_layout);
		directionCanvas = new DirectionPointerCanvas(getApplicationContext());
		layout.addView(directionCanvas);
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mAccelSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
		mLocationManager.removeUpdates(this);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mMagneticSensor, SensorManager.SENSOR_DELAY_UI);
		mSensorManager.registerListener(this, mAccelSensor,SensorManager.SENSOR_DELAY_UI);
		lastTimeStamp = System.currentTimeMillis();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(event.sensor == mAccelSensor){
			latestAccelValues = event.values.clone();
		}else if(event.sensor == mMagneticSensor){
			latestMagValues = event.values.clone();
		}
		if( latestMagValues != null && latestAccelValues != null){
			float[] rotationMatrix = new float[16];
			SensorManager.getRotationMatrix(rotationMatrix, null, latestAccelValues, latestMagValues);
			float[] orientation = new float[3];
			SensorManager.getOrientation(rotationMatrix, orientation);
			if(System.currentTimeMillis() - lastTimeStamp > 30){
//				Log.d(getClass().getName(),"Rotation around z = "+String.valueOf(orientation[0] * 180/Math.PI));
				lastTimeStamp = System.currentTimeMillis();
				directionCanvas.setAngle((int) (- orientation[0] * 180 / Math.PI));
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d(getClass().getName(),"Lat = "+String.valueOf(location.getLatitude())+" Long = "+String.valueOf(location.getLongitude()));
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d(getClass().getName(),provider + " is DISABLED");
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.d(getClass().getName(),provider + " is ENABLED");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	} 

}
