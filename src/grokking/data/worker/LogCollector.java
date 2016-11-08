package grokking.data.worker;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import grokking.data.config.Configuration;

public class LogCollector implements Runnable {

	static final Logger logger = LogManager.getLogger(LogCollector.class.getName());
	
	private Map<String, String[]> event;
	private long timestamp = 0;
	
	public LogCollector(Map<String, String[]> event, long time){

		this.event = event;
		this.timestamp = time;
	}
	
	@Override
	public void run() {

		try{
			
			String brokers = Configuration.getConfiguration().getString("kafka.brokers");

			Properties props = new Properties();
	        props.put("bootstrap.servers", brokers);
	        
	        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
	        props.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
	        
	        props.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");  
	        props.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
	        props.put("request.required.acks", "1");
	
	        String kafkaQueue = event.get("metric")[0];
	        
	        Map<String, Object> mpInfo = new HashMap<String, Object>();
	        Set<Entry<String, String[]>> entries = event.entrySet();
	        
	        Iterator<Entry<String, String[]>> it = entries.iterator();
	        
	        while(it.hasNext()){
	        	
	        	Entry<String, String[]> entry = it.next();
	        	String paramName = entry.getKey();
	        	String[] paramValues = entry.getValue();
	        	
	        	if(paramValues.length > 1){
	        		
	        		mpInfo.put(paramName, paramValues);
	        	}else{
	        		
	        		mpInfo.put(paramName, paramValues[0]);
	        	}
	        }
	        
	        mpInfo.put("timestamp", "" + timestamp);
	        
	        String json = "";
			ObjectMapper objMapper = new ObjectMapper();
			
			try {
				
				json = objMapper.writeValueAsString(mpInfo);
				Producer<String, String> producer = new KafkaProducer<>(props);
		        producer.send(new ProducerRecord<String, String>(kafkaQueue, "" + timestamp, json));
		        producer.close();
		        logger.info(json);
			} catch (JsonProcessingException e) {
	
				logger.error("Parsing request to json failed", e);
			}
		} catch (Exception ex){

			logger.error(event.toString());
			logger.error("Log Collector worker failed", ex);
		}
	}
}
