/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.reflect.classfinder

import dev.nikdekur.ndkore.reflect.ClassFinder
import dev.nikdekur.ndkore.reflect.classfinder.dir1.Dir1Class
import dev.nikdekur.ndkore.reflect.classfinder.dir2.Dir2Class1
import dev.nikdekur.ndkore.reflect.classfinder.dir2.Dir2Class2
import dev.nikdekur.ndkore.reflect.classfinder.dir3.Dir3Class1
import dev.nikdekur.ndkore.reflect.classfinder.dir3.Dir3Class2
import dev.nikdekur.ndkore.reflect.classfinder.dir3.Dir3Class3
import kotlin.test.Test
import kotlin.test.assertEquals

abstract class ClassFinderTest {

    abstract val finder: ClassFinder

    @Test
    fun `test find one class`() {
        val loader = Thread.currentThread().contextClassLoader
        finder.find(loader, "dev.nikdekur.ndkore.reflect.classfinder.dir1") {
            assertEquals(Dir1Class::class.java, it)
        }
    }

    @Test
    fun `test find multiple classes`() {
        val loader = Thread.currentThread().contextClassLoader
        val found = mutableListOf<Class<*>>()
        finder.find(loader, "dev.nikdekur.ndkore.reflect.classfinder.dir2") {
            found.add(it)
        }
        assertEquals(2, found.size)
        assertEquals(found[0], Dir2Class1::class.java)
        assertEquals(found[1], Dir2Class2::class.java)
    }

    @Test
    fun `test find classes from kotlin file`() {
        val loader = Thread.currentThread().contextClassLoader
        val found = mutableListOf<Class<*>>()
        finder.find(loader, "dev.nikdekur.ndkore.reflect.classfinder.dir3") {
            found.add(it)
        }
        assertEquals(3, found.size)
        assertEquals(found[0], Dir3Class1::class.java)
        assertEquals(found[1], Dir3Class2::class.java)
        assertEquals(found[2], Dir3Class3::class.java)
    }

    @Test
    fun `test find classes from empty or non existing package`() {
        val loader = Thread.currentThread().contextClassLoader
        val found = mutableListOf<Class<*>>()
        finder.find(loader, "dev.nikdekur.ndkore.reflect.classfinder.dir4") {
            found.add(it)
        }
        assertEquals(0, found.size)
    }
}