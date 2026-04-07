package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

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

        // ControlPanel en la parte superior
        ControlPanel controlPanel = new ControlPanel(ctrl);
        mainPanel.add(controlPanel, BorderLayout.PAGE_START);

        // StatusBar en la parte inferior (¡Añadido!)
        StatusBar statusBar = new StatusBar(ctrl);
        mainPanel.add(statusBar, BorderLayout.PAGE_END);

        // Panel central para las tablas
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Tabla de especies
        SpeciesTableModel speciesTableModel = new SpeciesTableModel(ctrl);
        InfoTable speciesTable = new InfoTable("Species", speciesTableModel);
        speciesTable.setPreferredSize(new Dimension(500, 250));
        contentPanel.add(speciesTable);

        // Tabla de regiones
        RegionsTableModel regionsTableModel = new RegionsTableModel(ctrl);
        InfoTable regionsTable = new InfoTable("Regions", regionsTableModel);
        regionsTable.setPreferredSize(new Dimension(500, 250));
        contentPanel.add(regionsTable);

        // Gestión del cierre de ventana
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ViewUtils.quit(MainWindow.this);
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        pack();
        setLocationRelativeTo(null); 
        setVisible(true);
    }
}