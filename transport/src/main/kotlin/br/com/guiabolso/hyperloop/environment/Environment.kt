package br.com.guiabolso.hyperloop.environment

fun getEnv(key: String, defaultValue: String = "") = System.getenv(key) ?: defaultValue
