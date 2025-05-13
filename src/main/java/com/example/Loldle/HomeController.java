package com.example.Loldle;

import com.example.Loldle.model.Champion;
import com.example.Loldle.service.GameService;
import com.example.Loldle.service.GameService.GuessResult;
import com.example.Loldle.service.LodleBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
public class HomeController {

    private final GameService gameService;
    private final SessionData sessionData;
    private final LodleBot lodlebot;

    @Autowired
    public HomeController(GameService gameService, SessionData sessionData, LodleBot lodlebot) {
        this.gameService = gameService;
        this.sessionData = sessionData;
        this.lodlebot = lodlebot;
    }

    // Home page
    @GetMapping("/home")
    public String home(Model model) {
        sessionData.reset();           // Reset all past guesses
        gameService.resetGame();        // Pick a new random champion

        // Debugging log to check if the answer is set correctly
        if (gameService.getAnswer() == null) {
            System.out.println("Error: Answer is null after resetGame!");
        } else {
            System.out.println("Game reset, new champion selected: " + gameService.getAnswer().getChampion());
        }

        model.addAttribute("header", gameService.getHeader());
        return "home"; // Return the home page
    }

    @GetMapping("/loldlebot")
    public String loldleBotPage(Model model) {
        List<String> botGuesses = lodlebot.getChosenChamps();
        model.addAttribute("botGuesses", botGuesses);
        return "loldlebot";
    }


    // Guess page (GET)
    @GetMapping("/guess")
    public String guess(Model model) {
        // Ensure answer is not null before adding to model
        if (gameService.getAnswer() == null) {
            System.out.println("Error: Attempted to load guess page with null answer.");
            model.addAttribute("error", "The game is not initialized properly.");
        } else {
            model.addAttribute("gameState", gameService.getAnswer()); // Show the current game state
        }

        model.addAttribute("header", gameService.getHeader());
        model.addAttribute("guesses", sessionData.getAnswers()); // Show previous guesses
        return "guess"; // Return to guess page
    }

    // Handle a guess (POST)
    @PostMapping("/guess")
    public String handleGuess(@RequestParam String guess, Model model) {
        // Check for null answer before continuing
        if (gameService.getAnswer() == null) {
            model.addAttribute("error", "The game is not initialized properly.");
            return "guess"; // Stay on the guess page
        }

        GuessResult guessResult = gameService.checkGuess(guess);
        Champion guessedChampion = gameService.getChampionByName(guess);
        String imageUrl = guessedChampion != null ? guessedChampion.getImageUrl() : "/images/default.png";


        // If the champion is not found, display an error message
        if (guessResult == null) {
            model.addAttribute("error", "Champion not found!");
            model.addAttribute("header", gameService.getHeader());
            model.addAttribute("guesses", sessionData.getAnswers());
            return "guess"; // Stay on the guess page
        }

        // Add guess to session before checking for win or loss
        String[] colors = guessResult.getColors();
        String[] attributes = guessResult.getAttributes();

        sessionData.addAnswer(new String[] {
                guess,
                colors[1], colors[2], colors[3], colors[4], colors[5], colors[6], colors[7],
                attributes[0], attributes[1], attributes[2], attributes[3], attributes[4], attributes[5], attributes[6], attributes[7],
                imageUrl
        });


        // If the guess is correct, show the win page
        if (gameService.getAnswer().getChampion().equalsIgnoreCase(guess)) {
            model.addAttribute("champion", guess);
            model.addAttribute("guesses", sessionData.getAnswers());
            return "win"; // Redirect to the win page
        }

        // If it's the 7th guess, show the lose page
        if (sessionData.getAnswers().size() >= 7) {
            model.addAttribute("answer", gameService.getAnswer()); // Display the correct answer
            model.addAttribute("guesses", sessionData.getAnswers()); // Display all guesses
            return "lose"; // Redirect to the lose page
        }

        // Continue to the guess page if the game isn't over
        model.addAttribute("header", gameService.getHeader());
        model.addAttribute("guesses", sessionData.getAnswers());
        return "guess"; // Stay on the guess page
    }
    @GetMapping("/suggestions")
    @ResponseBody
    public List<String> getChampionSuggestions(@RequestParam String prefix) {
        // Fetch suggestions from the ChampionTrie in GameService
        return gameService.getChampionTrie().getRecommendations(prefix);
    }


}
