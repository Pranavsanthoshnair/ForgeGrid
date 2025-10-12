package com.forgegrid.ui;

import javax.sound.sampled.*;

/**
 * Lightweight sound utility without external assets. Generates short tones
 * using Java Sound so we can signal success/skip/error.
 */
public final class Sound {

    private Sound() {}

    public static void playSuccess() {
        // Two short rising beeps
        playTone(880, 120, -10.0f);
        sleep(30);
        playTone(1100, 140, -10.0f);
    }

    public static void playSkip() {
        // Short mid beep
        playTone(650, 160, -8.0f);
    }

    public static void playError() {
        // Descending two-tone
        playTone(500, 140, -6.0f);
        sleep(30);
        playTone(350, 180, -6.0f);
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    /**
     * Generate and play a sine tone on a background thread.
     * @param hz frequency
     * @param msec duration
     * @param gainDb negative values reduce volume (e.g. -6.0f)
     */
    public static void playTone(int hz, int msec, float gainDb) {
        new Thread(() -> {
            try {
                float sampleRate = 44100f;
                byte[] buf = new byte[(int) (msec * sampleRate / 1000)];
                for (int i = 0; i < buf.length; i++) {
                    double angle = i / (sampleRate / hz) * 2.0 * Math.PI;
                    buf[i] = (byte) (Math.sin(angle) * 127);
                }
                AudioFormat af = new AudioFormat(sampleRate, 8, 1, true, false);
                try (SourceDataLine sdl = AudioSystem.getSourceDataLine(af)) {
                    sdl.open(af);
                    // Reduce initial pop by ramping up gain after start
                    if (sdl.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                        FloatControl gain = (FloatControl) sdl.getControl(FloatControl.Type.MASTER_GAIN);
                        float clamped = Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), gainDb));
                        gain.setValue(clamped);
                    }
                    sdl.start();
                    sdl.write(buf, 0, buf.length);
                    sdl.drain();
                }
            } catch (Exception ignored) {}
        }, "sound-tone").start();
    }
}


