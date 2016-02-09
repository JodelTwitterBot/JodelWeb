package org.cacert.gigi.output.template;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public abstract class Form implements Outputable {

	public static final String CSRF_FIELD = "csrf";

	private final String csrf;

	private final String action;

	public Form(HttpServletRequest hsr) {
		this(hsr, null);
	}

	public Form(HttpServletRequest hsr, String action) {
		csrf = RandomToken.generateToken(32);
		this.action = action;
		HttpSession hs = hsr.getSession();
		hs.setAttribute("form/" + getClass().getName() + "/" + csrf, this);
	}

	public abstract boolean submit(PrintWriter out, HttpServletRequest req);

	protected String getCsrfFieldName() {
		return CSRF_FIELD;
	}

	@Override
	public void output(PrintWriter out, Map<String, Object> vars) {
		if (action == null) {
			out.println("<form method='POST'>");
		} else {
			out.println("<form method='POST' action='" + action + "'>");
		}
		failed = false;
		outputContent(out, vars);
		out.print("<input type='hidden' name='" + CSRF_FIELD + "' value='");
		out.print(getCSRFToken());
		out.println("'></form>");
	}

	protected abstract void outputContent(PrintWriter out,
			Map<String, Object> vars);

	boolean failed;

	protected void outputErrorPlain(PrintWriter out, String text) {
		if (!failed) {
			failed = true;
			out.println("<div class='formError'>");
		}
		out.print("<div>");
		out.print(text);
		out.println("</div>");
	}

	public boolean isFailed(PrintWriter out) {
		if (failed) {
			out.println("</div>");
		}
		return failed;
	}

	protected String getCSRFToken() {
		return csrf;
	}

	public static <T extends Form> T getForm(HttpServletRequest req,
			Class<T> target) throws CSRFException {
		String csrf = req.getParameter(CSRF_FIELD);
		if (csrf == null) {
			throw new CSRFException();
		}
		HttpSession hs = req.getSession();
		if (hs == null) {
			throw new CSRFException();
		}
		Form f = (Form) hs
				.getAttribute("form/" + target.getName() + "/" + csrf);
		if (f == null) {
			throw new CSRFException();
		}
		return (T) f;
	}

	public static class CSRFException extends IOException {

	}
}
