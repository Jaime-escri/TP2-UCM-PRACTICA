package simulator.view;

import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.json.JSONObject;
import java.awt.Dimension;
import java.awt.Frame;
import simulator.control.Controller;
import simulator.launcher.Main;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

class AgeLimitStatistics extends JDialog implements EcoSysObserver {

    private DefaultTableModel dataTableModel;
    private Controller ctrl;
    private double AGE_LIMIT = 3.0;
    private JTextField ageLimitBox;
    private JLabel helpLabel;


    private String[] headers = { "Time", "Young", "Old" };

    public AgeLimitStatistics(Controller ctrl) {
        super((Frame) null, true);
        this.ctrl = ctrl;
        initGUI();
        this.ctrl.addObserver(this);
    }

    private void initGUI() {
        setTitle("Age Limit Statistics");
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setContentPane(mainPanel);

        // TODO crea varios paneles para organizar los componentes visuales en el
        // dialogo, y añadelos al mainpanel. P.ej., uno para el texto de ayuda,
        // uno para la tabla, uno para los combobox, y uno para los botones.
        JPanel helpPanel = new JPanel();
        helpLabel = new JLabel("Age statistics for age limit: " + AGE_LIMIT);
        JPanel tablePanel = new JPanel();
        JPanel comboPanel = new JPanel();
        JPanel buttonPanel = new JPanel();

        mainPanel.add(helpPanel);
        helpPanel.add(helpLabel);
        mainPanel.add(tablePanel);
        mainPanel.add(comboPanel);
        mainPanel.add(buttonPanel);


        this.dataTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.dataTableModel.setColumnIdentifiers(this.headers);

        // TODO crear un JTable que use dataTableModel, y añadirlo al diálogo
        JTable dataTable = new JTable(dataTableModel);
        JScrollPane scroll = new JScrollPane(dataTable);
        tablePanel.add(scroll);

        JLabel ageLimitText = new JLabel("Age Limit");
        ageLimitBox = new JTextField(String.valueOf(AGE_LIMIT));

        buttonPanel.add(ageLimitText);
        buttonPanel.add(ageLimitBox);
        JButton setButton = new JButton("SET");
        setButton.addActionListener((e) -> {
           
           try{
            this.AGE_LIMIT = Double.parseDouble(ageLimitBox.getText());
            if(AGE_LIMIT <= 0){
                ViewUtils.showErrorMsg(setButton, "El valor de Age Limit debe ser mayor que 0");
            }else{
                dataTableModel.setRowCount(0);
                helpLabel.setText("Age statistics for age limit: " + AGE_LIMIT);
                setVisible(false);
            }
           }catch(Exception e1){
                ViewUtils.showErrorMsg("El valor de Age Limit debe ser mayor que 0");
                return;
           }
        });
        buttonPanel.add(setButton);
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener((e) -> {
            setVisible(false);
        });
        buttonPanel.add(closeButton);

        setPreferredSize(new Dimension(700, 400)); // puedes usar otro tamaño
        pack();
        setResizable(false);
        setVisible(false);
    }

    public void open(Frame parent) {
        setLocation(
                parent.getLocation().x + parent.getWidth() / 2 - getWidth() / 2,
                parent.getLocation().y + parent.getHeight() / 2 - getHeight() / 2);
        pack();
        setVisible(true);
    }

    @Override
    public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {

    }

    @Override
    public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
        this.dataTableModel.setRowCount(0);
    }

    @Override
    public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {

    }

    @Override
    public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {

    }

    @Override
    public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
        int youngCounter = 0, oldCounter = 0;
        for(AnimalInfo a: animals){
            if(a.getAge() < AGE_LIMIT)youngCounter++;
            else oldCounter++;
        }
        this.dataTableModel.addRow(new Object[]{time, youngCounter, oldCounter});
    }
}
