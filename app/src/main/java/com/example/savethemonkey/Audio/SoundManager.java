package com.example.savethemonkey.Audio;
import android.content.Context;
import android.media.MediaPlayer;

import com.example.savethemonkey.R;

public class SoundManager {
    private MediaPlayer clickSound;
    private MediaPlayer swiperCollisionSound;
    private MediaPlayer bagTouchSound;

    public SoundManager(Context context) {
        clickSound = MediaPlayer.create(context, R.raw.click_sound);
        swiperCollisionSound = MediaPlayer.create(context, R.raw.swiper_collision_sound);
        bagTouchSound = MediaPlayer.create(context, R.raw.bag_touch_sound);
    }

    public void playClickSound() {
        if (clickSound != null) {
            clickSound.start();
        }
    }

    public void playSwiperCollisionSound() {
        if (swiperCollisionSound != null) {
            swiperCollisionSound.start();
        }
    }

    public void playBagTouchSound() {
        if (bagTouchSound != null) {
            bagTouchSound.start();
        }
    }

    // Call this method when your activity is getting destroyed
    public void releaseResources() {
        if (clickSound != null) {
            clickSound.release();
        }
        if (swiperCollisionSound != null) {
            swiperCollisionSound.release();
        }
        if (bagTouchSound != null) {
            bagTouchSound.release();
        }
    }
}
