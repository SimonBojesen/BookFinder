# BookFinder

## Who is the customer

**Boogle A/S**

Our customer is a software company by the name “Boogle”. They have licensed a book search system to various book stores, so their customers are able to find books that they can buy in their store.

Their current system is a legacy application, where the only feature of the application is the ability for users to search for books in their small and locally stored database. There is currently nothing, in the application, that ties the books to the stores and helps them get more sales.

Boogle has asked us, DevOrgs, to improve upon their system, so it’s easier for the users to find books they like and the bookstores to sell books.

-----------------------------------------------------------------------------------------------------------------------------------------------------------

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


