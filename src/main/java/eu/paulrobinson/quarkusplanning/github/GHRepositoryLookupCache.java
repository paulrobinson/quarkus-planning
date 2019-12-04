package eu.paulrobinson.quarkusplanning.github;

import eu.paulrobinson.quarkusplanning.QuarkusPlanningException;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class GHRepositoryLookupCache {

    private Map<String, GHRepository> cache = new HashMap<>();

    private GitHub github;

    public GHRepositoryLookupCache() throws QuarkusPlanningException {
        try {
            github = GitHub.connect();
        } catch (IOException e) {
            throw new QuarkusPlanningException("Error connecting to GitHub API", e);
        }
    }

    public GHRepository lookupByIssue(GHIssue issue) throws QuarkusPlanningException {

        try {

            String repoID = getRepoID(issue);
            GHRepository ghRepository = cache.get(repoID);
            if (ghRepository == null) {

                ghRepository = github.getRepository(repoID);
                cache.put(repoID, ghRepository);
            }

            return ghRepository;
        } catch (IOException e) {
            throw new QuarkusPlanningException("Error looking up GH Repo", e);
        }
    }

    private String getRepoID(GHIssue issue) {
        String[] urlSplit = issue.getUrl().getPath().split("/");
        return urlSplit[2] + "/" + urlSplit[3];
    }

}
