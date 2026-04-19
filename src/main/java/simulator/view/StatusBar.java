package simulator.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import netscape.javascript.JSException;
import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

class StatusBar extends JPanel implements EcoSysObserver {

    private JLabel timeLabel;
    private JLabel totalAnimalsLabel;
    private JLabel dimensionesLabel;

    StatusBar(Controller ctrl) {
        initGUI();
        ctrl.addObserver(this);
    }

    private void initGUI() {
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        this.setBackground(Color.LIGHT_GRAY);
        this.setPreferredSize(new Dimension(0, 30));


        //Panel izquierdo (time, total, sheep)
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);

        timeLabel = new JLabel("Time: 0.00");
        leftPanel.add(timeLabel);

        JSeparator sep1 = new JSeparator(SwingConstants.VERTICAL);
        sep1.setPreferredSize(new Dimension(5,20));
        leftPanel.add(sep1);

        totalAnimalsLabel = new JLabel("Total Animals: 0");
        leftPanel.add(totalAnimalsLabel);

        JSeparator sep2 = new JSeparator(SwingConstants.VERTICAL);
        sep2.setPreferredSize(new Dimension(5,20));
        leftPanel.add(sep2);

        dimensionesLabel = new JLabel("Dimensiones: ");
        leftPanel.add(dimensionesLabel);
        
        JSeparator sep5 = new JSeparator(SwingConstants.VERTICAL);
        sep5.setPreferredSize(new Dimension(5,20));
        leftPanel.add(sep5);
        
        this.add(leftPanel, BorderLayout.CENTER);
    }

    private void update(double time, List<AnimalInfo> animals, MapInfo map) {
        timeLabel.setText(String.format("Time: %.2f", time));
        
        int total = animals.size();
        int row = map.getRows();
        int col = map.getCols();
        int height = map.getHeight();
        int width = map.getWidth();

        totalAnimalsLabel.setText("Total Animals: " + total);
        dimensionesLabel.setText(String.format("Dimensiones: %dx%d (%dx%d)", width, height, col, row));
    }


    public void onRegister(double time, MapInfo map, List<AnimalInfo> animals){
        update(time, animals, map);
    }
    public void onReset(double time, MapInfo map, List<AnimalInfo> animals){
        update(time, animals, map);
    }
    public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a){
        update(time, animals, map);
    }
    public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {}
    public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt){
        update(time, animals, map);
    }
}
