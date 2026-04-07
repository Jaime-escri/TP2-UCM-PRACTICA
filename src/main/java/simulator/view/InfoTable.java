package simulator.view;

import javax.swing.JPanel; // Faltaba
import javax.swing.table.TableModel; // Faltaba
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;

public class InfoTable extends JPanel {

  private String title;
  private TableModel tableModel;

  InfoTable(String title, TableModel tableModel) {
    this.title = title;
    this.tableModel = tableModel;
    initGUI();
  }

  private void initGUI() {
    this.setLayout(new BorderLayout());
    this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(getBackground()), title));

    JTable table = new JTable(tableModel);

    JScrollPane scrollPane = new JScrollPane(table, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    this.add(scrollPane, BorderLayout.CENTER);
  }
}
