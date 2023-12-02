package fin;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AppRFCCMenores {
    public static void main(String[] args) {
        String brokerUrl = "tcp://localhost:61616";
        String topicName = "mtRFCCprovincia";
        String mqINImenoresQueueName = "mqRFCCmenores";

        try (ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
             Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
             MessageConsumer consumer = session.createConsumer(session.createTopic(topicName));
             MessageProducer producer = session.createProducer(session.createQueue(mqINImenoresQueueName))) {

            connection.start();

            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        try {
                            String text = textMessage.getText();
                            System.out.println("Mensaje recibido: " + text);

                            if (isMenorDeEdad(text)) {
                                TextMessage messageToSend = session.createTextMessage(text);
                                producer.send(messageToSend);
                                System.out.println("Mensaje enviado a mqINImenores: " + text);
                            }
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                    }
                }

                private boolean isMenorDeEdad(String jsonMessage) {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode rootNode = objectMapper.readTree(jsonMessage);
                        int edad = rootNode.get("edad").asInt();
                        return edad < 18;
                    } catch (Exception e) {
                        return false;
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