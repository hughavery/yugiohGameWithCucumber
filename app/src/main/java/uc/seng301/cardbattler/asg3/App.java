/*
 * Simple main class that starts a new game
 */
package uc.seng301.cardbattler.asg3;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uc.seng301.cardbattler.asg3.game.Game;

public class App {
    private static final Logger LOGGER = LogManager.getLogger(App.class);

    /**
     * Application entry point, runs the main game loop until player quits
     * 
     * @param args command line parameters
     */
    public static void main(String[] args) {
        LOGGER.info("Starting application...");
        new Game().play();
    }
}
