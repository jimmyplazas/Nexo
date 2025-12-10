package dev.alejo.core.data.database

import androidx.sqlite.SQLiteException
import dev.alejo.core.domain.Result
import dev.alejo.core.domain.util.DataError

suspend inline fun <T> safeDatabaseUpdate(update: suspend () -> T): Result<T, DataError.Local> {
    return try {
        Result.Success(update())
    } catch (e: SQLiteException) {
        Result.Failure(DataError.Local.DISK_FULL)
    }
}