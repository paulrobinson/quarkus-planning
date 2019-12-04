package eu.paulrobinson.quarkusplanning.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectColumn {

        public int id;
        public String url;
        public String project_url;
        public String cards_url;
        public String name;
}
