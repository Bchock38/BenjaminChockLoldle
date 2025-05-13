package com.example.Loldle;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SessionData {

    private List<String[]> answers = new ArrayList<>(); // List for storing game results
    private List<String> guesses = new ArrayList<>();   // List for storing user guesses

    public List<String[]> getAnswers() {
        return answers;
    }

    public void addAnswer(String[] answer) {
        answers.add(answer);
    }

    public void reset() {
        answers.clear();  // Clears the answers list (game data)
        guesses.clear();  // Clears the guesses list (user's guesses)
    }

    public void addGuess(String guess) {
        guesses.add(guess);
    }

    public List<String> getGuesses() {
        return guesses;
    }

}
