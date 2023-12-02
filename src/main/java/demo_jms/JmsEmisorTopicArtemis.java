package demo_jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;

import com.google.gson.Gson;

public class JmsEmisorTopicArtemis {

	public static void main(String[] args) throws Exception {
		Solicitud a = new Solicitud();
		a.setTexto("Solicitud de prueba 33");
		Gson gson = new Gson();
		String json = gson.toJson(a);
		System.out.println(json);
		String topicName = "mtopic"; 
		String url = "tcp://localhost:61616";
		
		ConnectionFactory cf = ActiveMQJMSClient.createConnectionFactory(url, null);
		Connection connection = cf.createConnection("admin", "admin");
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Topic topic = session.createTopic(topicName);
		Message msg = session.createTextMessage(json);
		msg.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
		System.out.println("Sending text '" + json + "'");
		MessageProducer producer = session.createProducer(topic);
		producer.setDeliveryMode(DeliveryMode.PERSISTENT);
		producer.send(msg);
		try {
			session.close();
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}
}
