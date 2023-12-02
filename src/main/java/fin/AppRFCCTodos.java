package fin;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class AppRFCCTodos {
    public static void main(String[] args) {
        String brokerUrl = "tcp://localhost:61616";
        String topicName = "mtRFCCprovincia";

        try (ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
             Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
             MessageConsumer consumer = session.createConsumer(session.createTopic(topicName))) {

            connection.start();

            Map<String, Integer> distritoCountMap = new HashMap<>();
            //int mayoresDeEdadCount = 0;
            //int menoresDeEdadCount = 0;

            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        try {
                            String jsonMessage = textMessage.getText();
                            System.out.println("Mensaje recibido: " + jsonMessage);

            
                            String distrito = obtenerDistrito(jsonMessage);
                            //int edad = obtenerEdad(jsonMessage);                 
                            distritoCountMap.put(distrito, distritoCountMap.getOrDefault(distrito, 0) + 1);

                            // Incrementar el contador de edad según sea mayor o menor de edad
                            //if (edad >= 18) {
                            //    mayoresDeEdadCount++;
                            //} else {
                            //    menoresDeEdadCount++;
                            //}

           
                            System.out.println("Cantidad de mensajes por distrito:");
                            for (Map.Entry<String, Integer> entry : distritoCountMap.entrySet()) {
                                System.out.println(entry.getKey() + ": " + entry.getValue());
                            }
                            //System.out.println("Cantidad de mensajes mayores de edad: " + mayoresDeEdadCount);
                            //System.out.println("Cantidad de mensajes menores de edad: " + menoresDeEdadCount);
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                    }
                }

                private String obtenerDistrito(String jsonMessage) {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode rootNode = objectMapper.readTree(jsonMessage);
                        return rootNode.get("distrito").asText();
                    } catch (Exception e) {
                        return "Desconocido"; 
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