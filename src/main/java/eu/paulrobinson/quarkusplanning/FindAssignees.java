package eu.paulrobinson.quarkusplanning;

import org.kohsuke.github.*;
import sun.java2d.loops.MaskFill;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FindAssignees {

    public static final String GH_ORGANIZATION = "quarkusio";
    public static final String GH_REPO = "quarkus";

    public static final int MILESTONE_1_0_0 = 38;
    //public static final int[] EPIC_IDS = {4369};

    public static void main(String[] args) throws Exception {

        GitHub github = GitHub.connect();

        GHOrganization org =  github.getOrganization(GH_ORGANIZATION);
        GHRepository quarkusRepo = org.getRepository(GH_REPO);


        List<GHEpic> allEpics = GHEpic.loadOpenEpics(quarkusRepo, quarkusRepo.getMilestone(MILESTONE_1_0_0));

        System.out.println("\n\n\n*** Open issues with no assignee ***");
        for (GHEpic epic : allEpics) {
            System.out.println("\n\n== #" + epic.getEpicIssue().getNumber() + " " + epic.getEpicIssue().getTitle() + " ==");
            for (GHIssue issue : epic.getChildIssues()) {
                if (issue.getAssignee() == null && issue.getState().equals(GHIssueState.OPEN)) {
                    System.out.println("#" + issue.getNumber() + " " + issue.getTitle());
                }
            }
        }

        Set<String> assignees = new HashSet<>();
        for (GHEpic epic : allEpics) {
            for (GHIssue issue : epic.getChildIssues()) {
                if (issue.getAssignee() != null && issue.getState().equals(GHIssueState.OPEN)) {
                    assignees.add(issue.getAssignee().getName());
                }
            }
        }

        System.out.println("\n\n\n*** Users with work assigned ***");
        for (String user : assignees) {
            System.out.println(user);
        }

    }
}
