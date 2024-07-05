## Vishal Yathish – Ktor Assignment 

### Part I: 
```
Task:
1. Create a gradle project(User Management) with Create and Read APIs.
2. Create api(POST): /user
3. Get all users(GET): /user
4. Get a single user's detail: /user/{id}


User should have below information(store in-memory)
User(
	id,
	name,
	age,
	email
)
```

### Part II: 
```
1. Take environment variables for database config.
2. Add all the CRUD APIs.
3. Add Caching(For Ref: Google Guvava -> com.google.guava:guava:<version>)
4. Create one more table to store user's address, and while get api join those columns as well.
5. Seperate each layer in code(routes, service, repository)
6. Use Koin for DI(can be done for cache object)
7. Create a api end point to create fake user.
   end point ->  /users/fake 
   In this end point internally call a public API and get the dummy data and store in a separate table(fake_users).
   Public API: https://randomuser.me/api/
   (To Call this public API try to use feign client)
8. For new tables add migration.(Liquibase can be used).
```