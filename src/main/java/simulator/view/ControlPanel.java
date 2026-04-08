package simulator.view;

import java.awt.BorderLayout;
import java.awt.BufferCapabilities;
import java.awt.TextArea;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.text.View;

import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

class ControlPanel extends JPanel implements EcoSysObserver {

  private Controller ctrl;
  private ChangeRegionsDialog changeRegionsDialog;
  private JToolBar toolBar;
  private JFileChooser fc;
  private boolean stopped = true; // utilizado en los botones de run/stop

  private JButton quitButton;
  private JButton runButton;
  private JButton stopButton;
  private JButton openButton;
  private JButton viewerButton;
  private JButton regionsButton;
  private JSpinner stepsSpinner;
  private JTextField dtTextField;
  private JLabel timeLabel;

  ControlPanel(Controller ctrl) {
    this.ctrl = ctrl;
    initGUI();
    this.ctrl.addObserver(this);
  }

  private void initGUI() {
    setLayout(new BorderLayout());
    toolBar = new JToolBar();
    add(toolBar, BorderLayout.PAGE_START);
    //Abrir
    this.openButton = new JButton();
    this.openButton.setToolTipText("Load input file");
    this.openButton.setIcon(loadImage("icons/open.png"));
    this.openButton.addActionListener((e) -> loadFile());
    this.toolBar.add(openButton);

    this.viewerButton = new JButton();
    viewerButton.setToolTipText("Open Map Viewer");
    this.viewerButton.setIcon(loadImage("icons/viewer.png"));
    viewerButton.addActionListener((e) -> {
        new MapWindow(ViewUtils.getWindow(this), ctrl);
    });
    this.toolBar.add(viewerButton);

    this.regionsButton = new JButton();
    regionsButton.setToolTipText("Change Regions");
    this.regionsButton.setIcon(loadImage("icons/regions.png"));
    regionsButton.addActionListener((e) -> {
        this.changeRegionsDialog.open(ViewUtils.getWindow(this));
    });
    this.toolBar.add(regionsButton);

    this.toolBar.addSeparator();

    this.runButton = new JButton();
    runButton.setToolTipText("Run Simulator");
    this.runButton.setIcon(loadImage("icons/run.png"));
    runButton.addActionListener((e) -> {
        this.stopped = false;
        enabledButtons(false);
        int steps = (Integer) this.stepsSpinner.getValue();
        double dt = Double.parseDouble(this.dtTextField.getText());
        runSim(steps, dt);
    });
    this.toolBar.add(runButton);   

    this.stopButton = new JButton();
    stopButton.setToolTipText("Stop Simulation");
    this.stopButton.setIcon(loadImage("icons/stop.png"));
    stopButton.addActionListener((e) -> this.stopped = true);
    this.toolBar.add(stopButton);

    this.toolBar.add(new JLabel("Steps"));
    this.stepsSpinner = new JSpinner(new SpinnerNumberModel(100,1,10000,1));
    this.toolBar.add(stepsSpinner);

    this.toolBar.add(new JLabel("Delta-Time"));
    this.dtTextField = new JTextField("0.03", 5);
    this.toolBar.add(dtTextField);

    this.toolBar.add(new JLabel("Time: "));
    this.timeLabel = new JLabel("0.00");
    this.toolBar.add(timeLabel);
    

    // Quit Button
    this.toolBar.add(Box.createGlue()); // this aligns the button to the right
    this.toolBar.addSeparator();

    this.quitButton = new JButton();
    this.quitButton.setToolTipText("Quit");
    this.quitButton.setIcon(loadImage("icons/exit.png"));
    this.quitButton.addActionListener((e) -> ViewUtils.quit(this));
    this.toolBar.add(quitButton);

    this.fc = new JFileChooser();
    this.fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/resources/examples"));

    this.changeRegionsDialog = new ChangeRegionsDialog(ctrl);

  }
  
    private void runSim(int n, double dt) {
        if (n > 0 && !this.stopped) {
            try {
                this.ctrl.advance(dt);
                SwingUtilities.invokeLater(() -> runSim(n - 1, dt));
            } catch (Exception e) {
                ViewUtils.showErrorMsg(this, "Error durante la simulacion: " + e.getMessage());
                this.stopped = true;
                enabledButtons(true);
                
            }
        } else {
            enabledButtons(true);
            this.stopped = true;
        }
    }

    public void enabledButtons(boolean b){
        this.runButton.setEnabled(b);
        this.openButton.setEnabled(b);
        this.viewerButton.setEnabled(b);
        this.regionsButton.setEnabled(b);
        this.stepsSpinner.setEnabled(b);
        this.dtTextField.setEnabled(b);
    }

    public void loadFile(){
        int returnVal = fc.showOpenDialog(ViewUtils.getWindow(this));

        if(returnVal == JFileChooser.APPROVE_OPTION){
            File file = fc.getSelectedFile();
            try{
                JSONObject jsonInput = new JSONObject(new JSONTokener(new FileInputStream(file)));

                int cols = jsonInput.getInt("cols");
                int rows = jsonInput.getInt("rows");
                int width = jsonInput.getInt("width");
                int height = jsonInput.getInt("height");
                ctrl.reset(cols, rows, width, height);

                ctrl.loadData(jsonInput);
            }catch(Exception e){
                ViewUtils.showErrorMsg(this, "Error al cargar el archivo JSON: " + e.getMessage());
            }
        }
    }

    public void onRegister(double time, MapInfo map, List<AnimalInfo> animals){
        this.timeLabel.setText(String.valueOf(time));
    }
    public void onReset(double time, MapInfo map, List<AnimalInfo> animals){
        this.timeLabel.setText(String.valueOf(time));
        enabledButtons(true);
    }
    public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a){
        
    }
    public void onRegionSet(int row, int col, MapInfo map, RegionInfo r){}
    public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt){
        this.timeLabel.setText(String.valueOf(time));
    }

    private ImageIcon loadImage(String path) {
        java.net.URL url = ClassLoader.getSystemResource(path);
        if (url == null) {
            System.err.println("¡ERROR! No se encuentra el archivo: " + path);
            return null; // O devuelve un icono vacío por defecto
        }
        return new ImageIcon(url);
    }
}
