package simulator.model;

import java.util.List;

public class DefaultRegion extends Region {
    final static double FOOD_EAT_RATE_HERBS = 60.0;
    final static double FOOD_SHORTAGE_TH_HERBS = 5.0;
    final static double FOOD_SHORTAGE_EXP_HERBS = 2.0;

    public double getfood(AnimalInfo a, double dt){
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
            return FOOD_EAT_RATE_HERBS*Math.exp(-Math.max(0, n-FOOD_SHORTAGE_TH_HERBS)*FOOD_SHORTAGE_EXP_HERBS)*dt;
        }
    }


    public void update(double dt){

    }

}
