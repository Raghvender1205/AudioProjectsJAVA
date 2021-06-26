import java.nio.ByteBuffer;

import javax.sound.sampled.*;

class SampleThread extends Thread {
    final static public int SAMPLING_RATE = 44100;
    final static public int SAMPLE_SIZE = 2;   // Sample Size in bytes
    final static public double BUFFER_DURATION = 0.100;  // About a 100ms buffer
    // Play with the size of this buffer, Making it smaller would speed it up
    // the response to the slider movement.

    // Size in bytes of sine wave samples on each loop pass
    final static public int SINE_PACKET_SIZE = (int)(BUFFER_DURATION * SAMPLING_RATE * SAMPLE_SIZE);

    SourceDataLine line;
    public double fFreq;   // Set the patch Slider
    public boolean bExitThread = false;

    // Get the number of queued samples in the SourceDataLine buffer
    private int getLineSampleCount() {
        return line.getBufferSize() - line.available();
    }

    // Continually fill the audio output buffer whenever it starts to get empty, SINE_PACKET_SIZE / 2 
    // samples at a time, until thread is exited.
    public void run() {
        // Position through the sine wave as a percentage 
        double fCyclePosition = 0;

        // Open up the audio output, using a sampling rate of 44100Mhz, 16 bit samples, mono and big endian byte ordering
        // Ask for a buffer size of at least 2*SINE_PACKET_SIZE
        try {
            AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format, SINE_PACKET_SIZE * 2);

            if (!AudioSystem.isLineSupported(info)) {
                throw new LineUnavailableException();
            }
            
            line = (SourceDataLine)AudioSystem.getLine(info);
            line.open(format);
            line.start();
        } catch (LineUnavailableException e) {
            System.out.println("Line of that type is not available");
            e.printStackTrace();
            System.exit(-1);
        }

        System.out.println("Seperated Line buffer size: " + SINE_PACKET_SIZE * 2);
        System.out.println("Actual line buffer size: " + line.getBufferSize());

        ByteBuffer cBuf = ByteBuffer.allocate(SINE_PACKET_SIZE);
        
        // On Each pass the main loop fills the available free space in the audio buffer
        // Main loop creates audio samples for sine wave, and it runs until we tell the thread to exit
        // Each sample is spaced 1 / SAMPLING_RATE apart in time
        while (bExitThread == false) {
            double fCycleInc = fFreq / SAMPLING_RATE;

            // Generate SINE_PACKET_SIZE samples based on the current fCycleInc from fFreq
            for (int i = 0; i < SINE_PACKET_SIZE / SAMPLE_SIZE; i++) {
                cBuf.putShort((short)(Short.MAX_VALUE * Math.sin(2 * Math.PI * fCyclePosition)));
                
                fCyclePosition += fCycleInc;
                if (fCyclePosition > 1) {
                    fCyclePosition -= 1;
                }
            }

            // Write sine samples to the line buffer
            line.write(cBuf.array(), 0, cBuf.position());

            // Wait until there are less than SINE_PACKET_SIZE samples in the buffer
            try {
                while (getLineSampleCount() > SINE_PACKET_SIZE) {
                    Thread.sleep(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } 
        }
        line.drain();
        line.close();
    } 

    public void exit() {
        bExitThread = true;
    }
}
