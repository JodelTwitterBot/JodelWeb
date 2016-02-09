package org.cacert.gigi.output.template;

import java.io.PrintWriter;
import java.util.Map;

public final class OutputVariableCommand implements Outputable {

	private final String raw;

	private final boolean unescaped;

	public OutputVariableCommand(String raw) {
		if (raw.charAt(0) == '!') {
			unescaped = true;
			this.raw = raw.substring(1);
		} else {
			unescaped = false;
			this.raw = raw;
		}
	}

	@Override
	public void output(PrintWriter out, Map<String, Object> vars) {
		Template.outputVar(out, vars, raw, unescaped);
	}
}
