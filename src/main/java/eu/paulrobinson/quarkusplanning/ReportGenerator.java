package eu.paulrobinson.quarkusplanning;

import eu.paulrobinson.quarkusplanning.github.GitHubConnection;
import org.kohsuke.github.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;

@Singleton
public class ReportGenerator {

    @Inject GitHubConnection gitHubConnection;

    public ReportGenerator() {

    }

    public void dumpReport() throws Exception {

        List<GHEpic> allEpics = getEpics();

        //userWorkReport(allEpics);
        //epicSummaryReport(allEpics);
        findChecklistSatusMismatches(allEpics);

    }

    public void loadData() throws QuarkusPlanningException {
        gitHubConnection.loadOpenEpics();
    }

    public List<GHEpic> getEpics() throws QuarkusPlanningException {
        return gitHubConnection.loadOpenEpics();
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
        System.out.println("\n\n\n*** Epic Summary (Open issues) ***");
        for (GHEpic epic : allEpics) {
            System.out.println("\n= " + epic.getTitle());
            for (GHSubTask subTask : epic.getSubTasks()) {

                if (subTask.isChecked()) {
                    continue;
                }

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
