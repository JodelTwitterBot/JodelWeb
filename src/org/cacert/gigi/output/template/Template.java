package org.cacert.gigi.output.template;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Template implements Outputable {

	class ParseResult {

		TemplateBlock block;

		String endType;

		public ParseResult(TemplateBlock block, String endType) {
			this.block = block;
			this.endType = endType;
		}

		public String getEndType() {
			return endType;
		}

		public TemplateBlock getBlock(String reqType) {
			if (endType == null && reqType == null) {
				return block;
			}
			if (endType == null || reqType == null) {
				throw new Error("Invalid block type: " + endType);
			}
			if (endType.equals(reqType)) {
				return block;
			}
			throw new Error("Invalid block type: " + endType);
		}
	}

	private TemplateBlock data;

	private long lastLoaded;

	private File source;

	private static final Pattern CONTROL_PATTERN = Pattern
			.compile(" ?([a-z]+)\\(\\$([^)]+)\\) ?\\{ ?");

	private static final Pattern ELSE_PATTERN = Pattern
			.compile(" ?\\} ?else ?\\{ ?");

	public Template(URL u) {
		try {
			Reader r = new InputStreamReader(u.openStream(), "UTF-8");
			try {
				if (u.getProtocol().equals("file")) {
					source = new File(u.toURI());
					lastLoaded = source.lastModified() + 1000;
				}
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			data = parse(r).getBlock(null);
			r.close();
		} catch (IOException e) {
			throw new Error(e);
		}
	}

	public Template(Reader r) {
		try {
			data = parse(r).getBlock(null);
			r.close();
		} catch (IOException e) {
			throw new Error(e);
		}
	}

	private ParseResult parse(Reader r) throws IOException {
		LinkedList<String> splitted = new LinkedList<String>();
		LinkedList<Outputable> commands = new LinkedList<Outputable>();
		StringBuffer buf = new StringBuffer();
		String blockType = null;
		outer: while (true) {
			while (!endsWith(buf, "<?")) {
				int ch = r.read();
				if (ch == -1) {
					break outer;
				}
				buf.append((char) ch);
			}
			buf.delete(buf.length() - 2, buf.length());
			splitted.add(buf.toString());
			buf.delete(0, buf.length());
			while (!endsWith(buf, "?>")) {
				int ch = r.read();
				if (ch == -1) {
					throw new EOFException();
				}
				buf.append((char) ch);
			}
			buf.delete(buf.length() - 2, buf.length());
			String com = buf.toString().replace("\n", "");
			buf.delete(0, buf.length());
			Matcher m = CONTROL_PATTERN.matcher(com);
			if (m.matches()) {
				String type = m.group(1);
				String variable = m.group(2);
				ParseResult body = parse(r);
				if (type.equals("if")) {
					if ("else".equals(body.getEndType())) {
						commands.add(new IfStatement(variable, body
								.getBlock("else"), parse(r).getBlock("}")));
					} else {
						commands.add(new IfStatement(variable, body
								.getBlock("}")));
					}
				} else if (type.equals("foreach")) {
					commands.add(new ForeachStatement(variable, body
							.getBlock("}")));
				} else {
					throw new IOException(
							"Syntax error: unknown control structure: " + type);
				}
				continue;
			} else if ((m = ELSE_PATTERN.matcher(com)).matches()) {
				blockType = "else";
				break;
			} else if (com.matches(" ?\\} ?")) {
				blockType = "}";
				break;
			} else {
				commands.add(parseCommand(com));
			}
		}
		splitted.add(buf.toString());
		return new ParseResult(new TemplateBlock(
				splitted.toArray(new String[splitted.size()]),
				commands.toArray(new Outputable[commands.size()])), blockType);
	}

	private boolean endsWith(StringBuffer buf, String string) {
		return buf.length() >= string.length()
				&& buf.substring(buf.length() - string.length(), buf.length())
						.equals(string);
	}

	private Outputable parseCommand(String s2) {
		if (s2.startsWith("=$")) {
			final String raw = s2.substring(2);
			return new OutputVariableCommand(raw);
		} else {
			System.out.println("Unknown processing instruction: " + s2);
		}
		return null;
	}

	@Override
	public void output(PrintWriter out, Map<String, Object> vars) {
		if (source != null) {
			if (lastLoaded < source.lastModified()) {
				try {
					System.out.println("Reloading template.... " + source);
					InputStreamReader r = new InputStreamReader(
							new FileInputStream(source), "UTF-8");
					data = parse(r).getBlock(null);
					r.close();
					lastLoaded = source.lastModified() + 1000;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		data.output(out, vars);
	}

	protected static void outputVar(PrintWriter out, Map<String, Object> vars,
			String varname, boolean unescaped) {
		Object s = vars.get(varname);

		if (s == null) {
			System.out.println("Empty variable: " + varname);
			// NOTE: Modificated from orig!
			s = "";
		}
		if (s instanceof Outputable) {
			((Outputable) s).output(out, vars);
		} else {
			out.print(s == null ? "null" : (unescaped ? s.toString()
					: HTMLEncoder.encodeHTML(s.toString())));
		}
	}
}
