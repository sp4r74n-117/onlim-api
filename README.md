# onlim-api
Onlim API is a tool that touristic service providers utilize to generate social media posts by injecting data about their offers into some templates.

# How to run on localhost?
1.) execute main() of onlim/api/services/OnlimApiApplocation.java
2.) Send e.g. onlim/api/parser/test/resources/event.json as body of a POST request to http://localhost:8080/onlim-api/
3.) The HTTP response contains the generate template text (if any)