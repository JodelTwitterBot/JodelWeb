package me.streib.janis.dbaufzug;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cacert.gigi.output.template.Template;
import org.json.JSONException;

public abstract class Page {
	private Template defaultTemplate;
	private String name;

	public Page(String name) {
		this.name = name;
		if (needsTemplate()) {
			URL resource = getClass().getResource(
					getClass().getSimpleName() + ".templ");
			if (resource != null) {
				defaultTemplate = new Template(resource);
			}
		}
	}

	/**
	 * By default, {@link #doGet()} is called.
	 * 
	 * @throws SQLException
	 * @throws JSONException
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse resp,
			Map<String, Object> vars) throws IOException, JSONException,
			SQLException {
		doGet(req, resp, vars);
	}

	public abstract void doGet(HttpServletRequest req,
			HttpServletResponse resp, Map<String, Object> vars)
			throws IOException, JSONException, SQLException;

	public String getName() {
		return name;
	}

	public Template getDefaultTemplate() {
		return defaultTemplate;
	}

	public abstract boolean needsTemplate();

}
