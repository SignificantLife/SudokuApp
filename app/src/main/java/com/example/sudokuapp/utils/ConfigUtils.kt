package com.example.sudokuapp.utils

import android.content.Context
import com.example.sudokuapp.R
import org.xmlpull.v1.XmlPullParser

object ConfigUtils {

    fun getIntValue(context: Context, key: String, defaultValue: Int): Int {
        val parser = context.resources.getXml(R.xml.config)
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.name == "key") {
                val itemKey = parser.getAttributeValue(null, "name")
                if (itemKey == key) {
                    return parser.nextText().toIntOrNull() ?: defaultValue
                }
            }
            eventType = parser.next()
        }
        return defaultValue
    }
}
