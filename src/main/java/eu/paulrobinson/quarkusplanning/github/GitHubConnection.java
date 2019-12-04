package eu.paulrobinson.quarkusplanning.github;

import eu.paulrobinson.quarkusplanning.GHEpic;
import eu.paulrobinson.quarkusplanning.GHSubTask;
import eu.paulrobinson.quarkusplanning.QuarkusPlanningException;
import org.kohsuke.github.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class GitHubConnection {

    @Inject
    public GHRepositoryLookupCache ghRepositoryLookupCache;

    public static final String QUERY_ALL_EPICS = "org:quarkusio is:open label:Epic";

    private List<GHEpic> cachedEpics;

    private ReentrantLock loadLock = new ReentrantLock();

    public List<GHEpic> loadOpenEpics() throws QuarkusPlanningException {

        try {
            loadLock.lock();

            if (cachedEpics != null) {
                return cachedEpics;
            }

            List<GHEpic> epicsToReturn = new ArrayList<>();
            for (GHIssue issue : loadAllEpicIssues()) {
                List<GHSubTask> subTasks = parseSubTasksFromDescription(issue);
                epicsToReturn.add(new GHEpic(issue, subTasks));
            }
            cachedEpics = epicsToReturn;

            return epicsToReturn;
        }
        finally {
            loadLock.unlock();
        }
    }

    public void clearCache() {
        cachedEpics = null;
    }

    private  List<GHIssue> loadAllEpicIssues() throws QuarkusPlanningException {
        try {
            GitHub github = GitHub.connect();
            GHIssueSearchBuilder searchBuilder = github.searchIssues().isOpen().q(QUERY_ALL_EPICS);

            List<GHIssue> issuesToReturn = new ArrayList<>();
            for (GHIssue issue : searchBuilder.list()) {
                issuesToReturn.add(issue);
            }
            return issuesToReturn;
        } catch (IOException e) {
            throw new QuarkusPlanningException("Error finding open Epics", e);
        }
    }


    private List<GHSubTask> parseSubTasksFromDescription(GHIssue issue) throws QuarkusPlanningException {

        List<GHSubTask> subTasks = new ArrayList<>();
        GHRepository ghRepository = ghRepositoryLookupCache.lookupByIssue(issue);

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
                    Integer linkedIssueIDInteger = parseIssueID(issueIDMatcher.group(1));
                    linkedIssue = ghRepository.getIssue(linkedIssueIDInteger);
                } catch (IOException e) {
                    throw new QuarkusPlanningException("Error looking up linked issue", e);
                }
            }
            subTasks.add(new GHSubTask(linkedIssue, checkStatus, fullDescription));

        }
        return subTasks;
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
}
