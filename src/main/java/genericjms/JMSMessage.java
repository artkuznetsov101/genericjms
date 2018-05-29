package genericjms;

import java.util.Arrays;

public class JMSMessage {

	public MessageType type;
	public String text;
	public byte[] data;

	public enum MessageType {
		TEXT, BYTES
	}

	public JMSMessage(String message) {
		this.type = MessageType.TEXT;
		this.text = message;
	}

	public JMSMessage(byte[] data) {
		this.type = MessageType.BYTES;
		this.data = data;
	}

	@Override
	public String toString() {
		return "JMSMessage [type=" + type + ", text=" + text + ", data=" + Arrays.toString(data) + "]";
	}
}
