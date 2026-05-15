package simulator.view;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

public class InfoTable extends JPanel {

  private String title;
  private TableModel tableModel;

  InfoTable(String title, TableModel tableModel) {
    this.title = title;
    this.tableModel = tableModel;
    initGUI();
  }

  private void initGUI() {
    // TODO cambiar el layout del panel a BorderLayout()
    setLayout(new BorderLayout());
    // TODO añadir un borde con título al JPanel, con el texto this.title
    setBorder( BorderFactory.createTitledBorder(this.title));
    // TODO añadir un JTable (con barra de desplazamiento vertical) que use
    //      this.tableModel
    JTable tabla = new JTable(tableModel);
    JScrollPane scroll = new JScrollPane(tabla);
    add(scroll);
  }
}
