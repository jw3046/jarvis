
To use the custom test domain:

ant run -Ddomain=domains/test-gcal/gcal-test.xml



Custom domains:
./domains/test-gcal/
./domains/gcal/ --> currently doesnt work

Custom module:
./src/jarvis/modules/gcal/CalendarUpdate.java
./src/jarvis/modules/parsing/Semafor.java


Implementation Blueprints:
Define:

w: {yes, no} --> yes/no response
x: {birthday, anniversary, graduation, party, event} --> type of event
y: {name, date, time, gift, dress, venue, ...} --> type of slot
z: unbounded (depends on u_u parse results) --> actual value in slot

====================================
u_u
-----------------------------------
uncontrained user input

====================================
a_u
-----------------------------------
Type(x)
Inform(y,z)
Acknowledge(w)

====================================
eventtype
-----------------------------------
x

====================================
name, date, time, gift, dress, venue, ...
-----------------------------------
z

====================================
a_m
-----------------------------------
Confirm(y,z)
Ground(y,int,[[y, z],...])
Resolve(y,z,y,z)
AddEvent
QueryEvent

====================================
u_m
-----------------------------------
slot-filling output based on a_m




Notes:

Ideas for guessing:
Use Person to guess Place slot
Use Object to guess Person slot

