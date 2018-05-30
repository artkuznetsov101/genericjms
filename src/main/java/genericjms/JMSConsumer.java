package genericjms;

import java.security.InvalidParameterException;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import genericjms.JMSDestinationFactory.DestinationType;
import genericjms.JMSDestinationFactory.JMSDestination;

public class JMSConsumer implements Runnable, AutoCloseable {

	private Connection connection;
	private Session session;
	private Destination destination;
	private MessageConsumer consumer;
	private boolean isTransacted;

	public JMSConsumer(ConnectionFactory factory, JMSDestination dest, String clientId, boolean isTransacted, boolean isDurable, String durableSubscriptionName) throws JMSException {
		this.isTransacted = isTransacted;

		connection = factory.createConnection();
		if (clientId != null)
			connection.setClientID(clientId);
		
		session = connection.createSession(isTransacted, Session.AUTO_ACKNOWLEDGE);

		if (dest.type == DestinationType.TOPIC)
			destination = session.createTopic(dest.name);
		else
			destination = session.createQueue(dest.name);

		if (dest.type == DestinationType.TOPIC && isDurable)
			consumer = session.createDurableSubscriber((Topic) destination, durableSubscriptionName);
		else
			consumer = session.createConsumer(destination);

		connection.start();
	}

	public JMSMessage poll() throws JMSException {
		Message msg;
		JMSMessage message;
		byte[] data;
		try {
			if ((msg = consumer.receiveNoWait()) != null) {
				switch (JMSMessage.MessageType.valueOf(msg.getJMSType())) {
				case BYTES:
					data = new byte[(int) ((BytesMessage) msg).getBodyLength()];
					((BytesMessage) msg).readBytes(data);
					message = new JMSMessage(data);
					break;
				case TEXT:
					message = new JMSMessage(((TextMessage) msg).getText());
					break;
				default:
					throw new InvalidParameterException();
				}
				System.out.println("receive: " + message);
				if (isTransacted)
					session.commit();
				return message;
			}
		} catch (JMSException ex) {
			ex.printStackTrace();
			if (isTransacted)
				try {
					session.rollback();
				} catch (JMSException e) {
					e.printStackTrace();
				}
		}
		return null;
	}

	@Override
	public void run() {
		Message msg;
		JMSMessage message;
		byte[] data;
		while (!Thread.currentThread().isInterrupted()) {
			try {
				if ((msg = consumer.receive()) != null) {
					switch (JMSMessage.MessageType.valueOf(msg.getJMSType())) {
					case BYTES:
						data = new byte[(int) ((BytesMessage) msg).getBodyLength()];
						((BytesMessage) msg).readBytes(data);
						message = new JMSMessage(data);
						break;
					case TEXT:
						message = new JMSMessage(((TextMessage) msg).getText());
						break;
					default:
						throw new InvalidParameterException();
					}
					System.out.println("receive: " + message);
					if (isTransacted)
						session.commit();
				}
			} catch (JMSException ex) {
				if (ex.getCause() instanceof InterruptedException) {

				} else {
					ex.printStackTrace();
				}
			}
		}
	}

	@Override
	public void close() {
		try {
			consumer.close();
			session.close();
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
