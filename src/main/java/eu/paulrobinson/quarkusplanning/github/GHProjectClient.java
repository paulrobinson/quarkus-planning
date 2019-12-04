package eu.paulrobinson.quarkusplanning.github;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import java.util.List;

@Path("/projects/")
@RegisterRestClient
@Produces("application/json")
@Consumes("application/json")
public interface GHProjectClient {

    @GET
    @Path("/3365484/columns")
    @Produces("application/json")
    public ProjectColumn[] getColumns(@HeaderParam("Accept") String accept, @HeaderParam("Authorization") String authorization);

    @GET
    @Path("/columns/{column_id}/cards")
    @Produces("application/json")
    public ProjectCard[] getCardsInColumn(@HeaderParam("Accept") String accept, @HeaderParam("Authorization") String authorization, @PathParam("column_id") Integer columnId);
}
