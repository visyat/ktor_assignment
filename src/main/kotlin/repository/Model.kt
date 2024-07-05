package repository

import org.jetbrains.exposed.sql.*

object UsersDB: Table () {
    val id: Column<Int> = integer("id").autoIncrement()
    val name: Column<String> = varchar("name", 50)
    val age: Column<Int> = integer("age")
    val email: Column<String> = varchar("email", 100)
    override val primaryKey = PrimaryKey(id)
}

object UsersAddresses: Table() {
    val id: Column<Int> = integer("id").references(UsersDB.id, onDelete = ReferenceOption.CASCADE)
    val address: Column<String> = varchar("address", 100).default("NO ADDRESS INSERTED")
    override val primaryKey = PrimaryKey(UsersDB.id)
}

object FakeUsersDB: Table() {
    val name: Column<String> = varchar("name", 50)
    val age: Column<Int> = integer("age")
    val email: Column<String> = varchar("email", 100)
    val address: Column<String> = varchar("address", 100)
    override val primaryKey = PrimaryKey(FakeUsersDB.name)
}