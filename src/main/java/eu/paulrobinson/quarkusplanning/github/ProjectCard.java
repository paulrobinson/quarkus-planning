package eu.paulrobinson.quarkusplanning.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = false)
public class ProjectCard {

    public String content_url;

}
