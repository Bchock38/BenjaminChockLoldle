package com.example.Loldle.service;

import com.example.Loldle.model.Champion;
import com.example.Loldle.util.TSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameService {
    private String[][] tsvData;
    private String header;
    private Champion[] champpool;
    private final Random rand = new Random();
    private final Map<String, Integer> champMap = new HashMap<>();
    private Champion answer;
    private Champion guess;
    private final ChampionTrie championTrie;

    @Autowired
    public GameService(ChampionTrie championTrie) {
        readData();
        createChampionMap();
        this.championTrie = championTrie;

        // Now safely populate the trie
        for (Champion champ : champpool) {
            championTrie.insert(champ.getChampion());
        }
    }

    public Champion[] getChamppool(){
        return champpool;
    }
    public List<String> getChampionRecommendations(String prefix) {
        return championTrie.getRecommendations(prefix);
    }

    public ChampionTrie getChampionTrie(){
        return championTrie;
    }

    // Reset the game state (called when page is reloaded)
    public void resetGame() {
        generateNewAnswer();// Generate a new answer (reset game state)
        guess = null;
        // You can add additional resets for other game state variables if necessary
    }

    public void generateNewAnswer() {
        int randomNum = rand.nextInt(170); // adjust based on your actual champ count
        answer = champpool[randomNum];
    }

    public String[] arrayToPrint(int[] code) {
        String[] colors = new String[code.length];
        for (int i = 0; i < code.length; i++) {
            if (code[i] == 0) {
                colors[i] = "green";
            } else if (code[i] == 1) {
                colors[i] = "yellow";
            } else {
                colors[i] = "red";
            }
        }
        return colors;
    }

    public String getHeader() {
        return header;
    }

    public Champion getAnswer() {
        return answer;
    }

    public Champion getChampionByName(String name) {
        Integer index = champMap.get(name);
        if (index == null) return null;
        return champpool[index];
    }

    public GuessResult checkGuess(String guessName) {
        guess = getChampionByName(guessName);
        if (guess == null) return null;

        int[] result = answer.check(guess); // Assuming this returns an int array
        String[] colors = arrayToPrint(result); // Convert result to colors
        String[] attributes = guess.getAttributes(); // Get champion's attributes

        return new GuessResult(colors, attributes);
    }


    public class GuessResult {
        private String[] colors;
        private String[] attributes;

        public GuessResult(String[] colors, String[] attributes) {
            this.colors = colors;
            this.attributes = attributes;
        }

        public String[] getColors() {
            return colors;
        }

        public String[] getAttributes() {
            return attributes;
        }
    }


    public List<String> getAllChampionNames() {
        return new ArrayList<>(champMap.keySet());
    }

    private void readData() {
        String filePath = "C:\\Users\\Benjamin Chock\\IdeaProjects\\BenChockLoldleFinalProject\\Resources\\Loldle Charcter Data - Sheet1.tsv";
        tsvData = TSVReader.readTSV(filePath);
        champpool = new Champion[tsvData.length - 1];
        for (int i = 1; i < tsvData.length; i++) {
            champpool[i - 1] = new Champion(tsvData[i][0], tsvData[i][1], tsvData[i][2], tsvData[i][3], tsvData[i][4], tsvData[i][5], tsvData[i][6], tsvData[i][7]);
        }
        header = tsvData[0][0] + " " + tsvData[0][1] + " " + tsvData[0][2] + " " + tsvData[0][3] + " "
                + tsvData[0][4] + " " + tsvData[0][5] + " " + tsvData[0][6] + " " + tsvData[0][7];
    }

    private void createChampionMap() {
        for (int i = 0; i < champpool.length; i++) {
            champMap.put(champpool[i].getChampion(), i);
            champMap.put(champpool[i].getChampion().toLowerCase(), i);
        }
    }
}
