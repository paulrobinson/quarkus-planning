BEWARE: HACKED TOGETHER, NOT YET DESIGNED FOR PUBLIC CONSUMPTION.

Steps to use this

* Visit https://github.com/settings/tokens/new and create a new token. Don't check any boxes, basic read-only is all that is required
* Set the token in ~/.github. E.g add `oauth=abcde12345abcde12345abcde12345`
* ./mvnw compile quarkus:dev
* Visit  http://localhost:8080//planning/dump
* observe report on console