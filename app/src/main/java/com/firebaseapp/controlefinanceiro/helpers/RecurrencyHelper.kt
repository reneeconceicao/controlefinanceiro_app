package com.firebaseapp.controlefinanceiro.helpers

import com.firebaseapp.controlefinanceiro.data.entities.Word
import com.firebaseapp.controlefinanceiro.data.entities.WordException
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.temporal.ChronoUnit


fun calculateWordOccurrences(
    words: List<Word>,
    exceptions: List<WordException>,
    startDate: LocalDateTime,
    endDate: LocalDateTime
): List<Pair<Word, Long?>> {

    // insert a pair with Word and an index
    // it is: (month interval amount between word to filter month)
    // example (word.date = 10/2026 --- filterDate 12/2026 --- index = 2)

    val result = mutableListOf<Pair<Word, Long?>>()

    // filter one time words by not isRecurrency and date filter interval
    val wordsList: List<Pair<Word, Long>> =
        words.filter { !it.isRecurring && (it.date in startDate..endDate) }.map { Pair(it, 0) }

    // filter recurring words
    val wordsRecurrency: List<Pair<Word, Long>> = words
        .filter {
            it.isRecurring
        }
        .mapNotNull { word ->

            // mapNotNull because if return null no show the word in the period

            // calculate index by interval from original word.date to current filter date
            val index = occurrenceIndex(word.date, YearMonth.from(startDate), word.interval)

            index?.let { index ->

                // get date to set in occurrence showed
                val occurrenceDate = getOccurrenceDate(word.date, index.toInt())

                // if word.endDate equals null, no end date to show
                if (word.endDate == null) {
                    // only past registers don't show
                    if (occurrenceDate < startDate) {
                        return@mapNotNull null
                    }
                } else {
                    // only range data show until word.endDate
                    if (occurrenceDate < startDate || occurrenceDate > word.endDate) {
                        return@mapNotNull null
                    }
                }

                // search for exception for this month by occurrenceIndex
                val exception = exceptions.find {
                    it.wordId == word.id &&
                            it.occurrenceIndex == index
                }


                when {

                    // if exception found...

                    // return null (don`t show nothing) if isDeleted equal true
                    exception?.isDeleted == true -> null

                    // show the previously saved exception date by replacing word data with its data
                    exception != null -> Pair(
                        word.copy(
                            date = exception.date,
                            type = exception.type,
                            value = exception.value,
                            categoryId = exception.categoryId,
                            categoryName = exception.categoryName,
                            categoryIcon = exception.categoryIcon,
                            images = exception.images,
                            notes = exception.notes
                        ), index
                    )

                    // if the search don't found an exception show the regular word
                    // with the date replaced by occurrenceDate
                    else -> Pair(word.copy(date = occurrenceDate), index)
                }
            }
        }


    result.addAll(wordsList)

    result.addAll(wordsRecurrency)

    return result.sortedBy { it.first.date }
}

fun Word.findExceptions(
    recurrenceIndex: Long = 0,
    exceptions: List<WordException> = listOf(),
    onOccurrenceFound: (Word) -> Unit,
    onExceptionFound: (WordException) -> Unit,
) {

    //search exceptions by wordId and occurrenceIndex
    val exception = exceptions.find {
        it.wordId == this.id &&
                it.occurrenceIndex == recurrenceIndex
    }

    //if found an exception
    exception?.let { exception ->
        onExceptionFound(exception)
        return
    }

    //if found a occurence, adjust date
    val date = getOccurrenceDate(this.date, recurrenceIndex.toInt())
    val recurringWord = this.copy(id = id, date = date)
    onOccurrenceFound(recurringWord)

}

fun occurrenceIndex(
    start: LocalDateTime,
    target: YearMonth,
    interval: Int
): Long? {
    // calculate interval between dates
    val months = ChronoUnit.MONTHS.between(
        YearMonth.from(start),
        target
    )

    // return null if months < 0 or skip "empty months"
    // ex: 01/2026 with interval 2 the next is 03/2026.
    if (months < 0 || months % interval != 0L) {
        return null
    }

    return months / interval
}

fun occurrenceIndex(
    start: LocalDateTime,
    target: LocalDateTime,
    interval: Int
): Long? {
    // calculate interval between dates
    val months = ChronoUnit.MONTHS.between(
        YearMonth.from(start),
        YearMonth.from(target)
    )

    // return null if months < 0 or skip "empty months"
    // ex: 01/2026 with interval 2 the next is 03/2026.
    if (months < 0 || months % interval != 0L) {
        return null
    }

    return months / interval
}

fun getOccurrenceDate(
    startDate: LocalDateTime,
    occurrenceIndex: Int
): LocalDateTime {
    return startDate.plusMonths(occurrenceIndex.toLong())
}