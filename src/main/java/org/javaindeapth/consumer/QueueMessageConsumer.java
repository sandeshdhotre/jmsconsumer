package org.javaindeapth.consumer;

import java.util.Hashtable;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.javaindeapth.exception.ApplicationException;

public class QueueMessageConsumer implements MessageListener{
	
	/**
	 * JMS_FACTORY Constant for Properties file. 
	 */
	public static final String JMS_FACTORY = "JMS_FACTORY";
	
	/**
	 * JMS_QUEUE Constant for Properties file.
	 */
	public static final String JMS_QUEUE = "JMS_QUEUE";
	
	/**
	 * URL Constant for Properties file.
	 */
	public static final String URL = "URL";
	
	/**
	 * INITIAL_CONTEXT_FACTORY Constant for Properties file.
	 */
	public static final String INITIAL_CONTEXT_FACTORY = "INITIAL_CONTEXT_FACTORY";
	
	/**
	 * value for jndiInitialContextFactory.
	 */
	private final String jndiInitialContextFactory;
	
	/**
	 * value for jmsQueueFactory.
	 */
	private String jmsQueueFactory;
	
	/**
	 * value for jmsQueue.
	 */
	private String jmsQueue;
	
	/**
	 * value for applicationServerUrl.
	 */
	private String applicationServerUrl;
	/**
	 * instance of connectionFactory.
	 */
	private QueueConnectionFactory connectionFactory;
	
	/**
	 * instance of QueueConnection.
	 */
	private QueueConnection connection;
	
	/**
	 * instance of QueueSession.
	 */
	private QueueSession session;
	
	/**
	 * instance of QueueSender.
	 */
	private QueueReceiver receiver;
	
	/**
	 * instance of JMS queue.
	 */
	private Queue queue;
	
	public QueueMessageConsumer(Properties properties) {
		jmsQueueFactory =  (String) properties.get(JMS_FACTORY);
		jmsQueue =  (String) properties.get(JMS_QUEUE);
		applicationServerUrl = (String) properties.get(URL);
		jndiInitialContextFactory = (String) properties.get(INITIAL_CONTEXT_FACTORY);
		InitialContext context;
		try {
			context = getInitialContext();
		} catch (NamingException exception) {
			throw new ApplicationException("Error Creating Initial Context", exception);
		}
		
		try {
		 	connectionFactory = (QueueConnectionFactory) context.lookup(jmsQueueFactory);
		    connection = connectionFactory.createQueueConnection();
		    session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		    queue = (Queue) context.lookup(jmsQueue);
		    receiver = session.createReceiver(queue);
		    receiver.setMessageListener(this);
		    connection.start();
		} catch (NamingException e) {
			throw new ApplicationException("Error Creating QueueConnectionFactory object", e);
		} catch (JMSException e) {
			throw new ApplicationException("Error getting QueueConnection object", e);
		}
	}

	public void onMessage(Message message) {
		
		try {
			String body = message.getBody(String.class);	
			System.out.println("Messages Processed:"+body);
		} catch (JMSException e) {
			throw new ApplicationException("Error Reading Message", e);
		}	
	}
	
	public InitialContext getInitialContext() throws NamingException {
		Hashtable<String,String> env = new Hashtable<String,String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, jndiInitialContextFactory);
		env.put(Context.PROVIDER_URL, applicationServerUrl);
		InitialContext context = new InitialContext(env);
		return context;
	}
	
	public void close() {
		try {
			receiver.close();
			session.close();
			connection.close();
		} catch (JMSException e) {
			throw new ApplicationException("Error Closing Connection", e);
		}
	}
}
