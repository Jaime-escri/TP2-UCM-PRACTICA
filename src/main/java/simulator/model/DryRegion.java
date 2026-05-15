package simulator.model;

import java.util.List;
import simulator.misc.Utils;

public class DryRegion extends Region {
    private double r1;
    private double r2;
    private double n;
    private double m;
    private RegionState r;
    private double duracionSequia;
    private double tiempoTranscurrido;
    

    final static double FOOD_EAT_RATE_HERBS = 60.0;
    final static double FOOD_SHORTAGE_TH_HERBS = 5.0;
    final static double FOOD_SHORTAGE_EXP_HERBS = 2.0;
    final static double INIT_FOOD = 100.0;
    final static double FACTOR = 2.0;

    public DryRegion(double r1, double r2, double n, double m){
        if(r1 < 0 || r1 >1) throw new IllegalArgumentException("El valor r1 debe estar entre 0 y 1");
        else this.r1 = r1;
        if(r2 < 0 || r2 >1) throw new IllegalArgumentException("El valor r2 debe estar entre 0 y 1");
        else this.r2 = r2;
        if(n<=0) throw new IllegalArgumentException("El valor de n debe ser mayor que 0");
        else this.n = n;
        if(m<=0) throw new IllegalArgumentException("El valor de m debe ser mayor que 0");
        else this.m = m;
        this.r = RegionState.NORMAL; 
        this.tiempoTranscurrido = 0.0;
        this.duracionSequia = 0.0;
    }



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
            double ret =  FOOD_EAT_RATE_HERBS*Math.exp(-Math.max(0, n-FOOD_SHORTAGE_TH_HERBS)*FOOD_SHORTAGE_EXP_HERBS)*dt;
            if(r == RegionState.SEQUIA) ret *= r1;
            return ret;
        }
    }

    public void update(double dt){
        this.tiempoTranscurrido += dt;

        if(r == RegionState.NORMAL && tiempoTranscurrido >n){
            r = RegionState.SEQUIA;
            duracionSequia = 1.0 + Utils.RAND.nextDouble() * (m-1.0);
            tiempoTranscurrido = 0.0;
        }
        if(r == RegionState.SEQUIA && tiempoTranscurrido > duracionSequia){
            r= RegionState.NORMAL;
            tiempoTranscurrido = 0.0;
            r1 *= r2;
        }
        
    }

    public String toString(){
        return "Dry food supply";
    }
}
