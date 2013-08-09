/**
 * 
 */
package com.archerabi.wheredoigo.model;

import java.text.DecimalFormat;
import java.util.Comparator;

import org.gmarz.googleplaces.models.Place;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.archerabi.wheredoigo.R;
import com.archerabi.wheredoigo.util.Utils;
import com.archerabi.wheredoigo.util.Utils.DISTANCE_UNIT;

/**
 * @author gautamichitteti
 * 
 */
public class DataModel extends ArrayAdapter<Place> {

	private LayoutInflater inflater;
	
	private Location lastKnownLocation;
	
	private DecimalFormat distanceFormatter;
	/**
	 * @return the lastKnownLocation
	 */
	public Location getLastKnownLocation() {
		return lastKnownLocation;
	}

	/**
	 * @param lastKnownLocation the lastKnownLocation to set
	 */
	public void setLastKnownLocation(Location lastKnownLocation) {
		Log.d(getClass().getName(),"Setting location");
		this.lastKnownLocation = lastKnownLocation;
		sort();
	}

	/**
	 * 
	 */
	public DataModel(Context context, int resourceid) {
		super(context, resourceid);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		distanceFormatter = new DecimalFormat("#.#");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getItem(int)
	 */
	@Override
	public Place getItem(int position) {
		return super.getItem(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 View vi=convertView;
        if(convertView==null){
            vi = inflater.inflate(R.layout.list_row, null);
        }
        TextView nameLabel = (TextView)vi.findViewById(R.id.list_item_name);
        TextView locationLabel = (TextView)vi.findViewById(R.id.list_item_location);
        TextView distanceLabel = (TextView)vi.findViewById(R.id.list_item_distance);
        Place place = getItem(position);
        nameLabel.setText(place.getName());
        locationLabel.setText(place.getAddress());
        if(lastKnownLocation != null){
        	String distanceInMiles = distanceFormatter.format(Utils.covertDistance(DISTANCE_UNIT.METERS, DISTANCE_UNIT.MILES, place.getDistanceTo(lastKnownLocation)))+" Miles";
        	distanceLabel.setText(distanceInMiles);
        }
        return vi;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.BaseAdapter#hasStableIds()
	 */
	@Override
	public boolean hasStableIds() {
		return true;
	}

	public void sort(){
		sort(new Comparator<Place>() {

			@Override
			public int compare(Place lhs, Place rhs) {
				if(lhs.getDistanceTo(lastKnownLocation) < rhs.getDistanceTo(lastKnownLocation)){
					return -1;
				}else if(lhs.getDistanceTo(lastKnownLocation) < rhs.getDistanceTo(lastKnownLocation)){
					return 0;
				}
				return 1;
			}
		});
	}
}
