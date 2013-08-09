/**
 * 
 */
package com.archerabi.wheredoigo;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.archerabi.wheredoigo.views.DirectionPointerCanvas;

/**
 * @author gautamichitteti
 * 
 */
public class DirectionPointerActivity extends Activity implements SensorEventListener, LocationListener {

	private SensorManager mSensorManager;
	private Sensor mMagneticSensor;
	private Sensor mAccelSensor;


	private float[] mGravity = new float[3];
	private float[] mGeomagnetic = new float[3];

	private DirectionPointerCanvas dirPointerCanvas;

	private Location destinationLocation;

	private Location lastKnownLocation;

	private LocationManager mLocationManager;

	private String destinationName;
	/**
	 * @return the destinationLocation
	 */
	public Location getDestinationLocation() {
		return destinationLocation;
	}

	/**
	 * @param destinationLocation
	 *            the destinationLocation to set
	 */
	public void setDestinationLocation(Location destinationLocation) {
		this.destinationLocation = destinationLocation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.direction_pointer);
		((TextView)findViewById(R.id.destination_label)).setText("asdfdsfs");
		dirPointerCanvas = (DirectionPointerCanvas) findViewById(R.id.direction_canvas);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mAccelSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		double lat = getIntent().getExtras().getDouble("LATITUDE");
		double longi = getIntent().getExtras().getDouble("LONGITUDE");
		setDestinationName( getIntent().getExtras().getString("DESTINATION") );
		destinationLocation = new Location("");
		destinationLocation.setLatitude(lat);
		destinationLocation.setLongitude(longi);
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
		mLocationManager.removeUpdates(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mMagneticSensor, SensorManager.SENSOR_DELAY_UI);
		mSensorManager.registerListener(this, mAccelSensor, SensorManager.SENSOR_DELAY_UI);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		final float alpha = 0.77f;

		synchronized (this) {
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				mGravity[0] = alpha * mGravity[0] + (1 - alpha) * event.values[0];
				mGravity[1] = alpha * mGravity[1] + (1 - alpha) * event.values[1];
				mGravity[2] = alpha * mGravity[2] + (1 - alpha) * event.values[2];
			}

			if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
				mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha) * event.values[0];
				mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha) * event.values[1];
				mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha) * event.values[2];
			}

			float R[] = new float[9];
			float I[] = new float[9];
			boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
			if (success) {
				float orientation[] = new float[3];
				SensorManager.getOrientation(R, orientation);
				float azimuth = (float) Math.toDegrees(orientation[0]); // orientation
				azimuth = (azimuth + 360) % 360;
				 if(lastKnownLocation != null){
					int bearing = (int) lastKnownLocation.bearingTo(destinationLocation);
					if( bearing < 0 ){
						bearing = 360 + bearing;
					}
					float directionAngle = bearing- azimuth;
					String print = "azimuth (deg): " + azimuth + "\t\t\tBearing is " + bearing + "\t\t\t\tDirection angle is "+directionAngle;
					Log.d(getClass().getName(),print);
					dirPointerCanvas.setAngle((int)directionAngle);
				}
			}
		}
		// if (event.sensor == mAccelSensor) {
		// latestAccelValues = event.values.clone();
		// } else if (event.sensor == mMagneticSensor) {
		// latestMagValues = event.values.clone();
		// }
		// if (latestMagValues != null && latestAccelValues != null) {
		// float[] rotationMatrix = new float[9];
		// float[] outR = new float[9];
		// float[] orientation = new float[3];
		// float[] zeroes = {0F,0F,0F};
		// SensorManager.getRotationMatrix(rotationMatrix, null,zeroes,
		// latestMagValues);
		// //SensorManager.remapCoordinateSystem(rotationMatrix,
		// SensorManager.AXIS_X, SensorManager.AXIS_Z, outR);
		// SensorManager.getOrientation(rotationMatrix, orientation);
		// if (System.currentTimeMillis() - lastTimestamp > 150) {
		// Log.d(getClass().getName(),"Rotation around z = "+String.valueOf(Math.toDegrees(orientation[0]))+"\tRotation around y = "+String.valueOf(Math.toDegrees(orientation[1])+"\tRotation around x = "+String.valueOf(Math.toDegrees(orientation[2]))));
		// lastTimestamp = System.currentTimeMillis();
		// dirPointerCanvas.setAngle((int) (-1 *
		// Math.toDegrees(orientation[0])));
		// if(lastKnownLocation != null){
		// Log.d(getClass().getName(),
		// "Bearing is "+lastKnownLocation.bearingTo(destinationLocation));
		// }
		// }
		// }
	}

	@Override
	public void onLocationChanged(Location location) {
		lastKnownLocation = location;
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d(getClass().getName(), provider + " is DISABLED");
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.d(getClass().getName(), provider + " is ENABLED");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	/**
	 * @return the destinationName
	 */
	public String getDestinationName() {
		return destinationName;
	}

	/**
	 * @param destinationName the destinationName to set
	 */
	public void setDestinationName(final String destinationName) {
		this.destinationName = destinationName;
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				((TextView)findViewById(R.id.destination_label)).setText("Travelling to "+destinationName);
			}
		});
	}

}
