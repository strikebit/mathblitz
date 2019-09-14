package io.strikebit.mathblitz.sound;

import android.content.Context;
import android.media.MediaPlayer;

import io.strikebit.mathblitz.R;

public class SoundManager {
    public void playCorrectAnswerSound(Context context) {
        MediaPlayer.create(context, R.raw.correct).start();
    }

    public void playLevelUpSound(Context context) {
        MediaPlayer.create(context, R.raw.level_up).start();
    }
}
