package sm.azure.sb.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sm.azure.sb.Constants;
import sm.azure.sb.bus.BusProducer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by saschamoellering on 01/05/15.
 */
@Path(Constants.CONTEXT_ROOT + Constants.API)
public class RestApi {
    private static Logger logger = LogManager.getLogger(RestApi.class);

    @POST
    @Path("/messages/{payload}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendMessage(@PathParam("payload") String payload) {
        try {
            logger.info("In sendMessage");
            BusProducer producer = BusProducer.getInstance();
            producer.sendMessage(payload);

            return Response.status(Response.Status.OK).build();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

}
