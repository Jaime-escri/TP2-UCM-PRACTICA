package simulator.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONArray;

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
    }

    public double getfood(AnimalInfo a, double dt) {
        if(a.getDiet() == Diet.CARNIVORE) return 0.0;
        long n =list.stream().filter(anim -> anim.getDiet() == Diet.HERBIVORE).count();
        return 60.0 * Math.exp(-Math.max(0, n -5.0)*2.0) * dt;
    }

    public JSONObject asJSON(){
        JSONObject obj =  new JSONObject();
        JSONArray animalsArray = new JSONArray();

        for(Animal a: list){
            animalsArray.put(a.asJSON());
        }

        obj.put("animals", animalsArray);
        return obj;
    }
}
