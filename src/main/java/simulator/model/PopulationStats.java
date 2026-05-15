package simulator.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PopulationStats implements EcoSysObserver{

    private Map<String, Integer> speciesCounter;
    private Map<String, Integer> limitExceededCounter;
    private double r;

    public PopulationStats(double limit){
        if(limit <= 0) throw new IllegalArgumentException("El valor de r debe ser mayor que 0");
        else r = limit;
        speciesCounter = new HashMap<>();
        limitExceededCounter = new HashMap<>();
    }

    @Override
    public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
        for(AnimalInfo a: animals){
            if(!speciesCounter.containsKey(a.getGeneticCode())){
                speciesCounter.put(a.getGeneticCode(),1);
            }
            else speciesCounter.put(a.getGeneticCode(), speciesCounter.get(a.getGeneticCode())+1);
        }
    }

    @Override
    public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
        
    }

    @Override
    public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
        
    }

    @Override
    public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
        
    }

    @Override
    public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
        //Creo un nuevo mapa con el que voy a recorrer todo la lista de animales y voy a contar cuantos animales hay de cada especie
        Map<String, Integer> aux = new HashMap<>();
        for(AnimalInfo a: animals){
            if(!aux.containsKey(a.getGeneticCode())){
                aux.put(a.getGeneticCode(), 1);
            }
            else aux.put(a.getGeneticCode(), aux.get(a.getGeneticCode()) +1);
        }

        //Recorro todo el mapa de contador de especies y hago las comprobaciones necesarias para saber si tengo que aumentaren una unidad el contador de veces que se ha excedido el limite
        for(String key: speciesCounter.keySet()){
            if(speciesCounter.get(key) <= r && aux.get(key) > r){
                if(!limitExceededCounter.containsKey(key)){
                    limitExceededCounter.put(key, 1);
                }
                else limitExceededCounter.put(key, limitExceededCounter.get(key) +1);
            }
        }

        speciesCounter = aux;
    }

    public void printResults(){
        System.out.println("Population change for r= " + r + ":");
        for(String key: limitExceededCounter.keySet()){
            System.out.println(key + " => " + limitExceededCounter.get(key));
        }
    }
    
}
