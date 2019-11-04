package eu.paulrobinson.quarkusplanning;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHUser;

import java.io.IOException;


public class GHSubTask {

    private GHIssue linkedGHIssue;

    private boolean epicChecklistStatus;

    private String epicChecklistDescription;

    public GHSubTask(GHIssue linkedGHIssue, boolean epicChecklistStatus, String epicChecklistDescription) {
        this.linkedGHIssue = linkedGHIssue;
        this.epicChecklistStatus = epicChecklistStatus;
        this.epicChecklistDescription = epicChecklistDescription;
    }

    public boolean hasLinkedGHIssue() {
        return linkedGHIssue != null;
    }

    public GHIssue getLinkedGHIssue() {
        return linkedGHIssue;
    }

    public boolean isChecked() {
        return epicChecklistStatus;
    }

    public String getEpicChecklistDescription() {
        return epicChecklistDescription;
    }
}
