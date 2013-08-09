package com.archerabi.wheredoigo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.gmarz.googleplaces.GooglePlaces;
import org.gmarz.googleplaces.models.Place;
import org.gmarz.googleplaces.models.PlacesResult;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.archerabi.wheredoigo.model.DataModel;

public class MainActivity extends FragmentActivity implements LocationListener {

	private LocationManager mLocationManager;

	private GooglePlaces gPlaces;

	private DataModel places;

	private final int LOCATION_ACCURACY_IN_METERS = 50;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		places = new DataModel(getApplicationContext(), android.R.layout.simple_list_item_1);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		ListView listView = (ListView) findViewById(R.id.list_view);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Place place = places.getItem(position);
				Intent intent = new Intent(getApplicationContext(), DirectionPointerActivity.class);
				intent.putExtra("LATITUDE", place.getLatitude());
				intent.putExtra("LONGITUDE", place.getLongitude());
				intent.putExtra("DESTINATION",((Place) arg0.getItemAtPosition(position)).getName());
				startActivity(intent);
			}
		});
		((WebView)findViewById(R.id.webview)).loadUrl("file:///android_asset/busy.gif");
		((WebView)findViewById(R.id.webview)).setVisibility(View.INVISIBLE);
		
		final EditText searchBox = (EditText) findViewById(R.id.search_box);
		searchBox.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					((WebView)findViewById(R.id.webview)).setVisibility(View.VISIBLE);
					final String query = searchBox.getText().toString();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
					places.clear();
					new Thread(new Runnable() {

						@Override
						public void run() {
							search(query);
							final Button mapButton = ((Button) findViewById(R.id.map_button));
							mapButton.post(new Runnable() {

								@Override
								public void run() {
									if (places.getCount() > 0) {
										mapButton.setEnabled(true);
									} else {
										mapButton.setEnabled(false);
									}
								}
							});
						}
					}).start();
					return true;
				}
				return false;
			}
		});
	}

	private void search(String query) {
		gPlaces = new GooglePlaces("AIzaSyC1kNWXkXU4N68Cd05eUlGomKamHZ4he6A");
		try {
			final PlacesResult result = gPlaces.getPlaces(query, 200, 40.288414, -74.548978);
			List<String> sPlaces = new ArrayList<String>();
			for (Place p : result.getPlaces()) {
				sPlaces.add(p.getName());
			}
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					places.addAll(result.getPlaces());
					ListView listView = (ListView) findViewById(R.id.list_view);
					// for (Place place : places) {
					// Log.d(getClass().getName(), place.getName());
					// LatLng latLng = new LatLng(place.getLatitude(),
					// place.getLongitude());
					// GoogleMap mMap = ((SupportMapFragment)
					// getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
					// CameraPosition position =
					// CameraPosition.fromLatLngZoom(latLng, 15);
					// mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
					// mMap.addMarker(new
					// MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))).setSnippet(place.getName());
					// }
					listView.setAdapter(places);
					((WebView)findViewById(R.id.webview)).setVisibility(View.INVISIBLE);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();

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
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, this);
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d(getClass().getName(), "Lat = " + String.valueOf(location.getLatitude()) + " Long = " + String.valueOf(location.getLongitude()));
		if (location.hasAccuracy() && location.getAccuracy() < LOCATION_ACCURACY_IN_METERS) {
			places.setLastKnownLocation(location);
			ListView listView = (ListView) findViewById(R.id.list_view);
			listView.invalidate();
		}
		Log.d(getClass().getName(), "Location accuracy is " + location.getAccuracy());
		// LatLng latLng = new LatLng(location.getLatitude(),
		// location.getLongitude());
		// GoogleMap mMap = ((SupportMapFragment)
		// getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		// CameraPosition position = CameraPosition.fromLatLngZoom(latLng, 15);
		// mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
		// mMap.addMarker(new
		// MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

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
