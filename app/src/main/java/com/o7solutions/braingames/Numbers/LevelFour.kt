package com.o7solutions.braingames.Numbers

import kotlin.random.Random

object LevelFour {

    fun returnRandom(min: Int,max: Int): Int {
        val randomNumber = Random.nextInt(min,max)
        return randomNumber
    }

    fun solveLevelFour(): ArrayList<String> {
        val operand1 = returnRandom(1, 100)
        val operand2 = returnRandom(1, 100)
        var operand3 = returnRandom(1,100)
        var operatorPosition = returnRandom(0,1)
        var operatorPosition2 = returnRandom(0,1)
        var operator1 = ""
        var operator2 = ""
        var answer = 0
        var tempAnswer = 0

        var returnList = arrayListOf<String>()

        when(operatorPosition2) {
            0-> {
                tempAnswer = operand1 * operand2
                operator1 = "x"
            }
            1-> {
                answer = operand1 / operand2
                operator1 = "/"
            }
        }
        when(operatorPosition) {
            0-> {
                answer = tempAnswer + operand3
                operator2 = "+"
            }
            1-> {
                answer = tempAnswer - operand3
                operator2 = "-"
            }
        }



        returnList.add(operand1.toString())
        returnList.add(operand2.toString())
        returnList.add(operand3.toString())
        returnList.add(operator1.toString())
        returnList.add(operator2.toString())
        returnList.add(answer.toString())


        return  returnList


    }
}