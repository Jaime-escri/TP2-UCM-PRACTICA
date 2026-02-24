package simulator.model;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public double getfood(AnimalInfo a, double dt) {
        // TODO: Buscar la región del animal en el mapa y pedirle comida.
        return 0.0;
    }

    @Override
    public void update(double dt){}

}
