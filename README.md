# java-filmorate
Template repository for Filmorate project.

https://app.quickdatabasediagrams.com/#/d/NvAnnc
![java-filmorate](https://github.com/KoshanSky1/java-filmorate/assets/147919859/903d79d7-c3fd-4098-9d7d-5ebb7bb46c4a)

Request Examples:

1. Get list of all films:
   SELECT *
   FROM films;
2. Get film by id:
   SELECT *
   FROM films
   WHERE film_id = id;
3. Get list of the count popular movies:
   "SELECT * FROM films
   LEFT JOIN (SELECT film_id, COUNT (film_user.user_id) as count from film_user
   GROUP BY films.film_id
   ORDER BY count DESC
   LIMIT count;
4. Get list of all users:
   SELECT *
   FROM users;
5. Get user by id:
   SELECT *
   FROM users
   WHERE user_id = id;
6. Get list of friends:
   SELECT * FROM users
   WHERE user_id IN (SELECT user_to_id FROM friendship
   WHERE user_from_id = id);
7. Get list of common friends:
   SELECT * FROM users
   WHERE user_id IN (SELECT user_to_id FROM friendship
   WHERE user_from_id IN(id, otherId) " +
   AND user_to_id NOT IN (id, otherId));
   
   
