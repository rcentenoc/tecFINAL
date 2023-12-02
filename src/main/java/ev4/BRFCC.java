package ev4;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

public class BRFCC {

    public static void main(String[] args) {
        String brokerUrl = "tcp://localhost:61616"; 
        String topicName = "TRFCC"; 

        try (ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
             Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
             MessageConsumer consumer = session.createConsumer(session.createTopic(topicName))) {

            connection.start();

            
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        try {
                            String text = textMessage.getText();
                            System.out.println("Mensaje recibido: " + text +"\n");
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            System.out.println("Presione cualquier tecla para salir...");
            System.in.read(); 

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}