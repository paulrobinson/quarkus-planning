Steps to use this

* Visit https://github.com/settings/tokens/new and create a new token. Don't check any boxes, basic read-only is all that is required
* Set the token in ~/.github. E.g add `oauth=abcde12345abcde12345abcde12345`
* Run `eu.paulrobinson.quarkusplanning.GenerateReport` from your IDE
* Observe a report of which issues are left unnasigned from the hard-coded list of epics in `eu.paulrobinson.quarkusplanning.GenerateReport`


Example report:

```
*** Issues with no assignee ***


== #4566 Platform & Tools MVP ==
#4361 document quarkus-extension.properties
#3962 Add Apache Camel extension available in project creation tooling
#4318 Setup CI job to test latest core with latest platform before release
#4423 Extension metadata and generation of extensions.json


== #4369 Security Tracker for 1.0 ==
#4347 Add a JPA Identity Extension
#2764 Support distributed configuration with HashiCorp Vault
#3586 Enhance Elytron Security extension to support a database as identity store


== #4366 HTTP features for 1.0 tracker ==
#4368 Removing locking in Vert.x router
#4332 AWS Lambda Virtual Channel Support
#4455 Read timeout for Servlet


== #4610 Create RSS test bed ==


== #4611 Pass critical MicroProfile TCK tests ==


== #4616 Tidy Extensions ==


== #4617 Release Activities ==
```