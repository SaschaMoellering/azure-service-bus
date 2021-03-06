package sm.azure.sb;

import com.codahale.metrics.health.HealthCheckRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;

import java.lang.management.ManagementFactory;

/**
 * Created by sascha.moellering on 30/04/2015.
 */
public class ServiceBusProducer {

    private static Logger logger = LogManager.getLogger(ServiceBusProducer.class);
    private static final int PORT_BINDING = 8080;

    private ServiceBusProducer() {

    }

    /**
     * Entry point for application.
     *
     * @param args Arguments for the app
     * @throws Exception If an exception occurs
     */
    public static void main(String... args) throws Exception {

        logger.info("Starting service-bus-producer ...");
        logger.info("Reading properties ...");

        final HealthCheckRegistry healthChecks = new HealthCheckRegistry();

        Server server = new Server(PORT_BINDING);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        server.setHandler(context);

        // Setup JMX
        MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
        server.addEventListener(mbContainer);
        server.addBean(mbContainer);
        server.addBean(Log.getLog());

        ServletContainer servletContainer = new ServletContainer();
        ServletHolder servletHolder = new ServletHolder(servletContainer);
        servletHolder.setInitParameter(ServerProperties.PROVIDER_PACKAGES, "sm.azure.sb.rest");
        servletHolder.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.PackagesResourceConfig");

        context.addServlet(servletHolder, "/*");
        server.start();

    }
}
