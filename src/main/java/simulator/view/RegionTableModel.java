
package simulator.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.Animal;
import simulator.model.AnimalInfo;
import simulator.model.Diet;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class RegionTableModel extends AbstractTableModel implements EcoSysObserver{
    private List<Object[]> filas;

    public RegionTableModel(Controller ctrl) {
      this.filas = new ArrayList<>();
      ctrl.addObserver(this);
    }

    @Override
    public int getRowCount() {
        return filas.size();
    }

    @Override
    public int getColumnCount() {
        return 3+ Diet.values().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return this.filas.get(rowIndex)[columnIndex];
    }

    public String getColumnName(int col){
        if(col == 0) return"Row";
        else if(col == 1) return "Col";
        else if(col == 2) return "Description";
        else return Diet.values()[col-3].toString();
    }

    private void updateTable(MapInfo map){
        filas.clear();
        for (MapInfo.RegionData rd : map) {
            Object[] object = new Object[getColumnCount()];
            for(int i = 0; i < getColumnCount(); i++){
                object[i] = 0;
            }
            object[0] = rd.row();
            object[1] = rd.col();
            object[2] = rd.r().toString();
            for(AnimalInfo a: rd.r().getAnimalsInfo()){
                object[3 + a.getDiet().ordinal()] =  (Integer) object[3 + a.getDiet().ordinal()] + 1;
            }

            filas.add(object);
        }
    }

    @Override
    public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
        updateTable(map);
        fireTableDataChanged();
    }

    @Override
    public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
        onRegister(time, map, animals);
    }

    @Override
    public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
        onRegister(time, map, animals);
    }

    @Override
    public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {

    }

    @Override
    public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
        onRegister(time, map, animals);
    }
}
