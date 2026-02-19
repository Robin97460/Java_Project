package com.github.lxquaver.stardisfactory;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Classe principale du jeu.
 *
 * Elle coordonne la boucle de rendu LibGDX, la logique monde, les interfaces
 * (menu/HUD/popups), les interactions joueur, ainsi que la sauvegarde.
 */

public class GameMain extends ApplicationAdapter {


    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private FitViewport viewport;


    private Stage uiStage;
    private Skin skin;
    private ButtonGroup<TextButton> toolGroup;
    private ButtonGroup<TextButton> buildingGroup;


    private Table mainMenuTable;
    private Window pauseWindow;
    private Table optionsMenuTable;
    private Table controlsMenuTable;
    private TextButton continueBtn;
    private Label statsLabel;
    private Label selectionLabel;
    private TextButton buildPlanterBtn;
    private TextButton buildConveyorBtn;
    private Label hudMoneyLabel;
    private Label hudSeedPotatoLabel;
    private Label hudSeedStrawberryLabel;
    private Label hudSeedLeekLabel;
    private Label hudCarryPotatoLabel;
    private Label hudCarryStrawberryLabel;
    private Label hudCarryLeekLabel;



    private Window planterWindow;
    private Building currentPlanterForUI = null;


    private Window hqWindow;
    private Building currentHQForUI = null;


    private Window auctionWindow;
    private Building currentAuctionForUI = null;


    private Music backgroundMusic;
    private Sound buyItemSound;
    private Sound sellItemSound;
    private Sound plantationInSound;
    private Sound plantationOutSound;
    private Sound planterPlacementSound;
    private Sound conveyorPlacementSound;
    private Sound tillSound;
    private Sound clearSound;
    private static final float SFX_VOLUME = 0.5f;


    private PlayerAnimationController playerAnimationController;
    private Vector2 playerPos;
    private final Vector2 conveyorItemTmp = new Vector2();
    private final float MOVE_SPEED = 5f;
    private static final float PLAYER_SIZE = 1f;



    private Texture tileGrass;
    private Texture tileTilled;
    private Texture hqTexture;
    private Texture auctionTexture;
    private Texture potatoTexture;
    private Texture strawberryTexture;
    private Texture leekTexture;
    private Texture potatoBagTexture;
    private Texture strawberryBagTexture;
    private Texture leekBagTexture;
    private Texture planterTexture;
    private Texture planterPotatoTexture;
    private Texture planterStrawberryTexture;
    private Texture planterLeekTexture;
    private Texture conveyorTopTexture;
    private Texture conveyorRightTexture;
    private Texture conveyorBottomTexture;
    private Texture conveyorLeftTexture;
    private Texture gameLogoTexture;
    private Texture backgroundTexture;
    private final float HQ_SPRITE_SCALE = 1.4f;
    private static final float PLANTER_VISUAL_SCALE = 1.0f;
    private static final float READY_FILTER_SCALE = 0.7f;




    private static final int MAP_SIZE = 100;
    private static final int MAP_OFFSET = 50;

    private Terrain[][] terrainGrid;
    private List<Building> buildings;


    private String selectedTool = "NONE";
    private Building.Type selectedBuildingType = null;
    private int currentRotation = 0;


    private enum GameState { MENU, PLAYING, PAUSED }
    private GameState currentState = GameState.MENU;
    private enum MenuPage { MAIN, OPTIONS, CONTROLS }
    private MenuPage currentMenuPage = MenuPage.MAIN;


    private String playerName = "Joueur";
    private float musicVolume = 0.1f;
    private int money = 0;
    private int buildingStockPlanter = 0;
    private int buildingStockConveyor = 0;
    private int seedBagsPotato = 0;
    private int seedBagsStrawberry = 0;
    private int seedBagsLeek = 0;
    private int carriedPotato = 0;
    private int carriedStrawberry = 0;
    private int carriedLeek = 0;

    private static final int AUCTION_TAB_SEEDS = 0;
    private static final int AUCTION_TAB_BUILDINGS = 1;
    private static final float AUCTION_TAB_WIDTH = 200f;
    private static final float AUCTION_TAB_HEIGHT = 46f;
    private static final float AUCTION_CARD_WIDTH = 230f;
    private static final float AUCTION_CARD_HEIGHT = 175f;
    private int auctionActiveTab = AUCTION_TAB_SEEDS;

    @Override
    /**
     * Initialise la partie technique du jeu: rendu, assets, audio, grille, UI et menu principal.
     */
    public void create() {

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        viewport = new FitViewport(32, 18, camera);


        uiStage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(uiStage);

        gameLogoTexture = new Texture("LOGO.png");
        gameLogoTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

        backgroundTexture = new Texture("background.png");
        backgroundTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);


        tileGrass = new Texture("tile_grass.png");
        tileTilled = new Texture("tile_tilled.png");
        hqTexture = new Texture("hq.png");
        auctionTexture = new Texture("hq2.png");
        potatoTexture = new Texture("CROPS/Patate.png");
        strawberryTexture = new Texture("CROPS/Fraise.png");
        leekTexture = new Texture("CROPS/Poireaux.png");
        potatoBagTexture = new Texture("CROPS/Patate_bag.png");
        strawberryBagTexture = new Texture("CROPS/Fraise_Bag.png");
        leekBagTexture = new Texture("CROPS/Poireaux_bag.png");
        planterTexture = new Texture("Jardinière.png");
        planterPotatoTexture = new Texture("Jardinière_patate.png");
        planterStrawberryTexture = new Texture("Jardinière_Fraise.png");
        planterLeekTexture = new Texture("Jardinière_Poireau.png");
        conveyorTopTexture = new Texture("Convoyeur_top.png");
        conveyorRightTexture = new Texture("Convoyeur_right.png");
        conveyorBottomTexture = new Texture("Convoyeur_bottom.png");
        conveyorLeftTexture = new Texture("Convoyeur_left.png");



        tileGrass.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        tileTilled.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        hqTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        auctionTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        potatoTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        strawberryTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        leekTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        potatoBagTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        strawberryBagTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        leekBagTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        planterTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        planterPotatoTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        planterStrawberryTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        planterLeekTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        conveyorTopTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        conveyorRightTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        conveyorBottomTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        conveyorLeftTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);



        try {
            if (Gdx.files.internal("music.mp3").exists()) {
                backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
                backgroundMusic.setLooping(true);
                backgroundMusic.setVolume(musicVolume);
            } else {
                System.out.println("Fichier music.mp3 introuvable dans assets/");
            }
        } catch (Exception e) {
            System.out.println("Erreur chargement musique: " + e.getMessage());
        }

        buyItemSound = loadSound("Sound_effect/Buy_item.mp3");
        sellItemSound = loadSound("Sound_effect/Sell_item.mp3");
        plantationInSound = loadSound("Sound_effect/Plantation_in.mp3");
        plantationOutSound = loadSound("Sound_effect/Plantation_out.mp3");
        planterPlacementSound = loadSound("Sound_effect/Jardinière_placement.mp3");
        conveyorPlacementSound = loadSound("Sound_effect/Convoyeur_placement.mp3");
        tillSound = loadSound("Sound_effect/Labourer.ogg");
        clearSound = loadSound("Sound_effect/Clear.ogg");


        playerAnimationController = new PlayerAnimationController();
        playerPos = new Vector2(0, 0);


        terrainGrid = new Terrain[MAP_SIZE][MAP_SIZE];
        buildings = new ArrayList<>();

        for (int x = 0; x < MAP_SIZE; x++) {
            for (int y = 0; y < MAP_SIZE; y++) {
                terrainGrid[x][y] = new Terrain(Terrain.Type.GRASS);
            }
        }

        createUI();


        showMainMenu();
    }
    /**
     * Construit le skin UI (styles) puis cree les ecrans HUD, menu principal et pause.
     */
    private void createUI() {
        skin = new Skin();


        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        skin.add("default", new BitmapFont());
        pixmap.dispose();




        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.GRAY);
        textButtonStyle.checked = skin.newDrawable("white", Color.ROYAL);
        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
        textButtonStyle.font = skin.getFont("default");
        skin.add("default", textButtonStyle);


        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default");
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);


        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = skin.getFont("default");
        textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.cursor = skin.newDrawable("white", Color.WHITE);
        textFieldStyle.selection = skin.newDrawable("white", Color.BLUE);
        textFieldStyle.background = skin.newDrawable("white", Color.DARK_GRAY);
        skin.add("default", textFieldStyle);


        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = skin.newDrawable("white", Color.DARK_GRAY);
        sliderStyle.knob = skin.newDrawable("white", Color.ROYAL);
        skin.add("default-horizontal", sliderStyle);


        CheckBox.CheckBoxStyle checkBoxStyle = new CheckBox.CheckBoxStyle();
        checkBoxStyle.checkboxOn = skin.newDrawable("white", Color.GREEN);
        checkBoxStyle.checkboxOff = skin.newDrawable("white", Color.RED);
        checkBoxStyle.font = skin.getFont("default");
        skin.add("default", checkBoxStyle);


        Window.WindowStyle windowStyle = new Window.WindowStyle(
                skin.getFont("default"),
                Color.WHITE,
                skin.newDrawable("white", new Color(0.1f, 0.1f, 0.1f, 0.9f))
        );
        skin.add("default", windowStyle);


        createGameHUD();
        createMainMenuUI();
        createPauseMenuUI();
    }


    /**
     * Construit le HUD de jeu (build, inventaire, outils) et relie les boutons a leurs actions.
     */
    private void createGameHUD() {
        Color panelBg = new Color(0.06f, 0.06f, 0.06f, 0.88f);
        Color sectionBg = new Color(0.15f, 0.15f, 0.15f, 0.95f);


        Table buildContainer = new Table();
        buildContainer.setFillParent(true);
        buildContainer.left().top().pad(12);
        buildContainer.setName("HUD_BUILD_PANEL");

        Table buildPanel = new Table();
        buildPanel.pad(10);
        buildPanel.defaults().left().padBottom(6);
        buildPanel.setBackground(skin.newDrawable("white", panelBg));

        Table planterRow = new Table();
        planterRow.setBackground(skin.newDrawable("white", sectionBg));
        planterRow.pad(4);
        planterRow.add(new Image(planterTexture)).size(24).padRight(6);
        buildPlanterBtn = new TextButton("Jardiniere x0", skin);
        buildPlanterBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                selectBuilding(Building.Type.PLANTER);
            }
        });
        planterRow.add(buildPlanterBtn).width(180).left();
        buildPanel.add(planterRow).width(220).row();

        Table conveyorRow = new Table();
        conveyorRow.setBackground(skin.newDrawable("white", sectionBg));
        conveyorRow.pad(4);
        conveyorRow.add(new Image(conveyorRightTexture)).size(24).padRight(6);
        buildConveyorBtn = new TextButton("Convoyeur x0", skin);
        buildConveyorBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                selectBuilding(Building.Type.CONVEYOR_BELT);
            }
        });
        conveyorRow.add(buildConveyorBtn).width(180).left();
        buildPanel.add(conveyorRow).width(220).row();

        buildingGroup = new ButtonGroup<>(buildPlanterBtn, buildConveyorBtn);
        buildingGroup.setMaxCheckCount(1);
        buildingGroup.setMinCheckCount(0);
        buildingGroup.setUncheckLast(true);

        selectionLabel = new Label("", skin);
        buildPanel.add(selectionLabel).left().padTop(2).row();

        buildContainer.add(buildPanel);
        uiStage.addActor(buildContainer);


        Table inventoryContainer = new Table();
        inventoryContainer.setFillParent(true);
        inventoryContainer.right().top().pad(12);
        inventoryContainer.setName("HUD_INVENTORY_PANEL");

        Table inventoryPanel = new Table();
        inventoryPanel.pad(10);
        inventoryPanel.defaults().left().padBottom(6);
        inventoryPanel.setBackground(skin.newDrawable("white", panelBg));

        hudMoneyLabel = new Label("Argent: 0$", skin);
        inventoryPanel.add(hudMoneyLabel).left().row();

        Table seedPotatoRow = new Table();
        seedPotatoRow.add(new Image(potatoBagTexture)).size(20).padRight(6);
        hudSeedPotatoLabel = new Label("x 0", skin);
        seedPotatoRow.add(hudSeedPotatoLabel).left();
        inventoryPanel.add(seedPotatoRow).left().row();

        Table seedStrawberryRow = new Table();
        seedStrawberryRow.add(new Image(strawberryBagTexture)).size(20).padRight(6);
        hudSeedStrawberryLabel = new Label("x 0", skin);
        seedStrawberryRow.add(hudSeedStrawberryLabel).left();
        inventoryPanel.add(seedStrawberryRow).left().row();

        Table seedLeekRow = new Table();
        seedLeekRow.add(new Image(leekBagTexture)).size(20).padRight(6);
        hudSeedLeekLabel = new Label("x 0", skin);
        seedLeekRow.add(hudSeedLeekLabel).left();
        inventoryPanel.add(seedLeekRow).left().padBottom(10).row();

        Table carryPotatoRow = new Table();
        carryPotatoRow.add(new Image(potatoTexture)).size(20).padRight(6);
        hudCarryPotatoLabel = new Label("Patate: 0", skin);
        carryPotatoRow.add(hudCarryPotatoLabel).left();
        inventoryPanel.add(carryPotatoRow).left().row();

        Table carryStrawberryRow = new Table();
        carryStrawberryRow.add(new Image(strawberryTexture)).size(20).padRight(6);
        hudCarryStrawberryLabel = new Label("Fraise: 0", skin);
        carryStrawberryRow.add(hudCarryStrawberryLabel).left();
        inventoryPanel.add(carryStrawberryRow).left().row();

        Table carryLeekRow = new Table();
        carryLeekRow.add(new Image(leekTexture)).size(20).padRight(6);
        hudCarryLeekLabel = new Label("Poireau: 0", skin);
        carryLeekRow.add(hudCarryLeekLabel).left();
        inventoryPanel.add(carryLeekRow).left().row();

        inventoryContainer.add(inventoryPanel).width(210);
        uiStage.addActor(inventoryContainer);


        Table toolContainer = new Table();
        toolContainer.setFillParent(true);
        toolContainer.left().bottom().pad(12);
        toolContainer.setName("HUD_TOOLS_PANEL");

        Table toolPanel = new Table();
        toolPanel.pad(8);
        toolPanel.defaults().left().padBottom(6);
        toolPanel.setBackground(skin.newDrawable("white", panelBg));

        TextButton btnTill = new TextButton("Labourer", skin);
        TextButton btnClear = new TextButton("Nettoyer", skin);
        TextButton btnBulldoze = new TextButton("Bulldozer", skin);

        btnTill.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                selectTool("TILL");
            }
        });
        btnClear.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                selectTool("CLEAR");
            }
        });
        btnBulldoze.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                selectTool("BULLDOZE");
            }
        });

        toolGroup = new ButtonGroup<>(btnTill, btnClear, btnBulldoze);
        toolGroup.setMaxCheckCount(1);
        toolGroup.setMinCheckCount(0);
        toolGroup.setUncheckLast(true);

        toolPanel.add(btnTill).width(160).row();
        toolPanel.add(btnClear).width(160).row();
        toolPanel.add(btnBulldoze).width(160).row();

        toolContainer.add(toolPanel);
        uiStage.addActor(toolContainer);

        statsLabel = hudMoneyLabel;
        updateStatsLabel();
    }


    /**
     * Active un outil terrain, desactive la selection de batiment et ferme les popups ouverts.
     */
    private void selectTool(String toolName) {
        selectedTool = toolName;
        selectedBuildingType = null;
        updateSelectionLabel();
        closePlanterPopup();
        closeHQPopup();
        closeAuctionPopup();
        if (buildingGroup != null) buildingGroup.uncheckAll();
    }

    /**
     * Active un type de batiment a placer, desactive l'outil courant et ferme les popups.
     */
    private void selectBuilding(Building.Type type) {
        selectedBuildingType = type;
        selectedTool = "NONE";
        updateSelectionLabel();
        closePlanterPopup();
        closeHQPopup();
        closeAuctionPopup();
        if (toolGroup != null) toolGroup.uncheckAll();
    }

    /**
     * Met a jour le texte de selection HUD selon l'outil ou le batiment actuellement choisi.
     */
    private void updateSelectionLabel() {
        if (selectionLabel == null) return;

        if (selectedBuildingType != null) {
            selectionLabel.setText("Selection: " + selectedBuildingType.name());
        } else if (!selectedTool.equals("NONE")) {
            selectionLabel.setText("Outil: " + selectedTool);
        } else {
            selectionLabel.setText("");
        }
    }


    /**
     * Cree les pages du menu principal (main/options/controles) et branche chaque bouton.
     */
    private void createMainMenuUI() {
        mainMenuTable = new Table();
        mainMenuTable.setFillParent(true);
        mainMenuTable.center();

        Image logoImage = new Image(gameLogoTexture);
        float logoMaxSize = 400f;
        float logoW = gameLogoTexture.getWidth();
        float logoH = gameLogoTexture.getHeight();
        float logoScale = Math.min(logoMaxSize / logoW, logoMaxSize / logoH);
        float logoDisplayW = logoW * logoScale;
        float logoDisplayH = logoH * logoScale;

        continueBtn = new TextButton("CONTINUER", skin);
        continueBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                continueGame();
            }
        });

        TextButton newGameBtn = new TextButton("NOUVELLE PARTIE", skin);
        newGameBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                SaveSystem.delete();
                startNewGame();
            }
        });

        TextButton optionsBtn = new TextButton("OPTIONS", skin);
        optionsBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                showOptionsPage();
            }
        });

        TextButton controlsBtn = new TextButton("CONTROLES", skin);
        controlsBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                showControlsPage();
            }
        });

        TextButton quitBtn = new TextButton("QUITTER", skin);
        quitBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });


        mainMenuTable.defaults().pad(8);
        mainMenuTable.add(logoImage).width(logoDisplayW).height(logoDisplayH).padBottom(12).row();
        mainMenuTable.add(continueBtn).width(260).height(48).padTop(10).row();
        mainMenuTable.add(newGameBtn).width(260).height(48).row();
        mainMenuTable.add(optionsBtn).width(260).height(48).row();
        mainMenuTable.add(controlsBtn).width(260).height(48).row();
        mainMenuTable.add(quitBtn).width(260).height(48).padTop(4).row();

        optionsMenuTable = new Table();
        optionsMenuTable.setFillParent(true);
        optionsMenuTable.center();

        Label optionsTitleLabel = new Label("OPTIONS", skin);
        optionsTitleLabel.setFontScale(1.6f);

        Label displayLabel = new Label("Affichage", skin);
        final TextButton displayModeBtn = new TextButton(getDisplayModeLabel(), skin);
        displayModeBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                toggleFullscreen();
                displayModeBtn.setText(getDisplayModeLabel());
            }
        });

        Label volumeLabel = new Label("Volume Musique", skin);
        final Label currentVolumeLabel = new Label(String.format("%d%%", Math.round(musicVolume * 100f)), skin);
        Slider menuVolumeSlider = new Slider(0f, 1f, 0.1f, false, skin);
        menuVolumeSlider.setValue(musicVolume);
        menuVolumeSlider.addListener(new ChangeListener() {
            @Override
            /**
             * Reagit au deplacement du slider de volume et applique la nouvelle valeur audio.
             */

            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                float value = ((Slider) actor).getValue();
                currentVolumeLabel.setText(String.format("%d%%", Math.round(value * 100f)));
                updateMusicVolume(value);
            }
        });

        TextButton optionsBackBtn = new TextButton("RETOUR", skin);
        optionsBackBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                showMainMenuPage();
            }
        });

        optionsMenuTable.defaults().pad(8);
        optionsMenuTable.add(optionsTitleLabel).padBottom(12).row();
        optionsMenuTable.add(displayLabel).left().row();
        optionsMenuTable.add(displayModeBtn).width(340).height(48).padBottom(8).row();
        optionsMenuTable.add(volumeLabel).left().row();
        optionsMenuTable.add(menuVolumeSlider).width(340).height(30).row();
        optionsMenuTable.add(currentVolumeLabel).left().padBottom(10).row();
        optionsMenuTable.add(optionsBackBtn).width(260).height(48).padTop(8).row();

        controlsMenuTable = new Table();
        controlsMenuTable.setFillParent(true);
        controlsMenuTable.center();

        Label controlsTitleLabel = new Label("CONTROLES", skin);
        controlsTitleLabel.setFontScale(1.6f);

        Table controlsContentTable = new Table();
        controlsContentTable.defaults().pad(4).left();
        controlsContentTable.add(new Label("Deplacement: ZQSD / Fleches", skin)).row();
        controlsContentTable.add(new Label("Interaction: E", skin)).row();
        controlsContentTable.add(new Label("Rotation batiment: R", skin)).row();
        controlsContentTable.add(new Label("Action principale: Clic gauche", skin)).row();
        controlsContentTable.add(new Label("Annuler selection: Clic droit", skin)).row();
        controlsContentTable.add(new Label("Pause: Echap", skin)).row();
        controlsContentTable.add(new Label("Sauvegarder rapide: F5", skin)).row();
        controlsContentTable.add(new Label("Charger rapide: F9", skin)).row();

        TextButton controlsBackBtn = new TextButton("RETOUR", skin);
        controlsBackBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                showMainMenuPage();
            }
        });

        controlsMenuTable.defaults().pad(8);
        controlsMenuTable.add(controlsTitleLabel).padBottom(12).row();
        controlsMenuTable.add(controlsContentTable).left().padBottom(8).row();
        controlsMenuTable.add(controlsBackBtn).width(260).height(48).padTop(4).row();

        uiStage.addActor(mainMenuTable);
        uiStage.addActor(optionsMenuTable);
        uiStage.addActor(controlsMenuTable);

        showMainMenuPage();
    }
    /**
     * Affiche uniquement la page principale du menu.
     */
    private void showMainMenuPage() {
        currentMenuPage = MenuPage.MAIN;
        mainMenuTable.setVisible(true);
        optionsMenuTable.setVisible(false);
        controlsMenuTable.setVisible(false);
    }

    /**
     * Affiche uniquement la page des options du menu.
     */
    private void showOptionsPage() {
        currentMenuPage = MenuPage.OPTIONS;
        mainMenuTable.setVisible(false);
        optionsMenuTable.setVisible(true);
        controlsMenuTable.setVisible(false);
    }

    /**
     * Affiche uniquement la page des controles du menu.
     */
    private void showControlsPage() {
        currentMenuPage = MenuPage.CONTROLS;
        mainMenuTable.setVisible(false);
        optionsMenuTable.setVisible(false);
        controlsMenuTable.setVisible(true);
    }

    /**
     * Retourne le libelle d'affichage selon le mode ecran actuel (fenetre ou plein ecran).
     */
    private String getDisplayModeLabel() {
        return Gdx.graphics.isFullscreen() ? "Plein ecran" : "Fenetre";
    }

    /**
     * Alterne entre fenetre 1280x720 et plein ecran natif.
     */
    private void toggleFullscreen() {
        if (Gdx.graphics.isFullscreen()) {
            Gdx.graphics.setWindowedMode(1280, 720);
        } else {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }
    }

    /**
     * Reinitialise l'etat de partie et demarre une nouvelle sauvegarde propre.
     */
    private void startNewGame() {
        playerName = "Joueur";

        currentState = GameState.PLAYING;
        mainMenuTable.setVisible(false);
        pauseWindow.setVisible(false);
        optionsMenuTable.setVisible(false);
        controlsMenuTable.setVisible(false);
        setGameHUDVisible(true);

        for (int x = 0; x < MAP_SIZE; x++) {
            for (int y = 0; y < MAP_SIZE; y++) {
                terrainGrid[x][y] = new Terrain(Terrain.Type.GRASS);
            }
        }

        buildings.clear();
        buildings.add(new Building(Building.Type.MAIN_HQ, 55, 55, 0));
        buildings.add(new Building(Building.Type.AUCTION_HOUSE, 58, 46, 0));

        playerPos.set(0, 0);
        if (playerAnimationController != null) playerAnimationController.reset();
        currentRotation = 0;
        money = 1000;
        buildingStockPlanter = 1;
        buildingStockConveyor = 0;
        seedBagsPotato = 0;
        seedBagsStrawberry = 0;
        seedBagsLeek = 0;
        carriedPotato = 0;
        carriedStrawberry = 0;
        carriedLeek = 0;
        cancelSelection();
        updateStatsLabel();
    }

    /**
     * Charge la sauvegarde si elle existe, sinon lance une nouvelle partie.
     */
    private void continueGame() {
        playerName = "Joueur";

        if (SaveSystem.exists()) {
            if (!loadGame()) {
                startNewGame();
            }
        } else {
            startNewGame();
        }

        currentState = GameState.PLAYING;
        mainMenuTable.setVisible(false);
        pauseWindow.setVisible(false);
        optionsMenuTable.setVisible(false);
        controlsMenuTable.setVisible(false);
        setGameHUDVisible(true);
    }


    /**
     * Construit la fenetre de pause (volume, reprise, retour menu avec/sans sauvegarde).
     */
    private void createPauseMenuUI() {
        pauseWindow = new Window("PAUSE", skin);
        pauseWindow.setModal(true);
        pauseWindow.setMovable(false);
        pauseWindow.pad(20);

        Label volumeLabel = new Label("Volume:", skin);
        final Slider pauseVolumeSlider = new Slider(0f, 1f, 0.1f, false, skin);
        pauseVolumeSlider.setValue(musicVolume);
        pauseVolumeSlider.addListener(new ClickListener() {
            @Override public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                updateMusicVolume(pauseVolumeSlider.getValue());
            }
        });

        TextButton resumeBtn = new TextButton("Reprendre", skin);
        resumeBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                resumeGame();
            }
        });

        TextButton saveQuitBtn = new TextButton("Sauvegarder & Menu", skin);
        saveQuitBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                saveGame();
                showMainMenu();
            }
        });

        TextButton abandonBtn = new TextButton("Abandonner (Sans Save)", skin);
        abandonBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                showMainMenu();
            }
        });

        pauseWindow.add(volumeLabel).pad(5);
        pauseWindow.add(pauseVolumeSlider).width(280).height(30).pad(5).row();
        pauseWindow.add(resumeBtn).width(200).pad(5).colspan(2).row();
        pauseWindow.add(saveQuitBtn).width(200).pad(5).colspan(2).row();
        pauseWindow.add(abandonBtn).width(200).pad(5).colspan(2).row();

        pauseWindow.pack();

        pauseWindow.setPosition(
                Gdx.graphics.getWidth() / 2f - pauseWindow.getWidth() / 2f,
                Gdx.graphics.getHeight() / 2f - pauseWindow.getHeight() / 2f
        );

        uiStage.addActor(pauseWindow);
        pauseWindow.setVisible(false);
    }

    /**
     * Met a jour le volume musique et relance la lecture si besoin.
     */
    private void updateMusicVolume(float volume) {
        musicVolume = volume;
        if (backgroundMusic != null) {
            backgroundMusic.setVolume(musicVolume);
            if (!backgroundMusic.isPlaying()) backgroundMusic.play();
        }
    }



    /**
     * Bascule vers l'etat MENU, masque le HUD jeu et prepare les controles du menu.
     */
    private void showMainMenu() {
        currentState = GameState.MENU;
        showMainMenuPage();
        pauseWindow.setVisible(false);
        setGameHUDVisible(false);
        if (continueBtn != null) continueBtn.setDisabled(!SaveSystem.exists());


        Gdx.input.setInputProcessor(uiStage);


        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            backgroundMusic.play();
        }
    }

    /**
     * Quitte la pause et remet le jeu en etat PLAYING.
     */
    private void resumeGame() {
        currentState = GameState.PLAYING;
        pauseWindow.setVisible(false);
    }

    /**
     * Garantit la presence du QG et de l'hotel des ventes dans la carte.
     */
    private void ensureCoreBuildings() {
        boolean hasHQ = false;
        boolean hasAuctionHouse = false;

        for (Building b : buildings) {
            if (b.isHQ()) hasHQ = true;
            if (b.isAuctionHouse()) hasAuctionHouse = true;
        }

        if (!hasHQ) buildings.add(new Building(Building.Type.MAIN_HQ, 55, 55, 0));
        if (!hasAuctionHouse) buildings.add(new Building(Building.Type.AUCTION_HOUSE, 58, 46, 0));
    }

    /**
     * Passe le jeu en pause et affiche la fenetre de pause au premier plan.
     */
    private void pauseGame() {
        currentState = GameState.PAUSED;
        pauseWindow.setVisible(true);
        pauseWindow.toFront();
    }

    /**
     * Affiche ou masque tous les panneaux HUD marques avec le prefixe HUD_.
     */
    private void setGameHUDVisible(boolean visible) {
        for (com.badlogic.gdx.scenes.scene2d.Actor actor : uiStage.getActors()) {

            if (actor.getName() != null && actor.getName().startsWith("HUD_")) {
                actor.setVisible(visible);
            }
        }
    }

    @Override
    /**
     * Boucle de frame principale: input, simulation, rendu monde, overlays et rendu UI.
     */

    public void render() {
        float dt = Gdx.graphics.getDeltaTime();


        if (currentState == GameState.PLAYING) {
            handleInput(dt);


            for (Building b : buildings) {
                b.update(dt);
            }
            updateConveyors(dt);


            camera.position.set(playerPos.x, playerPos.y, 0);
            camera.update();
        } else if (currentState == GameState.PAUSED) {

            if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
                resumeGame();
            }
        }



        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);

        if (currentState == GameState.MENU) {
            batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.begin();
            batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.end();
        }


        if (currentState == GameState.PLAYING || currentState == GameState.PAUSED) {


            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            for (int x = 0; x < MAP_SIZE; x++) {
                for (int y = 0; y < MAP_SIZE; y++) {
                    Terrain t = terrainGrid[x][y];
                    Texture tex;
                    switch (t.getType()) {
                        case GRASS:  tex = tileGrass; break;
                        case TILLED: tex = tileTilled; break;
                        default:     tex = tileGrass; break;
                    }
                    batch.draw(tex, x - MAP_OFFSET, y - MAP_OFFSET, 1f, 1f);
                }
            }
            batch.end();



            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            for (Building b : buildings) {
                if (b.getType() == Building.Type.MAIN_HQ || b.getType() == Building.Type.AUCTION_HOUSE) continue;
                float wx = b.getGridX() - MAP_OFFSET;
                float wy = b.getGridY() - MAP_OFFSET;
                boolean drawRect = true;


                switch (b.getType()) {
                    case CONVEYOR_BELT: shapeRenderer.setColor(Color.GRAY); break;
                    case PLANTER:
                        if (planterTexture != null || planterPotatoTexture != null || planterStrawberryTexture != null || planterLeekTexture != null) {
                            drawRect = false;
                            break;
                        }

                        if (b.isPlanterEmpty()) shapeRenderer.setColor(new Color(0.55f, 0.35f, 0.15f, 1f));
                        else if (b.isPlanterReady()) shapeRenderer.setColor(getCropReadyColor(b.getPlanterCrop()));
                        else shapeRenderer.setColor(getCropGrowingColor(b.getPlanterCrop()));
                        break;
                    default: shapeRenderer.setColor(Color.WHITE); break;
                }
                if (drawRect) {
                    if (b.isPlanter()) {
                        float drawWidth = b.getType().width * PLANTER_VISUAL_SCALE;
                        float drawHeight = b.getType().height * PLANTER_VISUAL_SCALE;
                        float drawX = wx + (b.getType().width - drawWidth) / 2f;
                        float drawY = wy + (b.getType().height - drawHeight) / 2f;
                        shapeRenderer.rect(drawX, drawY, drawWidth, drawHeight);
                    } else {
                        shapeRenderer.rect(wx, wy, b.getType().width, b.getType().height);
                    }
                }


                if (b.isConveyor() && b.hasItem()) {
                    Texture heldItemTexture = getCropTexture(b.getHeldItem());
                    if (heldItemTexture == null) {
                        shapeRenderer.setColor(getCropReadyColor(b.getHeldItem()));
                        Vector2 itemPos = getConveyorItemWorldPosition(b, conveyorItemTmp);
                        float itemX = itemPos.x;
                        float itemY = itemPos.y;
                        shapeRenderer.rect(itemX, itemY, 0.5f, 0.5f);
                    }
                }
            }
            shapeRenderer.end();


            batch.begin();
            for (Building b : buildings) {
                if (b.getType() == Building.Type.MAIN_HQ) {


                    final float visualWidth = b.getType().width * HQ_SPRITE_SCALE;
                    final float visualHeight = b.getType().height * HQ_SPRITE_SCALE;


                    final float logicalWidth = b.getType().width;
                    final float logicalHeight = b.getType().height;


                    float drawX = (b.getGridX() - MAP_OFFSET + logicalWidth / 2f) - (visualWidth / 2f);
                    float drawY = (b.getGridY() - MAP_OFFSET + logicalHeight / 2f) - (visualHeight / 2.2f);

                    batch.draw(hqTexture, drawX, drawY, visualWidth, visualHeight);
                } else if (b.getType() == Building.Type.AUCTION_HOUSE) {
                    final float visualWidth = b.getType().width * HQ_SPRITE_SCALE;
                    final float visualHeight = b.getType().height * HQ_SPRITE_SCALE;
                    final float logicalWidth = b.getType().width;
                    final float logicalHeight = b.getType().height;

                    float drawX = (b.getGridX() - MAP_OFFSET + logicalWidth / 2f) - (visualWidth / 2f);
                    float drawY = (b.getGridY() - MAP_OFFSET + logicalHeight / 2f) - (visualHeight / 2.2f);

                    batch.draw(auctionTexture, drawX, drawY, visualWidth, visualHeight);
                } else if (b.isPlanter()) {
                    float wx = b.getGridX() - MAP_OFFSET;
                    float wy = b.getGridY() - MAP_OFFSET;
                    Texture planterStateTexture;

                    if (!b.isPlanterEmpty()) {
                        switch (b.getPlanterCrop()) {
                            case POTATO:
                                planterStateTexture = planterPotatoTexture;
                                break;
                            case STRAWBERRY:
                                planterStateTexture = planterStrawberryTexture;
                                break;
                            case LEEK:
                                planterStateTexture = planterLeekTexture;
                                break;
                            default:
                                planterStateTexture = planterTexture;
                                break;
                        }
                    } else {
                        planterStateTexture = planterTexture;
                    }

                    if (planterStateTexture == null) {
                        planterStateTexture = planterTexture;
                    }

                    if (planterStateTexture != null) {
                        float drawWidth = b.getType().width * PLANTER_VISUAL_SCALE;
                        float drawHeight = b.getType().height * PLANTER_VISUAL_SCALE;
                        float drawX = wx + (b.getType().width - drawWidth) / 2f;
                        float drawY = wy + (b.getType().height - drawHeight) / 2f;
                        batch.draw(planterStateTexture, drawX, drawY, drawWidth, drawHeight);
                    }
                } else if (b.isConveyor()) {
                    Texture conveyorTexture = getConveyorTextureForRotation(b.getRotation());
                    float wx = b.getGridX() - MAP_OFFSET;
                    float wy = b.getGridY() - MAP_OFFSET;
                    if (conveyorTexture != null) {
                        batch.draw(conveyorTexture, wx, wy, b.getType().width, b.getType().height);
                    }
                    if (b.hasItem()) {
                        Texture heldItemTexture = getCropTexture(b.getHeldItem());
                        if (heldItemTexture != null) {
                            Vector2 itemPos = getConveyorItemWorldPosition(b, conveyorItemTmp);
                            float itemX = itemPos.x;
                            float itemY = itemPos.y;
                            batch.draw(heldItemTexture, itemX, itemY, 0.5f, 0.5f);
                        }
                    }
                }
            }
            batch.end();

            Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            for (Building b : buildings) {
                if (!b.isPlanter() || b.isPlanterEmpty() || !b.isPlanterReady()) continue;
                float wx = b.getGridX() - MAP_OFFSET;
                float wy = b.getGridY() - MAP_OFFSET;
                float overlaySize = READY_FILTER_SCALE;
                float offset = (1f - overlaySize) * 0.5f;
                Color c = getCropReadyOverlayColor(b.getPlanterCrop());
                shapeRenderer.setColor(c);
                shapeRenderer.rect(wx + offset, wy + offset, overlaySize, overlaySize);
            }
            shapeRenderer.end();


            Vector3 worldMouse = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            int mouseWorldX = MathUtils.floor(worldMouse.x);
            int mouseWorldY = MathUtils.floor(worldMouse.y);

            if (currentState == GameState.PLAYING) {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

                if (!selectedTool.equals("NONE") && selectedBuildingType == null && !selectedTool.equals("BULLDOZE")) {
                    shapeRenderer.setColor(isInInteractionRange(mouseWorldX, mouseWorldY) ? Color.YELLOW : Color.RED);
                    shapeRenderer.rect(mouseWorldX, mouseWorldY, 1, 1);
                }
                shapeRenderer.end();

                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

                if (selectedBuildingType != null) {
                    boolean ok = isInInteractionRange(mouseWorldX, mouseWorldY) && canPlaceBuilding(selectedBuildingType, mouseWorldX + MAP_OFFSET, mouseWorldY + MAP_OFFSET);
                    shapeRenderer.setColor(ok ? new Color(0f, 1f, 0f, 0.25f) : new Color(1f, 0f, 0f, 0.25f));
                    shapeRenderer.rect(mouseWorldX, mouseWorldY, selectedBuildingType.width, selectedBuildingType.height);


                    shapeRenderer.setColor(Color.YELLOW);
                    float cx = mouseWorldX + selectedBuildingType.width / 2f;
                    float cy = mouseWorldY + selectedBuildingType.height / 2f;
                    float len = 0.4f;
                    switch (currentRotation) {
                        case 0: shapeRenderer.rect(cx - 0.05f, cy, 0.1f, len); break;
                        case 1: shapeRenderer.rect(cx, cy - 0.05f, len, 0.1f); break;
                        case 2: shapeRenderer.rect(cx - 0.05f, cy - len, 0.1f, len); break;
                        case 3: shapeRenderer.rect(cx - len, cy - 0.05f, len, 0.1f); break;
                    }
                }
                shapeRenderer.end();
            }



            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
            shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.5f);
            for (int x = 0; x <= MAP_SIZE; x++) shapeRenderer.line(x - MAP_OFFSET, -MAP_OFFSET, x - MAP_OFFSET, MAP_SIZE - MAP_OFFSET);
            for (int y = 0; y <= MAP_SIZE; y++) shapeRenderer.line(-MAP_OFFSET, y - MAP_OFFSET, MAP_SIZE - MAP_OFFSET, y - MAP_OFFSET);
            shapeRenderer.end();


            batch.begin();
            TextureRegion playerFrame = playerAnimationController == null ? null : playerAnimationController.getCurrentFrame();
            if (playerFrame != null) {
                float drawX = playerPos.x - PLAYER_SIZE * 0.5f;
                float drawY = playerPos.y - PLAYER_SIZE * 0.5f;
                if (playerAnimationController.isFlipX()) {
                    batch.draw(playerFrame, drawX + PLAYER_SIZE, drawY, -PLAYER_SIZE, PLAYER_SIZE);
                } else {
                    batch.draw(playerFrame, drawX, drawY, PLAYER_SIZE, PLAYER_SIZE);
                }
            }
            batch.end();


            if (currentState == GameState.PAUSED) {
                Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(0f, 0f, 0f, 0.5f);
                shapeRenderer.rect(-1000, -1000, 2000, 2000);
                shapeRenderer.end();
            }
        }


        uiStage.act(dt);
        uiStage.draw();
    }
    /**
     * Gere les entrees clavier/souris: deplacements, interactions, outils, placement et pause.
     */
    private void handleInput(float dt) {

        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            pauseGame();
            return;
        }


        if (Gdx.input.isKeyJustPressed(Keys.F5)) saveGame();
        if (Gdx.input.isKeyJustPressed(Keys.F9)) loadGame();


        if (Gdx.input.isKeyJustPressed(Keys.R)) {
            currentRotation = (currentRotation + 1) % 4;
        }


        if (Gdx.input.isKeyJustPressed(Keys.E)) {
            tryInteract();
        }


        Vector2 lastPos = new Vector2(playerPos);
        float moveX = 0f;
        float moveY = 0f;
        if (Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.Z) || Gdx.input.isKeyPressed(Keys.UP)) {
            playerPos.y += MOVE_SPEED * dt;
            moveY += 1f;
        }
        if (Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN)) {
            playerPos.y -= MOVE_SPEED * dt;
            moveY -= 1f;
        }
        if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.Q)) {
            playerPos.x -= MOVE_SPEED * dt;
            moveX -= 1f;
        }
        if (Gdx.input.isKeyPressed(Keys.D)) {
            playerPos.x += MOVE_SPEED * dt;
            moveX += 1f;
        }

        if (isCollidingWithBuilding(playerPos.x, playerPos.y)) {
            playerPos.set(lastPos);
        }

        if (playerAnimationController != null) {
            playerAnimationController.update(dt, moveX, moveY);
        }


        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            cancelSelection();
            return;
        }


        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {

            Vector2 stageCoords = uiStage.screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            boolean hitUI = uiStage.hit(stageCoords.x, stageCoords.y, true) != null;
            if (hitUI) return;


            Vector3 worldPos = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            int worldGridX = MathUtils.floor(worldPos.x);
            int worldGridY = MathUtils.floor(worldPos.y);


            if (selectedTool.equals("BULLDOZE")) {
                if (isInInteractionRange(worldGridX, worldGridY)) {
                    deleteBuildingAtWorldCell(worldGridX, worldGridY);
                }
                return;
            }


            if (selectedBuildingType != null) {
                placeBuilding(worldPos.x, worldPos.y);
                return;
            }


            if (!selectedTool.equals("NONE")) {
                applyTool(worldPos.x, worldPos.y);
            }
        }
    }

    /**
     * Verifie si la hitbox du joueur chevauche un batiment solide (hors jardiniere/convoyeur).
     */
    private boolean isCollidingWithBuilding(float worldX, float worldY) {
        float playerHalfW = PLAYER_SIZE * 0.5f;
        float playerHalfH = PLAYER_SIZE * 0.5f;
        float playerLeft = worldX - playerHalfW;
        float playerRight = worldX + playerHalfW;
        float playerBottom = worldY - playerHalfH;
        float playerTop = worldY + playerHalfH;

        for (Building b : buildings) {
            if (b.isPlanter() || b.isConveyor()) continue;

            float bx = b.getGridX() - MAP_OFFSET;
            float by = b.getGridY() - MAP_OFFSET;
            float bw = b.getType().width;
            float bh = b.getType().height;

            if (playerLeft < bx + bw && playerRight > bx &&
                    playerBottom < by + bh && playerTop > by) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tente une interaction proche: recolter/planter sur jardiniere, ouvrir QG ou hotel des ventes.
     */
    private void tryInteract() {
        int playerWorldX = Math.round(playerPos.x);
        int playerWorldY = Math.round(playerPos.y);

        Building nearbyBuilding = null;


        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                Building b = getBuildingAtWorldCell(playerWorldX + dx, playerWorldY + dy);
                if (b != null) {
                    if (b.isPlanter() || b.isHQ() || b.isAuctionHouse()) {
                        nearbyBuilding = b;
                        break;
                    }
                }
            }
            if (nearbyBuilding != null) break;
        }

        if (nearbyBuilding == null) return;


        if (nearbyBuilding.isPlanter()) {

            if (!nearbyBuilding.isPlanterEmpty() && nearbyBuilding.isPlanterReady()) {
                Building.PlanterCrop crop = nearbyBuilding.harvest();
                int amount = Building.getYieldFor(crop);

                addToCarried(crop, amount);
                updateStatsLabel();
                playSfx(plantationOutSound);
                return;
            }

            if (nearbyBuilding.isPlanterEmpty()) {
                openPlanterPopup(nearbyBuilding);
            }
        }


        else if (nearbyBuilding.isHQ()) {
            openHQPopup(nearbyBuilding);
        } else if (nearbyBuilding.isAuctionHouse()) {
            openAuctionPopup(nearbyBuilding);
        }
    }

    /**
     * Rafraichit tous les compteurs du HUD (argent, stocks, sacs, ressources portees).
     */
    private void updateStatsLabel() {
        if (buildPlanterBtn != null) buildPlanterBtn.setText("Jardiniere x" + buildingStockPlanter);
        if (buildConveyorBtn != null) buildConveyorBtn.setText("Convoyeur x" + buildingStockConveyor);
        if (hudMoneyLabel != null) hudMoneyLabel.setText("Argent: " + money + "$");
        if (hudSeedPotatoLabel != null) hudSeedPotatoLabel.setText("x " + seedBagsPotato);
        if (hudSeedStrawberryLabel != null) hudSeedStrawberryLabel.setText("x " + seedBagsStrawberry);
        if (hudSeedLeekLabel != null) hudSeedLeekLabel.setText("x " + seedBagsLeek);
        if (hudCarryPotatoLabel != null) hudCarryPotatoLabel.setText("Patate: " + carriedPotato);
        if (hudCarryStrawberryLabel != null) hudCarryStrawberryLabel.setText("Fraise: " + carriedStrawberry);
        if (hudCarryLeekLabel != null) hudCarryLeekLabel.setText("Poireau: " + carriedLeek);
    }

    /**
     * Ouvre la popup jardiniere pour choisir une graine et planter selon le stock disponible.
     */
    private void openPlanterPopup(Building planter) {
        closePlanterPopup();
        closeHQPopup();
        closeAuctionPopup();

        currentPlanterForUI = planter;


        com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle ws =
                new com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle(
                        skin.getFont("default"),
                        Color.WHITE,
                        skin.newDrawable("white", new Color(0f, 0f, 0f, 0.7f))
                );

        planterWindow = new com.badlogic.gdx.scenes.scene2d.ui.Window("Jardiniere", ws);
        planterWindow.pad(10);

        TextButton potatoBtn = new TextButton("Patate (sac: " + seedBagsPotato + ")", skin);
        TextButton strawberryBtn = new TextButton("Fraise (sac: " + seedBagsStrawberry + ")", skin);
        TextButton leekBtn = new TextButton("Poireau (sac: " + seedBagsLeek + ")", skin);
        TextButton cancelBtn = new TextButton("Annuler", skin);

        potatoBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (currentPlanterForUI != null && seedBagsPotato > 0) {
                    seedBagsPotato--;
                    currentPlanterForUI.plant(Building.PlanterCrop.POTATO);
                    updateStatsLabel();
                    playSfx(plantationInSound);
                }
                closePlanterPopup();
            }
        });

        strawberryBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (currentPlanterForUI != null && seedBagsStrawberry > 0) {
                    seedBagsStrawberry--;
                    currentPlanterForUI.plant(Building.PlanterCrop.STRAWBERRY);
                    updateStatsLabel();
                    playSfx(plantationInSound);
                }
                closePlanterPopup();
            }
        });

        leekBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (currentPlanterForUI != null && seedBagsLeek > 0) {
                    seedBagsLeek--;
                    currentPlanterForUI.plant(Building.PlanterCrop.LEEK);
                    updateStatsLabel();
                    playSfx(plantationInSound);
                }
                closePlanterPopup();
            }
        });

        cancelBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                closePlanterPopup();
            }
        });

        planterWindow.add(potatoBtn).width(260).pad(5).row();
        planterWindow.add(strawberryBtn).width(260).pad(5).row();
        planterWindow.add(leekBtn).width(260).pad(5).row();
        planterWindow.add(cancelBtn).width(240).pad(5).row();

        planterWindow.pack();


        float cx = (Gdx.graphics.getWidth() - planterWindow.getWidth()) / 2f;
        float cy = (Gdx.graphics.getHeight() - planterWindow.getHeight()) / 2f;
        planterWindow.setPosition(cx, cy);

        uiStage.addActor(planterWindow);
    }

    /**
     * Ferme la popup jardiniere et nettoie la reference de jardiniere selectionnee.
     */
    private void closePlanterPopup() {
        if (planterWindow != null) {
            planterWindow.remove();
            planterWindow = null;
        }
        currentPlanterForUI = null;
    }

    /**
     * Ouvre la popup QG pour vendre le stock interne et transferer l'inventaire du joueur.
     */
    private void openHQPopup(Building hq) {
        closePlanterPopup();
        closeHQPopup();
        closeAuctionPopup();

        currentHQForUI = hq;

        Window.WindowStyle ws = new Window.WindowStyle(
                skin.getFont("default"),
                Color.WHITE,
                skin.newDrawable("white", new Color(0f, 0f, 0f, 0.8f))
        );

        hqWindow = new Window("QG - Vente et Stock", ws);
        hqWindow.pad(16);

        Table root = new Table();
        root.defaults().pad(6);

        Table titlePanel = new Table();
        titlePanel.setBackground(skin.newDrawable("white", new Color(0.15f, 0.15f, 0.15f, 1f)));
        titlePanel.pad(8);
        titlePanel.add(new Label("Stock interne HQ", skin));
        root.add(titlePanel).growX().row();

        Table cards = new Table();
        cards.defaults().pad(6);
        addHQSellRow(cards, "Sac de patate", Building.PlanterCrop.POTATO);
        addHQSellRow(cards, "Sac de fraise", Building.PlanterCrop.STRAWBERRY);
        addHQSellRow(cards, "Sac de poireau", Building.PlanterCrop.LEEK);
        root.add(cards).growX().row();

        TextButton storeBtn = new TextButton("Stocker sur moi -> HQ", skin);
        storeBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (currentHQForUI == null) return;

                if (carriedPotato > 0) {
                    currentHQForUI.addToHQStock(Building.PlanterCrop.POTATO, carriedPotato);
                    carriedPotato = 0;
                }
                if (carriedStrawberry > 0) {
                    currentHQForUI.addToHQStock(Building.PlanterCrop.STRAWBERRY, carriedStrawberry);
                    carriedStrawberry = 0;
                }
                if (carriedLeek > 0) {
                    currentHQForUI.addToHQStock(Building.PlanterCrop.LEEK, carriedLeek);
                    carriedLeek = 0;
                }

                updateStatsLabel();
                openHQPopup(currentHQForUI);
            }
        });

        TextButton closeBtn = new TextButton("Fermer", skin);
        closeBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                closeHQPopup();
            }
        });

        Table footerPanel = new Table();
        footerPanel.setBackground(skin.newDrawable("white", new Color(0.12f, 0.12f, 0.12f, 1f)));
        footerPanel.pad(8);
        footerPanel.defaults().pad(4);
        footerPanel.add(new Label("Sur moi: P " + carriedPotato + " | F " + carriedStrawberry + " | L " + carriedLeek, skin)).left().row();
        footerPanel.add(storeBtn).width(300).left().row();
        root.add(footerPanel).growX().row();

        hqWindow.add(root).row();
        hqWindow.add(closeBtn).width(200).pad(5).row();

        hqWindow.pack();
        hqWindow.setPosition(
                Gdx.graphics.getWidth() / 2f - hqWindow.getWidth() / 2f,
                Gdx.graphics.getHeight() / 2f - hqWindow.getHeight() / 2f
        );

        uiStage.addActor(hqWindow);
    }

    /**
     * Ferme la popup QG et efface la reference du QG actif.
     */
    private void closeHQPopup() {
        if (hqWindow != null) {
            hqWindow.remove();
            hqWindow = null;
        }
        currentHQForUI = null;
    }

    /**
     * Ajoute une carte de vente pour une culture avec prix, stock et boutons de vente rapide.
     */
    private void addHQSellRow(Table table, String title, Building.PlanterCrop crop) {
        int stock = currentHQForUI == null ? 0 : currentHQForUI.getHQStock(crop);
        int unitPrice = Building.getSellPriceFor(crop);

        Table card = new Table();
        card.setBackground(skin.newDrawable("white", new Color(0.18f, 0.18f, 0.18f, 1f)));
        card.pad(10);
        card.defaults().pad(3);

        card.add(new Label(title, skin)).row();

        Texture cropTexture = getCropTexture(crop);
        if (cropTexture != null) {
            card.add(new Image(cropTexture)).size(40).padBottom(2).row();
        } else {
            Table icon = new Table();
            icon.setBackground(skin.newDrawable("white", getCropReadyColor(crop)));
            card.add(icon).size(40, 26).padBottom(2).row();
        }

        card.add(new Label(unitPrice + "$ / unite", skin)).row();
        card.add(new Label("Stock: " + stock, skin)).row();

        TextButton sell1 = new TextButton("Vendre 1", skin);
        sell1.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                sellFromHQ(crop, 1);
            }
        });

        TextButton sell10 = new TextButton("Vendre 10", skin);
        sell10.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                sellFromHQ(crop, 10);
            }
        });

        TextButton sellAll = new TextButton("Tout vendre", skin);
        sellAll.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (currentHQForUI != null) {
                    sellFromHQ(crop, currentHQForUI.getHQStock(crop));
                }
            }
        });

        Table buttons = new Table();
        buttons.defaults().pad(2);
        buttons.add(sell1).width(72);
        buttons.add(sell10).width(72);
        buttons.add(sellAll).width(82);

        card.add(buttons).row();
        table.add(card).width(250).top();
    }

    /**
     * Ajoute une carte d'achat de sac de graines dans l'hotel des ventes.
     */
    private void addSeedCard(Table table, String title, String detail, Building.PlanterCrop crop, int price) {
        Table card = new Table();
        card.top();
        card.setBackground(skin.newDrawable("white", new Color(0.18f, 0.18f, 0.18f, 1f)));
        card.pad(10);
        card.defaults().pad(4);

        card.add(new Label(title, skin)).row();

        Texture cropBagTexture = getCropBagTexture(crop);
        if (cropBagTexture != null) {
            card.add(new Image(cropBagTexture)).size(40).row();
        } else {
            Table icon = new Table();
            icon.setBackground(skin.newDrawable("white", getCropReadyColor(crop)));
            card.add(icon).size(40, 26).row();
        }

        card.add(new Label(price + "$", skin)).row();
        card.add(new Label(detail, skin)).row();

        TextButton buy = new TextButton("BUY", skin);
        buy.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                buySeedBag(crop, price);
            }
        });
        card.add(buy).width(140).padTop(4).row();

        table.add(card).size(AUCTION_CARD_WIDTH, AUCTION_CARD_HEIGHT).top().pad(4);
    }

    /**
     * Ajoute une carte d'achat de stock de batiment (jardiniere ou convoyeur).
     */
    private void addBuildingCard(Table table, String title, int price, boolean planter) {
        Table card = new Table();
        card.top();
        card.setBackground(skin.newDrawable("white", new Color(0.18f, 0.18f, 0.18f, 1f)));
        card.pad(10);
        card.defaults().pad(4);

        card.add(new Label(title, skin)).row();

        Texture buildingTexture = planter ? planterTexture : conveyorBottomTexture;
        if (buildingTexture != null) {
            card.add(new Image(buildingTexture)).size(56, 32).row();
        } else {
            Table icon = new Table();
            icon.setBackground(skin.newDrawable("white", planter ? new Color(0.55f, 0.35f, 0.15f, 1f) : Color.GRAY));
            card.add(icon).size(56, 32).row();
        }

        card.add(new Label(price + "$", skin)).row();

        TextButton buy = new TextButton("BUY", skin);
        buy.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                buyBuildingStock(planter, price);
            }
        });
        card.add(buy).width(140).padTop(4).row();

        table.add(card).size(AUCTION_CARD_WIDTH, AUCTION_CARD_HEIGHT).top().pad(4);
    }

    /**
     * Vend une quantite donnee depuis le stock QG puis met a jour l'argent et l'UI.
     */
    private void sellFromHQ(Building.PlanterCrop crop, int amount) {
        if (currentHQForUI == null) return;
        int sold = currentHQForUI.removeFromHQStock(crop, amount);
        if (sold <= 0) return;

        money += sold * Building.getSellPriceFor(crop);
        updateStatsLabel();
        playSfx(sellItemSound);
        openHQPopup(currentHQForUI);
    }

    /**
     * Ouvre l'hotel des ventes avec onglets graines/batiments et etat des stocks.
     */
    private void openAuctionPopup(Building auctionHouse) {
        closePlanterPopup();
        closeHQPopup();
        closeAuctionPopup();

        currentAuctionForUI = auctionHouse;

        Window.WindowStyle ws = new Window.WindowStyle(
                skin.getFont("default"),
                Color.WHITE,
                skin.newDrawable("white", new Color(0f, 0f, 0f, 0.82f))
        );

        auctionWindow = new Window("Hotel des ventes", ws);
        auctionWindow.pad(16);

        Table root = new Table();
        root.defaults().pad(6);

        Table tabs = new Table();
        TextButton seedsTab = new TextButton("Graines", skin);
        seedsTab.setChecked(auctionActiveTab == AUCTION_TAB_SEEDS);
        seedsTab.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (auctionActiveTab == AUCTION_TAB_SEEDS) return;
                auctionActiveTab = AUCTION_TAB_SEEDS;
                if (currentAuctionForUI != null) openAuctionPopup(currentAuctionForUI);
            }
        });

        TextButton buildingsTab = new TextButton("Batiments", skin);
        buildingsTab.setChecked(auctionActiveTab == AUCTION_TAB_BUILDINGS);
        buildingsTab.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (auctionActiveTab == AUCTION_TAB_BUILDINGS) return;
                auctionActiveTab = AUCTION_TAB_BUILDINGS;
                if (currentAuctionForUI != null) openAuctionPopup(currentAuctionForUI);
            }
        });

        tabs.setBackground(skin.newDrawable("white", new Color(0.12f, 0.12f, 0.12f, 1f)));
        tabs.pad(8);
        tabs.add(seedsTab).size(AUCTION_TAB_WIDTH, AUCTION_TAB_HEIGHT).padRight(8);
        tabs.add(buildingsTab).size(AUCTION_TAB_WIDTH, AUCTION_TAB_HEIGHT);
        root.add(tabs).growX().row();

        Table content = new Table();
        content.setBackground(skin.newDrawable("white", new Color(0.1f, 0.1f, 0.1f, 1f)));
        content.pad(8);
        content.defaults().pad(4);

        if (auctionActiveTab == AUCTION_TAB_SEEDS) {
            addSeedCard(content, "Sac de patate", "Rendement 10 | 30s", Building.PlanterCrop.POTATO, 10);
            addSeedCard(content, "Sac de fraise", "Rendement 7 | 1min", Building.PlanterCrop.STRAWBERRY, 15);
            addSeedCard(content, "Sac de poireau", "Rendement 5 | 5min", Building.PlanterCrop.LEEK, 100);
        } else {
            addBuildingCard(content, "Jardiniere", 20, true);
            addBuildingCard(content, "Convoyeur", 50, false);
        }

        root.add(content).growX().row();

        Table stockPanel = new Table();
        stockPanel.setBackground(skin.newDrawable("white", new Color(0.12f, 0.12f, 0.12f, 1f)));
        stockPanel.pad(8);
        stockPanel.defaults().pad(2);
        stockPanel.add(new Label("Stocks sacs P/F/L: " + seedBagsPotato + "/" + seedBagsStrawberry + "/" + seedBagsLeek, skin)).left().row();
        stockPanel.add(new Label("Stocks batiments J/C: " + buildingStockPlanter + "/" + buildingStockConveyor, skin)).left().row();
        root.add(stockPanel).growX().row();

        TextButton closeBtn = new TextButton("Fermer", skin);
        closeBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                closeAuctionPopup();
            }
        });

        auctionWindow.add(root).row();
        auctionWindow.add(closeBtn).width(200).padTop(10).row();

        auctionWindow.pack();
        auctionWindow.setPosition(
                Gdx.graphics.getWidth() / 2f - auctionWindow.getWidth() / 2f,
                Gdx.graphics.getHeight() / 2f - auctionWindow.getHeight() / 2f
        );

        uiStage.addActor(auctionWindow);
    }

    /**
     * Ferme la popup de l'hotel des ventes et nettoie sa reference active.
     */
    private void closeAuctionPopup() {
        if (auctionWindow != null) {
            auctionWindow.remove();
            auctionWindow = null;
        }
        currentAuctionForUI = null;
    }

    /**
     * Achete un sac de graines si le joueur a assez d'argent, puis rafraichit l'UI.
     */
    private void buySeedBag(Building.PlanterCrop crop, int price) {
        if (money < price) return;
        money -= price;

        if (crop == Building.PlanterCrop.POTATO) seedBagsPotato++;
        if (crop == Building.PlanterCrop.STRAWBERRY) seedBagsStrawberry++;
        if (crop == Building.PlanterCrop.LEEK) seedBagsLeek++;

        updateStatsLabel();
        playSfx(buyItemSound);
        if (currentAuctionForUI != null) openAuctionPopup(currentAuctionForUI);
    }

    /**
     * Achete une unite de stock de batiment si le budget est suffisant.
     */
    private void buyBuildingStock(boolean planter, int price) {
        if (money < price) return;
        money -= price;
        if (planter) buildingStockPlanter++;
        else buildingStockConveyor++;
        updateStatsLabel();
        playSfx(buyItemSound);
        if (currentAuctionForUI != null) openAuctionPopup(currentAuctionForUI);
    }

    /**
     * Ajoute une recolte a l'inventaire porte par le joueur selon le type de culture.
     */
    private void addToCarried(Building.PlanterCrop crop, int amount) {
        if (amount <= 0) return;
        if (crop == Building.PlanterCrop.POTATO) carriedPotato += amount;
        if (crop == Building.PlanterCrop.STRAWBERRY) carriedStrawberry += amount;
        if (crop == Building.PlanterCrop.LEEK) carriedLeek += amount;
    }

    /**
     * Annule toute selection (outil/batiment), decoche les boutons et ferme les popups.
     */
    private void cancelSelection() {
        selectedBuildingType = null;
        selectedTool = "NONE";
        updateSelectionLabel();
        closePlanterPopup();
        closeHQPopup();
        closeAuctionPopup();
        if (toolGroup != null) toolGroup.uncheckAll();
        if (buildingGroup != null) buildingGroup.uncheckAll();
    }

    /**
     * Place le batiment selectionne si la case est a portee, valide et en stock.
     */
    private void placeBuilding(float worldX, float worldY) {
        int worldGridX = MathUtils.floor(worldX);
        int worldGridY = MathUtils.floor(worldY);

        if (!isInInteractionRange(worldGridX, worldGridY)) return;

        int gridX = worldGridX + MAP_OFFSET;
        int gridY = worldGridY + MAP_OFFSET;

        if (selectedBuildingType == Building.Type.PLANTER && buildingStockPlanter <= 0) return;
        if (selectedBuildingType == Building.Type.CONVEYOR_BELT && buildingStockConveyor <= 0) return;

        if (!canPlaceBuilding(selectedBuildingType, gridX, gridY)) return;

        buildings.add(new Building(selectedBuildingType, gridX, gridY, currentRotation));
        if (selectedBuildingType == Building.Type.PLANTER) buildingStockPlanter--;
        if (selectedBuildingType == Building.Type.CONVEYOR_BELT) buildingStockConveyor--;
        updateStatsLabel();
        if (selectedBuildingType == Building.Type.PLANTER) playSfx(planterPlacementSound);
        if (selectedBuildingType == Building.Type.CONVEYOR_BELT) playSfx(conveyorPlacementSound);
    }

    /**
     * Verifie les regles de pose: limites carte, terrain requis et absence de chevauchement.
     */
    private boolean canPlaceBuilding(Building.Type type, int gridX, int gridY) {
        if (type == null) return false;


        if (gridX < 0 || gridY < 0) return false;
        if (gridX + type.width > MAP_SIZE) return false;
        if (gridY + type.height > MAP_SIZE) return false;


        for (int x = gridX; x < gridX + type.width; x++) {
            for (int y = gridY; y < gridY + type.height; y++) {
                Terrain t = terrainGrid[x][y];


                if (type == Building.Type.PLANTER) {
                    if (t.getType() != Terrain.Type.TILLED) return false;
                }


                if (type == Building.Type.MAIN_HQ || type == Building.Type.AUCTION_HOUSE) {
                    if (t.getType() != Terrain.Type.GRASS) return false;
                }

            }
        }


        for (Building b : buildings) {
            if (rectOverlap(gridX, gridY, type.width, type.height,
                    b.getGridX(), b.getGridY(), b.getType().width, b.getType().height)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Teste si deux rectangles axes sur la grille se chevauchent.
     */
    private boolean rectOverlap(int ax, int ay, int aw, int ah, int bx, int by, int bw, int bh) {
        return ax < bx + bw && ax + aw > bx && ay < by + bh && ay + ah > by;
    }

    /**
     * Retourne le batiment occupant une case monde donnee (coordonnees converties en grille).
     */
    private Building getBuildingAtWorldCell(int worldX, int worldY) {
        int gridX = worldX + MAP_OFFSET;
        int gridY = worldY + MAP_OFFSET;

        for (Building b : buildings) {
            int bx = b.getGridX();
            int by = b.getGridY();
            int bw = b.getType().width;
            int bh = b.getType().height;

            if (gridX >= bx && gridX < bx + bw && gridY >= by && gridY < by + bh) {
                return b;
            }
        }
        return null;
    }

    /**
     * Supprime le batiment sous la case cible sauf QG/hotel des ventes (non destructibles).
     */
    private void deleteBuildingAtWorldCell(int worldX, int worldY) {
        int gridX = worldX + MAP_OFFSET;
        int gridY = worldY + MAP_OFFSET;

        Iterator<Building> it = buildings.iterator();
        while (it.hasNext()) {
            Building b = it.next();
            int bx = b.getGridX();
            int by = b.getGridY();
            int bw = b.getType().width;
            int bh = b.getType().height;

            if (gridX >= bx && gridX < bx + bw && gridY >= by && gridY < by + bh) {
                if (b.isHQ() || b.isAuctionHouse()) {
                    return;
                }
                it.remove();
                closePlanterPopup();
                closeHQPopup();
                closeAuctionPopup();
                return;
            }
        }
    }

    /**
     * Applique l'outil actif (labourer/nettoyer) sur la case cible si elle est a portee.
     */
    private void applyTool(float worldX, float worldY) {
        int worldGridX = MathUtils.floor(worldX);
        int worldGridY = MathUtils.floor(worldY);

        if (!isInInteractionRange(worldGridX, worldGridY)) return;

        int gridX = worldGridX + MAP_OFFSET;
        int gridY = worldGridY + MAP_OFFSET;

        if (gridX >= 0 && gridX < MAP_SIZE && gridY >= 0 && gridY < MAP_SIZE) {
            Terrain t = terrainGrid[gridX][gridY];
            Terrain.Type beforeType = t.getType();
            switch (selectedTool) {
                case "TILL":
                    t.till();
                    if (beforeType != t.getType()) playSfx(tillSound);
                    break;
                case "CLEAR":
                    t.clear();
                    if (beforeType != t.getType()) playSfx(clearSound);
                    break;
            }
        }
    }

    /**
     * Indique si une case est dans la portee d'interaction du joueur (rayon 1 case).
     */
    private boolean isInInteractionRange(int targetWorldX, int targetWorldY) {
        int playerWorldX = Math.round(playerPos.x);
        int playerWorldY = Math.round(playerPos.y - 0.5f);

        int dx = Math.abs(targetWorldX - playerWorldX);
        int dy = Math.abs(targetWorldY - playerWorldY);

        return dx <= 1 && dy <= 1;
    }


    /**
     * Serialize l'etat complet de la partie et l'enregistre via SaveSystem.
     */
    private void saveGame() {
        GameSave save = new GameSave();
        save.mapSize = MAP_SIZE;
        save.mapOffset = MAP_OFFSET;
        save.playerX = playerPos.x;
        save.playerY = playerPos.y;
        save.money = money;
        save.buildingStockPlanter = buildingStockPlanter;
        save.buildingStockConveyor = buildingStockConveyor;
        save.seedBagsPotato = seedBagsPotato;
        save.seedBagsStrawberry = seedBagsStrawberry;
        save.seedBagsLeek = seedBagsLeek;
        save.carriedPotato = carriedPotato;
        save.carriedStrawberry = carriedStrawberry;
        save.carriedLeek = carriedLeek;

        save.terrainTypeNames = new String[MAP_SIZE * MAP_SIZE];
        int idx = 0;
        for (int y = 0; y < MAP_SIZE; y++) {
            for (int x = 0; x < MAP_SIZE; x++) {
                save.terrainTypeNames[idx++] = terrainGrid[x][y].getType().name();
            }
        }

        for (Building b : buildings) {
            GameSave.BuildingSave bs = new GameSave.BuildingSave();
            bs.type = b.getType().name();
            bs.x = b.getGridX();
            bs.y = b.getGridY();
            bs.rotation = b.getRotation();

            bs.planterCrop = b.getPlanterCrop().name();
            bs.growTimerSeconds = b.getGrowTimerSeconds();
            bs.planterReady = b.isPlanterReady();

            bs.heldItem = b.getHeldItem().name();
            bs.heldAmount = b.getHeldAmount();
            bs.transportTimer = b.getTransportTimerSeconds();

            bs.hqPotatoes = b.getHQStock(Building.PlanterCrop.POTATO);
            bs.hqStrawberries = b.getHQStock(Building.PlanterCrop.STRAWBERRY);
            bs.hqLeeks = b.getHQStock(Building.PlanterCrop.LEEK);
            save.buildings.add(bs);
        }

        SaveSystem.save(save);
    }

    /**
     * Charge une sauvegarde, reconstruit le monde et restaure l'etat du joueur.
     */
    private boolean loadGame() {
        GameSave save = SaveSystem.load();
        if (save == null) return false;

        if (save.mapSize != MAP_SIZE || save.mapOffset != MAP_OFFSET) return false;
        boolean hasNewTerrainFormat = save.terrainTypeNames != null && save.terrainTypeNames.length == MAP_SIZE * MAP_SIZE;
        boolean hasLegacyTerrainFormat = save.terrainTypes != null && save.terrainTypes.length == MAP_SIZE * MAP_SIZE;
        if (!hasNewTerrainFormat && !hasLegacyTerrainFormat) return false;

        int idx = 0;
        for (int y = 0; y < MAP_SIZE; y++) {
            for (int x = 0; x < MAP_SIZE; x++) {
                Terrain.Type type = Terrain.Type.GRASS;
                if (hasNewTerrainFormat) {
                    String typeName = save.terrainTypeNames[idx];
                    if (typeName != null) {
                        try {
                            type = Terrain.Type.valueOf(typeName);
                        } catch (Exception ignored) {
                            type = Terrain.Type.GRASS;
                        }
                    }
                } else {
                    int ord = save.terrainTypes[idx];
                    type = mapLegacyTerrainOrdinal(ord);
                }
                idx++;
                terrainGrid[x][y] = new Terrain(type);
            }
        }

        playerPos.set(save.playerX, save.playerY);

        money = save.money;
        buildingStockPlanter = save.buildingStockPlanter;
        buildingStockConveyor = save.buildingStockConveyor;
        seedBagsPotato = save.seedBagsPotato;
        seedBagsStrawberry = save.seedBagsStrawberry;
        seedBagsLeek = save.seedBagsLeek;
        carriedPotato = save.carriedPotato;
        carriedStrawberry = save.carriedStrawberry;
        carriedLeek = save.carriedLeek;

        buildings.clear();
        if (save.buildings != null) {
            for (GameSave.BuildingSave bs : save.buildings) {
                try {
                    Building.Type t = Building.Type.valueOf(bs.type);
                    Building b = new Building(t, bs.x, bs.y, bs.rotation);

                    Building.PlanterCrop planterCrop = Building.PlanterCrop.NONE;
                    if (bs.planterCrop != null) {
                        try {
                            planterCrop = Building.PlanterCrop.valueOf(bs.planterCrop);
                        } catch (Exception ignored) {
                            planterCrop = Building.PlanterCrop.NONE;
                        }
                    }
                    b.setPlanterState(planterCrop, bs.growTimerSeconds, bs.planterReady);

                    Building.PlanterCrop heldItem = Building.PlanterCrop.NONE;
                    if (bs.heldItem != null) {
                        try {
                            heldItem = Building.PlanterCrop.valueOf(bs.heldItem);
                        } catch (Exception ignored) {
                            heldItem = Building.PlanterCrop.NONE;
                        }
                    }
                    b.setConveyorState(heldItem, bs.heldAmount, bs.transportTimer);

                    b.setHQStock(Building.PlanterCrop.POTATO, bs.hqPotatoes);
                    b.setHQStock(Building.PlanterCrop.STRAWBERRY, bs.hqStrawberries);
                    b.setHQStock(Building.PlanterCrop.LEEK, bs.hqLeeks);

                    buildings.add(b);
                } catch (Exception ignored) {

                }
            }
        }

        ensureCoreBuildings();
        updateStatsLabel();
        cancelSelection();
        return true;
    }

    /**
     * Convertit les anciens identifiants de terrain de sauvegarde vers le format actuel.
     */
    private Terrain.Type mapLegacyTerrainOrdinal(int legacyOrdinal) {
        if (legacyOrdinal == 2) return Terrain.Type.TILLED;

        return Terrain.Type.GRASS;
    }

    /**
     * Met a jour les convoyeurs: alimentation, progression et depot vers le QG.
     */
    private void updateConveyors(float dt) {
        for (Building b : buildings) {
            if (!b.isConveyor()) continue;

            if (!b.hasItem()) {
                tryFeedConveyor(b);
            }

            if (b.hasItem() && b.getTransportProgress() >= 1f) {
                int frontX = getFrontGridX(b);
                int frontY = getFrontGridY(b);
                Building front = getBuildingCoveringGridCell(frontX, frontY);

                if (front != null && front.isHQ() && canConveyorOutputToHQSide(b, front, frontX)) {
                    Building.ConveyedItem conveyedItem = b.takeConveyedItem();
                    if (conveyedItem.crop != Building.PlanterCrop.NONE && conveyedItem.amount > 0) {
                        front.addToHQStock(conveyedItem.crop, conveyedItem.amount);
                    }
                }
            }
        }
    }

    /**
     * Tente d'alimenter un convoyeur depuis un voisin convoyeur ou une jardiniere prete.
     */
    private void tryFeedConveyor(Building conveyor) {
        int[][] neighbors = new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        for (int[] dir : neighbors) {
            int nx = conveyor.getGridX() + dir[0];
            int ny = conveyor.getGridY() + dir[1];
            Building neighbor = getBuildingAtGridCell(nx, ny);
            if (neighbor == null || !neighbor.isConveyor()) continue;
            if (!neighbor.hasItem() || neighbor.getTransportProgress() < 1f) continue;

            if (getFrontGridX(neighbor) == conveyor.getGridX() && getFrontGridY(neighbor) == conveyor.getGridY()) {
                Building.ConveyedItem conveyedItem = neighbor.takeConveyedItem();
                int entryDirection = getEntryDirectionFromSource(neighbor.getGridX(), neighbor.getGridY(), conveyor);
                conveyor.receiveItem(conveyedItem.crop, conveyedItem.amount, entryDirection);
                return;
            }
        }

        for (int[] dir : neighbors) {
            int nx = conveyor.getGridX() + dir[0];
            int ny = conveyor.getGridY() + dir[1];
            Building neighbor = getBuildingAtGridCell(nx, ny);
            if (neighbor != null && neighbor.isPlanter() && !neighbor.isPlanterEmpty() && neighbor.isPlanterReady()) {
                Building.PlanterCrop crop = neighbor.harvest();
                int entryDirection = getEntryDirectionFromSource(neighbor.getGridX(), neighbor.getGridY(), conveyor);
                conveyor.receiveItem(crop, Building.getYieldFor(crop), entryDirection);
                playSfx(plantationOutSound);
                return;
            }
        }
    }

    /**
     * Charge un effet sonore depuis les assets et retourne null en cas d'echec.
     */
    private Sound loadSound(String path) {
        try {
            if (Gdx.files.internal(path).exists()) {
                return Gdx.audio.newSound(Gdx.files.internal(path));
            }
            System.out.println("Fichier son introuvable: " + path);
        } catch (Exception e) {
            System.out.println("Erreur chargement son " + path + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * Joue un effet sonore a volume standard s'il est disponible.
     */
    private void playSfx(Sound sound) {
        if (sound == null) return;
        sound.play(SFX_VOLUME);
    }

    /**
     * Determine de quel cote un item entre dans un convoyeur selon sa source.
     */
    private int getEntryDirectionFromSource(int sourceGridX, int sourceGridY, Building targetConveyor) {
        int dx = sourceGridX - targetConveyor.getGridX();
        int dy = sourceGridY - targetConveyor.getGridY();

        if (dx == -1 && dy == 0) return 3;
        if (dx == 1 && dy == 0) return 1;
        if (dx == 0 && dy == -1) return 2;
        if (dx == 0 && dy == 1) return 0;
        return getOppositeDirection(targetConveyor.getRotation());
    }

    /**
     * Calcule la position visuelle d'un item sur un convoyeur selon son avancement.
     */
    private Vector2 getConveyorItemWorldPosition(Building conveyor, Vector2 outPos) {
        float wx = conveyor.getGridX() - MAP_OFFSET;
        float wy = conveyor.getGridY() - MAP_OFFSET;

        int exitDirection = normalizeDirection(conveyor.getRotation());
        int entryDirection = conveyor.getEntryDirection();
        if (entryDirection < 0) entryDirection = getOppositeDirection(exitDirection);

        float progress = MathUtils.clamp(conveyor.getTransportProgress(), 0f, 1f);
        float localX;
        float localY;

        if (progress <= 0.5f) {
            float t = progress * 2f;
            localX = MathUtils.lerp(getConveyorEdgeOffsetX(entryDirection), 0.25f, t);
            localY = MathUtils.lerp(getConveyorEdgeOffsetY(entryDirection), 0.25f, t);
        } else {
            float t = (progress - 0.5f) * 2f;
            localX = MathUtils.lerp(0.25f, getConveyorEdgeOffsetX(exitDirection), t);
            localY = MathUtils.lerp(0.25f, getConveyorEdgeOffsetY(exitDirection), t);
        }

        return outPos.set(wx + localX, wy + localY);
    }

    /**
     * Retourne l'offset X local correspondant au bord d'entree/sortie d'un convoyeur.
     */
    private float getConveyorEdgeOffsetX(int direction) {
        if (direction == 1) return 0.75f;
        if (direction == 3) return -0.25f;
        return 0.25f;
    }

    /**
     * Retourne l'offset Y local correspondant au bord d'entree/sortie d'un convoyeur.
     */
    private float getConveyorEdgeOffsetY(int direction) {
        if (direction == 0) return 0.75f;
        if (direction == 2) return -0.25f;
        return 0.25f;
    }

    /**
     * Normalise une direction sur 4 orientations (0..3).
     */
    private int normalizeDirection(int direction) {
        int normalized = direction % 4;
        if (normalized < 0) normalized += 4;
        return normalized;
    }

    /**
     * Retourne la direction opposee (rotation + 180 degres).
     */
    private int getOppositeDirection(int direction) {
        return normalizeDirection(direction + 2);
    }

    /**
     * Calcule la coordonnee X de la case situee devant un batiment selon sa rotation.
     */
    private int getFrontGridX(Building b) {
        int x = b.getGridX();
        if (b.getRotation() == 1) x += 1;
        if (b.getRotation() == 3) x -= 1;
        return x;
    }

    /**
     * Calcule la coordonnee Y de la case situee devant un batiment selon sa rotation.
     */
    private int getFrontGridY(Building b) {
        int y = b.getGridY();
        if (b.getRotation() == 0) y += 1;
        if (b.getRotation() == 2) y -= 1;
        return y;
    }

    /**
     * Verifie qu'un convoyeur injecte bien sur un cote valide du QG.
     */
    private boolean canConveyorOutputToHQSide(Building conveyor, Building hq, int hqCellX) {
        if (conveyor.getRotation() == 1) {
            return hqCellX == hq.getGridX();
        }
        if (conveyor.getRotation() == 3) {
            return hqCellX == hq.getGridX() + hq.getType().width - 1;
        }
        return false;
    }

    /**
     * Retourne le batiment dont l'ancre est exactement sur la case grille donnee.
     */
    private Building getBuildingAtGridCell(int gridX, int gridY) {
        for (Building b : buildings) {
            if (b.getGridX() == gridX && b.getGridY() == gridY) {
                return b;
            }
        }
        return null;
    }

    /**
     * Retourne le batiment dont l'emprise couvre la case grille donnee.
     */
    private Building getBuildingCoveringGridCell(int gridX, int gridY) {
        for (Building b : buildings) {
            int bx = b.getGridX();
            int by = b.getGridY();
            int bw = b.getType().width;
            int bh = b.getType().height;
            if (gridX >= bx && gridX < bx + bw && gridY >= by && gridY < by + bh) {
                return b;
            }
        }
        return null;
    }

    /**
     * Retourne la couleur de rendu pour une culture mature/prete.
     */
    private Color getCropReadyColor(Building.PlanterCrop crop) {
        if (crop == Building.PlanterCrop.POTATO) return new Color(0.8f, 0.65f, 0.35f, 1f);
        if (crop == Building.PlanterCrop.STRAWBERRY) return new Color(0.9f, 0.2f, 0.2f, 1f);
        if (crop == Building.PlanterCrop.LEEK) return new Color(0.82f, 1f, 0.9f, 1f);
        return Color.WHITE;
    }

    /**
     * Retourne la couleur de filtre translucide indiquant qu'une culture est prete.
     */
    private Color getCropReadyOverlayColor(Building.PlanterCrop crop) {
        if (crop == Building.PlanterCrop.POTATO) return new Color(1f, 0.95f, 0.25f, 0.6f);
        if (crop == Building.PlanterCrop.STRAWBERRY) return new Color(1f, 0.15f, 0.15f, 0.6f);
        if (crop == Building.PlanterCrop.LEEK) return new Color(0.85f, 1f, 0.9f, 0.6f);
        return new Color(1f, 1f, 1f, 0.5f);
    }

    /**
     * Selectionne la texture de convoyeur adaptee a son orientation.
     */
    private Texture getConveyorTextureForRotation(int rotation) {
        int normalized = normalizeDirection(rotation);
        if (normalized == 0) return conveyorTopTexture;
        if (normalized == 1) return conveyorRightTexture;
        if (normalized == 2) return conveyorBottomTexture;
        return conveyorLeftTexture;
    }

    /**
     * Retourne la texture de la recolte (produit) pour une culture donnee.
     */
    private Texture getCropTexture(Building.PlanterCrop crop) {
        if (crop == Building.PlanterCrop.POTATO) return potatoTexture;
        if (crop == Building.PlanterCrop.STRAWBERRY) return strawberryTexture;
        if (crop == Building.PlanterCrop.LEEK) return leekTexture;
        return null;
    }

    /**
     * Retourne la texture du sac de graines associe a une culture.
     */
    private Texture getCropBagTexture(Building.PlanterCrop crop) {
        if (crop == Building.PlanterCrop.POTATO) return potatoBagTexture;
        if (crop == Building.PlanterCrop.STRAWBERRY) return strawberryBagTexture;
        if (crop == Building.PlanterCrop.LEEK) return leekBagTexture;
        return null;
    }

    /**
     * Retourne la couleur de rendu d'une culture en croissance (non prete).
     */
    private Color getCropGrowingColor(Building.PlanterCrop crop) {
        if (crop == Building.PlanterCrop.POTATO) return new Color(0.55f, 0.45f, 0.2f, 1f);
        if (crop == Building.PlanterCrop.STRAWBERRY) return new Color(0.55f, 0.15f, 0.15f, 1f);
        if (crop == Building.PlanterCrop.LEEK) return new Color(0.2f, 0.55f, 0.2f, 1f);
        return new Color(0.4f, 0.4f, 0.4f, 1f);
    }

    @Override
    /**
     * Met a jour les viewports lors d'un redimensionnement et recentre la pause.
     */

    public void resize(int width, int height) {
        viewport.update(width, height);
        uiStage.getViewport().update(width, height, true);


        if (pauseWindow != null) {
            pauseWindow.setPosition(
                    width / 2f - pauseWindow.getWidth() / 2f,
                    height / 2f - pauseWindow.getHeight() / 2f
            );
        }

    }

    @Override
    /**
     * Sauvegarde si necessaire puis libere toutes les ressources graphiques, audio et UI.
     */
    public void dispose() {
        if (currentState == GameState.PLAYING || currentState == GameState.PAUSED) {
            saveGame();
        }

        batch.dispose();
        shapeRenderer.dispose();

        if (playerAnimationController != null) playerAnimationController.dispose();

        if (tileGrass != null) tileGrass.dispose();
        if (tileTilled != null) tileTilled.dispose();
        if (hqTexture != null) hqTexture.dispose();
        if (auctionTexture != null) auctionTexture.dispose();
        if (potatoTexture != null) potatoTexture.dispose();
        if (strawberryTexture != null) strawberryTexture.dispose();
        if (leekTexture != null) leekTexture.dispose();
        if (potatoBagTexture != null) potatoBagTexture.dispose();
        if (strawberryBagTexture != null) strawberryBagTexture.dispose();
        if (leekBagTexture != null) leekBagTexture.dispose();
        if (planterTexture != null) planterTexture.dispose();
        if (planterPotatoTexture != null) planterPotatoTexture.dispose();
        if (planterStrawberryTexture != null) planterStrawberryTexture.dispose();
        if (planterLeekTexture != null) planterLeekTexture.dispose();
        if (conveyorTopTexture != null) conveyorTopTexture.dispose();
        if (conveyorRightTexture != null) conveyorRightTexture.dispose();
        if (conveyorBottomTexture != null) conveyorBottomTexture.dispose();
        if (conveyorLeftTexture != null) conveyorLeftTexture.dispose();
        if (gameLogoTexture != null) gameLogoTexture.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();

        if (backgroundMusic != null) backgroundMusic.dispose();
        if (buyItemSound != null) buyItemSound.dispose();
        if (sellItemSound != null) sellItemSound.dispose();
        if (plantationInSound != null) plantationInSound.dispose();
        if (plantationOutSound != null) plantationOutSound.dispose();
        if (planterPlacementSound != null) planterPlacementSound.dispose();
        if (conveyorPlacementSound != null) conveyorPlacementSound.dispose();
        if (tillSound != null) tillSound.dispose();
        if (clearSound != null) clearSound.dispose();

        uiStage.dispose();
        skin.dispose();
    }
}
