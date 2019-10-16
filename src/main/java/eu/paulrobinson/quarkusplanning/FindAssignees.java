package eu.paulrobinson.quarkusplanning;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.util.ArrayList;
import java.util.List;

public class FindAssignees {

    public static final String GH_ORGANIZATION = "quarkusio";
    public static final String GH_REPO = "quarkus";

    //TODO: get this list by querying project: https://github.com/orgs/quarkusio/projects/5
    public static final int[] EPIC_IDS = {4566, 4369, 4366, 4610, 4611, 4616, 4617};
    //public static final int[] EPIC_IDS = {4369};

    public static void main(String[] args) throws Exception {

        GitHub github = GitHub.connect();

        GHOrganization org =  github.getOrganization(GH_ORGANIZATION);
        GHRepository quarkusRepo = org.getRepository(GH_REPO);

        List<GHEpic> allEpics = new ArrayList<>();
        for (int epicID : EPIC_IDS) {
            allEpics.add(new GHEpic(quarkusRepo.getIssue(epicID)));
        }

        System.out.println("\n\n\n*** Issues with no assignee ***");
        for (GHEpic epic : allEpics) {
            System.out.println("\n\n== #" + epic.getEpicIssue().getNumber() + " " + epic.getEpicIssue().getTitle() + " ==");
            for (GHIssue issue : epic.getChildIssues()) {
                if (issue.getAssignee() == null) {
                    System.out.println("#" + issue.getNumber() + " " + issue.getTitle());
                }
            }
        }

    }
}
