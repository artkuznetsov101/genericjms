package genericjms;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import genericjms.JMSDestinationFactory.JMSDestination;;

public class JMSProducer implements AutoCloseable {

	private Connection connection;
	private Session session;
	private Destination destination;
	private MessageProducer producer;
	private boolean isTransacted;

	public JMSProducer(ConnectionFactory factory, JMSDestination dest, boolean isTransacted) throws JMSException {
		this.isTransacted = isTransacted;

		connection = factory.createConnection();
		connection.start();
		session = connection.createSession(isTransacted, Session.AUTO_ACKNOWLEDGE);
		switch (dest.type) {
		case QUEUE:
			destination = session.createQueue(dest.name);
			break;
		case TOPIC:
			destination = session.createTopic(dest.name);
			break;
		}
		producer = session.createProducer(destination);
	}

	public void send(JMSMessage msg) throws JMSException {
		Message message = null;
		switch (msg.type) {
		case BYTES:
			message = session.createBytesMessage();
			message.setJMSType(JMSMessage.MessageType.BYTES.name());
			((BytesMessage) message).writeBytes(msg.data);
			break;
		case TEXT:
			message = session.createTextMessage();
			message.setJMSType(JMSMessage.MessageType.TEXT.name());
			((TextMessage) message).setText(msg.text);
			break;
		}
		producer.send(destination, message);
		if (isTransacted)
			session.commit();
		System.out.println("send: " + msg);
	}

	@Override
	public void close() {
		try {
			producer.close();
			session.close();
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
