package com.test.iliketodo

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.test.Database
import com.test.DeletedTODOItem
import com.test.TODOItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class TodoUseCase(driverFactory: DriverFactory) {
    private val driver = driverFactory.createDriver()
    private val database = Database(driver)
    private val queries = database.toDoItemQueries
    private val deletedToDoItemQueries = database.deletedToDoItemQueries

    fun insertItem(title: String, imageUrl: String) {
        queries.insert(null, title, imageUrl,false)
    }

    fun deleteItem(
        id: Long,
        title: String = "",
        imageUrl: String = "",
        checked: Boolean = false,
        time: Long = 0L
    ) {
        queries.deleteById(id)
        deletedToDoItemQueries.insert(null, title, imageUrl, checked, time)
    }

    fun updateCheck(checked: Boolean, id: Long) {
        queries.updateFinish(checked, id)
    }

    fun getAllItemFlow() : Flow<List<TODOItem>> = queries.selectAll().asFlow().mapToList(Dispatchers.Main)

    fun getFinishedItemCountFlow(): Flow<Long> {
        return deletedToDoItemQueries.countFinishedItems()
            .asFlow()
            .mapToOne(Dispatchers.Main)
    }

    fun getLatestDeletedItemFlow(): Flow<DeletedTODOItem?> {
        return deletedToDoItemQueries.selectLatestDeletedItem()
            .asFlow()
            .mapToOneOrNull(Dispatchers.Main)
    }
}