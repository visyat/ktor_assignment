package api_routes

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import repository.*
import service.*

fun Application.configureRouting() {

    val dbOps by inject<UserDBOps>()
    val numUsers by inject<NumUsers>()
    val cacheOps by inject<CacheOps>()
    val fakeUserOps by inject<FakeUserOps>()

    routing {
        get("/") {
            call.respondText("KTOR Test Project Home Page!")
        }

        //CRUD Endpoints
        post("/users") {//CREATE
            val testing = call.receive<User>()
            dbOps.insertUser(testing, numUsers)
            call.respondText("User ${testing.name} Inserted!")
            //call.respondText("$test")
        }
        get("/users") {//READ
            call.respondText("All Users: " + cacheOps.getAllUsersCache())
        }
        get("/users/{id}") {
            val id = call.parameters["id"]?.toInt() ?: 1;
            try {
                call.respondText("User ${id}: " + cacheOps.getUserByIDCache(id))
            } catch (e: Exception) {
                call.respondText("User ${id}: NO USER WITH THIS ID!")
            }
        }
        delete("/users/{id}") {//DELETE
            val id = call.parameters["id"]?.toInt() ?: 1;
            cacheOps.deleteUserCache(id)
            call.respondText ("User $id Deleted!")
        }
        put ("/users/{id}") {//UPDATE
            val id = call.parameters["id"]?.toInt() ?: 1
            val userInfo = call.receive<User>()
            cacheOps.updateUserCache(id, userInfo)
            call.respondText ("Info for User $id Updated!")
        }

        //Fake User Endpoint
        get ("/users/fake") {
            call.respondText("New Fake User Added: ${fakeUserOps.fakeUserRequest()}")
        }
        get ("/users/fake/list") {
            call.respondText("List of Fake Users: ${fakeUserOps.getAllFakeUsers()}")
        }

        get ("/test/env") {
            call.respondText ("${System.getenv()}")
        }
    }
}
