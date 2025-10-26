package dev.alejo.core.domain

import dev.alejo.core.domain.util.Error

/**
 * A sealed interface representing the result of an operation that can either succeed or fail.
 * It is a generic type that holds a value of type [D] on success or an error of type [E] on failure.
 * This provides a standardized way to handle outcomes across the application, avoiding exceptions for predictable errors.
 *
 * @param D The type of the data in case of a successful operation. It is covariant (`out`).
 * @param E The type of the error in case of a failed operation. It must be a subtype of [Error]. It is covariant (`out`).
 *
 * @see Success A data class representing a successful result, containing the data.
 * @see Failure A data class representing a failed result, containing the error.
 */
sealed interface Result<out D, out E : Error> {
    data class Success<out D>(val data: D) : Result<D, Nothing>
    data class Failure<out E : Error>(val error: E) : Result<Nothing, E>
}

/**
 * Maps a `Result.Success` by applying a function to a contained value, leaving a `Result.Failure` untouched.
 *
 * This function can be used to compose the results of functions.
 *
 * @param T The type of the success value.
 * @param E The type of the error value.
 * @param R The type of the success value of the resulting `Result`.
 * @param map The function to apply to the success value.
 * @return A `Result` with the success value transformed by the `map` function, or the original `Failure` if the `Result` was a `Failure`.
 */
inline fun <T, E : Error, R> Result<T, E>.map(map: (T) -> R): Result<R, E> {
    return when (this) {
        is Result.Success -> Result.Success(map(data))
        is Result.Failure -> Result.Failure(error)
    }
}

/**
 * Executes the given [action] if the [Result] is a [Result.Success].
 * Returns the original [Result] instance unchanged.
 *
 * This is useful for performing side effects, such as logging or analytics,
 * when an operation succeeds, without altering the result itself.
 *
 * @param action The block of code to execute with the success data.
 * @return The original [Result] instance.
 */
inline fun <T, E : Error> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> {
    return when (this) {
        is Result.Success -> {
            action(data)
            this
        }
        is Result.Failure -> this
    }
}

/**
 * Executes the given [action] if this [Result] is a [Result.Failure].
 *
 * This function is useful for performing side-effects, such as logging or showing an alert,
 * when an operation fails, without altering the result itself. It returns the original [Result] instance.
 *
 * @param action The action to be executed, taking the [Error] as its parameter.
 * @return The original [Result] instance.
 */
inline fun <T, E : Error> Result<T, E>.onFailure(action: (E) -> Unit): Result<T, E> {
    return when (this) {
        is Result.Success -> this
        is Result.Failure -> {
            action(error)
            this
        }
    }
}

typealias EmptyResult<E> = Result<Unit, E>

/**
 * Converts a `Result<T, E>` to an `EmptyResult<E>`, which is a `Result<Unit, E>`.
 * This is useful when the successful data `T` is not needed, but the success/failure
 * status is still important.
 *
 * If the original `Result` is a `Success`, it returns `Result.Success(Unit)`.
 * If it's a `Failure`, the original error is preserved.
 *
 * @return An `EmptyResult<E>` representing the outcome without the successful data.
 */
fun <T, E : Error> Result<T, E>.asEmptyResult(): EmptyResult<E> {
    return map {  }
}