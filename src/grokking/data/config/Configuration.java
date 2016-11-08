package grokking.data.config;

import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.SubnodeConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Configuration {

	static final Logger logger = LogManager.getLogger(Configuration.class.getName());
	
	private static Configuration instance;
	private INIConfiguration configuration;
	
	private Configuration(){
		
		String mode = System.getProperty("mode");
		
		if(!"development".equals(mode)){
		
			mode = "production";
		}

		String filePath = "conf" + File.separator + mode + ".config.ini";
		Configurations configs = new Configurations();
		
		try {
			configuration = configs.ini(filePath);
			
		} catch (ConfigurationException e) {

			logger.error("No configuration found!", e);
		}
	}
	
	public static Configuration getInstance(){
		
		if(instance == null){
			
			synchronized (Configuration.class){
			
				if(instance == null){
			
					instance = new Configuration();
				}
			}
		}
		
		return instance;
	}
	
	private INIConfiguration getConfig(){
		
		return configuration;
	}
	
	public static INIConfiguration getConfiguration(){
		
		return getInstance().getConfig();
	}
	
	public static SubnodeConfiguration getSection(String key){
		
		return getInstance().getConfig().getSection(key);
	}
}
