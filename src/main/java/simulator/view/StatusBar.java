package simulator.view;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.FlowLayout;

public class StatusBar extends JPanel implements  EcoSysObserver{
    private JLabel tiempo;
    private JLabel num_animales;
    private JLabel dimensiones;

    public StatusBar(Controller ctrl){
        this.tiempo = new JLabel("0");
        this.num_animales = new JLabel("0");
        this.dimensiones = new JLabel("0");
        add(tiempo);
        add(num_animales);
        add(dimensiones);
        ctrl.addObserver(this);
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.setBorder(BorderFactory.createBevelBorder(1));
    }

    @Override
    public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
        this.tiempo.setText("Tiempo: " + String.valueOf(time));
        this.num_animales.setText(" Numero de animales: " + String.valueOf(animals.size()));
        this.dimensiones.setText(" Columnas: " + String.valueOf(map.getCols()) + 
                                " Filas: " + String.valueOf(map.getRows()) + 
                                " Height: " + String.valueOf(map.getHeight()) + 
                                "Width: " +String.valueOf(map.getWidth()));
    }

    @Override
    public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
        onRegister(time, map, animals);
    }

    @Override
    public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
        onRegister(time, map, animals);
    }

    @Override
    public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {

    }

    @Override
    public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
        onRegister(time, map, animals);
    }

}
