package com.o7solutions.braingames.Numbers

import kotlin.random.Random

object LevelTwo {

    fun returnRandom(min: Int,max: Int): Int {
        val randomNumber = Random.nextInt(min,max)
        return randomNumber
    }



    fun solveLevelTwo(): ArrayList<String> {
        val operand1 = returnRandom(1,100)
        val operand2 = returnRandom(1,100)
        var operatorPosition = returnRandom(0,4)
        var operator = ""
        var answer = 0

        var returnList = arrayListOf<String>()

        when(operatorPosition) {
            0-> {
                answer = operand1 + operand2
                operator = "+"
            }
            1-> {
                answer = operand1 - operand2
                operator = "-"
            }
            2 -> {
                answer = operand1 * operand2
                operator = "x"
            }
            3 -> {
                answer = operand1 / operand2
                operator = "/"
            }
        }
        returnList.add(operand1.toString())
        returnList.add(operand2.toString())
        returnList.add(operator.toString())
        returnList.add(answer.toString())


        return  returnList


    }
}