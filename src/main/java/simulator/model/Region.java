package simulator.model;

import java.util.ArrayList;
import java.util.List;

public class Region {
    private List<Animal> list;

    public Region(){
        this.list = new ArrayList<>();
    }

    public void addAnimal(Animal a){
        list.add(a);
    }

    public void removeAnimal(Animal a){
        list.remove(a);
    }

    public List<Animal> getAnimals(){
        return list;
    }
}
