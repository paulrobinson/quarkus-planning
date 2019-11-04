package eu.paulrobinson.quarkusplanning.api;

import eu.paulrobinson.quarkusplanning.GenerateReport;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/planning")
public class ExampleResource {

    @GET
    @Path("/dump")
    @Produces(MediaType.TEXT_PLAIN)
    public String dumpReport() throws Exception {

        long before = System.currentTimeMillis();
        GenerateReport.main(null);
        long after = System.currentTimeMillis();

        long duration = (after - before) / 1000;

        return "Dumped report to console in " + duration + "s";
    }


}