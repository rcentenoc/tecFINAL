package fin;

import javax.jms.Connection;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.Topic;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.json.JSONObject;

public class AppRFCCProvincia {

    public static void main(String[] args) {
        String brokerUrl = "tcp://localhost:61616";
        String inputQueueName = "mqRFCCprovincia";
        String outputTopicName = "mtRFCCprovincia";

        try (ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
             Connection connection = connectionFactory.createConnection()) {

            connection.start();

            JMSContext context = connectionFactory.createContext();

            Queue inputQueue = context.createQueue(inputQueueName);
            JMSConsumer consumer = context.createConsumer(inputQueue);

            Topic outputTopic = context.createTopic(outputTopicName);

            while (true) {
                Message message = consumer.receive();
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    String messageContent = textMessage.getText();
                    System.out.println("Mensaje recibido: " + messageContent);

         
                    JSONObject jsonMessage = new JSONObject(messageContent);


                    JSONObject orderedJson = new JSONObject();
                    orderedJson.put("departamento", jsonMessage.optString("departamento", ""));
                    orderedJson.put("provincia", jsonMessage.optString("provincia", ""));
                    orderedJson.put("distrito", jsonMessage.optString("distrito", ""));
                    orderedJson.put("enfermedad", jsonMessage.optString("enfermedad", ""));
                    orderedJson.put("ano", jsonMessage.optString("ano", ""));
                    orderedJson.put("semana", jsonMessage.optString("semana", ""));
                    orderedJson.put("diagnostic", jsonMessage.optString("diagnostic", ""));
                    orderedJson.put("diresa", jsonMessage.optString("diresa", ""));
                    orderedJson.put("edad", jsonMessage.optString("edad", ""));
                    orderedJson.put("sexo", jsonMessage.optString("sexo", ""));

        
                    System.out.println("Mensaje ordenado: " + orderedJson.toString());

    
                    JMSProducer producer = context.createProducer();
                    producer.send(outputTopic, orderedJson.toString());
                    System.out.println("Mensaje enviado al tópico '" + outputTopicName + "'");
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}