package com.github.lxquaver.stardisfactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Structure de donnees serialisable representant l'etat complet d'une partie.
 *
 * Cette classe est volontairement composee de champs publics pour simplifier
 * la serialisation JSON via LibGDX.
 */
public class GameSave {
    /**
     * Taille de la carte carree (nombre de cases par cote).
     */
    public int mapSize;

    /**
     * Decalage appliquant l'origine du monde au centre de la grille.
     */
    public int mapOffset;

    /**
     * Ancien format de terrain: tableau aplati d'ordinaux de Terrain.Type.
     */
    public int[] terrainTypes;

    /**
     * Nouveau format de terrain: tableau aplati de noms de Terrain.Type.
     */
    public String[] terrainTypeNames;

    /**
     * Position du joueur au moment de la sauvegarde.
     */
    public float playerX;
    public float playerY;

    /**
     * Ressources et inventaires du joueur.
     */
    public int money;
    public int buildingStockPlanter;
    public int buildingStockConveyor;
    public int seedBagsPotato;
    public int seedBagsStrawberry;
    public int seedBagsLeek;
    public int carriedPotato;
    public int carriedStrawberry;
    public int carriedLeek;

    /**
     * Liste des batiments poses avec leur etat interne.
     */
    public List<BuildingSave> buildings = new ArrayList<>();

    /**
     * Snapshot serialisable d'un batiment du monde.
     */
    public static class BuildingSave {
        /**
         * Type du batiment (ex: MAIN_HQ, PLANTER...).
         */
        public String type;

        /**
         * Position grille du coin d'ancrage du batiment.
         */
        public int x;
        public int y;

        /**
         * Rotation normalisee sur 4 directions.
         */
        public int rotation;

        /**
         * Etat de croissance d'une jardiniere.
         */
        public String planterCrop;
        public float growTimerSeconds;
        public boolean planterReady;

        /**
         * Etat de transport d'un convoyeur.
         */
        public String heldItem;
        public int heldAmount;
        public float transportTimer;

        /**
         * Stock interne du HQ.
         */
        public int hqPotatoes;
        public int hqStrawberries;
        public int hqLeeks;
    }
}
