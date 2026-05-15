package simulator.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;
import simulator.model.State;

public class SpeciesTableModel extends AbstractTableModel implements EcoSysObserver{
    private List<Object[]> filas;

    public SpeciesTableModel(Controller ctrl) {
      this.filas = new ArrayList<>();
      ctrl.addObserver(this);
    }

    @Override
    public int getRowCount() {
        return filas.size();
    }

    @Override
    public int getColumnCount() {
        return 1+ State.values().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return this.filas.get(rowIndex)[columnIndex];
    }

    public String getColumnName(int col){
        if(col == 0) return "Species";
        return State.values()[col-1].name();
    }

    private void updateTable(List<AnimalInfo> animals){
        this.filas.clear();
        //Recorro toda la lista de animales
        for(AnimalInfo a: animals){
            boolean encontrado = false;
            int i = 0;
            int j = a.getState().ordinal() + 1;
            while(!encontrado && i < filas.size()){
                if(a.getGeneticCode().equals(filas.get(i)[0])){
                    encontrado = true;
                    
                    filas.get(i)[j] = (Integer) filas.get(i)[j] + 1;
                }
                i++;
            }
            if(!encontrado){
                Object[] nuevaFila = new Object[getColumnCount()];
                for(int z = 0; z < getColumnCount(); z++){
                    nuevaFila[z] = 0;
                }
                nuevaFila[0] = a.getGeneticCode();
                nuevaFila[j] = 1;
                filas.add(nuevaFila);
            }
        }
    }
    @Override
    public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
        updateTable(animals);
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
