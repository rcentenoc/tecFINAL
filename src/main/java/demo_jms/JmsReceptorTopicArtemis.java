package demo_jms;


import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;

public class JmsReceptorTopicArtemis {

	public static void main(String[] args) throws Exception {
		LocalTime ahora = LocalTime.now();
		DateTimeFormatter f = DateTimeFormatter.ofPattern("HHmmss");
		String name = "Consumer" + ahora.format(f);
		String topicName = "mtopic";
		System.out.println("Se suscribe app " + name);
		String url = "tcp://localhost:61616";
		ConnectionFactory cf = ActiveMQJMSClient.createConnectionFactory(url, null);
		Connection connection = cf.createConnection("admin", "admin");
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Topic topic = session.createTopic(topicName);
		MessageConsumer consumer = session.createConsumer(topic);
			consumer.setMessageListener(new MessageListener() {
				@Override
				public void onMessage(Message message) {
					TextMessage msg = (TextMessage) message;
					String json;
					try {
						json = msg.getText();
						System.out.println("Recibiendo " + json + " desde " + name);
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
			});
		try {
			while(true) {
				
			}
		} finally {
			session.close();
			if (connection != null) {
				connection.close();
			}
		}
		
	}
}
