package com.juandgaines.auth.domain

interface PatternValidator {
    fun matches(value: String): Boolean
}