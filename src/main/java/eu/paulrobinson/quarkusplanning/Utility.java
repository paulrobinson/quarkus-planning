package eu.paulrobinson.quarkusplanning;

import org.kohsuke.github.GHIssue;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {

    public static String testString = "This is a tracker issue for all Security issues that should be part of 1.0.\n" +
            "\n" +
            "*  (**deferred**) JPA Identity Provider (optional) #4347\n" +
            "- [ ] FORM based auth support for simple applications (essential) #4348\n" +
            "- [x] #4334 CDI security interceptor for @RolesAllowed etc (essential) \n" +
            "- [x] CDI security interceptor for @RolesAllowed etc (essential) #4334\n" +
            "- [x] Unified HTTP permissions layer (essential)\n" +
            "- [ ] HashiCorp Vault: #2764 (mostly done)\n" +
            "- [x] Elytron JDBC IdentityProvider:#3586 (mostly done)\n" +
            "- [x] [OIDC enhancements](https://github.com/quarkusio/quarkus/issues?utf8=%E2%9C%93&q=is%3Aissue+is%3Aopen+label%3A%22component%3Aoidc%22) - (To be assigned to Pedroigor)";

    public static String[] getCheckListRows(String description) {

        Pattern checklistPattern = Pattern.compile("- \\[([ x])](.*)");
        Pattern issueIDPattern = Pattern.compile("#(\\S+)");


        Matcher checklistMatches = checklistPattern.matcher(description);
        while (checklistMatches.find()) {
            String checkStatus = checklistMatches.group(1).trim();
            String fullDescription = checklistMatches.group(2).trim();

            Matcher issueIDMatcher = issueIDPattern.matcher(fullDescription);

            String issueID = null;
            if (issueIDMatcher.find()) {
                issueID = issueIDMatcher.group(1);
            } else {
                System.out.println("not found: " + fullDescription);
            }

            System.out.println("(" + checkStatus + ") (" + issueID + ") " + fullDescription);

        }
        return null;
    }

    public static void main(String[] args) {

        Utility.getCheckListRows(Utility.testString);
    }

}
