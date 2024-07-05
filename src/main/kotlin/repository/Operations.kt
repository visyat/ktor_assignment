package repository

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import service.NumUsers

@Serializable
data class User (var name: String = "", var age: Int = 0, var email: String = "", var address: String = "" ) {
    fun fromRow (row: ResultRow) {
        this.name = row[UsersDB.name]
        this.age = row[UsersDB.age]
        this.email = row[UsersDB.email]
        this.address = row[UsersAddresses.address]
    }
}

class UserDBOps {
    init {
        Class.forName("org.postgresql.Driver")
        Database.connect(
            url = System.getenv("DB_URL"),
            driver = "org.postgresql.Driver",
            user = System.getenv("DB_USER"),
            password = System.getenv("DB_PASSWD")
        )
        transaction {
            SchemaUtils.create(UsersDB)
            SchemaUtils.create(UsersAddresses)
            SchemaUtils.create(FakeUsersDB)
        }
    }

    fun getIDs (): MutableList<Int> {
        val results = try {
            transaction {
                UsersDB.select(UsersDB.id).toList()
            }
        } catch (e: Exception) {
            mutableListOf()
        }
        val output: MutableList<Int> = mutableListOf()
        for (row in results) {
            output.add(row[UsersDB.id])
        }
        return output
    }

    fun getUserByID (inputID: Int): User? {
        val user = transaction {
            UsersDB.innerJoin(UsersAddresses).select(UsersDB.name, UsersDB.age, UsersDB.email, UsersAddresses.address).where { UsersDB.id eq inputID }.toList()
        }
        val output = User()
        try {
            val rowResult = user[0]
            output.fromRow(rowResult)
        } catch (e: Exception) {
            return null
        }
        return output
    }
    fun insertUser (newUser: User, numUsers: NumUsers) {
        transaction {
            UsersDB.insert {
                it[name] = newUser.name
                it[age] = newUser.age
                it[email] = newUser.email
            }
            val id = UsersDB.select(UsersDB.id).last()[UsersDB.id]
            numUsers.updateAdd(id)
            UsersAddresses.insert {
                it[UsersAddresses.id] = id
                it[address] = newUser.address
            }
        }
    }
    fun updateUser (inputID: Int, userInfo: User) {
        transaction {
            UsersDB.innerJoin(UsersAddresses).update({ UsersDB.id eq inputID }) {
                it[UsersDB.name] = userInfo.name
                it[UsersDB.age] = userInfo.age
                it[UsersDB.email] = userInfo.email
                it[UsersAddresses.address] = userInfo.address
            }
        }
    }
    fun deleteUser (inputID: Int) {
        transaction {
            UsersDB.deleteWhere { id eq inputID }
        }
    }
}
