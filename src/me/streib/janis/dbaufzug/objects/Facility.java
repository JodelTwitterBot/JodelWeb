package me.streib.janis.dbaufzug.objects;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import me.streib.janis.dbaufzug.DatabaseConnection;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

public abstract class Facility {

	public static LinkedList<Facility> getAllFacilities() throws SQLException {
		LinkedList<Facility> res = new LinkedList<Facility>();
		PreparedStatement prep = DatabaseConnection.getInstance().prepare(
				"SELECT * FROM posts WHERE created > (NOW() - INTERVAL 720 MINUTE)");
		ResultSet resSet = prep.executeQuery();
		resSet.beforeFirst();
		while (resSet.next()) {
			
				res.add(new Jodels(resSet));
		}
		return res;
	}

	

	private long equipmentNumber;
	private String description = null;
	private LocationLatLong location;
	private String timestamp = null;


	public Facility(ResultSet res) throws SQLException {
		String tempString = escapeHtml4(res.getString("message"));

		StringUtils.replaceEach(tempString, new String[]{"&", "\"", "<", ">"}, new String[]{"&amp;", "&quot;", "&lt;", "&gt;"});
		tempString = tempString.replaceAll("\n", "<br>").replaceAll("'", "");

		int MAX_CHAR = 500;
		int maxLength = (tempString.length() < MAX_CHAR)?tempString.length():MAX_CHAR;
		this.description = tempString.substring(0, maxLength);
		this.timestamp = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(res.getTimestamp("created"));
		this.equipmentNumber = res.getLong("id");
		this.location = new LocationLatLong(res);
	}

	public String getDescription() {
		return description;
	}


	
	public String getTimestamp() {
		return timestamp;
	}

	public LocationLatLong getLocation() {
		return location;
	}

}
