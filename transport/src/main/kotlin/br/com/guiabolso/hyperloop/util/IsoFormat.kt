package br.com.guiabolso.hyperloop.util

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun LocalDateTime.isoFormat(): String = this.atZone(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_INSTANT)