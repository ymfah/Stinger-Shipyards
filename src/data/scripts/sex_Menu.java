package data.scripts;

import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;

import java.util.List;
import java.util.Random;

public class sex_Menu implements EveryFrameCombatPlugin {

    private SpriteAPI[] banners;
    private int currentBannerIndex = 0;
    private float bannerSwitchTimer = 0f;
    private float bannerSwitchInterval = 8f; // 8 seconds
    private float fadeDuration = 1.5f; // 1.5 seconds for fade in/out
    private float currentBannerAlpha = 1f;

    @Override
    public void processInputPreCoreControls(float amount, List<InputEventAPI> events) {

    }

    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        // Check if the game is in the TITLE state
        if (Global.getCurrentState() == GameState.TITLE) {
            bannerSwitchTimer += amount;

            // Check if banners is not null and not empty
            if (banners != null && banners.length > 0) {
                // Check if it's time to switch banners
                if (bannerSwitchTimer >= bannerSwitchInterval) {
                    // Reset the timer
                    bannerSwitchTimer = 0f;

                    // Increment the banner index and wrap around if necessary
                    currentBannerIndex = (currentBannerIndex + 1) % banners.length;

                    // Reset the alpha for the current banner
                    currentBannerAlpha = 1f;
                }

                // Update the alpha for smooth cross-fade
                float alphaChangeRate = 1f / fadeDuration;
                currentBannerAlpha = Math.max(0f, currentBannerAlpha - alphaChangeRate * amount);
            }
        }
    }

    @Override
    public void renderInWorldCoords(ViewportAPI viewport) {

    }

    @Override
    public void renderInUICoords(ViewportAPI viewport) {
        // Check if the game is in the TITLE state and in a combat context, I have to do this otherwise game kills itself
        if (Global.getCurrentState() == GameState.TITLE && Global.getCombatEngine() != null) {
            // Render the current banner with alpha
            SpriteAPI currentBanner = getCurrentBanner();
            if (currentBanner != null) {
                currentBanner.setAlphaMult(currentBannerAlpha);
                currentBanner.setSize(Global.getSettings().getScreenWidth(), Global.getSettings().getScreenHeight());
                currentBanner.renderAtCenter(Global.getSettings().getScreenWidth() / 2, Global.getSettings().getScreenHeight() / 2);
            }

            // Render the next banner with alpha
            SpriteAPI nextBanner = getNextBanner();
            if (nextBanner != null) {
                nextBanner.setAlphaMult(1f - currentBannerAlpha);
                nextBanner.setSize(Global.getSettings().getScreenWidth(), Global.getSettings().getScreenHeight());
                nextBanner.renderAtCenter(Global.getSettings().getScreenWidth() / 2, Global.getSettings().getScreenHeight() / 2);
            }
        }
    }

    @Override
    public void init(CombatEngineAPI engine) {
        // Load the banner sprites when the mod is initialized
        banners = new SpriteAPI[6];
        for (int i = 0; i < 6; i++) {
            banners[i] = Global.getSettings().getSprite("menuimage", "sex_banner" + (i + 1));
        }

        // Shuffle the array of banners
        shuffleBanners();
    }

    // Helper method to shuffle the array of banners
    private void shuffleBanners() {
        if (banners != null && banners.length > 1) {
            Random random = new Random();
            for (int i = banners.length - 1; i > 0; i--) {
                int index = random.nextInt(i + 1);
                // Swap the current element with a random one
                SpriteAPI temp = banners[index];
                banners[index] = banners[i];
                banners[i] = temp;
            }
        }
    }


    // Helper method to get the current banner
    private SpriteAPI getCurrentBanner() {
        if (banners != null && banners.length > 0) {
            return banners[currentBannerIndex];
        }
        return null;
    }

    private Random random = new Random();
    private int lastRandomIndex = -1; // Initialize with an invalid value

    private SpriteAPI getNextBanner() {
        if (banners != null && banners.length > 1) {
            // Check if it's time to switch banners
            if (bannerSwitchTimer >= bannerSwitchInterval) {
                // Generate a random number between 0 and 4 and add 1 to make it between 1 and 5
                int nextBannerIndex = random.nextInt(6) + 1;
                // Ensure that the nextBannerIndex is not the same as the currentBannerIndex or the lastRandomIndex
                while (nextBannerIndex == currentBannerIndex || nextBannerIndex == lastRandomIndex) {
                    nextBannerIndex = random.nextInt(6) + 1;
                }
                // Adjust the index to match array indexing (subtract 1)
                nextBannerIndex -= 1;
                lastRandomIndex = nextBannerIndex;
                return banners[nextBannerIndex];
            } else {
                // Return the current next banner without generating a new random index
                int nextBannerIndex = (currentBannerIndex + 1) % banners.length;
                return banners[nextBannerIndex];
            }
        }
        return null;
    }
}