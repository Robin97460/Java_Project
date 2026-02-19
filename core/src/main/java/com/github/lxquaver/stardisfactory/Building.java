package com.github.lxquaver.stardisfactory;

/**
 * Modele unique pour tous les batiments poses sur la carte.
 *
 * Cette classe porte la position/rotation commune, puis des etats
 * specifiques selon le type (jardiniere, convoyeur, HQ, hotel des ventes).
 */
public class Building {

    /**
     * Types de batiments constructibles avec leur empreinte en grille.
     */
    public enum Type {
        MAIN_HQ(4, 4, BuildingCategory.LOGISTICS),
        AUCTION_HOUSE(4, 4, BuildingCategory.LOGISTICS),
        CONVEYOR_BELT(1, 1, BuildingCategory.TRANSPORT),
        PLANTER(1, 1, BuildingCategory.FARMING);

        public final int width;
        public final int height;
        public final BuildingCategory category;

        Type(int width, int height, BuildingCategory category) {
            this.width = width;
            this.height = height;
            this.category = category;
        }
    }

    public enum BuildingCategory {
        LOGISTICS,
        TRANSPORT,
        FARMING
    }

    /**
     * Types de cultures manipulables par les jardinieres et convoyeurs.
     */
    public enum PlanterCrop {
        NONE,
        POTATO,
        STRAWBERRY,
        LEEK
    }

    public static class ConveyedItem {
        public final PlanterCrop crop;
        public final int amount;

        public ConveyedItem(PlanterCrop crop, int amount) {
            this.crop = crop == null ? PlanterCrop.NONE : crop;
            this.amount = Math.max(0, amount);
        }
    }

    private final Type type;
    private final int gridX;
    private final int gridY;
    private int rotation;

    private PlanterCrop planterCrop = PlanterCrop.NONE;
    private float growTimerSeconds = 0f;
    private boolean ready = false;

    private PlanterCrop heldItem = PlanterCrop.NONE;
    private int heldAmount = 0;
    private float transportTimer = 0f;
    private int entryDirection = -1;
    private static final float TRANSPORT_TIME = 1.0f;

    private int hqPotatoes = 0;
    private int hqStrawberries = 0;
    private int hqLeeks = 0;

    public Building(Type type, int gridX, int gridY) {
        this(type, gridX, gridY, 0);
    }

    public Building(Type type, int gridX, int gridY, int rotation) {
        this.type = type;
        this.gridX = gridX;
        this.gridY = gridY;
        this.rotation = rotation % 4;
        if (this.rotation < 0) this.rotation += 4;
    }

    /**
     * Explique la methode getType.
     */
    public Type getType() { return type; }
    /**
     * Explique la methode getGridX.
     */
    public int getGridX() { return gridX; }
    /**
     * Explique la methode getGridY.
     */
    public int getGridY() { return gridY; }

    /**
     * Explique la methode rotate.
     */
    public void rotate() { rotation = (rotation + 1) % 4; }
    /**
     * Explique la methode getRotation.
     */
    public int getRotation() { return rotation; }
    /**
     * Explique la methode setRotation.
     */
    public void setRotation(int rotation) {
        this.rotation = rotation % 4;
        if (this.rotation < 0) this.rotation += 4;
    }

    /**
     * Explique la methode isPlanter.
     */
    public boolean isPlanter() {
        return type == Type.PLANTER;
    }

    /**
     * Explique la methode getPlanterCrop.
     */
    public PlanterCrop getPlanterCrop() {
        return planterCrop;
    }

    /**
     * Explique la methode isPlanterEmpty.
     */
    public boolean isPlanterEmpty() {
        return planterCrop == PlanterCrop.NONE;
    }

    /**
     * Explique la methode isPlanterReady.
     */
    public boolean isPlanterReady() {
        return ready;
    }

    /**
     * Explique la methode getPlanterGrowProgress01.
     */
    public float getPlanterGrowProgress01() {
        float total = getGrowTimeSecondsFor(planterCrop);
        if (total <= 0f) return 0f;
        return Math.min(1f, growTimerSeconds / total);
    }

    /**
     * Explique la methode plant.
     */
    public void plant(PlanterCrop crop) {
        if (!isPlanter()) return;
        if (crop == null || crop == PlanterCrop.NONE) return;

        planterCrop = crop;
        growTimerSeconds = 0f;
        ready = false;
    }

    /**
     * Explique la methode update.
     */
    public void update(float dt) {
        if (isPlanter()) {
            updatePlanter(dt);
        } else if (isConveyor()) {
            updateConveyor(dt);
        }
    }

    /**
     * Explique la methode updatePlanter.
     */
    private void updatePlanter(float dt) {
        if (planterCrop == PlanterCrop.NONE) return;
        if (ready) return;

        growTimerSeconds += dt;

        float total = getGrowTimeSecondsFor(planterCrop);
        if (growTimerSeconds >= total) {
            growTimerSeconds = total;
            ready = true;
        }
    }

    /**
     * Explique la methode harvest.
     */
    public PlanterCrop harvest() {
        if (!isPlanter()) return PlanterCrop.NONE;
        if (planterCrop == PlanterCrop.NONE) return PlanterCrop.NONE;
        if (!ready) return PlanterCrop.NONE;

        PlanterCrop harvested = planterCrop;
        planterCrop = PlanterCrop.NONE;
        growTimerSeconds = 0f;
        ready = false;

        return harvested;
    }

    /**
     * Explique la methode getGrowTimeSecondsFor.
     */
    public static float getGrowTimeSecondsFor(PlanterCrop crop) {
        if (crop == null) return 0f;
        switch (crop) {
            case POTATO: return 30f;
            case STRAWBERRY: return 60f;
            case LEEK: return 300f;
            default: return 0f;
        }
    }

    /**
     * Explique la methode getYieldFor.
     */
    public static int getYieldFor(PlanterCrop crop) {
        if (crop == null) return 0;
        switch (crop) {
            case POTATO: return 10;
            case STRAWBERRY: return 7;
            case LEEK: return 5;
            default: return 0;
        }
    }

    /**
     * Explique la methode getSellPriceFor.
     */
    public static int getSellPriceFor(PlanterCrop crop) {
        if (crop == null) return 0;
        switch (crop) {
            case POTATO: return 3;
            case STRAWBERRY: return 8;
            case LEEK: return 100;
            default: return 0;
        }
    }

    /**
     * Explique la methode isConveyor.
     */
    public boolean isConveyor() {
        return type == Type.CONVEYOR_BELT;
    }

    /**
     * Explique la methode hasItem.
     */
    public boolean hasItem() {
        return heldItem != PlanterCrop.NONE;
    }

    /**
     * Explique la methode getHeldItem.
     */
    public PlanterCrop getHeldItem() {
        return heldItem;
    }

    /**
     * Explique la methode getHeldAmount.
     */
    public int getHeldAmount() {
        return heldAmount;
    }

    /**
     * Explique la methode getTransportProgress.
     */
    public float getTransportProgress() {
        if (!hasItem()) return 0f;
        return Math.min(1f, transportTimer / TRANSPORT_TIME);
    }

    /**
     * Explique la methode canReceiveItem.
     */
    public boolean canReceiveItem() {
        return isConveyor() && !hasItem();
    }

    /**
     * Explique la methode receiveItem.
     */
    public void receiveItem(PlanterCrop item) {
        receiveItem(item, 1);
    }

    /**
     * Explique la methode receiveItem.
     */
    public void receiveItem(PlanterCrop item, int amount) {
        receiveItem(item, amount, getOppositeDirection(rotation));
    }

    /**
     * Explique la methode receiveItem.
     */
    public void receiveItem(PlanterCrop item, int amount, int entryDirection) {
        if (!canReceiveItem()) return;
        if (item == null || item == PlanterCrop.NONE) return;
        if (amount <= 0) return;
        heldItem = item;
        heldAmount = amount;
        transportTimer = 0f;
        this.entryDirection = normalizeDirection(entryDirection);
    }

    /**
     * Explique la methode takeItem.
     */
    public PlanterCrop takeItem() {
        if (!hasItem()) return PlanterCrop.NONE;
        PlanterCrop item = heldItem;
        heldItem = PlanterCrop.NONE;
        heldAmount = 0;
        transportTimer = 0f;
        entryDirection = -1;
        return item;
    }

    /**
     * Explique la methode takeConveyedItem.
     */
    public ConveyedItem takeConveyedItem() {
        if (!hasItem()) return new ConveyedItem(PlanterCrop.NONE, 0);
        ConveyedItem item = new ConveyedItem(heldItem, heldAmount);
        heldItem = PlanterCrop.NONE;
        heldAmount = 0;
        transportTimer = 0f;
        entryDirection = -1;
        return item;
    }

    /**
     * Explique la methode updateConveyor.
     */
    private void updateConveyor(float dt) {
        if (hasItem()) {
            transportTimer += dt;
            if (transportTimer > TRANSPORT_TIME) {
                transportTimer = TRANSPORT_TIME;
            }
        }
    }

    /**
     * Explique la methode isHQ.
     */
    public boolean isHQ() {
        return type == Type.MAIN_HQ;
    }

    /**
     * Explique la methode isAuctionHouse.
     */
    public boolean isAuctionHouse() {
        return type == Type.AUCTION_HOUSE;
    }

    /**
     * Explique la methode addToHQStock.
     */
    public void addToHQStock(PlanterCrop crop, int amount) {
        if (!isHQ()) return;
        if (amount <= 0) return;
        if (crop == PlanterCrop.POTATO) hqPotatoes += amount;
        if (crop == PlanterCrop.STRAWBERRY) hqStrawberries += amount;
        if (crop == PlanterCrop.LEEK) hqLeeks += amount;
    }

    /**
     * Explique la methode getHQStock.
     */
    public int getHQStock(PlanterCrop crop) {
        if (!isHQ()) return 0;
        if (crop == PlanterCrop.POTATO) return hqPotatoes;
        if (crop == PlanterCrop.STRAWBERRY) return hqStrawberries;
        if (crop == PlanterCrop.LEEK) return hqLeeks;
        return 0;
    }

    /**
     * Explique la methode removeFromHQStock.
     */
    public int removeFromHQStock(PlanterCrop crop, int amount) {
        if (!isHQ()) return 0;
        if (amount <= 0) return 0;

        if (crop == PlanterCrop.POTATO) {
            int sold = Math.min(amount, hqPotatoes);
            hqPotatoes -= sold;
            return sold;
        }
        if (crop == PlanterCrop.STRAWBERRY) {
            int sold = Math.min(amount, hqStrawberries);
            hqStrawberries -= sold;
            return sold;
        }
        if (crop == PlanterCrop.LEEK) {
            int sold = Math.min(amount, hqLeeks);
            hqLeeks -= sold;
            return sold;
        }
        return 0;
    }

    /**
     * Explique la methode clearHQStock.
     */
    public void clearHQStock() {
        hqPotatoes = 0;
        hqStrawberries = 0;
        hqLeeks = 0;
    }

    /**
     * Explique la methode getGrowTimerSeconds.
     */
    public float getGrowTimerSeconds() {
        return growTimerSeconds;
    }

    /**
     * Explique la methode setPlanterState.
     */
    public void setPlanterState(PlanterCrop crop, float growTimerSeconds, boolean ready) {
        if (!isPlanter()) return;
        this.planterCrop = crop == null ? PlanterCrop.NONE : crop;
        this.growTimerSeconds = Math.max(0f, growTimerSeconds);
        this.ready = ready;
    }

    /**
     * Explique la methode getTransportTimerSeconds.
     */
    public float getTransportTimerSeconds() {
        return transportTimer;
    }

    /**
     * Explique la methode setConveyorState.
     */
    public void setConveyorState(PlanterCrop item, int amount, float transportTimer) {
        if (!isConveyor()) return;
        this.heldItem = item == null ? PlanterCrop.NONE : item;
        this.heldAmount = this.heldItem == PlanterCrop.NONE ? 0 : Math.max(0, amount);
        this.transportTimer = Math.max(0f, Math.min(TRANSPORT_TIME, transportTimer));
        this.entryDirection = this.heldItem == PlanterCrop.NONE ? -1 : getOppositeDirection(rotation);
    }

    /**
     * Explique la methode getEntryDirection.
     */
    public int getEntryDirection() {
        if (!hasItem()) return -1;
        return entryDirection;
    }

    /**
     * Explique la methode normalizeDirection.
     */
    private static int normalizeDirection(int direction) {
        int normalized = direction % 4;
        if (normalized < 0) normalized += 4;
        return normalized;
    }

    /**
     * Explique la methode getOppositeDirection.
     */
    private static int getOppositeDirection(int direction) {
        return normalizeDirection(direction + 2);
    }

    /**
     * Explique la methode setHQStock.
     */
    public void setHQStock(PlanterCrop crop, int amount) {
        if (!isHQ()) return;
        int safeAmount = Math.max(0, amount);
        if (crop == PlanterCrop.POTATO) hqPotatoes = safeAmount;
        if (crop == PlanterCrop.STRAWBERRY) hqStrawberries = safeAmount;
        if (crop == PlanterCrop.LEEK) hqLeeks = safeAmount;
    }
}
