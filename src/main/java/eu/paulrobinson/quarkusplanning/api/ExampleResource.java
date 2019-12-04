package eu.paulrobinson.quarkusplanning.api;

import eu.paulrobinson.quarkusplanning.GHEpic;
import eu.paulrobinson.quarkusplanning.ReportGenerator;
import eu.paulrobinson.quarkusplanning.QuarkusPlanningException;
import eu.paulrobinson.quarkusplanning.github.GHProjectClient;
import eu.paulrobinson.quarkusplanning.github.Project;
import eu.paulrobinson.quarkusplanning.github.ProjectCard;
import eu.paulrobinson.quarkusplanning.github.ProjectColumn;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueSearchBuilder;
import org.kohsuke.github.GitHub;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/planning")
public class ExampleResource {


    @Inject
    ReportGenerator reportGenerator;

    @Inject
    @RestClient
    GHProjectClient ghProjectClient;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String health() {
        return "up";
    }

    @GET
    @Path("/epics")
    @Produces(MediaType.APPLICATION_JSON)
    public List<GHEpic> getEpics() throws QuarkusPlanningException {
        List<GHEpic> epics = reportGenerator.getEpics();
        return epics;
    }

    @GET
    @Path("/dump")
    @Produces(MediaType.TEXT_PLAIN)
    public String dumpReport() throws Exception {

        reportGenerator.dumpReport();

        return "Dumped report";
    }


    @GET
    @Path("/search")
    @Produces(MediaType.TEXT_PLAIN)
    public String search() throws Exception {

        GitHub github = GitHub.connect();
        GHIssueSearchBuilder searchBuilder = github.searchIssues().isOpen().q("org:quarkusio is:open label:Epic");
        for (GHIssue issue : searchBuilder.list()) {
            System.out.println(issue.getTitle());
        }

        return "Dumped search results";
    }

    @GET
    @Path("/project")
    @Produces(MediaType.APPLICATION_JSON)
    public void project() throws Exception {

        for (ProjectColumn column : ghProjectClient.getColumns("application/vnd.github.inertia-preview+json", "Bearer 05d5aa1717f0faadd8fcb800d44f07c73d578c57")) {
            System.out.println("***** " + column.name + " *****");
            ProjectCard[] projectCards = ghProjectClient.getCardsInColumn("application/vnd.github.inertia-preview+json", "Bearer 05d5aa1717f0faadd8fcb800d44f07c73d578c57", column.id);
            for (ProjectCard projectCard : projectCards) {
                System.out.println(projectCard.content_url);
            }
            System.out.println("\n");
        };

    }



}