package com.o7solutions.braingames.OddOut

object GameLib {

    fun getUniqueRandomNumbers(start: Int, end: Int, count: Int): List<Int> {

//        Throw exceptions
        require(end >= start) { "End must be greater than or equal to start" }
        require(count <= (end - start + 1)) { "Count cannot be more than the range size" }

//        return random numbers
        return (start..end).shuffled().take(count)
    }

    fun getRandomNumberFromList(numbers: List<Int>): Int {
        require(numbers.isNotEmpty()) { "List must not be empty" }
        return numbers.random()
    }


}