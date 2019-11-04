package eu.paulrobinson.quarkusplanning;

import org.kohsuke.github.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GHEpic {

    private GHIssue epicIssue;

    private List<GHSubTask> subTasks;

    private GHEpic(GHIssue epicIssue) throws QuarkusPlanningException {
        this.epicIssue = epicIssue;
        subTasks = parseSubTasksFromDescription(epicIssue.getBody());
    }

    public static List<GHEpic> loadOpenEpics(GHRepository repo, GHMilestone milestone) throws QuarkusPlanningException {
        try {
            List<GHEpic> epicsToReturn = new ArrayList<>();
            for (GHIssue issue : repo.getIssues(GHIssueState.OPEN, milestone)) {
                if (isEpic(issue)) {
                    epicsToReturn.add(new GHEpic(issue));
                }
            }
            return epicsToReturn;
        } catch (IOException e) {
            throw new QuarkusPlanningException("Error finding open Epics", e);
        }
    }


    public List<GHSubTask> parseSubTasksFromDescription(String description) throws QuarkusPlanningException {

        List<GHSubTask> subTasks = new ArrayList<>();

        Pattern checklistPattern = Pattern.compile("- \\[([ x])](.*)");
        Pattern issueIDPattern = Pattern.compile("#(\\d+)");

        Matcher checklistMatches = checklistPattern.matcher(description);
        while (checklistMatches.find()) {

            boolean checkStatus = parseChecklistState(checklistMatches.group(1).trim());

            String fullDescription = checklistMatches.group(2).trim();
            Matcher issueIDMatcher = issueIDPattern.matcher(fullDescription);

            GHIssue linkedIssue = null;
            if (issueIDMatcher.find()) {
                try {
                    linkedIssue = epicIssue.getRepository().getIssue(parseIssueID(issueIDMatcher.group(1)));
                } catch (IOException e) {
                    throw new QuarkusPlanningException("Error looking up linked issue", e);
                }
            }
            subTasks.add(new GHSubTask(linkedIssue, checkStatus, fullDescription));

        }
        return subTasks;
    }

    private static boolean isEpic(GHIssue issue) throws QuarkusPlanningException {

        try {
            for (GHLabel label : issue.getLabels()) {
                if (label.getName().equals("Epic")) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            throw new QuarkusPlanningException("Error looking up labels on issue" , e);
        }
    }


    private int parseIssueID(String issueId) throws QuarkusPlanningException {
        try {
            int issueIdInt = Integer.parseInt(issueId);

            if (issueIdInt <= 0) {
                throw new QuarkusPlanningException("Invalid issueID (cannot be negative): " + issueId);
            }
            return issueIdInt;

        } catch (NumberFormatException | NullPointerException e) {
            throw new QuarkusPlanningException("Error parsing IssueID, expected a positive integer. Got: '" + issueId + "'", e);
        }
    }

    private boolean parseChecklistState(String checkListState) throws  QuarkusPlanningException {

        if (!checkListState.equals("") && !checkListState.equals("x")) {
            throw new QuarkusPlanningException("Unexpected checklist state: '" + checkListState + "'");
        }
        return checkListState.equals("x");
    }


    public List<GHSubTask> getSubTasks() {
        return subTasks;
    }

    public GHIssue getEpicIssue() {
        return epicIssue;
    }

    public String getTitle() {
        return epicIssue.getTitle();
    }

}
