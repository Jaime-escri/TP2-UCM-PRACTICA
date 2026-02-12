package simulator.model;

import java.util.List;

public class SelectYoungest implements SelectionStrategy{
    public Animal select(Animal a, List<Animal> as){
        if(as.isEmpty() || as == null) return null;

        Animal youngest = as.get(0);
        for(int i = 1; i < as.size(); i++){
            Animal current = as.get(i);
            if(youngest.getAge() < current.getAge()){
                youngest = current;
            }
        }
        return youngest;
    }
}
