package simulator.view;



import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.json.JSONObject;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.AnimalMapView;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

class RegionsTableModel extends AbstractTableModel implements EcoSysObserver {

  MapInfo map;
  String[] columnNames = {"Row", "Col", "Type", "Food", "Animals"};

  RegionsTableModel(Controller ctrl) {
    ctrl.addObserver(this);
  }
  
  public int getRowCount(){
    return map.getCols() * map.getRows();
  }
  
  public int getColumnCount(){
    return columnNames.length;
  }

  public String getColumnName(int col){
    return columnNames[col];
  }

  public Object getValueAt(int rowIndex, int columnIndex){
    int row = rowIndex/map.getCols();
    int col = rowIndex % map.getCols();
    
    RegionInfo reg = null;
    Iterator<MapInfo.RegionData> it = map.iterator();
    while(reg == null && it.hasNext()){
      MapInfo.RegionData data = it.next();
      if(data.col() == col && data.row() == row){
        reg = data.r();
      }
    }
    
    switch (columnIndex) {
      case 0: return row;
      case 1: return col;
      case 2: return reg.toString();
      case 3:{
        JSONObject obj = reg.asJSON();
        Object food = obj.optDouble("food", 0.0);
        if(food.equals(0.0)){
          food = "Infinito";
        }
        return food;
      }
      case 4: return reg.getAnimalsInfo();
    }

    return null;
  }

  public void onRegister(double time, MapInfo map, List<AnimalInfo> animals){
    this.map = map;
    fireTableStructureChanged();
  }
  public void onReset(double time, MapInfo map, List<AnimalInfo> animals){
    this.map = map;
    fireTableStructureChanged();
  }
  public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a){
    this.map = map;
    fireTableDataChanged();
  }
  public void onRegionSet(int row, int col, MapInfo map, RegionInfo r){
    this.map = map;
    fireTableDataChanged();
  }
  public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt){
    this.map = map;
    fireTableDataChanged();
  }

  
}