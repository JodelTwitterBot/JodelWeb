package org.cacert.gigi.output.template;

import java.io.PrintWriter;
import java.util.Map;

public final class IfStatement implements Outputable {

	private final String variable;

	private final TemplateBlock iftrue;

	private final TemplateBlock iffalse;

	public IfStatement(String variable, TemplateBlock body) {
		this.variable = variable;
		this.iftrue = body;
		iffalse = null;
	}

	public IfStatement(String variable, TemplateBlock iftrue,
			TemplateBlock iffalse) {
		this.variable = variable;
		this.iftrue = iftrue;
		this.iffalse = iffalse;
	}

	@Override
	public void output(PrintWriter out, Map<String, Object> vars) {
		Object o = vars.get(variable);
		if (!(o == Boolean.FALSE || o == null)) {
			iftrue.output(out, vars);
		} else if (iffalse != null) {
			iffalse.output(out, vars);
		}
	}
}
