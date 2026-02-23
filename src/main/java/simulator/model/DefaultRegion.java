package simulator.model;

import java.util.List;

public class DefaultRegion extends Region {
    
    public double getFood(AnimalInfo a, double dt){
        if(a.getDiet() == Diet.CARNIVORE){
            return 0.0;
        }else{
            List<Animal> as = this.getAnimals();
            int n = 0;
            for(Animal an : as){
                if(an.getDiet() == Diet.HERBIVORE){
                    n++;
                }
            }
            return 60.0*Math.exp(-Math.max(0, n-5.0)*2.0)*dt;
        }
    }
    public void update(){}
}
