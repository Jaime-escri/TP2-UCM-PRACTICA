package simulator.model;

import java.util.List;
import simulator.misc.Utils;

public class DynamicSupplyRegion extends Region {
    private double food;
    private double factor;

    final static double FOOD_EAT_RATE_HERBS = 60.0;
    final static double FOOD_SHORTAGE_TH_HERBS = 5.0;
    final static double FOOD_SHORTAGE_EXP_HERBS = 2.0;
    final static double INIT_FOOD = 100.0;
    final static double FACTOR = 2.0;

    public DynamicSupplyRegion(double food, double factor){
        this.food = food;
        this.factor = factor;
    }

    

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
            double ret = Math.min(food,FOOD_EAT_RATE_HERBS*Math.exp(-Math.max(0,n-FOOD_SHORTAGE_TH_HERBS)*FOOD_SHORTAGE_EXP_HERBS)*dt);
            food -= ret;
            return ret;
        }
    }

    public void update(double dt){
        //Incrementa la comida con una probabilidad del 50%
        double prob = Utils.RAND.nextDouble();
        if(prob <= 0.5){
            food += factor*dt;
        }
    }

}
