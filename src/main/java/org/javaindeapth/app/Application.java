package org.javaindeapth.app;

import java.io.IOException;
import java.util.Properties;

import org.javaindeapth.consumer.QueueMessageConsumer;

public class Application {

	public static void main(String[] args) {
		Properties properties = new Properties();
		QueueMessageConsumer consumer =null;
		try {
			properties.load(Application.class.getResourceAsStream("/jms.properties"));
			
			consumer = new QueueMessageConsumer(properties);
		}
		catch (IOException e) {
			System.out.println("could not find jms.properties file");
		}
		finally {
			if(consumer!=null)
				consumer.close();
		}
	}

}
