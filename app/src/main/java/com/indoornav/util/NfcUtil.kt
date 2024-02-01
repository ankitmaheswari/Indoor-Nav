package com.indoornav.util

import android.content.Context
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.core.util.Consumer

object NfcUtil {
    public fun writeOnTag(tag: Tag?,
                           output: String,
                            context: Context,
                           consumer: Consumer<Boolean>,
                           ) {
        val data = output.toByteArray(Charsets.UTF_8)
        val message = NdefMessage(NdefRecord.createMime("text/plain", data))
        try {
            val ndef = Ndef.get(tag)

            if (ndef != null) {
                ndef.connect()
                ndef.writeNdefMessage(message)
                ndef.close()

                Toast.makeText(context, "Tag written successfully.", Toast.LENGTH_LONG).show()
                consumer.accept(true)
            } else {
                val format = NdefFormatable.get(tag)

                if (format != null) {
                    format.connect()
                    format.format(message)
                    format.close()

                    Toast.makeText(context, "Tag written successfully.", Toast.LENGTH_LONG).show()
                    consumer.accept(true)
                } else {
                    Toast.makeText(context, "Tag is not NDEF formatted.", Toast.LENGTH_LONG).show()
                    consumer.accept(false)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to write tag.", Toast.LENGTH_LONG).show()
            consumer.accept(false)
        }
    }

    public fun readTag(tag: Tag, context: Context, consumer: Consumer<String>) {
        try {
            val ndef = Ndef.get(tag)

            if (ndef != null) {
                ndef.connect()

                val message = ndef.ndefMessage
                val payload = message.records[0].payload
                val text = payload.toString(Charsets.UTF_8)

                ndef.close()
                consumer.accept(text)
            } else {
                Toast.makeText(context, "Tag is not NDEF formatted.", Toast.LENGTH_LONG).show()
                consumer.accept(null)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to read tag.", Toast.LENGTH_LONG).show()
            consumer.accept(null)
        }
    }
}