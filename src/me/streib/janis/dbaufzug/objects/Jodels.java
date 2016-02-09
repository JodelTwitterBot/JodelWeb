package me.streib.janis.dbaufzug.objects;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;

public class Jodels extends Facility {

	
	public Jodels(ResultSet resultSet) throws SQLException {
		super(resultSet);
	}
}
