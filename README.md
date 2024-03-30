# java-filmorate
Template repository for Filmorate project.

https://app.quickdatabasediagrams.com/#/d/NvAnnc
![2024-03-30_17-19-08](https://github.com/KoshanSky1/java-filmorate/assets/147919859/ed92b476-06ef-4db8-8e0d-175e0e5846fe)

Request Examples:

1. Getting a list of all movies:
   SELECT name
   FROM films;
2. Getting a movie by id:
   SELECT name
   FROM films
   WHERE film_id = id;
3. Getting a list of the most popular movies:
   SELECT name
   FROM films
   WHERE film_id = id
   ORDER BY likes DESC
   LIMIT 10;
4. Getting a list of all users:
   SELECT name
   FROM users;
5. Getting a user by id:
   SELECT name
   FROM users
   WHERE user_id = id;
6. Getting a list of friends:
   SELECT name
   FROM users
   WHERE user_id IN (SELECT friend_id
   FROM friends
   WHERE user_id = id);
7. Getting a list of mutual friends:
   SELECT name
   FROM users
   WHERE user_id IN (SELECT DISTINCT friend_id
   FROM friends
   WHERE user_id IN ((user_id, friend_id)
   AND friend_id NOT IN(user_id, friend_id)));
   
   
