# Project Requirement Specification

## Problem Statement
Since JHU has many students coming and leaving each year, there is a big 2nd hand goods market, but there isn’t any app like Craigslist to help students to sell their stuff. There is a Facebook Free & For Sale group, but it is extremely difficult to browse
and search for specific items there, and many things tend to get lost.

## Potential Clients
Our potential clients will primarily be JHU students, although other staff or community members are welcome to post/buy/sell if they want to.

## Proposed Solution
Our proposed solution is web application for people to post, browse, and buy
second hand items and services. A user will be able to create a post selling an object or service that will be tagged and easily searched and filtered for convenient browsing.
All communication and payments will be faciliatated through this platform as well, in order to keep the entire process of selling & buying consolidated in one place.
This website will function similarly to Facebook Marketplace or Craigslist, but it will be primarily dedicated to JHU students in the Homewood community, as we hope to integrate 
JHU SSO. We would also like to distinguish our platform by adding a wishlist feature and 
possible integration with a computer vision API such as Google Cloud Vision.

## Functional Requirements 

**Must-have**  
As a user, I want to login into the platform, so that I can access my past activities  
As a user, I want to sign up an account, so that I can log in  
As a user, I want to create posts, so that I can buy/sell things  
As a user, I want to include text in the post, so that I can describe things  
As a user, I want to sign in to post an item with tags for its category, so that a buyer can search through category  
As a user, I want to search for the item/service that I am interested, so that I don’t have to see things that I don’t want  
As a user, I want to include image in the post, so that I can show other users more clearly  
As a user, I want to edit my posts after I post, so that I can update the post when I feel like to  

As a user, I want to mark something as sold, so that selling information is kept up to date

As a user, I want to browse items by category, so that I can search for specific things to buy

As a user, I want to click on a specific post, so that I can see the details of the item

As a user, I want to have the chatting feature, so that I can coordinate with the person on the other end  
As a user, I want to be able to pay for my purchase through the website, so that I can keep transactions secure.  
As a user, I want to be able to sort by recently added, oldest, recently discounted, etc., so that I can see if there is anything I would like to buy that I otherwise wouldn't have seen.  
As a user, I want to see other people’s location information, so that I can buy from others close to me

**Nice-to-have**  
As a user, I want to login into the platform using my JHED account to verify my identity, so that sign up and login will be easy

As a user, I want to be informed about what object I am trying to sell using computer vision probably, so that I wouldn’t be confused about what hashtag to use  
As a user I want to make a wish list, so that I can show people what things I am looking for  

## Software Architecture & Technology Stack
**Frontend**
* Framework: React
* JS, HTML, CSS
  

**Backend**

* Framework: SparkJava
* Database: PostgreSQL

**Deploy**
* Heroku

