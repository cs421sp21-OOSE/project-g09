# Teamwork  
Team Leader: Samantha Fu 

Additional meeting time(s) if needed:  
Tuesday, Wednesday, Thursday, 10-11pm EST

# OO Design  
* Front end (view): React app to render UI
* Backend
  * Server (controller): processing HTTP requests and sending responses
  * Post (model): model representation of posts created by users in the app
  * PostDao: dact access object interface for the model class to provide DB operations
  * Sql2oPostDao: concrete implementation of the PostDao interface 
  * Datastore: utility class to provide samples.
  * Database: utility class to refresh database, provide databaseurl, and generate sql2o.

![](../assets/UML/UML-iteration1.png)



# Wireframe & Use-case  

### Browsing the homepage
1. The user visits our web application using a web browser.
2. The user will be presented with the homepage having sample posted items of different categories.
3. The user can choose to only see items from certain category by choosing the drop down category.
4. The user can choose the order of items listed by selecting in the sort drop style(most recent, least recent, price low to high, price hight to low).

![](../assets/Wireframe/Wireframe-home-iteration2.png)

### Account page for user
(After clicking on post on the account button)
1. The user can visit an account page where the username, location, and items posted by the user will be displayed.
2. The user can click on the home button to go back to the homepage where all posts are displayed.

![](../assets/Wireframe/Wireframe-myPosts-iteration2.png)

### Editing a post
(After clicking on post on the account page)
1. The user will be able to visit the editing panel of a post after clicking on the post on the account page.
2. The editing panel contains the same boxes for post title, price, location, category, description and options to upload image plus a sold button.
3. The user can edit the information in the boxes, and once the button save is clicked, the post will be updated in the database.
4. The user can mark the item as sold once the sold button is clicked, and the post would not appear under search anymore.

![](../assets/Wireframe/Wireframe-editPost-iteration2.png)

# Iteration Backlog  
* As a user, I want to post an item with tags for its category, so that a buyer can search through category  
* As a user, I want to edit my posts after I post, so that I can update the post when I feel like to  
* As a user, I want to mark something as sold, so that selling information is kept up to date.  
* As a user, I want to browse items by category, so that I can search for specific things to buy.  
* As a user, I want to search for the item/service I am interested in, so that I don’t have to see things that I don’t want 

# Tasks  
Post Editing
- [x] myPage of posts for editing (Sam)
- [x] Route for myPage + button to navigate there (Sam)
- [x] Editing UI form (same as before, but with data populated) (Chu)

Mark Item as Sold
- [x] Modify Database design (Qiao)
- [x] UI button for updating status - in post editor (Chu)
- [x] Homepage should only render unsold items - frontend job (Bohua)

Filter by Category
- [x] Button for category filtering (Junjie)

Sorting
- [x] One route method w/ query params (Chu)
- [x] Frontend choose sort type (Junjie)

Search
- [x] Update backend search - description, location, title, tags (Louie)
- [x] Frontend UI search bar (Junjie)

Backlog 
- [x] Create post redirect to home page (Chu)
- [x] Allow deletion of tags (Chu)
- [x] Make post detail popup not expand (Sam)
- [ ] Pagination (if we have time) - update API & frontend ui to show pages

# Retrospective  
This time, we started early to give ourselves more time to actually code; we made sure to 
break down user stories into tasks and assign them early on, which was extremely helpful.
We were able to complete all our user stories on time, so a user may sort items, filter by category,
& visit a general profile page to edit posts. One thing we did have a discussion about
was where sorting should be implemented; for now we've left it on the frontend but will likely be moving
it to the backend in the future. In addition to this, we did not have time to implement pagniation for our
app but will add that to our backlog. 
One thing that came up was the UI. We have researched and ultimately settled on using a CSS framework
and then borrowing design inspiration from similar websites, including Etsy and Depop. This will be a
future issue to work through at the start of the next iteration. Thus the current version of the website
that is deployed is using our old styling and is not browser repsonsive; this will be dealt with when we
migrate our CSS.
Moving forwards, we will try to coordinate more between members who are working on the same/similar features
and also ensure that work gets distributed equally.
