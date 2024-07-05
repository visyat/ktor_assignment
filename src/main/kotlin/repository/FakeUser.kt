package repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class FakeUserResponse (val results: List<FakeUser>)
@Serializable
data class FakeUser (val name: Name, val dob: DOB, val email: String, val location: Location)
@Serializable
data class Name (val title: String, val first: String, val last: String) {
    fun getFullName(): String {
        return "$title $first $last"
    }
}
@Serializable
data class DOB (val age: Int)
@Serializable
data class Location (val street: Street, val city: String, val state: String, val country: String, val postcode: Int) {
    fun getFullAddress(): String {
        return "${street.number} $city, $state, $postcode, $country"
    }
}
@Serializable
data class Street (val number: Int, val name: String)

class FakeUserOps
{
    suspend fun fakeUserRequest (): String {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
        }
        val response: FakeUserResponse = client.get("https://randomuser.me/api/").body()
        val fakeUser = response.results[0]
        val fakeUserToUser = User(fakeUser.name.getFullName(), fakeUser.dob.age, fakeUser.email, fakeUser.location.getFullAddress())
        addFakeUser(fakeUserToUser)

        return fakeUserToUser.toString()
    }
    private fun addFakeUser (fakeUser: User) {
        transaction {
            FakeUsersDB.insert {
                it[name] = fakeUser.name
                it[age] = fakeUser.age
                it[email] = fakeUser.email
                it[address] = fakeUser.address
            }
        }
    }
    fun getAllFakeUsers (): String {
        val dbOutput = transaction {
            FakeUsersDB.selectAll().toList()
        }
        val users: MutableList<User> = mutableListOf()
        for (row in dbOutput) {
            val u = User (
                row[FakeUsersDB.name],
                row[FakeUsersDB.age],
                row[FakeUsersDB.email],
                row[FakeUsersDB.address]
            )
            users.add(u)
        }
        return users.toString()
    }
}