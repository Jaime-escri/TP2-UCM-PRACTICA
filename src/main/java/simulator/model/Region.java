package simulator.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Region implements Entity, FoodSupplier, RegionInfo{
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

    public void update(double dt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    public double getfood(AnimalInfo a, double dt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getfood'");
    }

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
}