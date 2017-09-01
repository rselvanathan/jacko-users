package com.romeshselvan.jacko.domain

import org.hibernate.validator.constraints.Email
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "users")
data class User (

        @field:NotEmpty(message = "Email value cannot be empty")
        @field:Email(message = "Email value is not valid")
        @Id
        val email: String,

        @field:NotEmpty(message = "First Name value cannot be empty")
        val firstName: String,

        @field:NotEmpty(message = "Last Name value cannot be empty")
        val lastName: String,

        val dateOfBirth: LocalDate,

        @field:NotEmpty(message = "User Name value cannot be empty")
        val username: String
)
