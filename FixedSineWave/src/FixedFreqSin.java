import java.nio.ByteBuffer;
import javax.sound.sampled.*;

public class FixedFreqSin {
    public static void main(String[] args) throws InterruptedException, LineUnavailableException {
        final int SAMPLING_RATE = 44100;
        final int SAMPLE_SIZE = 2;  // Audio Sample size in bytes

        SourceDataLine line;
        double fFreq = 440;

        // Position through the Sine wave as a %
        double fCyclePosition = 0;

        // Open up audio output, using Sampling rate, 16 bit samples, mono and big endian byte ordering
        AudioFormat format = new AudioFormat(SAMPLING_RATE, 16, 1, true, true);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("Line Matching: " + info + " is not supported.");
            throw new LineUnavailableException();
        }

        line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(format);
        line.start();

        // Make our buffer size match audio system's buffer
        ByteBuffer cBuf = ByteBuffer.allocate(line.getBufferSize());

        int ctSampleTotal = SAMPLING_RATE * 5;

        // On each pass main loop fills the available free space in the audio buffer 
        // It creates a audio samples for sinwave, runs until we tell the thread to exit 
        // Each sample is spaced 1 / SAMPLING_RATE apart in time
        while (ctSampleTotal > 0) {
            double fCycleInc = fFreq / SAMPLING_RATE;    // Fraction of Cycles between samples
            cBuf.clear();

            // How many samples we can add...??
            int ctSamplesThisPass = line.available() / SAMPLE_SIZE;
            for (int i = 0; i < ctSamplesThisPass; i++) {
                cBuf.putShort((short) (Short.MAX_VALUE * Math.sin(2 * Math.PI * fCyclePosition)));

                fCyclePosition += fCycleInc;
                if (fCyclePosition > 1) {
                    fCyclePosition -= 1;
                }

                // Write sine samples to the line buffer, If the audio buffer is full, this will 
                // block until there is room
                line.write(cBuf.array(), 0, cBuf.position());
                ctSampleTotal -= ctSamplesThisPass;   // Update the total number of samples written

                // Wait until the buffer is atleast half empty before we add more
                while (line.getBufferSize()/2  < line.available()) {
                    Thread.sleep(1);
                }

                line.drain();
                line.close();
            }
        }
    }
}
