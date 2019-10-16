package eu.paulrobinson.quarkusplanning;

import org.kohsuke.github.GHIssue;

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
