# Teamwork
Team Leader: Bohua Wan

Additional meeting time(s) if needed:  
Tuesday, Wednesday, Thursday, 10-11pm EST

# OO Design
* Front end (view): React app to render UI
* Backend
  * Server (controller): processing HTTP requests and sending responses
  * Post (model): model representation of posts created by users in the app
  * PostDao: dact access object interface for the model class to provide DB operations
  * JdbiPostDao: concrete implementation of the PostDao interface 
  * Datastore: utility class to provide samples.
  * Database: utility class to refresh database, provide databaseurl, and generate sql2o.

![](../assets/UML/UML-iteration3.png)


# Wireframe & Use-case


### Browsing the homepage
1. The user visits our web application using a web browser.
2. The user will be presented with the homepage having sample posted items of different categories.
3. The user can choose to only see items from certain category by choosing the drop down category.
4. The user can choose the order of items listed by selecting in the sort drop style(most recent, least recent, price low to high, price hight to low).
6. The user can visit the account page by clicking the "Your Profile" from the drop down menu of the account icon.
7. The user can open the chat box by clicking the "Messages" from the drop down menu of the account icon. to view messages.
8. The user can login by clicking the Login button and then redirect to JHU SSO to login, then redirect to information registration page, and then back to homepage with logged in status.

![](../assets/Wireframe/Wireframe-HomepageLoggedIn-iteration3.png)

![](../assets/Wireframe/Wireframe-HomePageNotLoggedIn-iteration3.png)

### Chatting box
1. The user can chat with other users in this box.

![](../assets/Wireframe/Wireframe-message-iteration3.png)

### Reading a post
(After clicking on a post)
1. The user will see a pop up showing the title, image, and text description of the post clicked on.
2. The user will see the images in a slide show of images, along with a list of small images on the left of the slide show.
3. The user can click on the arrows in the main window or the small image list to switch images
4. The user can visit the account page of the user who posted this post by clicking on the user's icon.
5. The user can message the seller by clicking on the "Message Sellar" button.

![](../assets/Wireframe/Wireframe-PostDetail-iteration3.png)

### Account page for user
(After clicking on post on the account button)
1. The user can visit an account page where the username, location, and items posted by the user will be displayed.
2. The user can click on the home button to go back to the homepage where all posts are displayed.
3. The user can open the chat box in his/her account page to view messages.
4. The user can start a chat when visiting other's account page by clicking a button.
5. The user can click on a button to open an edit pop up to edit the user's information.

![](../assets/Wireframe/Wireframe-MyProfile-iteration3.png)

![](../assets/Wireframe/Wireframe-OtherUserProfile-iteration3.png)

### Account information editing page

1. The user can edit location, preferred email,  preferred user name, and upload avatar (an image) in this editing page.  
2. If it's the user's first login, this page contain default information based on user's JHED, other wise, it is filled with original values.  

![](../assets/Wireframe/Wireframe-editUser-iteration3.png)

# Iteration Backlog

As a user, I want to have the chatting feature, so that I can coordinate with the person on the other end  
As a user, I want to login into the platform, so that I can access my past activities  
As a user, I want to sign up an account, so that I can log in  
As a user, I want to login into the platform using my JHED account to verify my identity, so that sign up and login will be easy  
As a user, I want to make a wish list, so that I can show people what things I am looking for

# Tasks
- [ ] Implement wish list feature (Louie:Backend; Sam:Frontend)
- [X] Migrate to JDBI3 (Bohua)
- [x] Login Backend
  - [X]  Database Design, user table, user dao, user api (Qiao)
    - [X]   Setup User table (JHID, image_url, preferred name, location, preferred email)
    - [X]   Setup User model（listOfPosts）
    - [X]   User dao
    - [X]   CRUD api for User
  - [x] SSO apis & functions (Bohua, Louie)
    - [x] Handle redirect from frontend (when user click account button but not yet login) to redirect to SSO api
    - [x] Handle SSO call back
    - [x] Handle SSO encription and decription
  - [x] Handle Sessions (Bohua, Louie)
- [x] Login Frontend
  - [x] Editing user information page (route to this page/pop up in account page (must login)) (Chu)
  - [x] Handle user's login status (Junjie)
    - [x] Check if user is logged in
    - [x] Redirect to login backend api url if not
- [X] User frontend
  - [X] Add user link in post detail to allow user to visit the post owner's user page. (Sam)
  - [X] Add user information editing button in his/her account page to open the "Editing user information page". (Sam)
- [ ] Chatting box
  - [ ] Chat box component - firebase or other 3rd party lib (Junjie, Chu)
  - [x] Add chat box button in homepage and account page to allow user check his/her messages. (Sam)
  - [ ] Add start chatting button in other user's account page to start conversation. (Sam)
- [X] Migrate to Tailwind CSS framework (gradually, no need to get all done in this iteration)
  - [X] Homepage (Sam)
  - [X] post detail (Sam)
  - [x] post editing (Chu)
  - [X] account page (Bohua)
- [ ] Extend past activity functionalities (If we have time)
  - [ ] Implement my favorite posts (post detail have a heart, click then favorite it, show a list of favorites in account page)
  - [ ] Implement history of post creation, edition, view
- [ ] Pagination (If we have time)

# Retrospective
