package com.example.eduapp.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * DB testing: exercises AppDao against a real (in-memory, non-persistent) Room
 * database, running on an actual Android device/emulator.
 */
@RunWith(AndroidJUnit4::class)
class AppDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: AppDao

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.appDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndReadBack_returnsTheSameUser() = runTest {
        dao.insert(User(username = "Alex", level = "1", score = 4, duration = 30))

        val all = dao.getAllUsers().first()

        assertEquals(1, all.size)
        assertEquals("Alex", all.first().username)
        assertEquals("1", all.first().level)
        assertEquals(4, all.first().score)
        assertEquals(30, all.first().duration)
    }

    @Test
    fun getAllUsers_ordersNewestFirst() = runTest {
        dao.insert(User(username = "First", level = "1"))
        dao.insert(User(username = "Second", level = "1"))
        dao.insert(User(username = "Third", level = "1"))

        val all = dao.getAllUsers().first()

        assertEquals(listOf("Third", "Second", "First"), all.map { it.username })
    }

    @Test
    fun update_changesOnlyTheTargetedRow() = runTest {
        dao.insert(User(username = "Alx", level = "1", score = 2))
        dao.insert(User(username = "Sam", level = "2", score = 3))

        val alx = dao.getAllUsers().first().first { it.username == "Alx" }
        dao.update(alx.copy(username = "Alex"))

        val all = dao.getAllUsers().first()
        assertTrue(all.any { it.username == "Alex" })
        assertTrue(all.any { it.username == "Sam" })
        assertEquals(2, all.size)
    }

    @Test
    fun delete_removesOnlyThatRow() = runTest {
        dao.insert(User(username = "Alex", level = "1"))
        dao.insert(User(username = "Sam", level = "2"))

        val alex = dao.getAllUsers().first().first { it.username == "Alex" }
        dao.delete(alex)

        val all = dao.getAllUsers().first()
        assertEquals(1, all.size)
        assertEquals("Sam", all.first().username)
    }

    @Test
    fun deleteAll_clearsEveryRow() = runTest {
        dao.insert(User(username = "Alex", level = "1"))
        dao.insert(User(username = "Sam", level = "2"))

        dao.deleteAll()

        assertTrue(dao.getAllUsers().first().isEmpty())
    }
}
