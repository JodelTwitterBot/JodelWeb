package me.streib.janis.dbaufzug.objects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import org.json.JSONObject;

public class LocationLatLong {

	public static LocationLatLong getLocationLatLongByJSON(JSONObject jsObject) {
		return new LocationLatLong(jsObject);
	}

	private double lat, longi;
	Random rand = new Random();
	
	public LocationLatLong(JSONObject jsObject) {
		lat = jsObject.getDouble("geocoordY");
		longi = jsObject.getDouble("geocoordX");
	}

	public LocationLatLong(ResultSet res) throws SQLException {
		this.lat = res.getDouble("lat");
		this.longi = res.getDouble("lng");
	}

	public double getLat() {
		return lat + ((rand.nextFloat() * 0.005) - 0.0025);
	}

	public double getLongi() {
		return longi + ((rand.nextFloat() * 0.005) - 0.0025);
	}

}
