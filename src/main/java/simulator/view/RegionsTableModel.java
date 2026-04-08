package simulator.view;



import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.json.JSONObject;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.Diet;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

class RegionsTableModel extends AbstractTableModel implements EcoSysObserver {

  MapInfo map;
  String[] columnNames = {"Row", "Col", "Desc", "CARNIVORE", "HERBIVORE"};

  RegionsTableModel(Controller ctrl) {
    ctrl.addObserver(this);
  }
  
  public int getRowCount(){
    return map == null ? 0 : map.getCols() * map.getRows();
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

    int countCarnivore = 0;
    int countHervibore = 0;
    for(AnimalInfo a : reg.getAnimalsInfo()){
      if(a.getDiet() == Diet.CARNIVORE){
        countCarnivore++;
      }else if(a.getDiet() == Diet.HERBIVORE){
        countHervibore++;
      }
    }
    switch (columnIndex) {
      case 0: return row;
      case 1: return col;
      case 2: return reg.toString();
      case 3: return String.valueOf(countCarnivore);
      case 4: return String.valueOf(countHervibore);
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