package grokking.data.kafka.collector;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import grokking.data.worker.LogCollector;

public class CollectorServlet extends HttpServlet {

	private static final long serialVersionUID = -3976859205921089936L;
	static final Logger logger = LogManager.getLogger(CollectorServlet.class.getName());
	
	private ExecutorService executor = new ThreadPoolExecutor(10000, 100000, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100000));
	
	@Override
	public void init() throws ServletException {

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		long timestamp = System.currentTimeMillis();
		
        Map<String, String[]> mpParams = req.getParameterMap();
		Runnable worker = new LogCollector(mpParams, timestamp);
		executor.execute(worker);
		
		PrintWriter out = resp.getWriter();
		resp.setContentType("text/plain");
        out.print("OK2");
        out.close();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	}
}
