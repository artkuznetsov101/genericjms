package genericjms;

import java.util.Properties;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.qpid.jms.JmsConnectionFactory;

import com.rabbitmq.jms.admin.RMQConnectionFactory;

public class JMSConnectionFactory {

	public static ConnectionFactory getActiveMQFactory(Properties properties) {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(properties.getProperty("uri"));
		return factory;
	}

	public static ConnectionFactory getRabbitMQFactory(Properties properties) {
		RMQConnectionFactory factory = new RMQConnectionFactory();
		factory.setUsername(properties.getProperty("username"));
		factory.setPassword(properties.getProperty("password"));
		factory.setHost(properties.getProperty("host"));
		factory.setPort(Integer.parseInt(properties.getProperty("port")));
		factory.setVirtualHost(properties.getProperty("virtualHost"));
		return factory;
	}

	public static ConnectionFactory getQpidFactory(Properties properties) {
		JmsConnectionFactory factory = new JmsConnectionFactory(properties.getProperty("username"),
				properties.getProperty("password"), properties.getProperty("uri"));
		return factory;
	}

}
