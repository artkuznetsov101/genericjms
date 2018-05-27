package genericjms;

import java.util.UUID;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import genericjms.JMSConnectionFactory.MessageType;

public class JMSProducer implements Runnable {

	private Connection connection;
	private Session session;
	private Destination destination;
	private MessageProducer producer;
	private MessageType type;

	public JMSProducer(ConnectionFactory factory, String queue, MessageType type) throws JMSException {
		connection = factory.createConnection();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		destination = session.createQueue(queue);
		producer = session.createProducer(destination);
		this.type = type;
	}

	public void run() {
		try {
			Message message = null;
			switch (type) {
			case BYTES:
				message = session.createBytesMessage();
				break;
			case STRING:
				message = session.createTextMessage("test message " + UUID.randomUUID().toString());
				break;
			}

			producer.send(message);
			System.out.println("send: " + message.getJMSMessageID() + " " + type.name());

			producer.close();
			session.close();
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
