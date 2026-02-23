package simulator.model;

import java.util.List;

public class SelectClosest implements SelectionStrategy{

    public Animal select(Animal a, List<Animal> as){
        if(as == null || as.isEmpty()) return null;
        Animal ret = null;
        double minimum_distance = Double.MAX_VALUE;
        for(Animal candidate : as){
            double compare = getDistanceTotal(a, candidate);
            if(a != candidate){
                if(compare < minimum_distance){
                    minimum_distance = compare;
                    ret = candidate;
                }
            }  
        }
        return ret;
    }

    public double getDistanceTotal(Animal a1, Animal a2){
        return a1.getPosition().distanceTo(a2.getPosition());
    }
}
