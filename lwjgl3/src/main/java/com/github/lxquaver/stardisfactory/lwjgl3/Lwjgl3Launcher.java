package com.github.lxquaver.stardisfactory.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.github.lxquaver.stardisfactory.GameMain;

/**
 * Point d'entree desktop (LWJGL3).
 *
 * Cette classe prepare l'environnement natif puis lance l'application LibGDX.
 */
public class Lwjgl3Launcher {

    /**
     * Demarre l'application desktop.
     */
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return;
        createApplication();
    }

    /**
     * Construit l'application LibGDX avec la configuration par defaut.
     */
    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new GameMain(), getDefaultConfiguration());
    }

    /**
     * Definit les parametres de fenetre et de rafraichissement.
     */
    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Apprentissage LibGDX - Grille 2D");

        configuration.useVsync(true);
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate);

        configuration.setWindowedMode(1280, 720);

        return configuration;
    }
}
