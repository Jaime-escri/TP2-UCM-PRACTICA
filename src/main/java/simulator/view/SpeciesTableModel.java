package simulator.view;


import java.awt.Taskbar.State;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.Animal;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

class SpeciesTableModel extends AbstractTableModel implements EcoSysObserver {

    private Controller ctrl;
    private List<String> speciesNames;
    private List<AnimalInfo> animalsList;
    private String[] columns = {"Species", "NORMAL", "MATE", "HUNGER", "DANGER", "DEAD"};

    SpeciesTableModel(Controller ctrl) {
        this.ctrl = ctrl;
        this.speciesNames = new ArrayList<>();
        this.animalsList = new ArrayList<>();
        this.ctrl.addObserver(this);    
    }
  
    public void update(List<AnimalInfo> animals){
        this.speciesNames.clear();
        this.animalsList = animals;
        for(AnimalInfo a : animals){
            String name = a.getGeneticCode();

            if(!speciesNames.contains(name)){
                speciesNames.add(name);
            }
        }

        this.fireTableDataChanged();
    }

    public int getRowCount(){
        return speciesNames.size();
    }

    public int getColumnCount(){
        return 6;
    }

    public String getColumnName(int index){ 
        return this.columns[index];
    }

    public Object getValueAt(int row, int col){
        String species = speciesNames.get(row);
        int normal = 0;
        int mate = 0;
        int hunger = 0;
        int danger = 0;
        int dead = 0;
        for(AnimalInfo a : animalsList){
            if(a.getGeneticCode().equals(species)){
                switch (a.getState()) {
                    case NORMAL:
                        normal++;
                        break;
                    case MATE:
                        mate++;
                        break;
                    case HUNGER:
                        hunger++;
                        break;
                    case DANGER:
                        danger++;
                        break;
                    case DEAD:
                        dead++;
                        break;
                    
                    default:
                        break;
                }
            }
        }

        switch (col) {
            case 0:
                return species;
            case 1:
                return String.valueOf(normal);
            case 2:
                return String.valueOf(mate);
            case 3:
                return String.valueOf(hunger);
            case 4: 
                return String.valueOf(danger);
            case 5:
                return String.valueOf(dead);
            default: return "unknown";
        }
    }

    public void onRegister(double time, MapInfo map, List<AnimalInfo> animals){
        update(animals);
    }
    public void onReset(double time, MapInfo map, List<AnimalInfo> animals){
        update(animals);
    }
    public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a){
        update(animals);
    }
    public void onRegionSet(int row, int col, MapInfo map, RegionInfo r){}
    public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt){
        update(animals);
    }
}


/*public Object getValueAt(int row, int col){
        String species = speciesNames.get(row);

        switch (col) {
            case 0:
                return species;
            case 1: 
                for(AnimalInfo a : animalsList){
                    if(a.getGeneticCode().equals(species)){
                        return a.getDiet();
                    }
                }
                return "unknown";
            case 2:
                int normal = 0;
                int danger = 0;
                int mate = 0;
                for(AnimalInfo a : animalsList){
                    if(a.getGeneticCode().equals(species)){
                        switch (a.getState()) {
                            case NORMAL:
                                normal++;   
                                break;
                            case DANGER:
                                danger++;
                                break;
                            case MATE:
                                mate++;
                                break;
                        }
                    }
                }
                return String.format("Normal: %d, Danger: %d, Mate: %d", normal, danger, mate);
            case 3:
                int count = 0;
                for(AnimalInfo a : animalsList){
                    if(a.getGeneticCode().equals(species)){
                        count++;
                    }
                }
                return count;
            default:
                return null;
        }
    } */
