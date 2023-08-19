package com.rebane2001.aimobs;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;

public class AudioRecorder {
    // Format of the audio file
    private final AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

    // Target line to read from
    private TargetDataLine line;

    // Byte array to store the recorded audio
    private ByteArrayOutputStream audioOutputStream;

    // Thread for recording
    private Thread recordingThread;

    // Defines an audio format
    private AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 16;
        int channels = 1; // Mono
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    // Starts recording
    public void startRecording() {
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            // Checks if the system supports the data line
            if (!AudioSystem.isLineSupported(info)) {
                throw new UnsupportedOperationException("Line not supported");
            }

            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start(); // Start capturing

            audioOutputStream = new ByteArrayOutputStream();

            // Thread to continuously read audio data and write to output stream
            recordingThread = new Thread(() -> {
                try (AudioInputStream ais = new AudioInputStream(line)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = ais.read(buffer)) != -1) {
                        audioOutputStream.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            recordingThread.start();

        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }

    // Stops recording and returns an InputStream containing the WAV data
    public InputStream stopRecording() throws IOException {
        if (line != null) {
            line.stop();
            line.close();
        }
        if (recordingThread != null) {
            recordingThread.interrupt();
            recordingThread = null;
        }

        byte[] audioData = audioOutputStream.toByteArray();
        AudioFormat format = getAudioFormat();
        long numFrames = audioData.length / format.getFrameSize();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
        AudioInputStream audioInputStream = new AudioInputStream(byteArrayInputStream, format, numFrames);

        // Write to a WAV file
        File wavFile = new File("audio.wav");
        AudioSystem.write(audioInputStream, fileType, wavFile);

        // Return the AudioInputStream
        return new AudioInputStream(new ByteArrayInputStream(audioData), format, numFrames);
    }
}
