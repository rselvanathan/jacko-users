package com.romeshselvan.jacko.controllers

import com.romeshselvan.jacko.domain.User
import com.romeshselvan.jacko.exceptions.APIError
import com.romeshselvan.jacko.exceptions.BadRequestException
import com.romeshselvan.jacko.exceptions.NotFoundException
import com.romeshselvan.jacko.repositories.UsersRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/users")
class UserController(val usersRepository: UsersRepository) {

    @GetMapping(value = "/{email:.+}", produces = arrayOf("application/json"))
    fun getUserFromEmail(@PathVariable("email") email : String): User {
        val user : User? = usersRepository.findOne(email)
        if(user == null) throw NotFoundException(APIError.EMAIL_NOT_FOUND) else return user
    }

    @PostMapping(value = "/add", consumes = arrayOf("application/json"), produces = arrayOf("application/json"))
    @ResponseStatus(HttpStatus.CREATED)
    fun saveUser(@Valid @RequestBody user: User): User {
        if(usersRepository.findOne(user.email) == null) return usersRepository.save(user)
        else throw BadRequestException(APIError.USER_ALREADY_EXISTS)
    }

    @PutMapping(value = "/{email:.+}", consumes = arrayOf("application/json"))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateUser(@Valid @RequestBody user: User, @PathVariable("email") email: String) {
        if(email != user.email) throw BadRequestException(APIError.PATH_BODY_ERROR)
        if(usersRepository.findOne(email) == null) throw NotFoundException(APIError.EMAIL_NOT_FOUND)
        else usersRepository.save(user)
    }

    @DeleteMapping(value = "/{email:.+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUser(@PathVariable("email") email: String) {
        if(usersRepository.findOne(email) == null) throw NotFoundException(APIError.EMAIL_NOT_FOUND)
        else usersRepository.delete(email)
    }
}