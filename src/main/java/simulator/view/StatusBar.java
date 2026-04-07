package simulator.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

class StatusBar extends JPanel implements EcoSysObserver {

    private JLabel timeLabel;
    private JLabel totalAnimalsLabel;
    private JLabel sheepLabel;
    private JLabel wolfLabel;
    private JLabel dimensionesLabel;

    StatusBar(Controller ctrl) {
        initGUI();
        ctrl.addObserver(this);
    }

    private void initGUI() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.setBorder(BorderFactory.createBevelBorder(1));

        timeLabel = new JLabel("Time: 0.00");
        timeLabel.setPreferredSize(new Dimension(100,200));
        this.add(timeLabel);

        this.add(new JSeparator(SwingConstants.VERTICAL));

        totalAnimalsLabel = new JLabel("Total: 0");
        totalAnimalsLabel.setPreferredSize(new Dimension(80, 20));
        this.add(totalAnimalsLabel);

        this.add(new JSeparator(SwingConstants.VERTICAL));

        sheepLabel = new JLabel("Sheep: 0");
        sheepLabel.setPreferredSize(new Dimension(80, 20));
        this.add(sheepLabel);

        wolfLabel = new JLabel("Wolves: 0");
        wolfLabel.setPreferredSize(new Dimension(80, 20));
        this.add(wolfLabel);

        dimensionesLabel = new JLabel("Dimensiones: ");
        dimensionesLabel.setPreferredSize(new Dimension(80, 20));
        this.add(dimensionesLabel);
        
        JSeparator s = new JSeparator(JSeparator.VERTICAL);
        s.setPreferredSize(new Dimension(10, 20));
        this.add(s);
    }

    private void update(double time, List<AnimalInfo> animals, MapInfo map) {
        timeLabel.setText(String.format("Time: %.2f", time));
        
        int total = animals.size();
        int sheepCount = 0;
        int wolfCount = 0;


        for (AnimalInfo a : animals) {
            if (a.getGeneticCode().equalsIgnoreCase("Sheep")) {
                sheepCount++;
            } else if (a.getGeneticCode().equalsIgnoreCase("Wolf")) {
                wolfCount++;
            }
        }

        int row = map.getRows();
        int col = map.getCols();
        int height = map.getHeight();
        int width = map.getWidth();

        totalAnimalsLabel.setText("Total: " + total);
        sheepLabel.setText("Sheep: " + sheepCount);
        wolfLabel.setText("Wolves: " + wolfCount);
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
