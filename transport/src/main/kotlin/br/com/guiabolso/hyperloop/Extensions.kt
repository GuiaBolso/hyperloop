package br.com.guiabolso.hyperloop

import org.apache.commons.codec.digest.DigestUtils
import java.nio.charset.Charset
import java.util.Base64

fun String.md5(): String {
    return DigestUtils.md5Hex(this)
}

fun ByteArray.b64(charset: Charset = Charsets.UTF_8) = Base64.getEncoder().encode(this).toString(charset)
