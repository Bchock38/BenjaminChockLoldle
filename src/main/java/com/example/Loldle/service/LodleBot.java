package com.example.Loldle.service;

import com.example.Loldle.model.Champion;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.io.FileReader;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class LodleBot {
    private GameService botServe;
    private Map<String, Map<String, int[]>> feedbackCache = new HashMap<>();
    private ArrayList<Champion> allChampions = new ArrayList<>();
    private ArrayList<Champion> remainingChampions = new ArrayList<>();
    private ArrayList<String> chosenChamps = new ArrayList<String>();

    public LodleBot(GameService botServe) {
        this.botServe = botServe;
        allChampions.addAll(Arrays.asList(botServe.getChamppool()));
        remainingChampions.addAll(allChampions);
        loadFeedbackCacheFromFile("feedbackCache.json");
        if (feedbackCache.isEmpty()) {
            makeMap();
            saveFeedbackCacheToFile("feedbackCache.json");
        }
    }

    // Find the most optimal path to guess a champion
    public void playGame() {
        remainingChampions = new ArrayList<>(allChampions);
        while (remainingChampions.size() > 1) {
            // Use entrophy to find the best next guess
            Champion bestGuess = selectBestGuess();
            chosenChamps.add(bestGuess.getChampion());
            int[] feedbackReceived = getFeedback(bestGuess, remainingChampions.get(0));
            // Filter out champions that don't match previous champions guessed parameters
            filterRemainingChampions(bestGuess, feedbackReceived);
        }
        if (remainingChampions.size() == 1) {
            Champion correctChampion = remainingChampions.get(0);
            System.out.println("Game Over! Correct champion is: " + correctChampion.getChampion());
        }
    }

    // Find next best guess based on guesses a user made
    public Champion getNextGuess(List<Champion> previousGuesses, List<int[]> feedbacks) {
        List<Champion> simulatedRemaining = new ArrayList<>(allChampions);
        for (int i = 0; i < previousGuesses.size(); i++) {
            Champion guess = previousGuesses.get(i);
            int[] feedback = feedbacks.get(i);
            simulatedRemaining = filterList(simulatedRemaining, guess, feedback);
        }
        return selectBestGuessFromList(simulatedRemaining);
    }

    public ArrayList<String> getChosenChamps(){
        return chosenChamps;
    }

    // Filter out all champions that don't fit paremters after a champion is guessed
    private List<Champion> filterList(List<Champion> list, Champion guess, int[] feedbackReceived) {
        List<Champion> filtered = new ArrayList<>();
        for (Champion champ : list) {
            int[] testFeedback = getFeedback(guess, champ);
            boolean keep = true;
            for (int j = 0; j < feedbackReceived.length; j++) {
                int expected = feedbackReceived[j];
                int actual = testFeedback[j];
                if ((expected == 0 && actual != 0) ||
                        (expected == 1 && actual == 2) ||
                        (expected == 2 && actual != 2)) {
                    keep = false;
                    break;
                }
            }
            if (keep) filtered.add(champ);
        }
        return filtered;
    }

    // Use entrophy to select best next guess of all remaining canidates
    private Champion selectBestGuessFromList(List<Champion> candidates) {
        int bestEntropy = Integer.MAX_VALUE;
        Champion bestGuess = null;
        for (Champion guess : candidates) {
            int[] distribution = new int[candidates.size()];
            for (Champion target : candidates) {
                int[] feedback = getFeedback(guess, target);
                distribution[feedbackHashCode(feedback)]++;
            }
            int entropy = calculateEntropy(distribution);
            if (entropy < bestEntropy) {
                bestEntropy = entropy;
                bestGuess = guess;
            }
        }
        return bestGuess;
    }

    public Champion selectBestGuess() {
        return selectBestGuessFromList(remainingChampions);
    }

    // Calculate Entrophy (Chat did this for me had no idea to calculate on my own)
    public int calculateEntropy(int[] feedbackDistribution) {
        double entropy = 0.0;
        int total = 0;
        for (int count : feedbackDistribution) {
            total += count;
        }
        for (int count : feedbackDistribution) {
            if (count > 0) {
                double probability = (double) count / total;
                entropy -= probability * Math.log(probability) / Math.log(2);
            }
        }
        return (int) (entropy * 100);
    }

    // Make a map saving feedback between all champs to make it an easy look up
    public void makeMap() {
        for (Champion guess : botServe.getChamppool()) {
            Map<String, int[]> innerMap = new HashMap<>();
            for (Champion target : allChampions) {
                if (!guess.equals(target)) {
                    int[] feedback = getFeedback(guess, target);
                    innerMap.put(target.getChampion(), feedback);
                }
            }
            feedbackCache.put(guess.getChampion(), innerMap);
        }
    }

    public int feedbackHashCode(int[] feedback) {
        return Arrays.hashCode(feedback);
    }

    // Filter remaining Champions based on feedback recieved
    public void filterRemainingChampions(Champion guess, int[] feedbackReceived) {
        for (int i = 0; i < remainingChampions.size(); i++) {
            Champion currentChampion = remainingChampions.get(i);
            int[] testFeedback = getFeedback(guess, currentChampion);
            boolean eliminate = false;
            for (int j = 0; j < feedbackReceived.length; j++) {
                int expected = feedbackReceived[j];
                int actual = testFeedback[j];
                if ((expected == 0 && actual != 0) ||
                        (expected == 1 && actual == 2) ||
                        (expected == 2 && actual != 2)) {
                    eliminate = true;
                    break;
                }
            }
            if (eliminate) {
                remainingChampions.remove(i);
                i--;
            }
        }
    }

    int[] getFeedback(Champion guess, Champion target) {
        if (guess == null) return null;
        return target.check(guess);
    }

    // Use JSON to save feedback map to file so you don't need to recreate it every usage
    public void saveFeedbackCacheToFile(String filename) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(feedbackCache, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load in feedback map file
    public void loadFeedbackCacheFromFile(String filename) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filename)) {
            Type type = new TypeToken<Map<String, Map<String, int[]>>>() {}.getType();
            feedbackCache = gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
