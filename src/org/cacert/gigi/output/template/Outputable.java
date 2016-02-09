package org.cacert.gigi.output.template;

import java.io.PrintWriter;
import java.util.Map;

public interface Outputable {

	public void output(PrintWriter out, Map<String, Object> vars);
}
