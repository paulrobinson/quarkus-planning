package eu.paulrobinson.quarkusplanning.github;

import eu.paulrobinson.quarkusplanning.GHEpic;
import eu.paulrobinson.quarkusplanning.QuarkusPlanningException;

import java.util.*;

public class Project {

    private Map<String, List<GHEpic>> projectStructure = new HashMap<>();

    public void addColumn(String columnName) throws QuarkusPlanningException {
        if (projectStructure.get(columnName) != null) {
            throw new QuarkusPlanningException("Column already added: " + columnName);
        }
        projectStructure.put(columnName, new ArrayList<>());
    }

    public void addEpicToColumn(String columnName, GHEpic epic) throws QuarkusPlanningException {
        List<GHEpic> columnEpics = projectStructure.get(columnName);
        if (columnEpics == null) {
            throw new QuarkusPlanningException("Column doesn't exist " + columnName);
        }
        columnEpics.add(epic);
    }

    public List<GHEpic> getEpics(String columnName) throws QuarkusPlanningException {
        List<GHEpic> columnEpics = projectStructure.get(columnName);
        if (columnEpics == null) {
            throw new QuarkusPlanningException("Column doesn't exist " + columnName);
        }
        return columnEpics;
    }

    public Set<String> getColumnNames() {
        return projectStructure.keySet();
    }

}
