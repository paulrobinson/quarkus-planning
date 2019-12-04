package eu.paulrobinson.quarkusplanning.api;

import eu.paulrobinson.quarkusplanning.GHEpic;
import eu.paulrobinson.quarkusplanning.GenerateReport;
import eu.paulrobinson.quarkusplanning.QuarkusPlanningException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/planning")
public class ExampleResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String health() {
        return "up";
    }

    @GET
    @Path("/epics")
    @Produces(MediaType.APPLICATION_JSON)
    public List<GHEpic> getEpics() throws QuarkusPlanningException {
        List<GHEpic> epics = GenerateReport.getEpics();
        return epics;
    }

    @GET
    @Path("/dump")
    @Produces(MediaType.TEXT_PLAIN)
    public String dumpReport() throws Exception {

        GenerateReport.main(null);

        return "Dumped report";
    }




}