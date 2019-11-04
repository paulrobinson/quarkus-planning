package eu.paulrobinson.quarkusplanning;

import org.kohsuke.github.*;

import java.io.IOException;
import java.util.List;

public class GenerateReport {

    public static final String GH_ORGANIZATION = "quarkusio";
    public static final String GH_REPO = "quarkus";
    public static final String GH_REPO_URL = "https://github.com/" + GH_ORGANIZATION + "/" + GH_REPO;


    public static final int MILESTONE_1_0_0 = 38;

    public static void main(String[] args) throws Exception {

        System.out.println("Connecting...");
        GitHub github = GitHub.connect();

        GHOrganization org =  github.getOrganization(GH_ORGANIZATION);
        GHRepository quarkusRepo = org.getRepository(GH_REPO);

        System.out.println("Loading...");
        List<GHEpic> allEpics = GHEpic.loadOpenEpics(quarkusRepo, quarkusRepo.getMilestone(MILESTONE_1_0_0));

        userWorkReport(allEpics);

        epicSummaryReport(allEpics);

        findChecklistSatusMismatches(allEpics);

    }

    public static void findChecklistSatusMismatches(List<GHEpic> allEpics) {
        System.out.println("\n\n\n*** Epics with mismatched checkboxes ***");
        for (GHEpic epic : allEpics) {
            for (GHSubTask subTask : epic.getSubTasks()) {
                if (subTask.hasLinkedGHIssue()) {
                    boolean checkboxStatus = subTask.isChecked();
                    boolean issueStatus = subTask.getLinkedGHIssue().getState().equals(GHIssueState.CLOSED);
                    if (checkboxStatus != issueStatus) {
                        System.out.println("See " + epic.getEpicIssue().getHtmlUrl() + " and check line: '" + subTask.getEpicChecklistDescription());
                    }
                }
            }
        }
    }

    public static void epicSummaryReport(List<GHEpic> allEpics) {
        System.out.println("\n\n\n*** Epic Summary ***");
        for (GHEpic epic : allEpics) {
            System.out.println("\n= " + epic.getTitle());
            for (GHSubTask subTask : epic.getSubTasks()) {

                Integer issueID = null;
                if (subTask.getLinkedGHIssue() != null) {
                    issueID = subTask.getLinkedGHIssue().getNumber();
                }

                System.out.println("(" + subTask.isChecked() + ") (" + issueID + ") " + subTask.getEpicChecklistDescription());
            }
        }
    }

    public static void userWorkReport(List<GHEpic> allEpics) throws IOException {
        System.out.println("\n\n\n*** Open issues with no assignee ***");
        for (GHEpic epic : allEpics) {
            System.out.println("\n\n== #" + epic.getEpicIssue().getNumber() + " " + epic.getEpicIssue().getTitle() + " ==");
            for (GHSubTask issue : epic.getSubTasks()) {
                if (issue.getLinkedGHIssue() != null && issue.getLinkedGHIssue().getAssignee() == null && issue.getLinkedGHIssue().getState().equals(GHIssueState.OPEN)) {
                    System.out.println("#" + issue.getLinkedGHIssue().getNumber() + " " + issue.getLinkedGHIssue().getTitle());
                }
            }
        }

        CountingSet<String> assignees = new CountingSet<>();
        for (GHEpic epic : allEpics) {
            for (GHSubTask issue : epic.getSubTasks()) {
                if (issue.getLinkedGHIssue() != null && issue.getLinkedGHIssue().getAssignee() != null && issue.getLinkedGHIssue().getState().equals(GHIssueState.OPEN)) {
                    assignees.add(issue.getLinkedGHIssue().getAssignee().getName());
                }
            }
        }

        System.out.println("\n\n\n*** Users with work assigned ***");
        for (String user : assignees) {
            System.out.println(assignees.getOccurances(user) + " " + user);
        }
    }

}
