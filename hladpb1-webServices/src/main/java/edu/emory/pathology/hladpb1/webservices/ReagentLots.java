package edu.emory.pathology.hladpb1.webservices;

import edu.emory.pathology.hladpb1.imgtdb.data.ReagentLot;
import java.util.List;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

/**
 * This class implements the ReagentLots RESTful web services.
 * 
 * @author ghsmith
 */
@Path("reagentLots")
public class ReagentLots {

    @GET
    @Produces("application/json")
    public List<ReagentLot> getJson() {
        return SessionFilter.reagentLotFinder.get().getReagentLots();
    }
    
}
