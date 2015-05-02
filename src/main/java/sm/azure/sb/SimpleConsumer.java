package sm.azure.sb;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;

/**
 * Created by saschamoellering on 02/05/15.
 */
public class SimpleConsumer implements MessageListener {

    private static Logger logger = LogManager.getLogger(SimpleConsumer.class);

    private Session receiveSession;
    private MessageConsumer receiver;
    private Connection connection;

    public static final void main (String ... args) {
        try {

            logger.info("Starting simple-consumer ...");
            logger.info("Reading properties ...");

            SimpleConsumer simpleConsumer = new SimpleConsumer();
            simpleConsumer.setUpConsumer();
            while (true) {
                Thread.sleep(5000);
            }
        }

        catch (NamingException | JMSException | InterruptedException exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    private void setUpConsumer() throws NamingException, JMSException {

        // Configure JNDI environment
        Hashtable<String, String> env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY,
                Constants.CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, Constants.PROVIDER_URL);
        Context context = new InitialContext(env);

        // Lookup ConnectionFactory and Queue
        ConnectionFactory cf = (ConnectionFactory) context.lookup("SBCF");
        Destination queue = (Destination) context.lookup("QUEUE");

        // Create Connection
        connection = cf.createConnection();

        // Create receiver-side Session, MessageConsumer,and MessageListener
        receiveSession = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        receiver = receiveSession.createConsumer(queue);
        receiver.setMessageListener(this);
        connection.start();
    }

    public void onMessage(Message message) {
        try {
            logger.info("Received message with JMSMessageID = " + message.getJMSMessageID());
            message.acknowledge();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
