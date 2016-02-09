package me.streib.janis.dbaufzug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

public class JodelWebConfiguration {
	private Properties p;
	private static JodelWebConfiguration instance;

	protected JodelWebConfiguration(InputStream in) throws IOException,
			SQLException {
		this.p = new Properties();
		p.load(in);
		instance = this;
	}

	public int getPort() {
		return Integer.parseInt(p.getProperty("jodel.port"));
	}

	public String getHostName() {
		return p.getProperty("jodel.name");
	}

	protected String getDB() {
		return p.getProperty("jodel.db");
	}

	protected String getDBUser() {
		return p.getProperty("jodel.db.user");
	}

	protected String getDBPW() {
		return p.getProperty("jodel.db.pw");
	}

	protected String getJDBCDriver() {
		return p.getProperty("jodel.db.driver");
	}

	public static JodelWebConfiguration getInstance() {
		return instance;
	}

	private void store() {
		File f = new File("conf/");
		if (!f.exists()) {
			f.mkdir();
		}
		f = new File("conf/jodel.properties");
		try {
			p.store(new FileOutputStream(f), "");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String getMapSource() {
		return p.getProperty("jodel.mapsource");
	}

	public boolean isHSTSEnabled() {
		return Boolean.getBoolean(p.getProperty("jodel.hsts", "true"));
	}
}
