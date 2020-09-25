package br.com.guiabolso.hyperloop.util

import java.time.LocalDateTime

interface Clock {
    fun now(): LocalDateTime
}
