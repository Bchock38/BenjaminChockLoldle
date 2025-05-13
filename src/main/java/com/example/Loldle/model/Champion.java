package com.example.Loldle.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Champion {
    private String champion;
    private String gender;
    private String position;
    private String species;
    private String resource;
    private String range;
    private String regions;
    private String skins;
    private Set<String> positions;
    private String imageUrl;
    private static final int MATCH = 0;
    private static final int PARTIAL = 1;
    private static final int NO_MATCH = 2;

    public Champion(String Champ, String Gender, String Position, String Species, String Resource, String Range, String Region, String Skin){
        this.champion = Champ;
        this.gender = Gender;
        this.position = Position;
        this.species = Species;
        this.range = Range;
        this.regions = Region;
        this.skins = Skin;
        this.resource = Resource;
        this.imageUrl = generateImageUrl(Champ);
        positions = new HashSet<>(Arrays.asList(position.split("\\s*,\\s*")));
    }
    @Override
    public String toString() {
        return champion + " " + gender + " " + position + " " + species + " "
                + resource + " " + range + " " + regions + " " + skins;
    }


    public static void main (String[] args){

    }
    public int[] check(Champion newChamp) {
        int[] checked = new int[8];

        // Local variables for newChamp's attributes
        String newChampion = newChamp.getChampion();
        String newGender = newChamp.getGender();
        String newPosition = newChamp.getPosition();
        String newResource = newChamp.getResource();
        String newRange = newChamp.getRange();
        String newRegions = newChamp.getRegions(); // Updated to String
        String newSkins = newChamp.getSkins(); // Skins is a String

        // Compare Champion got ? MATCH : NO_MATCH and trim from chatgpt
        checked[0] = newChampion.trim().equals(champion) ? MATCH : NO_MATCH;

        // Compare Gender
        checked[1] = newGender.trim().equals(gender.trim()) ? MATCH : NO_MATCH;

        // (Asked chatgpt how to do this since I wasn't too sure how to compare the values of a string seperated by commas)
        checked[2] = (checkPositionMatch(newChamp.getPosition(), position));

        // Compare Species (use retainAll for species, since it's a Set<String>)
        checked[3] = compareSpecies(newChamp);

        // Compare Resource
        checked[4] = newResource.trim().equals(resource) ? MATCH : NO_MATCH;

        // Compare Range
        checked[5] = newRange.trim().equals(range) ? MATCH : NO_MATCH;

        // Compare Regions (direct String comparison)
        checked[6] = newRegions.trim().equals(regions) ? MATCH : NO_MATCH;

        // Compare Skins (updated to string comparison)
        checked[7] = newSkins.trim().equals(skins) ? MATCH : NO_MATCH;

        return checked;
    }
    public String[] getAttributes() {
        return new String[] {
                champion,
                gender,
                position,
                species,
                resource,
                range,
                regions,
                skins
        };
    }
    public String getRace() {
        if (species == null || species.isBlank()) return "";

        int parenIndex = species.indexOf('(');
        if (parenIndex == -1) {
            // No parentheses: the whole species is the race
            return species.trim().toLowerCase();
        }
        // Race is everything before '('
        return species.substring(0, parenIndex).trim().toLowerCase();
    }

    public String getTrait() {
        if (species == null || species.isBlank()) return "";

        int start = species.indexOf('(');
        int end = species.indexOf(')');

        if (start != -1 && end != -1 && end > start) {
            // Trait is the text inside the parentheses
            return species.substring(start + 1, end).trim().toLowerCase();
        }
        // No trait if no parentheses
        return "";
    }

    private String generateImageUrl(String championName) {

        if (championName.equalsIgnoreCase("Mel")) {
            // Replace with the actual path where you store the images locally
            return "file:///C:/Users/Benjamin%20Chock/Downloads/Loldle/Loldle/src/main/java/com/example/Loldle/util/52ef003ccb9a9464bbb87d72ded0e4ae11b4fe32-496x560.avif";
        }
        else if (championName.equalsIgnoreCase("Ambessa")){
            return "file:///C:/Users/Benjamin%20Chock/Downloads/Loldle/Loldle/src/main/java/com/example/Loldle/util/1b20e5e8cea542296a62b09dd4a67e81570ce80c-496x560.avif";
        }

        // Normalize curly quotes and apostrophes, remove special characters
        String formattedName = championName
                .replaceAll("[’'`]", "")     // Remove apostrophes
                .replaceAll("\\.", "")       // Remove periods (e.g., "Dr. Mundo")
                .replaceAll("&", "And");     // Replace "&" (e.g., "Nunu & Willump")

        // Split by whitespace and capitalize each part
        String[] parts = formattedName.split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                builder.append(Character.toUpperCase(part.charAt(0)));
                builder.append(part.substring(1));
            }
        }

        formattedName = builder.toString();

        return "https://ddragon.leagueoflegends.com/cdn/12.6.1/img/champion/" + formattedName + ".png";
    }



    public String getImageUrl() {
        return imageUrl;
    }


    public int checkPositionMatch(String position1, String position2) {
        // Split and normalize both position strings into sets
        Set<String> set1 = new HashSet<>();
        Set<String> set2 = new HashSet<>();

        for (String p : position1.split(",")) {
            set1.add(p.trim().toLowerCase());
        }
        for (String p : position2.split(",")) {
            set2.add(p.trim().toLowerCase());
        }

        // Full match: all positions exactly the same
        if (set1.equals(set2)) {
            return MATCH; // 0 = full green
        }

        // Partial match: at least one shared position
        for (String pos : set1) {
            if (set2.contains(pos)) {
                return PARTIAL; // 1 = yellow
            }
        }

        // No match
        return NO_MATCH; // 2 = red
    }



    public int compareSpecies(Champion other) {
        String myRace = this.getRace();
        String myTrait = this.getTrait();
        String otherRace = other.getRace();
        String otherTrait = other.getTrait();

        if (myRace.equals(otherRace)) {
            if (myTrait.isEmpty() || otherTrait.isEmpty() || myTrait.equals(otherTrait)) {
                return MATCH;  // Race matches, and traits either match or are missing
            }
            return PARTIAL;  // Race matches, traits different
        }

        if (!myTrait.isEmpty() && myTrait.equals(otherTrait)) {
            return PARTIAL;  // Different race, same trait
        }

        return NO_MATCH;
    }



    public String getChampion(){
        return champion;
    }

    public Set<String> getPositions() {
        return positions;
    }

    public String getGender() {
        return gender;
    }

    public String getPosition() {
        return position;
    }

    public String getRange() {
        return range;
    }

    public String getRegions() {
        return regions;
    }

    public String getResource() {
        return resource;
    }

    public String getSkins() {
        return skins;
    }

    public String getSpecies() {
        return species;
    }
}
