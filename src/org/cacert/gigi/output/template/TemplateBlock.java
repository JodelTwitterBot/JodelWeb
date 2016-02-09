package org.cacert.gigi.output.template;

import java.io.PrintWriter;
import java.util.Map;

class TemplateBlock implements Outputable {

	private String[] contents;

	private Outputable[] vars;

	public TemplateBlock(String[] contents, Outputable[] vars) {
		this.contents = contents;
		this.vars = vars;
	}

	@Override
	public void output(PrintWriter out, Map<String, Object> vars) {
		for (int i = 0; i < contents.length; i++) {
			out.print(contents[i]);
			if (i < this.vars.length) {
				this.vars[i].output(out, vars);
			}
		}
	}

}
