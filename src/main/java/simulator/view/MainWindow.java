package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import simulator.control.Controller;

public class MainWindow extends JFrame {

  private Controller ctrl;

  public MainWindow(Controller ctrl) {
    super("[ECOSYSTEM SIMULATOR]");
    this.ctrl = ctrl;
    initGUI();
  }

  private void initGUI() {
    JPanel mainPanel = new JPanel(new BorderLayout());
    setContentPane(mainPanel);

    // TODO crear ControlPanel y añadirlo en PAGE_START de mainPanel
    ControlPanel cp = new ControlPanel(ctrl);
    mainPanel.add(cp, BorderLayout.PAGE_START);

    // TODO crear StatusBar y añadirlo en PAGE_END de mainPanel
    StatusBar sb = new StatusBar(ctrl);
    mainPanel.add(sb, BorderLayout.PAGE_END);

    // Definición del panel de tablas (usa un BoxLayout vertical)
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    mainPanel.add(contentPanel, BorderLayout.CENTER);

    // TODO crear la tabla de especies y añadirla a contentPanel.
    //      Usa setPreferredSize(new Dimension(500, 250)) para fijar su tamaño
    SpeciesTableModel stm = new SpeciesTableModel(ctrl);
    InfoTable it_stm = new InfoTable("Species", stm);
    it_stm.setPreferredSize(new Dimension(500, 250));
    contentPanel.add(it_stm);
    // TODO crear la tabla de regiones.
    //      Usa setPreferredSize(new Dimension(500, 250)) para fijar su tamaño
    RegionTableModel rtm = new RegionTableModel(ctrl);
    InfoTable it_rtm = new InfoTable("Regions", rtm);
    it_rtm.setPreferredSize(new Dimension(500, 250));
    contentPanel.add(it_rtm);

    // TODO llama a ViewUtils.quit(MainWindow.this) en el método windowClosing
    addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
            ViewUtils.quit(MainWindow.this);
        }
    });

    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    pack();
    setVisible(true);
   }
}
