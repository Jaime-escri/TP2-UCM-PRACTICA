package simulator.model;

import java.util.List;

public interface SelectionStrategy {
    Animal select(Animal a, List<Animal> as);
    private Animal SelectFirst(Animal a, List<Animal> as){
        return as.getFirst();
    }

    //private Animal SelectClosest(Animal a, List<Animal> as){
    // HAY QUE IMPLEMENTAR  
    //}

    private Animal SelectYoungest(Animal a, List<Animal> as){
        Animal youngest = as.getFirst();
        for(int i = 0; i < as.size(); i++){
            if(youngest.getAge() > as.get(i).getAge()){
                youngest = as.get(i);
            }
        }
        return youngest;
    }
}