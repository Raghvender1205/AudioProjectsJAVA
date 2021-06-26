import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import javax.swing.*;

public class SliderFreqSine extends JFrame {
    private SampleThread mThread;
    private JSlider mSliderPitch;

    // Launch App
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    SliderFreqSine frame = new SliderFreqSine();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public SliderFreqSine() {
        // UI with WindowsBuilder
        addWindowListener(new WindowAdapter () {
            @Override
            public void windowClosing(WindowEvent e) {
                mThread.exit();
                System.exit(0);
            }
        });

        setTitle("Slider Frequency Sine Wave Generator");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 783, 166);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout(0, 0));
    
        mSliderPitch = new JSlider();
        mSliderPitch.setName("");
        mSliderPitch.setMinimum(100);
        mSliderPitch.setPaintTicks(true);
        mSliderPitch.setMajorTickSpacing(500);
        mSliderPitch.setMaximum(4100);
        mSliderPitch.setValue(880);
        getContentPane().add(mSliderPitch);

        JLabel lblAdjustPitch = new JLabel("Adjust Pitch");
        lblAdjustPitch.setHorizontalAlignment(SwingConstants.CENTER);
        lblAdjustPitch.setFont(new Font("Tahoma", Font.PLAIN, 18));
        getContentPane().add(lblAdjustPitch, BorderLayout.NORTH);

        // Non-UI 
        mThread = new SampleThread();
        mThread.start();
    }
}