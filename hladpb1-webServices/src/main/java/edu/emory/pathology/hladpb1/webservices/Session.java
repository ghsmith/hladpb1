package edu.emory.pathology.hladpb1.webservices;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

/**
 * This class returns session information. All updates to to attributes are
 * persisted in the session and are disposed of when the session ends.
 * 
 * @author ghsmith
 */
@Path("session")
public class Session {

    @GET
    @Produces("application/json")
    public String getJson() {
        return SessionFilter.sessionMutex.get();
    }

    @PUT
    @Path("reset")
    @Produces("application/json")
    public void putReset(@Context HttpServletRequest request) {
        request.getSession().invalidate();
    }

}
