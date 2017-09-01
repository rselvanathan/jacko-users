package com.romeshselvan.jacko.repositories

import com.romeshselvan.jacko.domain.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UsersRepository : MongoRepository<User, String>