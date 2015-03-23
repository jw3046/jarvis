# jarvis


Our system uses SEMAFOR for frame-based parsing (NLU) and
Opendial for NLU, DM and NLG.

We will also use AT&T for ASR and TTS.


Notes:
I tried downloading SEMAFOR, but it requires 8Gb to run the parser. Instead, we can
just use the API call to retrieve the frame parse results as a JSON.



We will be using google calendar in the backend to keep track of user data.

The python files will be rewritten in java. They are just temporary scripts to test
out the functionality of our components.


To try out the google calendar authorization test, ie, login (JGcal class):

Build:
javac -d bin -Djava.ext.dirs=google-api-java-client/libs/:calendar src/Jgcal.java

Run:
java -cp "google-api-java-client/libs/*:calendar/*:bin" Jgcal



Similarly to try the CalendarSample class (from the google developers sample code)

Build:
javac -d bin -Djava.ext.dirs=google-api-java-client/libs/:calendar src/cmdline/*.java

Run:
java -cp "google-api-java-client/libs/*:calendar/*:apache-tomcat-8.0.20/lib/*:bin:resources" cmdline.CalendarSample

