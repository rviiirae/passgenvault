package com.example.passvaultgenerator

import org.junit.Test

import org.junit.Assert.*

class PasswordGeneratorTest {
    @Test
    fun generatePassword_isCorrectLength() {
        val password = PasswordGenerator.generatePassword(16, true, true, true, true)
        assertEquals(16, password.length)
    }
}