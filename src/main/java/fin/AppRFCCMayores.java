package fin;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AppRFCCMayores {
    public static void main(String[] args) {
        String brokerUrl = "tcp://localhost:61616";
        String topicName = "mtRFCCprovincia";
        String restEndpointUrl = "http://localhost:8080/";

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
                            System.out.println("Mensaje recibido: " + text);

                            if (isMayorDeEdad(text)) {
                                enviarMensajeRest(text, restEndpointUrl);
                            }
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                    }
                }

                private void enviarMensajeRest(String jsonMessage, String restUrl) {
                    try {
                        HttpClient httpClient = HttpClients.createDefault();
                        HttpPost httpPost = new HttpPost(restUrl);
                        httpPost.setHeader("Content-Type", "application/json");

                        StringEntity entity = new StringEntity(jsonMessage);
                        httpPost.setEntity(entity);

                        HttpResponse response = httpClient.execute(httpPost);

                        if (response.getStatusLine().getStatusCode() == 200) {
                            System.out.println("Mensaje enviado por HTTP POST exitosamente.");
                        } else {
                            System.err.println("Error al enviar el mensaje por HTTP POST. Código de respuesta: " + response.getStatusLine().getStatusCode());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                private boolean isMayorDeEdad(String jsonMessage) {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode rootNode = objectMapper.readTree(jsonMessage);
                        int edad = rootNode.get("edad").asInt();
                        return edad >= 18;
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