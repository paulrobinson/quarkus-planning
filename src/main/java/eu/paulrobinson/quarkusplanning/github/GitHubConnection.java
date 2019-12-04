package eu.paulrobinson.quarkusplanning.github;

import eu.paulrobinson.quarkusplanning.GHEpic;
import eu.paulrobinson.quarkusplanning.GHSubTask;
import eu.paulrobinson.quarkusplanning.QuarkusPlanningException;
import org.kohsuke.github.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitHubConnection {

    private static List<GHEpic> cachedEpics;

    private static ReentrantLock loadLock = new ReentrantLock();

    public static List<GHEpic> loadOpenEpics(String organization, String repository, int milestone) throws QuarkusPlanningException {

        try {
            loadLock.lock();

            if (cachedEpics != null) {
                return cachedEpics;
            }

            try {
                GitHub github = GitHub.connect();
                GHOrganization ghOrg =  github.getOrganization(organization);
                GHRepository ghRepo = ghOrg.getRepository(repository);
                GHMilestone ghMilestone = ghRepo.getMilestone(milestone);

                List<GHEpic> epicsToReturn = new ArrayList<>();

                List<GHIssue> issues = loadEpicIssues(ghRepo, ghMilestone);
                for (GHIssue issue : issues) {
                    List<GHSubTask> subTasks = parseSubTasksFromDescription(issue);
                    epicsToReturn.add(new GHEpic(issue, subTasks));
                }

                cachedEpics = epicsToReturn;

                return epicsToReturn;
            } catch (IOException e) {
                throw new QuarkusPlanningException("Error connecting to GitHub and loading issue data", e);
            }
        } finally {
            loadLock.unlock();
        }
    }

    public static void clearCache() {
        cachedEpics = null;
    }

    private static List<GHIssue> loadEpicIssues(GHRepository repo, GHMilestone milestone) throws QuarkusPlanningException {
        try {
            List<GHIssue> issuesToReturn = new ArrayList<>();
            for (GHIssue issue : repo.getIssues(GHIssueState.OPEN, milestone)) {
                if (isEpic(issue)) {
                    issuesToReturn.add(issue);
                }
            }
            return issuesToReturn;
        } catch (IOException e) {
            throw new QuarkusPlanningException("Error finding open Epics", e);
        }
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

    private static List<GHSubTask> parseSubTasksFromDescription(GHIssue issue) throws QuarkusPlanningException {

        List<GHSubTask> subTasks = new ArrayList<>();

        Pattern checklistPattern = Pattern.compile("- \\[([ x])](.*)");
        Pattern issueIDPattern = Pattern.compile("#(\\d+)");

        Matcher checklistMatches = checklistPattern.matcher(issue.getBody());
        while (checklistMatches.find()) {

            boolean checkStatus = parseChecklistState(checklistMatches.group(1).trim());

            String fullDescription = checklistMatches.group(2).trim();
            Matcher issueIDMatcher = issueIDPattern.matcher(fullDescription);

            GHIssue linkedIssue = null;
            if (issueIDMatcher.find()) {
                try {
                    linkedIssue = issue.getRepository().getIssue(parseIssueID(issueIDMatcher.group(1)));
                } catch (IOException e) {
                    throw new QuarkusPlanningException("Error looking up linked issue", e);
                }
            }
            subTasks.add(new GHSubTask(linkedIssue, checkStatus, fullDescription));

        }
        return subTasks;
    }


    private static int parseIssueID(String issueId) throws QuarkusPlanningException {
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

    private static boolean parseChecklistState(String checkListState) throws  QuarkusPlanningException {

        if (!checkListState.equals("") && !checkListState.equals("x")) {
            throw new QuarkusPlanningException("Unexpected checklist state: '" + checkListState + "'");
        }
        return checkListState.equals("x");
    }
}