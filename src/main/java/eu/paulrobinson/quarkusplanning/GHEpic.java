package eu.paulrobinson.quarkusplanning;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHMilestone;
import org.kohsuke.github.GHRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GHEpic {

    private GHIssue epicIssue;

    private List<GHIssue> childIssues = new ArrayList<GHIssue>();

    public GHEpic(GHIssue epicIssue) throws IOException {
        this.epicIssue = epicIssue;

        String issueBody = epicIssue.getBody();

        for (String line : issueBody.split(System.getProperty("line.separator"))) {
            childIssues.addAll(extractIssueIds(line));
        }
    }

    public static List<GHEpic> loadOpenEpics(GHRepository repo, GHMilestone milestone) throws IOException {
        List<GHEpic> allEpics = new ArrayList<>();
        for (GHIssue issue : repo.getIssues(GHIssueState.OPEN, milestone)) {
            allEpics.add(new GHEpic(issue));
        }
        return allEpics;
    }

    private List<GHIssue> extractIssueIds(String text) throws IOException {
        Pattern ISSUE_PATTERN = Pattern.compile("#(\\S+)");
        Matcher mat = ISSUE_PATTERN.matcher(text);
        List<GHIssue> issueIDs=new ArrayList<GHIssue>();
        while (mat.find()) {
            String issueID = mat.group(1);
            if (isIssueID(issueID)) {
                GHIssue childIssue = epicIssue.getRepository().getIssue(Integer.parseInt(issueID));
                issueIDs.add(childIssue);
            }
        }
        return issueIDs;
    }


    public static boolean isIssueID(String issueId) {
        try {
            int issueIdInt = Integer.parseInt(issueId);

            if (issueIdInt <= 0) {
                return false;
            }

            return true;
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }

    }

    public List<GHIssue> getChildIssues() {
        return childIssues;
    }

    public GHIssue getEpicIssue() {
        return epicIssue;
    }
}
