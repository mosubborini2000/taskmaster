# taskmaster

## lab26

For today's lab ,I started to build an Android app that will be a main focus of the second half of the course: 
TaskMaster. While I started small today, over time this will grow to be a fully-featured application.

I just create a 3 pages home page i have two buttons:
1-move me to add task page that I can add task and submitt the task
2- all tasks that is empty until now just I have image

![Alt Text](screenshot/lab26/home.PNG)
![Alt Text](screenshot/lab26/addtask.PNG)
![Alt Text](screenshot/lab26/allTask.PNG)

## lab27

For today's lab , I updated the home page and added Settings and Task Detail Page ,the user name in home page will be the same user name that user enter in setting page.

Settings Page:

I Created a Settings page.allow users to enter their username and hit save.

Homepage:
I Created The main page different buttons with hardcoded task titles. When a user taps one of the titles,go to the Task Detail page, and the title at the top of the page should match the task title that was tapped on the previous page.

also I Created a Task Detail page used Lorem Ipsum description.

![Alt Text](screenshot/lab27/task1.PNG)
![Alt Text](screenshot/lab27/task2.PNG)
![Alt Text](screenshot/lab27/task3.PNG)
![Alt Text](screenshot/lab27/task4.PNG)

## lab28

For today's lab ..
I updated the home page to use a RecyclerView for displaying Task data. This has hardcoded Task data for now.
I created a Task class. A Task should have a title, a body, and a state. The state should be one of “new”, “assigned”, “in progress”, or “complete”.
when user click on anyone of tasks move him to details about this task 

![Alt Text](screenshot/lab28/lab281.PNG)
![Alt Text](screenshot/lab28/lab282.PNG)
![Alt Text](screenshot/lab28/lab283.PNG)


## lab29

For today's lab ..
I updated the add Task page to add task to room database .
I Updated a main page and detail when i click in one of tasks move me to detail of the class i added, to represent tabel in databse
I fetched the data from the database to the home's RecyclerView

![Alt Text](screenshot/lab29/lab291.PNG)
![Alt Text](screenshot/lab29/java292.PNG)
![Alt Text](screenshot/lab29/lab293.PNG)

## Lab31
For today's lab ..

Ensured espreeso tests are functional.

Refactored Main Activity and all code
![Alt Text](screenshot/lab31/sc1.PNG)
![Alt Text](screenshot/lab31/sc2.PNG)
![Alt Text](screenshot/lab31/sc3.PNG)

## Lab31
For today's lab ..

Updated all references to the Task data to instead use AWS Amplify to access data in DynamoDB instead of in Room.
Modified Add Task to save the data entered in as a Task to DynamoDB.
get all tasks from DynamoDB to render it in Main Activity
Ensured espreeso tests are work fine.

![Alt Text](screenshot/lab32/sc32.PNG)
![Alt Text](screenshot/lab32/sc322.PNG)