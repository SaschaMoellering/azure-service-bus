package sm.azure.sb.bus;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;
import java.util.Random;

/**
 * Created by saschamoellering on 01/05/15.
 */
public class BusProducer {

    private Connection connection;
    private MessageProducer sender;
    private Session sendSession;
    private static Random randomGenerator = new Random();
    private static Logger logger = LogManager.getLogger(BusProducer.class);

    private static final BusProducer BUS_PRODUCER = new BusProducer();

    private BusProducer() {
        try {
            this.createConnection();
        }

        catch (JMSException | NamingException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static final BusProducer getInstance() {
        return BUS_PRODUCER;
    }

    protected void createConnection() throws NamingException, JMSException{
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.qpid.amqp_1_0.jms.jndi.PropertiesFileInitialContextFactory");
        env.put(Context.PROVIDER_URL, "file:///tmp/servicebus.properties");
        InitialContext context = new InitialContext(env);

        // Lookup ConnectionFactory and Queue
        ConnectionFactory cf = (ConnectionFactory) context.lookup("SBCF");
        Destination queue = (Destination) context.lookup("QUEUE");

        // Create Connection
        connection = cf.createConnection();
        sendSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        sender = sendSession.createProducer(queue);
    }

    public void sendMessage() throws JMSException {
        TextMessage message = sendSession.createTextMessage();
        message.setText("Test AMQP message from JMS");
        long randomMessageID = randomGenerator.nextLong() >>>1;
        message.setJMSMessageID("ID:" + randomMessageID);
        sender.send(message);
        logger.debug("Sent message with JMSMessageID = " + message.getJMSMessageID());
    }

    public void close() {
        try {
            if (connection != null)
                connection.close();
        }

        catch (JMSException e) {
            logger.error(e.toString(), e);
        }
    }

}
