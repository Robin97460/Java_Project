package com.github.lxquaver.stardisfactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

/**
 * Centralise la persistance du jeu sur disque.
 *
 * Le systeme enregistre et recharge une instance de {@link GameSave}
 * au format JSON dans le dossier externe de l'application.
 */
public class SaveSystem {

    /**
     * Chemin relatif du fichier de sauvegarde.
     */
    private static final String SAVE_FILE = ".stardisfactory/savegame.json";

    /**
     * Retourne un handle LibGDX pointant vers le fichier de sauvegarde.
     */
    private static FileHandle getSaveFileHandle() {
        return Gdx.files.external(SAVE_FILE);
    }

    /**
     * Ecrit la sauvegarde complete sur disque.
     */
    public static void save(GameSave data) {
        Json json = new Json();
        json.setUsePrototypes(false);

        FileHandle fh = getSaveFileHandle();
        fh.parent().mkdirs();
        fh.writeString(json.prettyPrint(data), false);
    }

    /**
     * Charge la sauvegarde depuis le disque.
     *
     * @return les donnees de sauvegarde, ou null si le fichier n'existe pas.
     */
    public static GameSave load() {
        FileHandle fh = getSaveFileHandle();
        if (!fh.exists()) return null;

        Json json = new Json();
        json.setUsePrototypes(false);

        return json.fromJson(GameSave.class, fh);
    }

    /**
     * Indique si une sauvegarde existe deja.
     */
    public static boolean exists() {
        return getSaveFileHandle().exists();
    }

    /**
     * Supprime la sauvegarde existante si presente.
     */
    public static void delete() {
        FileHandle fh = getSaveFileHandle();
        if (fh.exists()) {
            fh.delete();
        }
    }
}
