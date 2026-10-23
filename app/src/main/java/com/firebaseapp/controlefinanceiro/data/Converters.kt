package com.firebaseapp.controlefinanceiro.data

import androidx.room.TypeConverter
import com.firebaseapp.controlefinanceiro.data.entities.CategoryType
import com.firebaseapp.controlefinanceiro.data.entities.WordType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date


class Converters {

    private val gson = Gson()
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.format(dateTimeFormatter)
    }

    @TypeConverter
    fun toLocalDateTime(dateTimeString: String?): LocalDateTime? {
        return dateTimeString?.let { LocalDateTime.parse(it, dateTimeFormatter) }
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromBigDecimal(value: BigDecimal): String {
        return value.toString()
    }

    @TypeConverter
    fun stringToBigDecimal(value: String): BigDecimal {
        return BigDecimal(value)
    }

    @TypeConverter
    fun fromWordType(type: WordType): String {
        return type.value
    }

    @TypeConverter
    fun stringToWordType(string: String) : WordType {
        if (string == "word_income") {
            return WordType.Income
        }

        if (string == "word_expense") {
            return WordType.Expense
        }

        return WordType.Income
    }

    @TypeConverter
    fun fromCategoryType(type: CategoryType): String {
        return type.value
    }

    @TypeConverter
    fun stringToCategoryType(string: String) : CategoryType {
        if (string == "category_income") {
            return CategoryType.Income
        }

        if (string == "category_expense") {
            return CategoryType.Expense
        }

        return CategoryType.Income
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }
}