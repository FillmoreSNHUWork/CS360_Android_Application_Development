# CS360_Android_Application_Development
Repo for Weight Tracking Android Application Java/Kotlin/Android Studio


*Briefly summarize the requirements and goals of the app you developed. What user needs was this app designed to address?
The requirements for this weight tracking applications was as follows:
- Login screen with username and password prompt
- Login screen with button to create a new user
- Password requirements enforced
- Ability to track user weight and date entered
- Ability to track a goal weight
- Ability to delete a weight record
- Ability to edit a weight record
- Prompt user for SMS permissions for Goal weight notifications
- SMS message sent upon successful completion of recording reaching a goal weight.
 
*What screens and features were necessary to support user needs and produce a user-centered UI for the app? How did your UI designs keep users in mind? Why were your designs successful?
I really only needed three different screens to support these requirements - a main login screen, a main display screen that displayed weights and dates with buttons to add/delete/edit weights and add goals weights. I then added a third screen for adding goal weights to allow the user to be brought to an "add weight" screen to add the weight. I could have added more screens and actions to make my application more dynamic but it wasn't needed for this simple product. I think this design is successful just because of its simplicity - all of the applications features being located between three screens leaves little room for users to get lost or spend time looking for features. If you simply need to track your weight on your phone, this application would fit the need perfectly!

*How did you approach the process of coding your app? What techniques or strategies did you use? How could those be applied in the future?
I approached this project by first designing my UI and the flow of my application navigation through those screens. I then focused on the big actions happening between the screens and started to build my business logic based on those actions. For example - I built a dbhelper class to support database creation upon application launch, to support tracking user login and passwords and to support tracking weights and goal weights. I built a WeightData class so I could represent database records as class object, encapsulating them and providing a structured way to pass them around my application. These classes could easily be applied or extended in the future - for example my DbHelper class is a great example for me to base future Dbhelper classes off of, I can easily reuse the code and change the table names to fit whatever project I'm working on. 

*How did you test to ensure your code was functional? Why is this process important and what did it reveal?
I tested through Android studio, constantly compiling and debugging my code as I wrote. I used the emulator feature to test the different screens and features of my application, resetting the data and uninstalling and reinstalling the application and I added and removed code.

*Considering the full app design and development process, from initial planning to finalization, where did you have to innovate to overcome a challenge?
I actually ended up building the WeightData class later in the project because I was having issues tying individual weight records to a user ID based on how I had originally structured my table. Adding the WeightData class actually uncovered that my problem was I was using an auto-incrented table ID rather than the user ID as my primary key and allowed my to fix that issue. Using a class object to structure the data was just a clean, nice way to organize my data and access components needed across methods.

*In what specific component from your mobile app were you particularly successful in demonstrating your knowledge, skills, and experience?
I think my back-end code is really solid and could easily be improved on through future iterations. I've got no bugs at all in my code - the application works from end to end and handles all issues gracefully without crashing the application. I think I've seperated functionality nicely between my classes encapsulating functionality while leaving room for modularity. 

