package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.control.Controller;
import simulator.launcher.Main;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

class ChangeRegionsDialog extends JDialog implements EcoSysObserver {

    private DefaultComboBoxModel<String> regionsModel;
    private DefaultComboBoxModel<String> fromRowModel;
    private DefaultComboBoxModel<String> toRowModel;
    private DefaultComboBoxModel<String> fromColModel;
    private DefaultComboBoxModel<String> toColModel;
    private JComboBox<String> comboBox;

    private DefaultTableModel dataTableModel;
    private Controller ctrl;
    private List<JSONObject> regionsInfo;
    private int status;

    private String[] headers = { "Key", "Value", "Description" };

    ChangeRegionsDialog(Controller ctrl) {
        super((Frame)null, true);
        this.ctrl = ctrl;
        initGUI();
        this.ctrl.addObserver(this);
    }

    private void initGUI() {
        setTitle("Change Regions");
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setContentPane(mainPanel);

        JPanel helpPanel = new JPanel();
        JPanel tablePanel = new JPanel();
        JPanel comboPanel = new JPanel();
        JPanel buttonPanel = new JPanel();

        JLabel textLabel = new JLabel();
        textLabel.setText("<html>Select a region type, the rows/cols interval, and provide values for the parameters <br> in the Value Column (default values are used for parameters with no value)</html>");
        helpPanel.add(textLabel);
        mainPanel.add(helpPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        
        
        this.regionsInfo = Main.selectionRegionFactory.getInfo();

        this.dataTableModel = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            if(column != 1) return false;
            else return true;
        }
        };
        this.dataTableModel.setColumnIdentifiers(this.headers);

        JTable dataTable = new JTable(dataTableModel);
        JScrollPane scrollTablePane = new JScrollPane(dataTable);
        tablePanel.add(scrollTablePane);
        mainPanel.add(tablePanel);
        mainPanel.add(Box.createVerticalStrut(10));

        this.regionsModel = new DefaultComboBoxModel<>();


        for(JSONObject jo : this.regionsInfo){
            this.regionsModel.addElement(jo.getString("type"));
        }

        this.comboBox = new JComboBox<>(this.regionsModel);
        this.comboBox.addActionListener(e->{
            updateTableModel();
        });
        comboPanel.add(comboBox);
        
        MapInfo map = ctrl.getMap();
        this.toRowModel = new DefaultComboBoxModel<>();
        this.toColModel = new DefaultComboBoxModel<>();
        this.fromRowModel = new DefaultComboBoxModel<>();
        this.fromColModel = new DefaultComboBoxModel<>();
        setDimensions(map);
        
        JLabel fromRowLabel = new JLabel("From Row");
        JComboBox<String> fromRowBox = new JComboBox<>(this.fromRowModel);
        JLabel toRowLabel = new JLabel("To row");
        JComboBox<String> toRowBox = new JComboBox<>(this.toRowModel);
        JLabel fromColLabel = new JLabel("From Col");
        JComboBox<String> fromColBox = new JComboBox<>(this.fromColModel);
        JLabel toColLabel = new JLabel("To Col");
        JComboBox<String> toColBox = new JComboBox<>(this.toColModel);

        comboPanel.add(fromRowLabel);
        comboPanel.add(fromRowBox);
        comboPanel.add(toRowLabel);
        comboPanel.add(toRowBox);
        comboPanel.add(fromColLabel);
        comboPanel.add(fromColBox);
        comboPanel.add(toColLabel);
        comboPanel.add(toColBox);
        mainPanel.add(comboPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        JButton ok = new JButton("OK");
        JButton cancel = new JButton("CANCEL");

        ok.addActionListener(e -> {
            try {
                int fromRow = Integer.parseInt(fromRowBox.getSelectedItem().toString());
                int toRow = Integer.parseInt(toRowBox.getSelectedItem().toString());
                int fromCol = Integer.parseInt(fromColBox.getSelectedItem().toString());
                int toCol = Integer.parseInt(toColBox.getSelectedItem().toString());

                JSONObject spec = new JSONObject();
                spec.put("type", regionsModel.getSelectedItem());
                JSONObject data = new JSONObject();
                for(int i = 0; i < this.dataTableModel.getRowCount(); i++){
                    String key = this.dataTableModel.getValueAt(i, 0).toString();
                    Object valueObject = this.dataTableModel.getValueAt(i, 1);

                    if(valueObject != null && !valueObject.toString().isEmpty()){
                        double value = Double.parseDouble(valueObject.toString());
                        data.put(key, value);
                    }
                }
                spec.put("data", data);

                JSONArray rows = new JSONArray();
                rows.put(fromRow).put(toRow);
                JSONArray cols = new JSONArray();
                cols.put(fromCol).put(toCol);

                JSONObject region = new JSONObject();
                region.put("row", rows);
                region.put("col", cols);
                region.put("spec", spec);
                JSONArray regionsArray = new JSONArray();
                regionsArray.put(region);
                JSONObject finalJson = new JSONObject();
                finalJson.put("regions", regionsArray);
                ctrl.setRegions(finalJson);
                setVisible(false);
                this.status = 1;
            } catch (Exception ex) {
                ViewUtils.showErrorMsg(ex.getMessage());
            }
        });

        cancel.addActionListener(e -> {
            this.status = 0;
            setVisible(false);
        });

        buttonPanel.add(ok);
        buttonPanel.add(cancel);
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        setPreferredSize(new Dimension(700, 400)); // puedes usar otro tamaño
        updateTableModel();
        pack();
        setResizable(false);
        setVisible(false);
    }

    public void open(Frame parent) {
        this.status = 0;
        setLocation(
        parent.getLocation().x + parent.getWidth() / 2 - getWidth() / 2,
        parent.getLocation().y + parent.getHeight() / 2 - getHeight() / 2);
        pack();
        setVisible(true);
    }

    private void updateTableModel(){
        this.dataTableModel.setRowCount(0);
            String type = comboBox.getSelectedItem().toString();
            for(JSONObject jo : this.regionsInfo){
                if(type.equals(jo.getString("type"))){
                    if(jo.has("data")){
                        JSONObject data = jo.getJSONObject("data");
                        for(String key : data.keySet()){
                            dataTableModel.addRow(new Object[] {key, "", "Default "+ data.get(key)});
                        }
                    }
                    break;
                }  
            }
    }

    public void onRegister(double time, MapInfo map, List<AnimalInfo> animals){

    }
    public void onReset(double time, MapInfo map, List<AnimalInfo> animals){
        this.toColModel.removeAllElements();
        this.fromColModel.removeAllElements();
        this.toRowModel.removeAllElements();
        this.fromRowModel.removeAllElements();
        setDimensions(map);
    }
    public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a){

    }
    public void onRegionSet(int row, int col, MapInfo map, RegionInfo r){

    }
    public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt){

    }

    private void setDimensions(MapInfo map){
        for(int i = 0; i < map.getCols(); i++){
            this.toColModel.addElement(String.valueOf(i));
            this.fromColModel.addElement(String.valueOf(i));
        }

        for(int i = 0; i < map.getRows(); i++){
            this.toRowModel.addElement(String.valueOf(i));
            this.fromRowModel.addElement(String.valueOf(i));
        }
    }

    public int getStatus(){
        return status;
    }

}
