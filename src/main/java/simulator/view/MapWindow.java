package simulator.view;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

class MapWindow extends JFrame implements EcoSysObserver {

  private Controller ctrl;
  private AbstractMapViewer viewer;
  private Frame parent;
  private MapViewer map_viewer;

  MapWindow(Frame parent, Controller ctrl) {
    super("[MAP VIEWER]");
    this.ctrl = ctrl;
    this.parent = parent;
    intiGUI();
    // TODO registrar this como observador
    ctrl.addObserver(this);
  }

  private void intiGUI() {
    JPanel mainPanel = new JPanel(new BorderLayout());
    // TODO poner contentPane como mainPanel
    setContentPane(mainPanel);
    // TODO crear el viewer y añadirlo a mainPanel (en el centro)
    this.map_viewer = new MapViewer();
    mainPanel.add(map_viewer, BorderLayout.CENTER);

    // TODO en el método windowClosing, eliminar ‘MapWindow.this’ de los
    //      observadores
    
    addWindowListener(new WindowAdapter() { 
        public void windowClosing(WindowEvent e){
            ctrl.removeObserver(MapWindow.this);
        }
    });

    pack();

    if (this.parent != null)
      setLocation(
        this.parent.getLocation().x + parent.getWidth() / 2 - getWidth() / 2,
        this.parent.getLocation().y + parent.getHeight() / 2 - getHeight() / 2);
      setResizable(false);
      setVisible(true);
  }
  // TODO otros métodos van aquí….

  @Override
  public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
    SwingUtilities.invokeLater(() -> {
        map_viewer.reset(time, map, animals);
        pack();
    });
  }

  @Override
  public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
    SwingUtilities.invokeLater(() -> {
        onRegister(time, map, animals);
    });
  }

  @Override
  public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
    
  }

  @Override
  public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
    
  }

  @Override
  public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
    SwingUtilities.invokeLater(() -> {
        map_viewer.update(animals, time);
    });
  }
}
