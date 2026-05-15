package simulator.view;

import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.Dimension;
import java.awt.Frame;
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

  private DefaultTableModel dataTableModel;
  private Controller ctrl;
  private List<JSONObject> regionsInfo;

  private String[] headers = { "Key", "Value", "Description" };

  // TODO en caso de ser necesario, añadir los atributos aquí…
  ChangeRegionsDialog(Controller ctrl) {
    super((Frame)null, true);
    this.ctrl = ctrl;
    initGUI();
    // TODO registrar this como observer;
    this.ctrl.addObserver(this);
  }

  private void initGUI() {
    setTitle("Change Regions");
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    setContentPane(mainPanel);

    // TODO crea varios paneles para organizar los componentes visuales en el
    //      dialogo, y añadelos al mainpanel. P.ej., uno para el texto de ayuda,
    //      uno para la tabla, uno para los combobox, y uno para los botones.
    JPanel helpPanel = new JPanel();
    JLabel helpLabel = new JLabel("Select a region type and the rows/cols range, then click OK");
    JPanel tablePanel = new JPanel();
    JPanel comboPanel = new JPanel();
    JPanel buttonPanel = new JPanel();

    mainPanel.add(helpPanel);
    helpPanel.add(helpLabel);
    mainPanel.add(tablePanel);
    mainPanel.add(comboPanel);
    mainPanel.add(buttonPanel);

    // TODO crear el texto de ayuda que aparece en la parte superior del diálogo y
    //      añadirlo al panel correspondiente diálogo (Ver el apartado Figuras)

    // this.regionsInfo se usará para establecer la información en la tabla
    this.regionsInfo = Main.selectionRegionFactory.getInfo();
    //Main.regionsFactory.getInfo();

    // this.dataTableModel es un modelo de tabla que incluye todos los parámetros de
    // la region
    this.dataTableModel = new DefaultTableModel() {
      @Override
      public boolean isCellEditable(int row, int column) {
        return column == 1;
      }
    };
    this.dataTableModel.setColumnIdentifiers(this.headers);

    // TODO crear un JTable que use dataTableModel, y añadirlo al diálogo
    JTable dataTable = new JTable(dataTableModel);
    JScrollPane scroll = new JScrollPane(dataTable);
    tablePanel.add(scroll);


    // this.regionsModel es un modelo de combobox que incluye los tipos de regiones
    this.regionsModel = new DefaultComboBoxModel<>();
    
    

    // TODO añadir la descripción de todas las regiones a regionsModel. Para eso
    //      usa la clave “desc” o “type” de los JSONObject en regionsInfo,
    //      ya que estos nos dan información sobre lo que puede crear la factoría.
    for(JSONObject o: this.regionsInfo){
      regionsModel.addElement(o.getString("desc"));
    }
    // TODO crear un combobox que use regionsModel y añadirlo al diálogo.
    JComboBox<String> regionsCombo = new JComboBox<>(regionsModel);
    regionsCombo.addActionListener((e) -> {
      dataTableModel.setRowCount(0);

      int i = regionsCombo.getSelectedIndex();

      JSONObject info = regionsInfo.get(i);
      JSONObject data = info.getJSONObject("data");

      for(String key : data.keySet()){
        dataTableModel.addRow(new Object[]{key, "", data.getString(key)});
      }
    });
    comboPanel.add(regionsCombo);

    // TODO crear 4 modelos de combobox para this.fromRowModel, this.toRowModel,
    //      this.fromColModel y this.toColModel.
    this.fromRowModel = new DefaultComboBoxModel<>();
    this.toRowModel = new DefaultComboBoxModel<>();
    this.fromColModel = new DefaultComboBoxModel<>();
    this.toColModel = new DefaultComboBoxModel<>();

    // TODO crear 4 combobox que usen estos modelos y añadirlos al diálogo.
    JComboBox<String> fromRowCombo = new JComboBox<>(fromRowModel);
    JComboBox<String> toRowCombo = new JComboBox<>(toRowModel);
    JComboBox<String> fromColCombo = new JComboBox<>(fromColModel);
    JComboBox<String> toColCombo = new JComboBox<>(toColModel);
    comboPanel.add(fromRowCombo);
    comboPanel.add(toRowCombo);
    comboPanel.add(fromColCombo);
    comboPanel.add(toColCombo);

    // TODO crear los botones OK y Cancel y añadirlos al diálogo.
    JButton okButton = new JButton("ok");
    okButton.addActionListener((e) -> {
      int rowFrom = Integer.parseInt((String) fromRowModel.getSelectedItem());
      int rowTo = Integer.parseInt((String) toRowModel.getSelectedItem());
      int colFrom = Integer.parseInt((String) fromColModel.getSelectedItem());
      int colTo = Integer.parseInt((String) toColModel.getSelectedItem());

      int idx = regionsModel.getIndexOf(regionsModel.getSelectedItem());
      String regionType = regionsInfo.get(idx).getString("type");

      JSONObject regionData = new JSONObject();
      for(int i = 0; i < dataTableModel.getRowCount(); i++){
        String key = (String) dataTableModel.getValueAt(i, 0);
        String value = (String) dataTableModel.getValueAt(i, 1);
        if(value != null && !value.isEmpty()){
          regionData.put(key, value);
        }
      }

      JSONObject spec = new JSONObject();
      spec.put("type", regionType);
      spec.put("data", regionData);

      JSONObject region = new JSONObject();
      region.put("row", new JSONArray(new int[]{rowFrom, rowTo}));
      region.put("col", new JSONArray(new int[]{colFrom, colTo}));
      region.put("spec", spec);

      JSONObject rs = new JSONObject();
      rs.put("regions", new JSONArray().put(region));

      try {
        ctrl.setRegions(rs);
        setVisible(false);
      } catch (Exception ex) {
        ViewUtils.showErrorMsg(ex.getMessage());
      }
    });
    buttonPanel.add(okButton);
    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener((e) ->{
      setVisible(false);
    });
    buttonPanel.add(cancelButton);

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
    fromRowModel.removeAllElements();
    toRowModel.removeAllElements();
    for(int i = 0; i < map.getRows(); i++){
      fromRowModel.addElement(String.valueOf(i));
      toRowModel.addElement(String.valueOf(i));
    }

    fromColModel.removeAllElements();
    toColModel.removeAllElements();
    for(int i = 0; i < map.getCols(); i++){
      fromColModel.addElement(String.valueOf(i));
      toColModel.addElement(String.valueOf(i));
    }
  }

  @Override
  public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
    onRegister(time, map, animals);
  }

  @Override
  public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {

  }

  @Override
  public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
    
  }

  @Override
  public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
  
  }
}