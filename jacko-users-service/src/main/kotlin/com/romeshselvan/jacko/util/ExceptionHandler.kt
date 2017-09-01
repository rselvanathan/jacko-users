package com.romeshselvan.jacko.util

import com.romeshselvan.jacko.exceptions.BadRequestException
import com.romeshselvan.jacko.exceptions.NotFoundException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(exception: RuntimeException, webRequest: WebRequest) : ResponseEntity<Any> =
        handleExceptionInternal(exception, Error("The resource was not found"), HttpHeaders(), HttpStatus.NOT_FOUND, webRequest)

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(exception: RuntimeException, webRequest: WebRequest): ResponseEntity<Any> =
        handleExceptionInternal(exception, Error("The resource had a bad request"), HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest)
}