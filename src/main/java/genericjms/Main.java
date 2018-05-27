package genericjms;

import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

public class Main {

	public static void main(String[] args) throws InterruptedException, JMSException {

		Thread thread;

		// user set destination
		String queue = "testqueue";

		// user set message type
		JMSConnectionFactory.MessageType type = JMSConnectionFactory.MessageType.STRING;

		// user select rabbitmq

		// different brokers need different connection attributes
		Properties properties = new Properties();
		properties.setProperty("username", "test");
		properties.setProperty("password", "test");
		properties.setProperty("host", "10.168.45.49");
		properties.setProperty("port", "5672");
		properties.setProperty("virtualHost", "/");

		// get connection factory
		ConnectionFactory factory = JMSConnectionFactory.getRabbitMQFactory(properties);

		// pass factory, destination, message type to producer class for message sending
		JMSProducer producer = new JMSProducer(factory, queue, type);
		thread = new Thread(producer);
		thread.start();

		// pass factory, destination, message to consumer class for message receiving
		JMSConsumer consumer = new JMSConsumer(factory, queue, type);
		thread = new Thread(consumer);
		thread.start();
		Thread.sleep(5000);
		thread.interrupt();

		// user select activemq

		// different brokers need different connection attributes
		properties.setProperty("uri", "failover://(tcp://10.168.45.49:61616)");

		// get connection factory
		factory = JMSConnectionFactory.getActiveMQFactory(properties);

		// pass factory, destination, message type to producer class for message sending
		producer = new JMSProducer(factory, queue, type);
		thread = new Thread(producer);
		thread.start();

		// pass factory, destination, message to consumer class for message receiving
		consumer = new JMSConsumer(factory, queue, type);
		thread = new Thread(consumer);
		thread.start();
		Thread.sleep(5000);
		thread.interrupt();
	}
}
