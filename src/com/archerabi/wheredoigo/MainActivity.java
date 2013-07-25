package com.archerabi.wheredoigo;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.gmarz.googleplaces.GooglePlaces;
import org.gmarz.googleplaces.models.Place;
import org.gmarz.googleplaces.models.PlacesResult;
import org.json.JSONException;

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
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CameraPositionCreator;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements SensorEventListener, LocationListener {

	private SensorManager mSensorManager;
	private Sensor mMagneticSensor;
	private Sensor mAccelSensor;

	private LocationManager mLocationManager;

	private float[] latestAccelValues = null;
	private float[] latestMagValues = null;

	private long lastTimeStamp;

	private GooglePlaces gPlaces;

	private DirectionPointerCanvas directionCanvas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// LinearLayout layout = (LinearLayout) findViewById(R.id.root_layout);
		// directionCanvas = new
		// DirectionPointerCanvas(getApplicationContext());
		// layout.addView(directionCanvas);

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mAccelSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, this);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		
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
		// mSensorManager.registerListener(this, mMagneticSensor,
		// SensorManager.SENSOR_DELAY_UI);
		// mSensorManager.registerListener(this,
		// mAccelSensor,SensorManager.SENSOR_DELAY_UI);
		// lastTimeStamp = System.currentTimeMillis();
		new Thread(new Runnable() {

			@Override
			public void run() {
				gPlaces = new GooglePlaces("AIzaSyC1kNWXkXU4N68Cd05eUlGomKamHZ4he6A");
				try {
					PlacesResult result = gPlaces.getPlaces("coffee", 200, 40.288414, -74.548978);
					final List<Place> places = result.getPlaces();
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							for (Place place : places) {
								Log.d(getClass().getName(), place.getName());
								LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
								GoogleMap mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
								CameraPosition position = CameraPosition.fromLatLngZoom(latLng, 15);
								mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
								mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))).setSnippet(place.getName());
							}
						}
					});
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor == mAccelSensor) {
			latestAccelValues = event.values.clone();
		} else if (event.sensor == mMagneticSensor) {
			latestMagValues = event.values.clone();
		}
		if (latestMagValues != null && latestAccelValues != null) {
			float[] rotationMatrix = new float[9];
			float[] outR = new float[9];
			float[] orientation = new float[3];
			SensorManager.getRotationMatrix(rotationMatrix, null, latestAccelValues, latestMagValues);
			SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, outR);
			SensorManager.getOrientation(outR, orientation);
			if (System.currentTimeMillis() - lastTimeStamp > 150) {
				// Log.d(getClass().getName(),"Rotation around z = "+String.valueOf(Math.toDegrees(orientation[0]))+"\tRotation around y = "+String.valueOf(Math.toDegrees(orientation[1])+"\tRotation around x = "+String.valueOf(Math.toDegrees(orientation[2]))));
				lastTimeStamp = System.currentTimeMillis();
				directionCanvas.setAngle((int) (-1 * Math.toDegrees(orientation[0])));
			}
		}

	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d(getClass().getName(), "Lat = " + String.valueOf(location.getLatitude()) + " Long = " + String.valueOf(location.getLongitude()));
		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
		GoogleMap mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		CameraPosition position = CameraPosition.fromLatLngZoom(latLng, 15);
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
		mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

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

}
