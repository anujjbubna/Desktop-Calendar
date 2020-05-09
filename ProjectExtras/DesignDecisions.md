#Design Decisions

##Facade Design Pattern
###Classes involved:
CalendarSystem, EventSystem, AlertSystem and MemoSystem

###Why was it implemented:
If we called to create events, alerts and memos through CalendarSystem, it would have been more than one responsibility for our
CalendarSystem and would make our class very long needlessly. This was a lot of work for a single class. So, we added classes
like EventSystem, AlertSystem and MemoSystem which would individually act with Events,Alerts and Memos respectively.

##Single Responsibility Principle
###Classes involved:
All classes in our Phase 2

###Why was it implemented:
To avoid any complexities amongst a group of six working in this project, we made sure that each class had a single responsibility.
This ensured that without getting too deep into a single class, we looked at how one would just look at the name of a class and realize
it's responsibility.

##Dependency injection
###Classes involved:
Users, User

###Why was it implemented:
To avoid hard dependency, we created a User and added it to a list of users in Users.


##Functionality and Designs improved from Phase 1 -
1. Removal of the interface Generatables.
    We removed the interface Generatables as it particularly has no use to AlertGenerator or SeriesGenerator.

2. Added functionality which were missing - Functionality such as the ability to convert an existing event to a series
    event and searching for an event through tags were implemented. We also improved the alert system which prompt alerts on time.

3. Improved the text user interface into a GUI which made it easier for the user to interact with their calendar.

##Observer Design Pattern
###Classes involved:
1) ClockGui (Observable) and AlertTrackerGui (Observer)

2) EventCreationGui (Observable), CalenderSystemsGui (Observable), MemoGui (Observable), CalGui (Observer)

###Why was it implemented:

1) ClockGui runs a JavaFX Timeline which runs our internal clock every second. This allows us to change the speed of 
time within our calender as we can set 1 real second into 1 (sec,min,hour,day, week). 
ClockGui notifies its observer every second with the new DateTime (Our internal class for handing dates and times). 
AlertTrackerGui was responsible for checking to see if there should be any Alert popups within that second 
(Note if the time was speed up it would check within that range i.e that minute or hour or ...). 
When update was called it would do said check, and then display any needed Alerts. In using the observer, we were able to
decouple the classes so ClockGui wasn't dependant on ClockGui.

2)  The class EventCreationGui, CalenderSystemsGui and MemoGui are all able to change what the screen would show by
modifying the current events, which calenders are enabled, and modifying memos respectively. CalGui is the central 
display object. Thus when its update method was called, it called the appropriate methods to redraw the the screen. 
We were able to decouple CalGui from its Observables and thus remove unwanted dependencies.


## Singleton Design Pattern
###Classes involved:
Theme

###Why was it implemented:
We used the singleton design pattern and haveing a single static class theme allowed us to easily implement dark mode.
Since the display should only ever be in 1 theme (Either light or dark) we would only need one theme object. Without the
singleton we would have had to pass an instance of theme to every single class which needs to set its stylesheets (Theme).
But since only 1 instance would ever be required we can have Theme hold a static instance of itself. Then we can easily
call Theme.getInstance().getStylesheet() from anywhere in the project to get the current the current stylesheet. This also allows
for easy extensibility as we could add more stylesheets to theme or change them out without changing any of the other code. 
    
