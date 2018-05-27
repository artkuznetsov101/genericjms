package genericjms;

import java.util.UUID;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import genericjms.JMSConnectionFactory.MessageType;

public class JMSConsumer implements Runnable {

	private Connection connection;
	private Session session;
	private Destination destination;
	private MessageConsumer consumer;
	private MessageType type;

	public JMSConsumer(ConnectionFactory factory, String queue, MessageType type) throws JMSException {
		connection = factory.createConnection();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		destination = session.createQueue(queue);
		consumer = session.createConsumer(destination);
		this.type = type;

		connection.start();
	}

	public void run() {
		Message message;
		try {
			while (!Thread.currentThread().isInterrupted()) {
				message = consumer.receive();
				if (message != null) {

					switch (type) {
					case BYTES:
						// !!! raw cast !!!
						System.out.println("receive: " + ((BytesMessage) message).getBodyLength() + " " + type.name());
						break;
					case STRING:
						// !!! raw cast !!!
						System.out.println("receive: " + ((TextMessage) message).getText() + " " + type.name());
						break;
					}
				}
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}

		try {
			consumer.close();
			session.close();
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
