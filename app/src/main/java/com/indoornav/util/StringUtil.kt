package com.indoornav.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.util.Base64

object StringUtil {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getBase64EncodedString(message: String?): String? {
        if (message == null) return null
        return Base64.getEncoder().encodeToString(message.toByteArray(Charsets.UTF_8))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getBase64DecodedString(message: String?): String? {
        if (message == null) return null
        return Base64.getDecoder().decode(message).toString(Charsets.UTF_8)
    }
}