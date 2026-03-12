package simulator.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class Region implements Entity, FoodSupplier, RegionInfo{
    protected List<Animal> list;

    public Region(){
        this.list = new ArrayList<>();
    }

    public void addAnimal(Animal a){
        list.add(a);
    }

    public void removeAnimal(Animal a){
        list.remove(a);
    }

    public final List<Animal> getAnimals(){
        return list;
    }

    public abstract void update(double dt);

    public abstract double getfood(AnimalInfo a, double dt);

    public JSONObject asJSON() {
        JSONObject jo = new JSONObject();
        JSONArray ja = new JSONArray();
        
        // Recorremos la lista de animales y añadimos su representación JSON
        for (Animal a : list) {
            ja.put(a.asJSON());
        }
        
        jo.put("animals", ja);
        return jo;
    }

    public List<AnimalInfo> getAnimalsInfo(){
        return Collections.unmodifiableList(list); //Esto es similar a new ArratList<>(list) lo que pasa es que devuelves de forma que no se peuda modifcar para mayor seguridad en el código
    }
}