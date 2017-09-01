package com.romeshselvan.jacko.exceptions

abstract class APIException(apiError: APIError) : RuntimeException(apiError.reason)

class NotFoundException(apiError: APIError) : APIException(apiError)

class BadRequestException(apiError: APIError) : APIException(apiError)

enum class APIError(val reason: String) {
    EMAIL_NOT_FOUND("User with provided e-mail not found"),
    USER_ALREADY_EXISTS("User with provided e-mail already exists, so user cannot be re-added"),
    PATH_BODY_ERROR("The path value and the corresponding body value do not match")
}