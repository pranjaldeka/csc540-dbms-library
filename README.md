# csc540-dbms-library

Database management (CSC540) online library project


##Team members:

Anindita Mozumder (amozumd@)  
Devarshi Pratap Singh (dsingh4@)  
Pranjal Deka (pdeka@)  
Simerdeep Singh Jolly (sjolly@)  
Sudhansu Shekhar Singh (ssingh25@)

###Running the application:

Run the following command with the executable jar (dbms-csc540.jar) from command line

    java -jar dbms-csc540.jar


###Using the application:

On running the application, a prompt will appear. Enter 1 to login using appropriate credentials

Upon successful login, user will be presented with a menu. 

    1. Profile 			
    2. Resources
    3. Checked-Out Resources 		 
    4. Notification
    5. Due-Balance				 
    6. Logout

1. Selecting profile will let the user view his/her profile. User can also edit profile details using modify profile option inside profile

2. Selecting resources will prompt the user to select from publications, rooms and cameras. Upon selecting the appropriate option, user can checkout a book , reserve a room, reserve a camera.

3. Selecting Checked-Out Resources will prompt the user to select from publications, rooms and cameras. Upon selecting books inside publications, user can view the list of books checked out by him/her. User can either return a book or renew a book. On selecting rooms, user can view the list of rooms reserved by the user. If the user has reserved a room, the user can check out a room. On selecting cameras, user can view the list of cameras reserved by the user. User can checkout a camera from the list with appropriate constraints.

4. Selecting Notification will lists down the due dates for the un-returned resources.

5. Selecting Due-Balance will show the due balances of the user for all the resources.

6. Selecting Logout will destroy the session of the user.


Jars:
-----------------
ojdbc14.jar : Add this jar to project build settings.