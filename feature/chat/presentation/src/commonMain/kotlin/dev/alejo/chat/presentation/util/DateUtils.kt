package dev.alejo.chat.presentation.util

import dev.alejo.core.presentation.util.UiText
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import nexo.feature.chat.presentation.generated.resources.Res
import nexo.feature.chat.presentation.generated.resources.today
import nexo.feature.chat.presentation.generated.resources.yesterday
import kotlin.time.Clock
import kotlin.time.Instant

object DateUtils {

    fun formatMessageTime(instant: Instant, clock: Clock = Clock.System): UiText {
        val timeZone = TimeZone.currentSystemDefault()
        val messageDateTime = instant.toLocalDateTime(timeZone)
        val todayDate = clock.now().toLocalDateTime(timeZone).date
        val yesterdayDate = todayDate.minus(1, DateTimeUnit.DAY)

        val formattedTime = messageDateTime.format(
            format = LocalDateTime.Format {
                amPmHour()
                char(':')
                minute()
                char(' ')
                amPmMarker("am", "pm")
            }
        )
        val formattedDateTime = messageDateTime.format(
            format = LocalDateTime.Format {
                day()
                char('/')
                monthNumber()
                char('/')
                year()
                char(' ')
                chars(formattedTime)
            }
        )

        return when (messageDateTime.date) {
            todayDate -> UiText.Resource(Res.string.today, arrayOf(formattedTime))
            yesterdayDate -> UiText.Resource(Res.string.yesterday, arrayOf(formattedTime))
            else -> UiText.DynamicString(formattedDateTime)
        }
    }
}