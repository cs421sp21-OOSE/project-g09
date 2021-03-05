# JHUsed

 This is a web application for JHU affiliated people to post, browse, and buy second hand items and services

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

A quick introduction of the minimal setup you need to get the app up & running.

```shell
commands here
```

Here you should say what actually happens when you execute the code above.

## Developing

### Built With
#### Backend
##### Framework:
SparkJava 2.9.3  

##### Build Tool & Package Manager:
Gradle 6.7  

#### Frontend
##### Framework:
React 17.0.1  

##### Build Tool & Package Manager:
Node.js 14.16.0

### Prerequisites
What is needed to set up the dev environment. For instance, global dependencies or any other tools. include download links.  
[Intellij 2020.3.2](https://www.jetbrains.com/idea/)  
[Node >= 10.16 and npm >= 5.6](https://nodejs.org/en/)  

Install belows in frontend folder (code/frontend/jhused-ui):  
[Create-React-App](https://github.com/facebookincubator/create-react-app)    
[axios](https://github.com/axios/axios)  
[react-router-dom](https://reactrouter.com/web/guides/quick-start)

### Setting up Dev

Here's a brief intro about what a developer must do in order to start developing
the project further:

```shell
git clone https://github.com/cs421sp21-homework/project-g09.git
# to setup backend:
Open Intellj
Open Project in code/backend/jhused-api-server
Refresh Gradle
Setup Environment Value for DATABASE_URL for api/ApiServer, which can be found at heroku (Account: awesomeexpressshop@gmail.com Pass: Johnshopkins6!).
Edit what you want
Run api/ApiServer
visit http://localhost:4567

# to setup frontend
got to code/frontend/jhused-ui
Open a terminal
Install denpendencies in #Prerequisites
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
just refresh gradle

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

# to upload backend to heroku:
back to /projectg09
git subtree push --prefix code/backend/jhused-api-server heroku-api master

# to upload frontend to heroku
git subtree push --prefix code/frontend/jhused-ui heroku-ui master
```

And again you'd need to tell what the previous code actually does.

## Versioning

We use [SemVer](http://semver.org/) for versioning. 

## Configuration

Here you should write what are all of the configurations a user can enter when using the project.

## Tests

Describe and show how to run the tests with code examples.
Explain what these tests test and why.

```shell
Give an example
```

## Style guide

Explain your code style and show how to check it.

## Api Reference

If the api is external, link to api documentation. If not describe your api including authentication methods as well as explaining all the endpoints with their required parameters.

## Database

PostgreSQL heroku supplied  
~~Explaining what database (and version) has been used. Provide download links.~~
