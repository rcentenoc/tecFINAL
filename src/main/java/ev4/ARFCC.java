package ev4;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.json.JSONObject;

public class ARFCC {

    public static void main(String[] args) {
        String brokerUrl = "tcp://localhost:61616"; 
        String topicName = "TRFCC"; // 
        try (ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
             Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
             MessageProducer producer = session.createProducer(session.createTopic(topicName))) {

            JSONObject jsonMessage = new JSONObject();
            jsonMessage.put("id", "0108");
            jsonMessage.put("producto", "Shampoo");
            jsonMessage.put("cantidad", 4);

            String messageContent = jsonMessage.toString();

            System.out.println("Mensaje a enviar: " + messageContent);

            TextMessage message = session.createTextMessage(messageContent);
            producer.send(message);
            System.out.println("Mensaje enviado al tópico '" + topicName + "'");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}