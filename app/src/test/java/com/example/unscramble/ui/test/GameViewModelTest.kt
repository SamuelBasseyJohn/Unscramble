package com.example.unscramble.ui.test

import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.getUnscrambledWord
import com.example.unscramble.ui.GameViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameViewModelTest {
    private val viewModel = GameViewModel()
    companion object {
        private const val SCORE_AFTER_FIRST_CORRECT_ANSWER = SCORE_INCREASE
        private const val TOTAL_NO_OF_WORDS = MAX_NO_OF_WORDS
    }

    //Success Path Tests
    @Test
    fun gameViewModel_CorrectWordGuessed_ScoreUpdatedAndErrorFlagUnset() {
        var currentGameUiState = viewModel.uiState.value
        val correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)
        viewModel.updateUserGuess(correctPlayerWord)
        viewModel.checkUserGuess()
        currentGameUiState = viewModel.uiState.value

        // Assert that checkUserGuess() method updates isGuessedWordWrong is updated correctly.
        assertFalse(currentGameUiState.isGuessedWordWrong)

        // Assert that score is updated correctly.
        assertEquals(SCORE_AFTER_FIRST_CORRECT_ANSWER, currentGameUiState.score)
    }

    // Error Path Tests
    @Test
    fun gameViewModel_IncorrectGuess_ErrorFlagSet() {
        // Given an incorrect word as an input
        val incorrectPlayerWord = "and"
        viewModel.updateUserGuess(incorrectPlayerWord)
        viewModel.checkUserGuess()
        var currentGameUiState = viewModel.uiState.value

        // Assert that the score is unchanged
        assertEquals(0, currentGameUiState.score)

        // Assert that isGuessedWordWrong is updated correctly
        assertTrue(currentGameUiState.isGuessedWordWrong)

    }

    // Boundary Case Tests
    @Test
    fun gameViewModel_GameViewModelInitialized_WordAndScoreAreSetToDefault() {
        val currentGameUiState = viewModel.uiState.value
        val unscrambledWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)

        // Assert that the current score is 0
        assertEquals(0, currentGameUiState.score)
        // Asserts that the game isn't over at the beginning of the game
        assertFalse(currentGameUiState.isGameOver)
        // Asserts that the current word count is 1
        assertEquals(1, currentGameUiState.currentWordCount)
        // Asserts that the current scrambled word is not the same as the original unscrambled word
        assertNotEquals(unscrambledWord, currentGameUiState.currentScrambledWord)
        // Asserts that the wrong word guessed is false

        assertFalse(currentGameUiState.isGuessedWordWrong)
    }

    @Test
    fun gameViewModel_AllWordGuessed_UiStateUpdatedCorrectly(){
        viewModel.resetGame()
        var expectedScore = 0
        var currentGameUiState = viewModel.uiState.value
        var userCorrectEntry = getUnscrambledWord(currentGameUiState.currentScrambledWord)
        repeat (TOTAL_NO_OF_WORDS){
            viewModel.updateUserGuess(userCorrectEntry)
            println("User Guess: ${viewModel.userGuess}, CurrentScrambled: ${currentGameUiState.currentScrambledWord}")
            viewModel.checkUserGuess()
            println("User Guess: ${viewModel.userGuess}, CurrentScrambled: ${currentGameUiState.currentScrambledWord}")
            expectedScore += SCORE_INCREASE
            currentGameUiState.score = expectedScore
            userCorrectEntry = getUnscrambledWord(currentGameUiState.currentScrambledWord)
            currentGameUiState = viewModel.uiState.value
            userCorrectEntry = getUnscrambledWord(currentGameUiState.currentScrambledWord)
        }
        assertEquals(TOTAL_NO_OF_WORDS, currentGameUiState.currentWordCount)
        assertEquals(expectedScore, currentGameUiState.score)
        assertTrue(currentGameUiState.isGameOver)

    }

    @Test
    fun gameViewModel_WordSkipped_ScoreUnchangedAndWordCountIncreased() {
        var currentGameUiState = viewModel.uiState.value
        val correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)
        viewModel.updateUserGuess(correctPlayerWord)
        viewModel.checkUserGuess()

        currentGameUiState = viewModel.uiState.value
        val lastWordCount = currentGameUiState.currentWordCount
        viewModel.skipWord()
        currentGameUiState = viewModel.uiState.value
        // Assert that score remains unchanged after word is skipped.
        assertEquals(SCORE_AFTER_FIRST_CORRECT_ANSWER, currentGameUiState.score)
        // Assert that word count is increased by 1 after word is skipped.
        assertEquals(lastWordCount + 1, currentGameUiState.currentWordCount)
    }

}