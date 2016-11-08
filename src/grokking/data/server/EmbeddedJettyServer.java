package grokking.data.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import grokking.data.config.Configuration;
import grokking.data.kafka.collector.CollectorServlet;

public class EmbeddedJettyServer {

	public static void main(String[] args) {
		
        String webPort = (String) Configuration.getConfiguration().getProperty("jetty.port");
        if(webPort == null || webPort.isEmpty()) {
            webPort = "8080";
        }
        
        int min_threads = Configuration.getConfiguration().getInt("jetty.minthread");
        int max_threads = Configuration.getConfiguration().getInt("jetty.maxthread");
        
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMaxThreads(max_threads);
        threadPool.setMinThreads(min_threads);
        
        try {
            //Server server = new Server(Integer.parseInt(webPort));
        	Server server = new Server(threadPool);
        	ServerConnector http = new ServerConnector(server);
        	http.setPort(Integer.parseInt(webPort));
        	http.setIdleTimeout(60000);
        	
        	server.addConnector(http);
            
            // servlet handlers
            ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
            handler.addServlet(CollectorServlet.class, "/logs");
            
            // static resource handler
            ContextHandler resource_handler = new ContextHandler("/public");
            resource_handler.setResourceBase("./public/");
            resource_handler.setHandler(new ResourceHandler());

            // use a handler to handle all requests
            HandlerList handlers = new HandlerList();
            handlers.setHandlers(new Handler[]{handler, resource_handler});
            server.setHandler(handlers);
            server.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	}
}
