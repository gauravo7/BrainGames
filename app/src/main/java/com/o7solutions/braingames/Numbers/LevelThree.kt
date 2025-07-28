package com.o7solutions.braingames.Numbers

import kotlin.random.Random

object LevelThree {

    fun returnRandom(min: Int,max: Int): Int {
        val randomNumber = Random.nextInt(min,max)
        return randomNumber
    }

    fun solveLevelThree(): ArrayList<String> {
        val operand1 = returnRandom(1, 10)
        val operand2 = returnRandom(1, 10)
        var operand3 = returnRandom(1,10)
        var operatorPosition = returnRandom(0,2)
        var operatorPosition2 = returnRandom(0,2)
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



//        answer = operand1 if(oper)
        returnList.add(operand1.toString())
        returnList.add(operand2.toString())
        returnList.add(operand3.toString())
        returnList.add(operator1.toString())
        returnList.add(operator2.toString())
        returnList.add(answer.toString())


        return  returnList


    }
}