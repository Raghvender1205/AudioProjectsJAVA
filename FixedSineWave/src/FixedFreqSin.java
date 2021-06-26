import java.nio.ByteBuffer;
import javax.sound.sampled.*;

public class FixedFreqSin {
    public static void main(String[] args) throws InterruptedException, LineUnavailableException {
        final int SAMPLING_RATE = 44100;
        final int SAMPLE_RATE = 2;  // Audio Sample size in bytes

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

        // On each pass main loop
    }
}
