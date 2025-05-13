package com.example.Loldle.service;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChampionTrie {

    private TrieNode root;

    public ChampionTrie() {
        root = new TrieNode();
    }

    // Method to insert a champion name into the trie
    public void insert(String championName) {
        TrieNode node = root;
        for (char c : championName.toLowerCase().toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
        }
        node.isEndOfWord = true;
    }

    // Method to get a list of champions starting with the given prefix
    public List<String> getRecommendations(String prefix) {
        TrieNode node = root;
        List<String> recommendations = new ArrayList<>();

        // Traverse the trie to find the node representing the end of the prefix
        for (char c : prefix.toLowerCase().toCharArray()) {
            node = node.children.get(c);
            if (node == null) {
                return recommendations; // Return empty list if prefix is not found
            }
        }
        // Perform DFS from the current node to find all champions with the given prefix
        findAllWords(node, prefix, recommendations);
        return recommendations;
    }

    private void findAllWords(TrieNode node, String prefix, List<String> recommendations) {
        if (node.isEndOfWord) {
            recommendations.add(prefix);
        }
        for (char c : node.children.keySet()) {
            findAllWords(node.children.get(c), prefix + c, recommendations);
        }
    }

    // TrieNode class
    private static class TrieNode {
        private boolean isEndOfWord;
        private final java.util.Map<Character, TrieNode> children;
        public TrieNode() {
            children = new java.util.HashMap<>();
        }
    }
}
