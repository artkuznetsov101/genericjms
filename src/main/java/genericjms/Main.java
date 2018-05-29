package genericjms;

import java.util.Properties;

import javax.jms.ConnectionFactory;

import genericjms.JMSDestinationFactory.JMSDestination;

public class Main {

	public static void main(String[] args) throws Exception {

		// set destination
		JMSDestination destination = JMSDestinationFactory.getJMSDestinationQueue("test");
		// set text message
		JMSMessage textMessage = new JMSMessage("test message");
		// set bytes message
		JMSMessage bytesMessage = new JMSMessage("test message".getBytes());

		// different brokers need different connection attributes
		Properties properties = new Properties();

		// set activemq broker
		properties.setProperty("uri", "failover://(tcp://localhost:61616)");

		// set rabbitmq broker
		// properties.clear();
		// properties.setProperty("username", "test");
		// properties.setProperty("password", "test");
		// properties.setProperty("host", "localhost");
		// properties.setProperty("port", "5672");
		// properties.setProperty("virtualHost", "/");

		// set qpid broker
		// properties.clear();
		// properties.setProperty("username", "guest");
		// properties.setProperty("password", "guest");
		// properties.setProperty("uri", "amqp://localhost:5672");

		// get proper connection factory
		ConnectionFactory factory = JMSConnectionFactory.getActiveMQFactory(properties);

		// send: pass factory, destination, transaction mode
		try (JMSProducer prod = new JMSProducer(factory, destination, true)) {
			prod.send(textMessage);
			prod.send(bytesMessage);
		}

		// poll receive: pass factory, destination, transaction mode
		try (JMSConsumer consumer = new JMSConsumer(factory, destination, true)) {
			textMessage = consumer.poll(100);
		}

		// thread receive: pass factory, destination, transaction mode
		try (JMSConsumer consumer = new JMSConsumer(factory, destination, true)) {
			Thread thread = new Thread(consumer);
			thread.start();
			Thread.sleep(5000);
			thread.interrupt();
		}
	}
}
