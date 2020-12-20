# BookFinder

## Who is the customer

**Boogle A/S**

Our customer is a software company by the name “Boogle”. They have licensed a book search system to various book stores, so their customers are able to find books that they can buy in their store.

Their current system is a legacy application, where the only feature of the application is the ability for users to search for books in their small and locally stored database. There is currently nothing, in the application, that ties the books to the stores and helps them get more sales.

Boogle has asked us, DevOrgs, to improve upon their system, so it’s easier for the users to find books they like and the bookstores to sell books.

---

## Identified problems and need-to-have subsystems on Boogle's product

We immediately noticed how their current system had nothing for the users. It needed more services the user could use to make their system desirable. We agreed upon the following services to increase user satisfaction: 

1. Search system: Boogle’s main problem right now is that they use their own database in their search system, this creates problems because they would need to maintain this database and manually add new releases all the time. This is a problem that can be solved by using Google’s Book API which we know gets new releases, so this can help relieve some of the pressure maintaining this application would put on Boogle.

2. Login system, this is important to increase the number of personalized features we 
can make for the users of the platform.

3. Subscribe/Unsubscribe system, is a system where a user has a list of books that they subscribe to. If a user is looking up books and then decides they might want to buy a book at some point they can subscribe to it so it is saved for later.

4. Update prices on subscribed books system, which gets the current prices from the bookstores every time the user chooses to see subscribed books. This list also gives the user direct links to the bookstore’s website which can help increase sales for the bookstores that licence this platform.

5. Rating system that makes it possible for a user to rate a book 1-5 stars after having read it. These rating’s are shown on search pages as an average rating that is calculated by combining the ratings from our system and the ratings on Google Books.

Reflection on other systems that we thought of:

6. Geolocation system to help discover places to buy the book physically near you. This helps users locate physical bookstores near the user where you can go and look at the books in hand if the user is unsure if he wants to buy it from the data we provide.

7. Want-to-read list system, meaning a  list the user can use to collect all the books they have read. This feature helps the user organize the books they have read in one place, which might help if you have read hundreds of books. This also ties into the next needed feature.

8. Recommendation feature. A feature that would use machine learning to figure out what the user likes, the books they’ve read, the books they subscribe to and the books they look up. This would be super helpful for the user that is looking for something to read but doesn’t know what to read at the moment. This system could help with that. 

These eight main subsystems are the ones we would have liked to make. Unfortunately because of time, we only managed to make the first 5. 

---

## Business Process Models and Descriptions

### Book Search Process

![Book Search Process](/images/booksearch-scenario.png "booksearch-scenario.png").

When a user wants a book, he types in a search query. The query is then sent to the search api, which forwards the request to Google. The result, if found, is shown as a result list. If the user does not find his book, the process ends. If the user did find the book he was looking for, he clicks “Show Details” and is presented with a detailed view of book data, and the process ends.

### Login system

![Login system](/images/login.png "login.png").

This process is quite simple. We designed it around a MySQL Database that we would have to introduce on the legacy system. 
When a user of the platform wants to use it they will be prompted to login. If it is a new user they would have to first create a user to get on to the platform. Otherwise the user writes his/her credentials which the system then validates, and if the system recognises the credentials the user will be granted access to the site which is the end of the process.

### Subscribe/Unsubscribe system

![Subscribe/Unsubscribe system](/images/book-subscription.png "book-subscription.png").

This process happens when a user is on the detailed view page of a certain book. The process first checks to see if the user is subscribed to this book or not, to figure out which option to present the user with, either a subscribe or an unsubscribe button. When the user clicks either of these two options the next part of the process is quite similar on both, it either adds or deletes the book from the users personalized subscription list. When either of these finishes, the process would then switch the option presented to the user from f.ex. subscribe to unsubscribe. This is the end of the process.

### Update prices on subscribed books system

![Update prices on subscribed books system](/images/update-prices-kafka.png "update-prices-kafka.png").

The user wants to see a list of subscribed books with prices. He clicks on “See subscribed books”. This starts a process where the system listens to kafka for incoming messages, and sends the users booklist to the BookFinderAlert microservice. The microserve scrapes the web for prices, and sends them to a topic in kafka matching the username. This message is picked up by our initiating system, which updates the MySQL database with the updated prices. The user is then directed to the “Subscribed books” page, where the updated prices are shown.

Problem: Since the requests are not synchronous, there is a slight delay before the subscribed books page can be shown. This could be mitigated by making the update prices functionality asynchronous and updating the prices when they are received instead.

### Rating system

![Rating system](/images/star-rating.png "star-rating.png").

This process shows how our ratings work. If the user does not want to rate a book the process ends. Otherwise the user will choose a 1-5 star rating and click “rate”. This will send a request to our Rating Microservice which saves all ratings in a mongo database. The next step is for the process to calculate the new average rating on the book and display it on the page for the users. This happens by making 2 get requests on our rating DB and on Google API’s. The process then puts these together and calculates the average score and updates the DOM on the current page. This ends the process.

---

## Over-all system architecture design

![System architecture](/images/BookFinderArchetecture.jpg "BookFinderArchetecture.jpg").

The Book Finder application is the main monolith application that Boogle wanted us to improve upon. We added a login system to the monolith application with a mysql database that stores the users. The Book Finder application gets its books from the Google Books api, where previously they were stored locally in a DB. 

We have also added ratings to the Book Finder app, by creating a  Rating Microservice, that creates and stores book ratings in a Mongo database.The Rating microservice is a restful api, that the Book Finder app makes get and post requests to to add and obtain ratings from. We then add the ratings from the microservice, with the ratings from the google api to get an average rating for each book.

Another thing we added to the Book Finder app was the ability to subscribe to books and get a list of prices for each subscribed book. In order to do that we created a BookFinderAlert API. When you go to the list of subscribed books, the Book Finder app does 2 things. First it starts to listen  to a kafka topic that is based on the user’s username. The second thing the Book Finder app does is to make a post request to the BookFinderAlert API, where it creates a topic in Kafka based on the username of the logged in user. The BookFinderAlert then scrapes the Google Books website  prices based on the users subscribed books and where they can buy them. The prices are then stored in a JSON object that is sent as a message to the kafka topic.After these 2 things are done, the Book Finder app receives the kafka message with the stored prices and links to a page where the user can buy them.

---

## Deployment strategy

We discussed internally how we should deploy the project, and agreed on a docker-compose solution. We have deployed each individual project to the cloud as Docker Images (Docker Hub). We then made a docker-compose.yml file that when run downloads each image and runs them with their dependencies so it simulates running on a “real” server. 
We wanted to use Docker as it makes it possible to deploy on different OS, without any dependencies, except Docker and Docker Compose.

### How to run

#### Prerequisites

Docker engine and Docker Compose must be installed. For ease of installation, use Docker Desktop.

#### Installation

Download the “Bookfinder” project.
Run it by opening a command line in the bookfinder project directory and type “docker-compose up”

#### BookFinder program

Open a browser and navigate to “localhost:8080”.
Click create user and type in desired username and password. Username must be unique, and password at least 4 characters. Click submit. You should now be able to login.

#### System metrics

Open a browser and navigate to:	“http://localhost:3000/d/K5CPjC1Mk/jvm-micrometer?orgId=1&refresh=30s&from=now-5m&to=now”
Login with user: admin, password: 12345678



