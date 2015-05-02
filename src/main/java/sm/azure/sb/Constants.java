package sm.azure.sb;

/**
 * Created by saschamoellering on 01/05/15.
 */
public final class Constants {

    public static final String CONTEXT_ROOT = "/service-bus-producer";
    public static final String API = "/api/v1";
    public static final String HEALTH_CHECK_URL = CONTEXT_ROOT + "/admin/health/check";
    public static final String PROVIDER_URL = "file:///tmp/servicebus.properties";
    public static final String CONTEXT_FACTORY = "org.apache.qpid.amqp_1_0.jms.jndi.PropertiesFileInitialContextFactory";
    public static final String QUEUE_NAME = "redis_update_queue";

    //Monitoring constants
    public static final String HEALTH_NAME = "healthCheckName";
    public static final String HEALTH_STATUS = "healthStatus";
    public static final String HEALTH = "health";
    public static final String DETAIL = "detail";
    public static final String HEALTHY = "HEALTHY";
    public static final String UNHEALTHY = "UNHEALTHY";

    private Constants() {
    }
}
