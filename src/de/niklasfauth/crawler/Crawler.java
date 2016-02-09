package de.niklasfauth.crawler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import me.streib.janis.dbaufzug.DatabaseConnection;

import org.json.JSONArray;
import org.json.JSONObject;

public class Crawler implements Runnable {

	public void run() {
		while (true) {
			try {
				URL url = new URL("https://api.go-tellm.com/api/v2/posts/");
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setRequestProperty("Authorization", "Bearer "
						+ "a8b8d490-0596-4422-b32e-eb4273b52dec");
				urlConnection.setRequestMethod("GET");

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(urlConnection.getInputStream()));

				StringBuilder sb = new StringBuilder();

				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				String result = null;
				result = sb.toString();

				reader.close();

				// System.out.println(result);
				JSONObject jObject = new JSONObject(result);

				JSONArray jArray = jObject.getJSONArray("posts");

				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss");
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

				Date now = new Date();
				System.out.println("Update DB");
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject oneObject = jArray.getJSONObject(i);
					// Pulling items from the array

					// System.out.println(i);

					/*
					 * if (oneObject.has("thumbnail_url")) {
					 * System.out.println("bild!"); Date d =
					 * sdf.parse(oneObject.getString("created_at")); if
					 * (now.getTime() - d.getTime() < 30 * 1000) { String
					 * imageURL = oneObject.getString("thumbnail_url"); imageURL
					 * = "http:" + imageURL; System.out.println(imageURL);
					 * 
					 * URL Imageurl = new URL(imageURL); InputStream in = new
					 * BufferedInputStream( Imageurl.openStream()); OutputStream
					 * out = new BufferedOutputStream( new
					 * FileOutputStream("./tmp.jpeg"));
					 * 
					 * for (int i1; (i1 = in.read()) != -1;) { out.write(i1); }
					 * in.close(); out.close();
					 * 
					 * int maxLength = (message.length() < 139) ? message
					 * .length() : 139; message = message.substring(0,
					 * maxLength);
					 * 
					 * 
					 * } }
					 */

					JSONObject localtion = oneObject.getJSONObject("location")
							.getJSONObject("loc_coordinates");

					float lng = (float) localtion.getDouble("lng");
					float lat = (float) localtion.getDouble("lat");

					Date d = sdf.parse(oneObject.getString("created_at"));
					String message = oneObject.getString("message");
					String postId = oneObject.getString("post_id");

					Timestamp timestamp = new Timestamp(d.getTime());

					PreparedStatement updateemp = DatabaseConnection
							.getInstance()
							.prepare(
									"INSERT INTO posts (post_id, lat, lng, message, created) VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE id=id;");
					updateemp.setString(1, postId);
					updateemp.setFloat(2, lat);
					updateemp.setFloat(3, lng);
					updateemp.setString(4, message);
					updateemp.setTimestamp(5, timestamp);
					updateemp.executeUpdate();

				}
			} catch (Exception e) {
				System.out.println(e);
			}
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
