# JHUsed

 This is a web application for JHU affiliated people to post, browse, and buy second hand items and services.   
 Frontend: https://jhused-ui.herokuapp.com/  
 API Backend: https://jhused-api-server.herokuapp.com/  
 Chat Backend: https://jhused-chat-server.herokuapp.com

**Advisors** 

| Name | JHU Email | GitHub Username |
| ---- | --------- | --------------- |
| Nanxi Ye | nye3@jhu.edu | maxye-frz |

**Team**

|        Name          |       JHU Email       |  GitHub Username   |
| -------------------- | --------------------- | ------------------ |
|   Louie DiBernardo   |    ldibern1@jh.edu    |    JustATechie     |
|     Samantha Fu      |     sfu12@jh.edu      |      sf11047       |
|     Junjie Yang      |  junjie.yang@jhu.edu  |   JunjieYang1997   |
|      Bohua Wan       |     bwan2@jh.edu      |      GlenGGG       |
|      Chu Ding        |     chud@jhu.edu      |      cding91       |
|       Qiao Lu        |    qlu19@jh.edu       |     Giraffea1      |

## Installing / Getting started

### Run frontend locally:
Set the current directory to code/frontend/jhused-ui. Then type the following in the terminal to install all the front end dependencies

```shell
npm install
```
Type the following to start the app on the [localhost:3000](http://localhost:3000)
```shell
npm start
```
### Run backend locally
```shell
Open Intellj
Open Project in code/backend/jhused-api-server
Refresh Gradle
Setup Environment Value for DATABASE_URL for api/ApiServer, which can be found at heroku (Account: jhusedatheroku@gmail.com Pass: Johnshopkins6!).
Run api/ApiServer
```
Then the server should be running on the [localhost:8080](http://localhost:8080)

## Developing

### Built With
#### Backend
##### Framework:
API server:  
SparkJava 2.9.3 for api server  
Jdbi3 3.16.0 for database interaction  
Pac4j 4.0.0 for SSO integration and security  
chat server:  
Express 4.17.1  
Socket.IO 2.3.0 for realtime chatting

##### Build Tool & Package Manager:
Gradle 6.7  

#### Frontend
##### Framework:
React 17.0.1  
Tailwind 2.0.4 for css framework  
Formik 2.2.6  for user input validation  
Opencv.js 3.4.0 for grabcut  

##### Build Tool & Package Manager:
Node.js 14.16.0

### Prerequisites
What is needed to set up the dev environment. For instance, global dependencies or any other tools. include download links.  
[Java >=11](https://adoptopenjdk.net/)  
[Intellij 2020.3.2](https://www.jetbrains.com/idea/)  
[Node >= 10.16 and npm >= 5.6](https://nodejs.org/en/)  

### Setting up Dev

Here's a brief intro about what a developer must do in order to start developing
the project further:

```shell
git clone https://github.com/cs421sp21-homework/project-g09.git
# to setup backend:
Open Intellj
Open Project in code/backend/jhused-api-server
Refresh Gradle
Setup Environment Value for DATABASE_URL for api/ApiServer, which can be found at heroku (Account: jhusedatheroku@gmail.com Pass: Johnshopkins6!).
Edit what you want
Run api/ApiServer
visit http://localhost:8080

# to setup frontend
got to code/frontend/jhused-ui
Open a terminal
# Install denpendencies in Prerequisites by running:
npm install
Open a editor, vscode for example
Edit what you want
# Once you are done, run
npm start
# in the terminal
```

And state what happens step-by-step. If there is any virtual environment, local server or database feeder needed, explain here.

### Building

If your project needs some additional steps for the developer to build the
project after some code changes, state them here. for example:

```shell
# for backend
just refresh gradle if add additional dependencies
use idea to build and run if code changes

# for frontend
npm start
```

Here again you should state what actually happens when the code above gets
executed.

### Deploying / Publishing
give instructions on how to build and release a new version
In case there's some step you have to take that publishes this project to a
server, this is the right time to state it.

```shell
#packagemanager deploy your-project -s server.com -u username -p password

heroku login
# These are necessary because we are using one git repo to store two separate project (apiserver and ui)
# These set up two different heroku remote
heroku git:remote --remote heroku-api -a jhused-api-server
heroku git:remote --remote heroku-ui -a jhused-ui
heroku git:remote --remote heroku-chat-server -a jhused-chat-server

# to upload backend to heroku:
back to /projectg09
git subtree push --prefix code/backend/jhused-api-server heroku-api master

# to upload frontend to heroku
git subtree push --prefix code/frontend/jhused-ui heroku-ui master

# to upload chat server to heroku
git subtree push --prefix code/backend/jhused-chat-server heroku-chat-server master
```


And again you'd need to tell what the previous code actually does.

Make sure you check out the branch you want to deploy first. The command "git subtree push ..." will deploy the HEAD branch to heroku. 

#### Special note for frontend deploy
For the frontend deploy, you need to go to package.json. Under "script", change start: "craco start" to start: "node server.js". See more information about it [here](https://github.com/gsoft-inc/craco/issues/233#issuecomment-757575452).

## Versioning

We use [SemVer](http://semver.org/) for versioning. 

<!--## Configuration-->

<!--Here you should write what are all of the configurations a user can enter when using the project.-->

<!--## Tests-->

<!--Describe and show how to run the tests with code examples.-->
<!--Explain what these tests test and why.-->

<!--```shell-->
<!--Give an example-->
<!--```-->

## Style guide

Java
https://google.github.io/styleguide/javaguide.html  
Javascript/React
https://airbnb.io/javascript/react/  

## Api Reference

API server: https://jhused-api-server.herokuapp.com/  
[Postman API documentation](https://documenter.getpostman.com/view/14357023/Tz5i8zkB)  

## Database

PostgreSQL, heroku supplied, for all of the data except images.  
[Firebase](https://firebase.google.com/products/storage) for storing images.  

## Database framework
We originally used sql2o 1.6.0 to interact with the database until iteration 3.  
From iteration 3, we are using Jdbi3 3.16.0, as it supports customized join SQLs, for nested inner objects (or lists of objects).
