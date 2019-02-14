package br.com.guiabolso.hyperloop.util

import java.time.LocalDateTime

object DefaultClock : Clock {
    override fun now(): LocalDateTime = LocalDateTime.now()
}
