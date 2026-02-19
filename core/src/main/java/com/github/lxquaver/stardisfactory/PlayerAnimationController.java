package com.github.lxquaver.stardisfactory;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Gere le chargement et la selection des animations du joueur.
 *
 * Le controleur choisit automatiquement l'animation idle/walk selon la
 * direction et le vecteur de mouvement recu a chaque frame.
 */
public class PlayerAnimationController {

    /**
     * Directions de regard supportees par les sprites.
     */
    private enum Facing {
        DOWN,
        UP,
        SIDE
    }

    /**
     * Textures source des spritesheets.
     */
    private Texture idleDownTexture;
    private Texture idleUpTexture;
    private Texture idleSideTexture;
    private Texture walkDownTexture;
    private Texture walkUpTexture;
    private Texture walkSideTexture;

    /**
     * Animations decoupees depuis les spritesheets.
     */
    private Animation<TextureRegion> idleDownAnimation;
    private Animation<TextureRegion> idleUpAnimation;
    private Animation<TextureRegion> idleSideAnimation;
    private Animation<TextureRegion> walkDownAnimation;
    private Animation<TextureRegion> walkUpAnimation;
    private Animation<TextureRegion> walkSideAnimation;

    /**
     * Etat courant de l'avatar.
     */
    private Facing facing = Facing.DOWN;
    private boolean moving = false;
    private boolean flipX = false;
    private float stateTime = 0f;
    private TextureRegion currentFrame;

    /**
     * Charge toutes les textures et construit les animations.
     */
    public PlayerAnimationController() {
        idleDownTexture = createTexture("character/Idle.png");
        idleUpTexture = createTexture("character/Idle_up.png");
        idleSideTexture = createTexture("character/Idle_side.png");
        walkDownTexture = createTexture("character/Walk_down.png");
        walkUpTexture = createTexture("character/Walk_up.png");
        walkSideTexture = createTexture("character/Walk.png");

        idleDownAnimation = createAnimation(idleDownTexture, 0.22f);
        idleUpAnimation = createAnimation(idleUpTexture, 0.22f);
        idleSideAnimation = createAnimation(idleSideTexture, 0.22f);
        walkDownAnimation = createAnimation(walkDownTexture, 0.12f);
        walkUpAnimation = createAnimation(walkUpTexture, 0.12f);
        walkSideAnimation = createAnimation(walkSideTexture, 0.12f);

        currentFrame = idleDownAnimation.getKeyFrame(0f, true);
    }

    /**
     * Reinitialise l'etat d'animation au spawn/menu.
     */
    public void reset() {
        facing = Facing.DOWN;
        moving = false;
        flipX = false;
        stateTime = 0f;
        currentFrame = idleDownAnimation.getKeyFrame(0f, true);
    }

    /**
     * Met a jour l'orientation et la frame courante selon le mouvement.
     */
    public void update(float dt, float moveX, float moveY) {
        final float epsilon = 0.0001f;
        boolean isMovingNow = Math.abs(moveX) > epsilon || Math.abs(moveY) > epsilon;

        Facing newFacing = facing;
        boolean newFlipX = flipX;

        if (isMovingNow) {
            if (Math.abs(moveY) > Math.abs(moveX)) {
                newFacing = moveY > 0f ? Facing.UP : Facing.DOWN;
            } else {
                newFacing = Facing.SIDE;
                if (moveX > 0f) newFlipX = false;
                else if (moveX < 0f) newFlipX = true;
            }
        }

        if (newFacing != facing || isMovingNow != moving) {
            stateTime = 0f;
        } else {
            stateTime += dt;
        }

        facing = newFacing;
        moving = isMovingNow;
        flipX = newFlipX;
        currentFrame = getCurrentAnimation().getKeyFrame(stateTime, true);
    }

    /**
     * Retourne la frame a dessiner pour cette frame logique.
     */
    public TextureRegion getCurrentFrame() {
        return currentFrame;
    }

    /**
     * Indique si la frame doit etre miroir horizontalement.
     */
    public boolean isFlipX() {
        return flipX;
    }

    /**
     * Libere toutes les textures chargees.
     */
    public void dispose() {
        if (idleDownTexture != null) idleDownTexture.dispose();
        if (idleUpTexture != null) idleUpTexture.dispose();
        if (idleSideTexture != null) idleSideTexture.dispose();
        if (walkDownTexture != null) walkDownTexture.dispose();
        if (walkUpTexture != null) walkUpTexture.dispose();
        if (walkSideTexture != null) walkSideTexture.dispose();
    }

    /**
     * Choisit l'animation active selon l'etat courant.
     */
    private Animation<TextureRegion> getCurrentAnimation() {
        if (moving) {
            if (facing == Facing.UP) return walkUpAnimation;
            if (facing == Facing.SIDE) return walkSideAnimation;
            return walkDownAnimation;
        }

        if (facing == Facing.UP) return idleUpAnimation;
        if (facing == Facing.SIDE) return idleSideAnimation;
        return idleDownAnimation;
    }

    /**
     * Charge une texture de spritesheet en filtrage pixel-perfect.
     */
    private Texture createTexture(String path) {
        Texture texture = new Texture(path);
        texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        return texture;
    }

    /**
     * Decoupe une spritesheet carree et cree une animation en boucle.
     */
    private Animation<TextureRegion> createAnimation(Texture texture, float frameDuration) {
        int frameSize = texture.getHeight();
        int frameCount = Math.max(1, texture.getWidth() / frameSize);

        TextureRegion[] frames = new TextureRegion[frameCount];
        for (int i = 0; i < frameCount; i++) {
            frames[i] = new TextureRegion(texture, i * frameSize, 0, frameSize, frameSize);
        }

        Animation<TextureRegion> animation = new Animation<>(frameDuration, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
        return animation;
    }
}
