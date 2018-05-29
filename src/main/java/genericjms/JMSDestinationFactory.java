package genericjms;

public class JMSDestinationFactory {
	public enum DestinationType {
		QUEUE, TOPIC
	}

	static class JMSDestination {
		public DestinationType type;
		public String name;

		private JMSDestination(DestinationType type, String name) {
			this.type = type;
			this.name = name;
		}
	}

	public static JMSDestination getJMSDestinationTopic(String name) {
		return new JMSDestination(DestinationType.TOPIC, name);
	}

	public static JMSDestination getJMSDestinationQueue(String name) {
		return new JMSDestination(DestinationType.QUEUE, name);
	}
}
