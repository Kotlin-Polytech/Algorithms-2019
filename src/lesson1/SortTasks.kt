@file:Suppress("UNUSED_PARAMETER")

package lesson1

import java.io.File
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.util.Arrays.sort

/**
 * Сортировка времён
 *
 * Простая
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле с именем inputName содержатся моменты времени в формате ЧЧ:ММ:СС AM/PM,
 * каждый на отдельной строке. См. статью википедии "12-часовой формат времени".
 *
 * Пример:
 *
 * 01:15:19 PM
 * 07:26:57 AM
 * 10:00:03 AM
 * 07:56:14 PM
 * 01:15:19 PM
 * 12:40:31 AM
 *
 * Отсортировать моменты времени по возрастанию и вывести их в выходной файл с именем outputName,
 * сохраняя формат ЧЧ:ММ:СС AM/PM. Одинаковые моменты времени выводить друг за другом. Пример:
 *
 * 12:40:31 AM
 * 07:26:57 AM
 * 10:00:03 AM
 * 01:15:19 PM
 * 01:15:19 PM
 * 07:56:14 PM
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
// N - кол-во строк
//Трудоёмкость ~ O(NLog(N))
//Ремурсоёмкость ~ O(N)
fun sortTimes(inputName: String, outputName: String) {
    val amTime = arrayListOf<Pair<Int, String>>()
    val pmTime = arrayListOf<Pair<Int, String>>()
    val resultAM = arrayListOf<String>()
    val resultPM = arrayListOf<String>()

    for (line in File(inputName).readLines()) {
        if (!Regex("""([0-9]{2}):([0-9]{2}):([0-9]{2}) (AM|PM)""")
                .matches(line)
        ) throw IllegalArgumentException()
        else {
            val timeLine = line.split(" ")
            val time = timeLine[0].split(":")
            val hour = time[0].toInt()
            val min = time[1].toInt()
            val sec = time[2].toInt()

            when {
                hour !in 0..12 || min !in 0..59 || sec !in 0..59 ->
                    throw IllegalArgumentException()

                line.contains("AM") ->
                    amTime += Pair(hour % 12 * 3600 + min * 60 + sec, line)
                else ->
                    pmTime += Pair(hour % 12 * 3600 + min * 60 + sec, line)
            }
        }
    }

    amTime.asSequence().sortedBy { it.first }.mapTo(resultAM) { it.second }
    pmTime.asSequence().sortedBy { it.first }.mapTo(resultPM) { it.second }

    File(outputName).writeText((resultAM + resultPM).joinToString("\n"))
}

/**
 * Сортировка адресов
 *
 * Средняя
 *
 * Во входном файле с именем inputName содержатся фамилии и имена жителей города с указанием улицы и номера дома,
 * где они прописаны. Пример:
 *
 * Петров Иван - Железнодорожная 3
 * Сидоров Петр - Садовая 5
 * Иванов Алексей - Железнодорожная 7
 * Сидорова Мария - Садовая 5
 * Иванов Михаил - Железнодорожная 7
 *
 * Людей в городе может быть до миллиона.
 *
 * Вывести записи в выходной файл outputName,
 * упорядоченными по названию улицы (по алфавиту) и номеру дома (по возрастанию).
 * Людей, живущих в одном доме, выводить через запятую по алфавиту (вначале по фамилии, потом по имени). Пример:
 *
 * Железнодорожная 3 - Петров Иван
 * Железнодорожная 7 - Иванов Алексей, Иванов Михаил
 * Садовая 5 - Сидоров Петр, Сидорова Мария
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
//N - кол-во строк в файле
//Трудоёмкость= O(N^2)
//Ресурсоёмкость = O(N*logN) (not sure about this one)
fun sortAddresses(inputName: String, outputName: String) {
    val output = HashMap<Pair<String, Int>, MutableList<Pair<String, String>>>()
    val writer = File(outputName).bufferedWriter()
    val delimiter = Regex(""" -? ?""")

    for (line in File(inputName).readLines()) {
        if (!Regex("""\S* \S* - \S* [1-9][0-9]*""")
                .matches(line)
        ) throw IllegalArgumentException()

        val part = line.split(delimiter)
        val person = Pair(part[0], part[1])
        val address = Pair(part[2], part[3].toInt())

        output.getOrPut(address, {
            mutableListOf(person)
        })
        output[address]!!.add(person)
    }

    for (line in output.keys.toSortedSet(compareBy(
        { it.first },
        { it.second }
    ))) {
        writer.append(line.first + " " + line.second + " - ")
        val sortedResidents = output[line]!!.toSortedSet(compareBy(
            { it.first },
            { it.second }
        ))
        writer.appendln(sortedResidents.joinToString { it.first + " " + it.second })
    }
    writer.close()
}

/**
 * Сортировка температур
 *
 * Средняя
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле заданы температуры различных участков абстрактной планеты с точностью до десятых градуса.
 * Температуры могут изменяться в диапазоне от -273.0 до +500.0.
 * Например:
 *
 * 24.7
 * -12.6
 * 121.3
 * -98.4
 * 99.5
 * -12.6
 * 11.0
 *
 * Количество строк в файле может достигать ста миллионов.
 * Вывести строки в выходной файл, отсортировав их по возрастанию температуры.
 * Повторяющиеся строки сохранить. Например:
 *
 * -98.4
 * -12.6
 * -12.6
 * 11.0
 * 24.7
 * 99.5
 * 121.3
 */
//N - кол-во строк(температур)
//Трудоёмкость = O(N)
//Ресурсоёмкость = O(N)
fun sortTemperatures(inputName: String, outputName: String) {
    val pluses = arrayListOf<Int>()
    val minuses = arrayListOf<Int>()

    for (line in File(inputName).readLines()) {
        if (!Regex("""-?(\d+)\.(\d)""")
                .matches(line)
        ) throw IllegalArgumentException()
        else {
            val temp = (line.toDouble() * 10).toInt()
            when {
                temp >= 0 -> pluses += temp
                else -> minuses += temp * -1
            }

        }
    }
    val positives = countingSort(pluses.toIntArray(), 5000)
        .joinToString("\n") {
            (it.toDouble() / 10).toString()
        }

    val negatives = countingSort(minuses.toIntArray(), 2730)
        .reversedArray()
        .joinToString("\n") {
            "-" + (it.toDouble() / 10)
        }

    val result = listOf(negatives, positives)
        .filter {
            it.isNotBlank()
        }
        .joinToString("\n")

    File(outputName).writeText(result)
}

/**
 * Сортировка последовательности
 *
 * Средняя
 * (Задача взята с сайта acmp.ru)
 *
 * В файле задана последовательность из n целых положительных чисел, каждое в своей строке, например:
 *
 * 1
 * 2
 * 3
 * 2
 * 3
 * 1
 * 2
 *
 * Необходимо найти число, которое встречается в этой последовательности наибольшее количество раз,
 * а если таких чисел несколько, то найти минимальное из них,
 * и после этого переместить все такие числа в конец заданной последовательности.
 * Порядок расположения остальных чисел должен остаться без изменения.
 *
 * 1
 * 3
 * 3
 * 1
 * 2
 * 2
 * 2
 */
//N - кол-во строк(температур)
//Трудоёмкость = O(N)
//Ресурсоёмкость = O(N)
fun sortSequence(inputName: String, outputName: String) {
    val numSec = arrayListOf<Int>()
    val result = arrayListOf<Int>()

    for (n in File(inputName).readLines()) {
        numSec += n.toInt()
    }

    val count = numSec.groupingBy { it }.eachCount()
    val max = count.values.max() ?: 0
    var min = Int.MAX_VALUE
    for (it in count) {
        if (it.value == max && it.key < min)
            min = it.key
    }

    for (it in numSec) {
        when {
            it != min -> result += it
        }
    }

    repeat((1..max).count()) {
        result += min
    }

    File(outputName).writeText(result.joinToString("\n"))
}

/**
 * Соединить два отсортированных массива в один
 *
 * Простая
 *
 * Задан отсортированный массив first и второй массив second,
 * первые first.size ячеек которого содержат null, а остальные ячейки также отсортированы.
 * Соединить оба массива в массиве second так, чтобы он оказался отсортирован. Пример:
 *
 * first = [4 9 15 20 28]
 * second = [null null null null null 1 3 9 13 18 23]
 *
 * Результат: second = [1 3 4 9 9 13 15 20 23 28]
 */
fun <T : Comparable<T>> mergeArrays(first: Array<T>, second: Array<T?>) {
    TODO()
}

