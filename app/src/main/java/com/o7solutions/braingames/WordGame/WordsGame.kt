package com.o7solutions.braingames.WordGame

import com.o7solutions.braingames.utils.AppFunctions

object WordsGame {


    fun giveShuffledWord(word: String): String {
        val s = word
        val shuffled = s.toList().shuffled().joinToString("")
        return shuffled
    }
    fun playGame(): ArrayList<String> {

        var numbers = "0123"
        var alphabets= arrayListOf<String>("a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","z","y","z")

        var actualWorld = Dictionary.giveWord()

        var shuffledAnswer = giveShuffledWord(actualWorld)

        var wrongAnswer1 = giveShuffledWord(
            shuffledAnswer.replace(shuffledAnswer[AppFunctions.returnRandom(0,6)].toString(),
                alphabets[AppFunctions.returnRandom(0,6)])
        )

        var wrongAnswer2 = giveShuffledWord(
            shuffledAnswer.replace(shuffledAnswer[AppFunctions.returnRandom(0,6)].toString(),
                alphabets[AppFunctions.returnRandom(0,6)])
        )

        var wrongAnswer3 = giveShuffledWord(
            shuffledAnswer.replace(shuffledAnswer[AppFunctions.returnRandom(0,6)].toString(),
                alphabets[AppFunctions.returnRandom(0,6)])
        )

        var shuffledNumbers =  giveShuffledWord(numbers)
        var answerList = arrayListOf<String>()
        answerList.add(shuffledNumbers[0].code,shuffledAnswer)
        answerList.add(shuffledNumbers[1].code,wrongAnswer1)
        answerList.add(shuffledNumbers[2].code,wrongAnswer2)
        answerList.add(shuffledNumbers[3].code,wrongAnswer3)
        answerList.add(4,shuffledAnswer)
        answerList.add(5,actualWorld)


        return answerList

    }
}