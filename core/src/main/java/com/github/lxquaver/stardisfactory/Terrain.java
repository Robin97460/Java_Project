package com.github.lxquaver.stardisfactory;

/**
 * Modele une case de terrain de la grille.
 *
 * La case porte un type de sol et expose des actions de base
 * (labourer/nettoyer) ainsi que des verifications de compatibilite.
 */
public class Terrain {

    /**
     * Types de sol disponibles dans le jeu.
     */
    public enum Type {
        GRASS(true, false),
        TILLED(false, true);

        /**
         * Indique si les batiments animaux sont autorises sur ce sol.
         */
        public final boolean supportsAnimals;

        /**
         * Indique si les batiments de culture sont autorises sur ce sol.
         */
        public final boolean supportsCrops;

        Type(boolean supportsAnimals, boolean supportsCrops) {
            this.supportsAnimals = supportsAnimals;
            this.supportsCrops = supportsCrops;
        }
    }

    /**
     * Type courant de la case.
     */
    private Type currentType;

    /**
     * Cree une case avec un type initial.
     */
    public Terrain(Type startType) {
        this.currentType = startType;
    }

    /**
     * Retourne le type de sol actuel.
     */
    public Type getType() {
        return currentType;
    }

    /**
     * Convertit l'herbe en terre labouree.
     */
    public void till() {
        if (currentType == Type.GRASS) {
            currentType = Type.TILLED;
        }
    }

    /**
     * Restaure une terre labouree en herbe.
     */
    public void clear() {
        if (currentType == Type.TILLED) {
            currentType = Type.GRASS;
        }
    }

    /**
     * Verifie si un batiment animalier peut etre place sur cette case.
     */
    public boolean canPlaceAnimalBuilding() {
        return currentType.supportsAnimals;
    }

    /**
     * Verifie si un batiment de culture peut etre place sur cette case.
     */
    public boolean canPlantCrop() {
        return currentType.supportsCrops;
    }
}
