package sm.azure.sb.bus;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sm.azure.sb.Constants;

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
    private InitialContext context;
    private static Random randomGenerator = new Random();
    private static Logger logger = LogManager.getLogger(BusProducer.class);

    private static final BusProducer BUS_PRODUCER = new BusProducer();

    private BusProducer() {
        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, Constants.CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, Constants.PROVIDER_URL);
            context = new InitialContext(env);
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

        // Lookup ConnectionFactory and Queue
        ConnectionFactory cf = (ConnectionFactory) context.lookup("SBCF");
        Destination queue = (Destination) context.lookup("QUEUE");

        // Create Connection
        connection = cf.createConnection();

        connection.setExceptionListener(exception -> {
            logger.error("ExceptionListener triggered: " + exception.getMessage(), exception);
            try {
                Thread.sleep(5000); // Wait 5 seconds (JMS server restarted?)
                createConnection();
            } catch (InterruptedException | NamingException | JMSException e) {
                logger.error("Error pausing thread" + e.getMessage());
            }
        });

        sendSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        sender = sendSession.createProducer(queue);
    }

    public void sendMessage(final Integer value) throws JMSException {
        TextMessage message = sendSession.createTextMessage();
        message.setText("Test AMQP message from JMS with value " + value);
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
