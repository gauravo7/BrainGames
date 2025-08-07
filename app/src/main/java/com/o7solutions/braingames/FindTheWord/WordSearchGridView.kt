package com.example.zigzag

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.GridLayout
import kotlin.math.abs

class WordSearchGridView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr) {

    private val linePaint = Paint().apply {
        isAntiAlias = true
        strokeWidth = 8f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val colors = listOf(
        Color.parseColor("#00CED1"), 
        Color.parseColor("#FFA500"), 
        Color.BLUE,
        Color.parseColor("#FF69B4")
    )

    private var currentWord = ""
    private var selectedCells = mutableListOf<GridCellView>()
    private var currentColorIndex = 0
    private var isDragging = false
    private var lastTouchX = 0f
    private var lastTouchY = 0f

    private var onWordFoundListener: ((String, Int) -> Unit)? = null
    private var targetWords = mutableListOf<String>()
    private var foundWordPaths = mutableMapOf<String, MutableList<GridCellView>>()
    private var hintedWordPaths = mutableMapOf<String, MutableList<GridCellView>>()

    fun setOnWordFoundListener(listener: (String, Int) -> Unit) {
        onWordFoundListener = listener
    }

    fun setTargetWords(words: List<String>) {
        targetWords.clear()
        targetWords.addAll(words)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDragging = true
                val cell = findCellAt(event.x, event.y)
                if (cell != null && !selectedCells.contains(cell)) {
                    selectedCells.clear()
                    currentWord = ""
                }
                lastTouchX = event.x
                lastTouchY = event.y
                handleTouch(event.x , event.y)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (isDragging) {
                    handleTouch(event.x, event.y)
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                isDragging = false
                checkWord()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun handleTouch(x: Float, y: Float) {
        val cell = findCellAt(x, y) ?: return
        
        if (selectedCells.isEmpty() || isValidNeighbor(selectedCells.last(), cell)) {
            if (!selectedCells.contains(cell)) {
                selectedCells.add(cell)
                cell.isCellSelected = true
                cell.selectionColor = colors[currentColorIndex % colors.size]
                currentWord += cell.text
                invalidate()
            }
        } else if (selectedCells.isNotEmpty() && selectedCells.contains(cell)) {
            val index = selectedCells.indexOf(cell)
            if (index >= 0 && index < selectedCells.size - 1) {
                for (i in selectedCells.size - 1 downTo index + 1) {
                    selectedCells[i].isCellSelected = false
                    selectedCells.removeAt(i)
                }
                currentWord = selectedCells.joinToString("") { it.text }
                invalidate()
            }
        }
        
        lastTouchX = x
        lastTouchY = y
    }

    private fun findCellAt(x: Float, y: Float): GridCellView? {
        for (i in 0 until childCount) {
            val child = getChildAt(i) as? GridCellView ?: continue
            if (x >= child.left && x <= child.right && y >= child.top && y <= child.bottom) {
                return child
            }
        }
        return null
    }

    private fun isValidNeighbor(cell1: GridCellView, cell2: GridCellView): Boolean {
        val index1 = indexOfChild(cell1)
        val index2 = indexOfChild(cell2)
        
        val row1 = index1 / columnCount
        val col1 = index1 % columnCount
        val row2 = index2 / columnCount
        val col2 = index2 % columnCount
        return (row1 == row2 && abs(col1 - col2) == 1) || (col1 == col2 && abs(row1 - row2) == 1)
    }

    private fun checkWord() {
        if (currentWord.length >= 3) {
            val foundWord = targetWords.find { it.equals(currentWord, ignoreCase = true) }
            if (foundWord != null) {
                foundWordPaths[foundWord] = selectedCells.toMutableList()
                onWordFoundListener?.invoke(foundWord, currentColorIndex)
            } else {
                onWordFoundListener?.invoke(currentWord, currentColorIndex)
            }
        }
        
        selectedCells.forEach { cell ->
            val foundWord = foundWordPaths.entries.find { it.value.contains(cell) }?.key
            val hintedWord = hintedWordPaths.entries.find { it.value.contains(cell) }?.key
            
            if (foundWord != null) {
                val colorIndex = foundWordPaths.keys.indexOf(foundWord)
                cell.selectionColor = colors[colorIndex % colors.size]
            } else if (hintedWord != null) {
                cell.selectionColor = Color.parseColor("#FF69B4")
            } else {
                cell.isCellSelected = false
            }
        }
        selectedCells.clear()
        currentWord = ""
        invalidate()
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
    }

    fun resetSelection() {
        selectedCells.forEach { it.isCellSelected = false }
        foundWordPaths.values.forEach { cells ->
            cells.forEach { it.isCellSelected = false }
        }
        hintedWordPaths.values.forEach { cells ->
            cells.forEach { it.isCellSelected = false }
        }
        selectedCells.clear()
        foundWordPaths.clear()
        hintedWordPaths.clear()
        currentWord = ""
        invalidate()
    }

    fun setNextColor() {
        currentColorIndex++
    }
    
    fun removeHint(word: String) {
        val cellsToRemove = hintedWordPaths[word]
        cellsToRemove?.forEach { cell ->
            cell.isCellSelected = false
        }
        hintedWordPaths.remove(word)
        invalidate()
    }
    
    fun highlightWordPermanent(word: String) {
        for (i in 0 until childCount) {
            val cell = getChildAt(i) as? GridCellView ?: continue
            val row = i / columnCount
            val col = i % columnCount
            if (col + word.length <= columnCount) {
                var found = true
                for (j in word.indices) {
                    val checkCell = getChildAt(row * columnCount + col + j) as? GridCellView
                    if (checkCell?.text != word[j].toString()) {
                        found = false
                        break
                    }
                }
                if (found) {
                    val highlightedCells = mutableListOf<GridCellView>()
                    for (j in word.indices) {
                        val highlightCell = getChildAt(row * columnCount + col + j) as GridCellView
                        highlightCell.isCellSelected = true
                        highlightCell.selectionColor = Color.parseColor("#FF69B4")
                        highlightedCells.add(highlightCell)
                    }
                    hintedWordPaths[word] = highlightedCells
                    invalidate()
                    return
                }
            }
            
            if (row + word.length <= rowCount) {
                var found = true
                for (j in word.indices) {
                    val checkCell = getChildAt((row + j) * columnCount + col) as? GridCellView
                    if (checkCell?.text != word[j].toString()) {
                        found = false
                        break
                    }
                }
                if (found) {
                    val highlightedCells = mutableListOf<GridCellView>()
                    for (j in word.indices) {
                        val highlightCell = getChildAt((row + j) * columnCount + col) as GridCellView
                        highlightCell.isCellSelected = true
                        highlightCell.selectionColor = Color.parseColor("#FF69B4")
                        highlightedCells.add(highlightCell)
                    }
                    hintedWordPaths[word] = highlightedCells
                    invalidate()
                    return
                }
            }
        }
        
        for (i in 0 until childCount) {
            val cell = getChildAt(i) as? GridCellView ?: continue
            val row = i / columnCount
            val col = i % columnCount
            if (col + word.length <= columnCount) {
                var found = true
                for (j in word.indices) {
                    val checkCell = getChildAt(row * columnCount + col + j) as? GridCellView
                    if (checkCell?.text != word[word.length - 1 - j].toString()) {
                        found = false
                        break
                    }
                }
                if (found) {
                    val highlightedCells = mutableListOf<GridCellView>()
                    for (j in word.indices) {
                        val highlightCell = getChildAt(row * columnCount + col + j) as GridCellView
                        highlightCell.isCellSelected = true
                        highlightCell.selectionColor = Color.parseColor("#FF69B4") // Pink color
                        highlightedCells.add(highlightCell)
                    }
                    hintedWordPaths[word] = highlightedCells
                    invalidate()
                    return
                }
            }
            
            if (row + word.length <= rowCount) {
                var found = true
                for (j in word.indices) {
                    val checkCell = getChildAt((row + j) * columnCount + col) as? GridCellView
                    if (checkCell?.text != word[word.length - 1 - j].toString()) {
                        found = false
                        break
                    }
                }
                if (found) {
                    val highlightedCells = mutableListOf<GridCellView>()
                    for (j in word.indices) {
                        val highlightCell = getChildAt((row + j) * columnCount + col) as GridCellView
                        highlightCell.isCellSelected = true
                        highlightCell.selectionColor = Color.parseColor("#FF69B4") // Pink color
                        highlightedCells.add(highlightCell)
                    }
                    hintedWordPaths[word] = highlightedCells
                    invalidate()
                    return
                }
            }
        }
    }
    
    fun highlightWord(word: String) {
        for (i in 0 until childCount) {
            val cell = getChildAt(i) as? GridCellView ?: continue
            val row = i / columnCount
            val col = i % columnCount
            if (col + word.length <= columnCount) {
                var found = true
                for (j in word.indices) {
                    val checkCell = getChildAt(row * columnCount + col + j) as? GridCellView
                    if (checkCell?.text != word[j].toString()) {
                        found = false
                        break
                    }
                }
                if (found) {
                    val highlightedCells = mutableListOf<GridCellView>()
                    for (j in word.indices) {
                        val highlightCell = getChildAt(row * columnCount + col + j) as GridCellView
                        highlightCell.isCellSelected = true
                        highlightCell.selectionColor = Color.parseColor("#FF69B4")
                        highlightedCells.add(highlightCell)
                    }
                    hintedWordPaths[word] = highlightedCells
                    invalidate()
                    postDelayed({
                        if (hintedWordPaths.containsKey(word)) {
                            for (j in word.indices) {
                                val highlightCell = getChildAt(row * columnCount + col + j) as GridCellView
                                highlightCell.isCellSelected = false
                            }
                            hintedWordPaths.remove(word)
                            invalidate()
                        }
                    }, 4000)
                    return
                }
            }
            if (row + word.length <= rowCount) {
                var found = true
                for (j in word.indices) {
                    val checkCell = getChildAt((row + j) * columnCount + col) as? GridCellView
                    if (checkCell?.text != word[j].toString()) {
                        found = false
                        break
                    }
                }
                if (found) {
                    val highlightedCells = mutableListOf<GridCellView>()
                    for (j in word.indices) {
                        val highlightCell = getChildAt((row + j) * columnCount + col) as GridCellView
                        highlightCell.isCellSelected = true
                        highlightCell.selectionColor = Color.parseColor("#FF69B4")
                        highlightedCells.add(highlightCell)
                    }
                    hintedWordPaths[word] = highlightedCells
                    invalidate()
                    postDelayed({
                        if (hintedWordPaths.containsKey(word)) {
                            for (j in word.indices) {
                                val highlightCell = getChildAt((row + j) * columnCount + col) as GridCellView
                                highlightCell.isCellSelected = false
                            }
                            hintedWordPaths.remove(word)
                            invalidate()
                        }
                    }, 4000)
                    return
                }
            }
        }
        for (i in 0 until childCount) {
            val cell = getChildAt(i) as? GridCellView ?: continue
            val row = i / columnCount
            val col = i % columnCount
            if (col + word.length <= columnCount) {
                var found = true
                for (j in word.indices) {
                    val checkCell = getChildAt(row * columnCount + col + j) as? GridCellView
                    if (checkCell?.text != word[word.length - 1 - j].toString()) {
                        found = false
                        break
                    }
                }
                if (found) {
                    val highlightedCells = mutableListOf<GridCellView>()
                    for (j in word.indices) {
                        val highlightCell = getChildAt(row * columnCount + col + j) as GridCellView
                        highlightCell.isCellSelected = true
                        highlightCell.selectionColor = Color.parseColor("#FF69B4")
                        highlightedCells.add(highlightCell)
                    }
                    hintedWordPaths[word] = highlightedCells
                    invalidate()
                    postDelayed({
                        if (hintedWordPaths.containsKey(word)) {
                            for (j in word.indices) {
                                val highlightCell = getChildAt(row * columnCount + col + j) as GridCellView
                                highlightCell.isCellSelected = false
                            }
                            hintedWordPaths.remove(word)
                            invalidate()
                        }
                    }, 4000)
                    return
                }
            }
            if (row + word.length <= rowCount) {
                var found = true
                for (j in word.indices) {
                    val checkCell = getChildAt((row + j) * columnCount + col) as? GridCellView
                    if (checkCell?.text != word[word.length - 1 - j].toString()) {
                        found = false
                        break
                    }
                }
                if (found) {
                    val highlightedCells = mutableListOf<GridCellView>()
                    for (j in word.indices) {
                        val highlightCell = getChildAt((row + j) * columnCount + col) as GridCellView
                        highlightCell.isCellSelected = true
                        highlightCell.selectionColor = Color.parseColor("#FF69B4")
                        highlightedCells.add(highlightCell)
                    }
                    hintedWordPaths[word] = highlightedCells
                    invalidate()
                    postDelayed({
                        if (hintedWordPaths.containsKey(word)) {
                            for (j in word.indices) {
                                val highlightCell = getChildAt((row + j) * columnCount + col) as GridCellView
                                highlightCell.isCellSelected = false
                            }
                            hintedWordPaths.remove(word)
                            invalidate()
                        }
                    }, 4000)
                    return
                }
            }
        }
    }
} 