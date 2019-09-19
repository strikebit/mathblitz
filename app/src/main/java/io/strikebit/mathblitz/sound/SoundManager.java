package io.strikebit.mathblitz.sound;

import android.content.Context;
import android.media.MediaPlayer;

import com.crashlytics.android.Crashlytics;

import io.strikebit.mathblitz.R;

public class SoundManager {
    private MediaPlayer mediaPlayer;

    public void playCorrectAnswerSound(Context context) {
        try {
            mediaPlayer = MediaPlayer.create(context, R.raw.correct);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                    mp.release();
                }
            });
        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }

    public void playIncorrectAnswerSound(Context context) {
        try {
            mediaPlayer = MediaPlayer.create(context, R.raw.wrong);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                    mp.release();
                }
            });
        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }

    public void playLevelUpSound(Context context) {
        try {
            mediaPlayer = MediaPlayer.create(context, R.raw.level_up);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                    mp.release();
                }
            });
        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }

    public void playGameEndSound(Context context) {
        try {
            mediaPlayer = MediaPlayer.create(context, R.raw.end_game);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                    mp.release();
                }
            });
        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }
}
