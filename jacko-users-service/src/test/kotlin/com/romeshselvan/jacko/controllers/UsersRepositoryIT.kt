package com.romeshselvan.jacko.controllers

import com.jayway.restassured.RestAssured
import com.jayway.restassured.RestAssured.`when`
import com.jayway.restassured.RestAssured.given
import com.jayway.restassured.http.ContentType
import com.romeshselvan.jacko.domain.User
import com.romeshselvan.jacko.repositories.UsersRepository
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate
import kotlin.test.assertEquals

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(value = *arrayOf("classpath:config.properties"))
class UsersRepositoryIT {

    val EMAIL = "romesh@hotmail.com"
    val FIRST_NAME = "Romesh"
    val LAST_NAME = "Selvan"
    val DATE_OF_BIRTH = LocalDate.of(2011, 12, 3)
    val USER_NAME = "majin"

    @Value("\${local.server.port}")
    lateinit var port: String

    @Autowired
    lateinit var usersRepository: UsersRepository

    @Before
    fun before() {
        usersRepository.deleteAll()
        RestAssured.port = port.toInt()
    }

    @Test
    fun `return a 404 error code when the email is not found`() {
        `when`()
                .get("/users/$EMAIL")
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
    }

    @Test
    fun `return the user when the email is found`() {
        val user = defaultUser()
        usersRepository.save(user)

        `when`()
                .get("/users/$EMAIL")
        .then()
                .statusCode(200)
        .and()
                .body("email", equalTo(user.email))
                .body("firstName", equalTo(user.firstName))
                .body("lastName", equalTo(user.lastName))
                .body("dateOfBirth", equalTo(user.dateOfBirth.toString()))
                .body("username", equalTo(user.username))
    }

    @Test
    fun `when adding a new user with valid fields, the request should return created status and the user object`() {
        val body = """{"email":"$EMAIL", "firstName":"$FIRST_NAME", "lastName":"$LAST_NAME", "dateOfBirth":"$DATE_OF_BIRTH", "username":"$USER_NAME"}"""
        val user: User = defaultUser()

        given()
                .contentType(ContentType.JSON)
                .body(body)
        .`when`()
                .post("/users/add")
        .then()
                .statusCode(HttpStatus.CREATED.value())
        .and()
                .body("email", equalTo(user.email))
                .body("firstName", equalTo(user.firstName))
                .body("lastName", equalTo(user.lastName))
                .body("dateOfBirth", equalTo(user.dateOfBirth.toString()))
                .body("username", equalTo(user.username))

        assertEquals(user, usersRepository.findOne(EMAIL), "Not the Same")
    }

    @Test
    fun `when a user already exist and the save user endpoint is hit with the user changing a field, expect a 400`() {
        val user = defaultUser()
        usersRepository.save(user)

        val body = """{"email":"$EMAIL", "firstName":"new name", "lastName":"$LAST_NAME", "dateOfBirth":"$DATE_OF_BIRTH", "username":"$USER_NAME"}"""

        given()
                .contentType(ContentType.JSON)
                .body(body)
        .`when`()
                .post("/users/add")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun `when one of the user fields is null, then return a 400 error response`() {
        val body = """{"firstName":"$FIRST_NAME", "lastName":"$LAST_NAME", "dateOfBirth":"$DATE_OF_BIRTH", "username":"$USER_NAME"}"""

        given()
                .contentType(ContentType.JSON)
                .body(body)
        .`when`()
                .post("/users/add")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun `when adding a user with empty email, then return a 400 error response`() {
        val body = """{"email": "", "firstName":"$FIRST_NAME", "lastName":"$LAST_NAME", "dateOfBirth":"$DATE_OF_BIRTH", "username":"$USER_NAME"}"""

        given()
                .contentType(ContentType.JSON)
                .body(body)
        .`when`()
                .post("/users/add")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun `when adding a user with invalid email, then return a 400 error response`() {
        val body = """{"email": "rome", "firstName":"$FIRST_NAME", "lastName":"$LAST_NAME", "dateOfBirth":"$DATE_OF_BIRTH", "username":"$USER_NAME"}"""

        given()
                .contentType(ContentType.JSON)
                .body(body)
        .`when`()
                .post("/users/add")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun `when adding a user with empty first name, then return a 400 error response`() {
        val body = """{"email": "$EMAIL", "firstName":"", "lastName":"$LAST_NAME", "dateOfBirth":"$DATE_OF_BIRTH", "username":"$USER_NAME"}"""

        given()
                .contentType(ContentType.JSON)
                .body(body)
        .`when`()
                .post("/users/add")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun `when adding a user with empty last name, then return a 400 error response`() {
        val body = """{"email": "$EMAIL", "firstName":"$FIRST_NAME", "lastName":"", "dateOfBirth":"$DATE_OF_BIRTH", "username":"$USER_NAME"}"""

        given()
                .contentType(ContentType.JSON)
                .body(body)
        .`when`()
                .post("/users/add")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun `when adding a user with empty user name, then return a 400 error response`() {
        val body = """{"email": "$EMAIL", "firstName":"$FIRST_NAME", "lastName":"$LAST_NAME", "dateOfBirth":"$DATE_OF_BIRTH", "username":""}"""

        given()
                .contentType(ContentType.JSON)
                .body(body)
        .`when`()
                .post("/users/add")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun `when updating a user if the user does not exist, then expect a 404 response`() {
        val newName = "newName"
        val body = """{"email": "$EMAIL", "firstName":"$newName", "lastName":"$LAST_NAME", "dateOfBirth":"$DATE_OF_BIRTH", "username":"$USER_NAME"}"""

        given().contentType(ContentType.JSON).body(body).`when`().put("/users/$EMAIL").then().statusCode(HttpStatus.NOT_FOUND.value())
    }

    @Test
    fun `when updating a user if the user does exist, then expect update the User`() {
        val defaultUser: User = defaultUser()
        usersRepository.save(defaultUser)
        assertEquals(usersRepository.findOne(EMAIL), defaultUser)

        val newName = "newName"
        val body = """{"email": "$EMAIL", "firstName":"$newName", "lastName":"$LAST_NAME", "dateOfBirth":"$DATE_OF_BIRTH", "username":"$USER_NAME"}"""
        val updatedUser = User(EMAIL, newName, LAST_NAME, DATE_OF_BIRTH, USER_NAME)

        given().contentType(ContentType.JSON).body(body).`when`().put("/users/$EMAIL").then().statusCode(HttpStatus.NO_CONTENT.value())

        assertEquals(updatedUser, usersRepository.findOne(EMAIL))
    }

    @Test
    fun `when updating a user if the email in path does not match email in body does exist, then expect 400 response`() {
        val badEmail = "badEmail"
        val body = """{"email": "$EMAIL", "firstName":"$FIRST_NAME", "lastName":"$LAST_NAME", "dateOfBirth":"$DATE_OF_BIRTH", "username":"$USER_NAME"}"""

        given().contentType(ContentType.JSON).body(body).`when`().put("/users/$badEmail").then().statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun `when deleting user, if user is not found, then expect a 404 response`() {
        `when`().delete("/users/$EMAIL").then().statusCode(HttpStatus.NOT_FOUND.value())
    }

    @Test
    fun `when deleting user successfully expect a 204 response`() {
        usersRepository.save(defaultUser())

        `when`().delete("/users/$EMAIL").then().statusCode(HttpStatus.NO_CONTENT.value())

        assertEquals(null, usersRepository.findOne(EMAIL))
    }

    private fun defaultUser() = User(EMAIL, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, USER_NAME)
}