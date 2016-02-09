package me.streib.janis.dbaufzug;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import de.niklasfauth.crawler.Crawler;

public class Launcher {
	public static void main(String[] args) throws Exception {
		new Thread(new Crawler()).start();// hourly
		JodelWebConfiguration conf;
		
		
		if (args.length != 1) {
			InputStream ins;
			File confFile = new File("conf/jodel.properties");
			if (confFile.exists()) {
				ins = new FileInputStream(confFile);
			} else {
				ins = Launcher.class.getResourceAsStream("jodel.properties");
			}
			conf = new JodelWebConfiguration(ins);
		} else {
			conf = new JodelWebConfiguration(new FileInputStream(new File(
					args[0])));
		}
		DatabaseConnection.init(conf);
		Server s = new Server(new InetSocketAddress(conf.getHostName(),
				conf.getPort()));
		((QueuedThreadPool) s.getThreadPool()).setMaxThreads(20);
		ServletContextHandler h = new ServletContextHandler(
				ServletContextHandler.SESSIONS);
		h.setInitParameter(SessionManager.__SessionCookieProperty, "Jodel-Session");
		h.addServlet(JodelWeb.class, "/*");
		HandlerList hl = new HandlerList();
		hl.setHandlers(new Handler[] { generateStaticContext(), h });
		s.setHandler(hl);
		s.start();
	}

	private static Handler generateStaticContext() {
		final ResourceHandler rh = new ResourceHandler();
		rh.setResourceBase("static/");
		ContextHandler ch = new ContextHandler("/static");
		ch.setHandler(rh);
		return ch;
	}
}
