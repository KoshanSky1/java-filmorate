# java-filmorate
Template repository for Filmorate project.

https://app.quickdatabasediagrams.com/#/d/NvAnnc
![java-filmorate](https://github.com/KoshanSky1/java-filmorate/assets/147919859/903d79d7-c3fd-4098-9d7d-5ebb7bb46c4a)

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
   
   
