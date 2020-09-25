package br.com.guiabolso.hyperloop.utils

fun <T1 : Any, T2 : Any> allNull(p1: T1?, p2: T2?): Boolean {
    return (p1 == null && p2 == null)
}
