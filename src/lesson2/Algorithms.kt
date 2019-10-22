@file:Suppress("UNUSED_PARAMETER")

package lesson2

import java.io.File
import java.lang.IllegalArgumentException
import kotlin.math.sqrt

/**
 * Получение наибольшей прибыли (она же -- поиск максимального подмассива)
 * Простая
 *
 * Во входном файле с именем inputName перечислены цены на акции компании в различные (возрастающие) моменты времени
 * (каждая цена идёт с новой строки). Цена -- это целое положительное число. Пример:
 *
 * 201
 * 196
 * 190
 * 198
 * 187
 * 194
 * 193
 * 185
 *
 * Выбрать два момента времени, первый из них для покупки акций, а второй для продажи, с тем, чтобы разница
 * между ценой продажи и ценой покупки была максимально большой. Второй момент должен быть раньше первого.
 * Вернуть пару из двух моментов.
 * Каждый момент обозначается целым числом -- номер строки во входном файле, нумерация с единицы.
 * Например, для приведённого выше файла результат должен быть Pair(3, 4)
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
// N = кол-во строк
//Трудоёмкость = O(N)
//Ресурсоемкость = O(N)
fun optimizeBuyAndSell(inputName: String): Pair<Int, Int> {
    val pricesList = mutableListOf<Int>()

    for (line in File(inputName).readLines()) {
        if (!Regex("""\d+""").matches(line)) throw IllegalArgumentException()
        else {
            pricesList.add(line.toInt())
        }
    }

    var bestBuy = 0
    var bestSell = 0
    var minIndex = 0


    for (index in 1 until pricesList.size) {
        if (pricesList[index] < pricesList[minIndex])
            minIndex = index

        when {
            pricesList[index] - pricesList[minIndex] > pricesList[bestSell] - pricesList[bestBuy] -> {
                bestBuy = minIndex
                bestSell = index
            }
        }

    }
    return (Pair(bestBuy + 1, bestSell + 1))
}

/**
 * Задача Иосифа Флафия.
 * Простая
 *
 * Образовав круг, стоят menNumber человек, пронумерованных от 1 до menNumber.
 *
 * 1 2 3
 * 8   4
 * 7 6 5
 *
 * Мы считаем от 1 до choiceInterval (например, до 5), начиная с 1-го человека по кругу.
 * Человек, на котором остановился счёт, выбывает.
 *
 * 1 2 3
 * 8   4
 * 7 6 х
 *
 * Далее счёт продолжается со следующего человека, также от 1 до choiceInterval.
 * Выбывшие при счёте пропускаются, и человек, на котором остановился счёт, выбывает.
 *
 * 1 х 3
 * 8   4
 * 7 6 Х
 *
 * Процедура повторяется, пока не останется один человек. Требуется вернуть его номер (в данном случае 3).
 *
 * 1 Х 3
 * х   4
 * 7 6 Х
 *
 * 1 Х 3
 * Х   4
 * х 6 Х
 *
 * х Х 3
 * Х   4
 * Х 6 Х
 *
 * Х Х 3
 * Х   х
 * Х 6 Х
 *
 * Х Х 3
 * Х   Х
 * Х х Х
 *
 * Общий комментарий: решение из Википедии для этой задачи принимается,
 * но приветствуется попытка решить её самостоятельно.
 */
//N = menNumber
//Трудоёмкость = O(N)
//Ресурсоёмкость = O(1)
fun josephTask(menNumber: Int, choiceInterval: Int): Int {
    var previous = 0

    (1..menNumber).forEach {
        previous = (choiceInterval - 1 + previous) % it + 1
    }
    return previous
}

/**
 * Наибольшая общая подстрока.
 * Средняя
 *
 * Дано две строки, например ОБСЕРВАТОРИЯ и КОНСЕРВАТОРЫ.
 * Найти их самую длинную общую подстроку -- в примере это СЕРВАТОР.
 * Если общих подстрок нет, вернуть пустую строку.
 * При сравнении подстрок, регистр символов *имеет* значение.
 * Если имеется несколько самых длинных общих подстрок одной длины,
 * вернуть ту из них, которая встречается раньше в строке first.
 */
//N = first.length
// M = second.length
//Трудоемкость = O(M*N) ~ O(N^2)
//Ресурсоёмкость = O(M*N) ~ O(N^2)
fun longestCommonSubstring(first: String, second: String): String {
    if (first.isEmpty() || second.isEmpty()) return ""
    if (first == second) return first

    val count = Array(first.length + 1) { IntArray(second.length + 1) }
    var maxIndex = 0
    var maxLength = 0

    first.indices.forEach { i ->
        second.indices.forEach { j ->
            if (first[i] == second[j]) {
                count[i + 1][j + 1] = count[i][j] + 1
                if (count[i + 1][j + 1] > maxLength) {
                    maxLength = count[i + 1][j + 1]
                    maxIndex = i
                }
            }
        }
    }
    return when {
        maxLength == 0 -> ""
        else -> first.substring(maxIndex - maxLength + 1, maxIndex + 1)
    }


}

/**
 * Число простых чисел в интервале
 * Простая
 *
 * Рассчитать количество простых чисел в интервале от 1 до limit (включительно).
 * Если limit <= 1, вернуть результат 0.
 *
 * Справка: простым считается число, которое делится нацело только на 1 и на себя.
 * Единица простым числом не считается.
 */
//N=limit.size
//Трудоёмкость = O(log(N))
//Ресурсоёмкость = O(1)
fun calcPrimesNumber(limit: Int): Int = (1..limit).count { isPrime(it) }

//in tandem with this fun
fun isPrime(n: Int): Boolean {
    return when {
        n <= 1 -> false
        n == 2 -> true
        else -> {
            for (num in 2..sqrt(n.toDouble()).toInt()) {
                if (n % num == 0)
                    return false
            }
            true
        }
    }
}

/**
 * Балда
 * Сложная
 *
 * В файле с именем inputName задана матрица из букв в следующем формате
 * (отдельные буквы в ряду разделены пробелами):
 *
 * И Т Ы Н
 * К Р А Н
 * А К В А
 *
 * В аргументе words содержится множество слов для поиска, например,
 * ТРАВА, КРАН, АКВА, НАРТЫ, РАК.
 *
 * Попытаться найти каждое из слов в матрице букв, используя правила игры БАЛДА,
 * и вернуть множество найденных слов. В данном случае:
 * ТРАВА, КРАН, АКВА, НАРТЫ
 *
 * И т Ы Н     И т ы Н
 * К р а Н     К р а н
 * А К в а     А К В А
 *
 * Все слова и буквы -- русские или английские, прописные.
 * В файле буквы разделены пробелами, строки -- переносами строк.
 * Остальные символы ни в файле, ни в словах не допускаются.
 */
fun baldaSearcher(inputName: String, words: Set<String>): Set<String> {
    TODO()
}