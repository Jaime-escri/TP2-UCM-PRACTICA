package simulator.view;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import org.json.JSONObject;
import org.json.JSONTokener;
import simulator.control.Controller;
import simulator.launcher.Main;

class ControlPanel extends JPanel {

  private Controller ctrl;
  private ChangeRegionsDialog changeRegionsDialog;
  private AgeLimitStatistics ageLimitStatistics;

  private JToolBar toolBar;
  private JFileChooser fc;
  private boolean stopped = true; // utilizado en los botones de run/stop
  private JButton quitButton;

  // TODO añade más atributos aquí …
  JButton open;
  JButton viewer;
  JButton regions;
  JButton run;
  JButton stop;
  JButton stats;
  JTextField dTime; //delta-time
  JSpinner nPasos; //número de pasos

  ControlPanel(Controller ctrl) {
    this.ctrl = ctrl;
    initGUI();
  }

  private void initGUI() {
    setLayout(new BorderLayout());
    toolBar = new JToolBar();
    add(toolBar, BorderLayout.PAGE_START);

    // TODO crear los diferentes botones/atributos y añadirlos a la toolBar.
    //      Todos ellos han de tener su correspondiente tooltip. Puedes utilizar
    //      this.toolaBar.addSeparator() para añadir la línea de separación vertical
    //      entre las componentes que lo necesiten.
    
    //Open Button
    this.open = new JButton();
    this.open.setToolTipText("Open");
    this.open.setIcon(new ImageIcon(getClass().getClassLoader().getResource("open.png")));
    this.open.addActionListener((e) -> {
        int result = fc.showOpenDialog(ViewUtils.getWindow(this));
        if(result == JFileChooser.APPROVE_OPTION){
            try{
                JSONObject aux = new JSONObject(new JSONTokener(new FileInputStream(fc.getSelectedFile())));
                ctrl.reset(aux.getInt("cols"), aux.getInt("rows"), aux.getInt("width"), aux.getInt("height"));
                ctrl.loadData(aux);
            }catch(Exception ex){
                ViewUtils.showErrorMsg(ex.getMessage());
            }
        }
    });
    this.toolBar.add(open);

    //Viewer Button
    this.viewer = new JButton();
    this.viewer.setToolTipText("Viewer");
    this.viewer.setIcon(new ImageIcon(getClass().getClassLoader().getResource("viewer.png")));
    this.viewer.addActionListener((e)-> {
        MapWindow map_window = new MapWindow(ViewUtils.getWindow(this), ctrl);

    });
    this.toolBar.add(viewer);

    //Regions Button
    this.regions = new JButton();
    this.regions.setToolTipText("Regions");
    this.regions.setIcon(new ImageIcon(getClass().getClassLoader().getResource("regions.png")));
    this.regions.addActionListener((e)->{
        changeRegionsDialog.open(ViewUtils.getWindow(this));
    });
    this.toolBar.add(regions);

    //Run Button
    this.nPasos = new JSpinner(new SpinnerNumberModel(1000, 1, 10000, 1));
    toolBar.add(nPasos);
    this.dTime = new JTextField(String.valueOf(Main.deltaTime));
    toolBar.add(dTime);
    this.run = new JButton();
    this.run.setToolTipText("Run");
    this.run.setIcon(new ImageIcon(getClass().getClassLoader().getResource("run.png")));
    this.run.addActionListener((e)->{
        open.setEnabled(false);
        viewer.setEnabled(false);
        regions.setEnabled(false);
        run.setEnabled(false);
        quitButton.setEnabled(false);

        this.stopped = false;

        double aux = Double.parseDouble(dTime.getText());
        int n = (int) nPasos.getValue();
        runSim(n , aux);
    });
    this.toolBar.add(run);

    //Stop Button
    this.stop = new JButton();
    this.stop.setToolTipText("Stop");
    this.stop.setIcon(new ImageIcon(getClass().getClassLoader().getResource("stop.png")));
    this.stop.addActionListener((e)->{
        this.stopped = true;
    });
    this.toolBar.add(stop);


    // Quit Button
    this.toolBar.add(Box.createGlue()); // this aligns the button to the right
    this.toolBar.addSeparator();
    this.quitButton = new JButton();
    this.quitButton.setToolTipText("Quit");
    this.quitButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("exit.png")));
    this.quitButton.addActionListener((e) -> ViewUtils.quit(this));
    this.toolBar.add(quitButton);

    //Stats Button
    this.stats = new JButton();
    this.stats.setToolTipText("Stats");
    this.stats.setIcon(new ImageIcon(getClass().getClassLoader().getResource("stats.png")));
    this.stats.addActionListener((e)-> {
        ageLimitStatistics.open(ViewUtils.getWindow(this));
    });
    this.toolBar.add(this.stats);

    this.fc = new JFileChooser();
    this.fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/resources/examples"));

    this.changeRegionsDialog = new ChangeRegionsDialog(ctrl);
    this.ageLimitStatistics = new AgeLimitStatistics(ctrl);

    
    
    

  }
  
  private void runSim(int n, double dt) {
      if (n > 0 && !this.stopped) {
        try {
              this.ctrl.advance(dt);
              SwingUtilities.invokeLater(() -> runSim(n - 1, dt));
        } catch (Exception e) {
          ViewUtils.showErrorMsg(e.getMessage());
          open.setEnabled(true);
          viewer.setEnabled(true);
          regions.setEnabled(true);
          run.setEnabled(true);
          quitButton.setEnabled(true);
          run.setEnabled(true);
          this.stopped = true;
        }
      } else {
        open.setEnabled(true);
        viewer.setEnabled(true);
        regions.setEnabled(true);
        run.setEnabled(true);
        quitButton.setEnabled(true);
        run.setEnabled(true);
        this.stopped = true;
      }
    }
}
