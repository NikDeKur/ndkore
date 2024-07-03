/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 Nik De Kur
 */

package dev.nikdekur.ndkore.tools

import dev.nikdekur.ndkore.ext.*
import java.security.SecureRandom


const val DEFAULT_LENGTH = 12

val DEFAULT_LETTERS = ('a'..'z').toSet()
const val DEFAULT_UPPERCASE = true
const val DEFAULT_ALLOW_LETTERS = true

val DEFAULT_SPECIAL_CHARACTERS =
    setOf('!', '@', '#', '$', '%',
          '^', '&', '*', '(', ')',
          '-', '_', '+', '=', '{',
          '}', '[', ']', ';', ':',
          '<', '>', '/', '?', )
val DEFAULT_ADVANCED_SPECIAL_CHARACTERS =
    setOf('!', '@', '#', '$', '%',
          '^', '&', '*', '(', ')',
          '-', '_', '+', '=', '{',
          '}', '[', ']', '|', ';',
          ':', ',', '.', '<', '>',
          '/', '?', '~', '`', '\'',
          '\"')
const val DEFAULT_ALLOW_SPECIAL_CHARACTERS = true

val DEFAULT_NUMBERS = setOf(
    '1', '2', '3',
    '4', '5', '6',
    '7', '8', '9',
)
const val DEFAULT_ALLOW_NUMBERS = true





class PasswordGenerator {

    val random = SecureRandom()

    var length: Int = DEFAULT_LENGTH
    fun withLength(length: Int): PasswordGenerator {
        this.length = length
        return this
    }



    var letters: Collection<Char> = DEFAULT_LETTERS
    fun withLetters(letters: Collection<Char>): PasswordGenerator {
        this.letters = letters
        return this
    }

    fun withLetters(state: Boolean = DEFAULT_ALLOW_LETTERS): PasswordGenerator {
        return if (state)
            withLetters(DEFAULT_LETTERS)
        else
            withLetters(emptySet())
    }

    var allowUppercase = DEFAULT_UPPERCASE
    fun withUppercase(state: Boolean = DEFAULT_UPPERCASE): PasswordGenerator {
        this.allowUppercase = state
        return this
    }



    var specialCharacters: Collection<Char> = DEFAULT_SPECIAL_CHARACTERS
    fun withSpecialCharacters(characters: Collection<Char>): PasswordGenerator {
        this.specialCharacters = characters
        return this
    }

    fun withSpecialCharacters(state: Boolean = DEFAULT_ALLOW_SPECIAL_CHARACTERS): PasswordGenerator {
        return if (state)
                withSpecialCharacters(DEFAULT_ADVANCED_SPECIAL_CHARACTERS)
            else
                this.withSpecialCharacters(emptySet())
    }



    var numbers: Collection<Char> = DEFAULT_NUMBERS
    fun withNumbers(characters: Collection<Char>): PasswordGenerator {
        this.numbers = characters
        return this
    }

    fun withNumbers(state: Boolean = DEFAULT_ALLOW_NUMBERS): PasswordGenerator {
        return if (state)
            this.withNumbers(DEFAULT_NUMBERS)
        else
            this.withNumbers(emptySet())
    }





    val symbols: List<Char>
        get() {
            val symbols = letters
                .map {
                    if (allowUppercase && random.chance(50.0)) {
                        return@map it.uppercaseChar()
                    }
                    return@map it
                }
                .toMutableList()
            
            symbols += numbers
            symbols += specialCharacters
            return symbols
        }


    fun build(): String {
        val choose = symbols.random(random, length)
        return choose.joinToString("")
    }



    companion object {

        @JvmStatic
        val default: PasswordGenerator = PasswordGenerator()

        @JvmStatic
        fun generate(length: Int = DEFAULT_LENGTH,

                     letters: Collection<Char> = DEFAULT_LETTERS,
                     allowLetters: Boolean = DEFAULT_ALLOW_LETTERS,
                     uppercase: Boolean = DEFAULT_UPPERCASE,

                     specialCharacters: Collection<Char> = DEFAULT_SPECIAL_CHARACTERS,
                     allowSpecialCharacters: Boolean = DEFAULT_ALLOW_SPECIAL_CHARACTERS,

                     numbers: Collection<Char> = DEFAULT_NUMBERS,
                     allowNumbers: Boolean = DEFAULT_ALLOW_NUMBERS): String {

            return PasswordGenerator()
                .withLength(length)

                .withLetters(letters)
                .withLetters(allowLetters)
                .withUppercase(uppercase)

                .withSpecialCharacters(specialCharacters)
                .withSpecialCharacters(allowSpecialCharacters)

                .withNumbers(numbers)
                .withNumbers(allowNumbers)

                .build()
        }
    }
}


fun main() {
    println(
        PasswordGenerator
            .generate(length = 20)
    )
}