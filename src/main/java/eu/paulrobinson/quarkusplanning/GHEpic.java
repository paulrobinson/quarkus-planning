package eu.paulrobinson.quarkusplanning;

import org.kohsuke.github.*;

import java.util.List;

public class GHEpic {

    private GHIssue epicIssue;

    private List<GHSubTask> subTasks;

    public GHEpic(GHIssue epicIssue, List<GHSubTask> subTasks) {
        this.epicIssue = epicIssue;
        this.subTasks = subTasks;
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
