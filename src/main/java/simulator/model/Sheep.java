package simulator.model;

import java.util.List;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Sheep extends Animal {
    private Animal dangerSource;
    private SelectionStrategy dangerStrategy;

    final static String SHEEP_GENETIC_CODE = "Sheep";
    final static double INIT_SIGHT_SHEEP = 40;
    final static double INIT_SPEED_SHEEP = 35;
    final static double BOOST_FACTOR_SHEEP = 2.0;
    final static double MAX_AGE_SHEEP = 8;
    final static double FOOD_DROP_BOOST_FACTOR_SHEEP = 1.2;
    final static double FOOD_DROP_RATE_SHEEP = 20.0;
    final static double DESIRE_THRESHOLD_SHEEP = 65.0;
    final static double DESIRE_INCREASE_RATE_SHEEP = 40.0;
    final static double PREGNANT_PROBABILITY_SHEEP = 0.9;

    public Sheep(SelectionStrategy mateStrategy, SelectionStrategy dangerStrategy,  Vector2D pos){
        super(SHEEP_GENETIC_CODE, Diet.HERBIVORE, INIT_SIGHT_SHEEP, INIT_SPEED_SHEEP, mateStrategy, pos);
        this.dangerStrategy = dangerStrategy;
    }

    protected Sheep(Sheep p1, Animal p2){
        super(p1,p2);
        this.dangerStrategy = p1.dangerStrategy;
        this.dangerSource = null;
    }

    public void update(double dt){
        switch (this.getState()) {
            case NORMAL:
                updateNormal(dt);
                break;
            case DEAD: break;
            case HUNGER: break;
            case DANGER:
                updateDanger(dt);
                break;
            case MATE: 
                updateMate(dt);
                break;
            default:
                break;
        }

        //Si la posición está fuera del mapa, reajustarlo y normal
        this.adjustPosition();
        //Energy = 0.0 o Age = 8.0 -> DEAD
        if(this.getAge() > 8.0 || this.getEnergy() == 0.0){
            this.setState(State.DEAD);
            return; //Para que el animal muerto no pida comida
        }

        //Pedir comida si su estado no es DEAD
        this.askFood(dt);
    }

    

    

    //Métodos abstractos heredados
    protected void setNormalStateAction(){
        this.dangerSource = null;
        this.setMateTarget(null);
    }
    protected void setMateStateAction(){
        this.dangerSource = null;
    }

    protected void setHungerStateAction(){}
    protected void setDangerStateAction(){
        this.setMateTarget(null);
    }
    protected void setDeadStateAction(){
        this.dangerSource = null;
        this.setMateTarget(null);
    }


    //Diagrama de estados y acciones

    //Punto 1 del estado normal
    public void moveNormal(double dt){
        if(this.getPosition().distanceTo(this.getDestination()) < 8.0){
            double newX = Utils.RAND.nextDouble() * this.getRegionMngr().getWidth();
            double newY = Utils.RAND.nextDouble() * this.getRegionMngr().getHeight();
            this.setDestination(new Vector2D(newX, newY));
        }
        this.move(this.getSpeed()*dt*Math.exp((this.getEnergy()-100.0)*0.007));
        this.addAge(dt);
        this.setEnergy(Utils.constrainValueInRange(this.getEnergy() - (20.0 * dt), 0.0, 100.0));
        this.setDesire(Utils.constrainValueInRange(this.getDesire() + (40.0 * dt), 0.0, 100.0));
    }

     public void updateNormal(double dt){
        moveNormal(dt);
        if(this.dangerSource == null){
            searchDangerousAnimals();
        }

        if(this.dangerSource != null){
            this.setState(State.DANGER);
        }else if(this.getDesire() > DESIRE_THRESHOLD_SHEEP){
            this.setState(State.MATE);
        }
    }

    public void updateDanger(double dt){
        if(dangerSource != null && dangerSource.getState() == State.DEAD) dangerSource = null;
        if(dangerSource == null) moveNormal(dt);
        else{
            this.setDestination(this.getPosition().plus(this.getPosition().minus(dangerSource.getPosition()).direction()));
            this.move(2.0*this.getSpeed()*dt*Math.exp((this.getEnergy()-100.0)*0.007));
            this.addAge(dt);
            this.setEnergy(Utils.constrainValueInRange(this.getEnergy() - (20.0 * 1.2 * dt), 0.0, 100.0));
            this.setDesire(Utils.constrainValueInRange(this.getDesire() + (40.0 * dt), 0.0, 100.0));
        }

        if(this.dangerSource == null || this.getPosition().distanceTo(this.dangerSource.getPosition()) > this.getSightRange() ){
            searchDangerousAnimals();
            if (this.dangerSource == null) {
            if (this.getDesire() < DESIRE_THRESHOLD_SHEEP) {
                this.setState(State.NORMAL);
            } else {
                this.setState(State.MATE);
            }
}
        }
    }

    public void updateMate(double dt){
        if(this.getMateTarget() != null && 
        (this.getMateTarget().getState() == State.DEAD || 
        this.getPosition().distanceTo(this.getMateTarget().getPosition()) > this.getSightRange())){
            this.setMateTarget(null);
        }

        if(this.getMateTarget() == null){
            List<Animal> mates = this.getRegionMngr().getAnimalsInRange(this, a -> a.getGeneticCode().equals(this.getGeneticCode()));
            this.setMateTarget(this.getMateStrategy().select(this, mates));

            if(this.getMateTarget() == null){
                moveNormal(dt);
            }
        }

        if(this.getMateTarget() != null){
            this.setDestination(this.getMateTarget().getPosition());
            this.move(2.0*this.getSpeed()*dt*Math.exp((this.getEnergy()-100.0)*0.007));
            this.addAge(dt);
            this.setEnergy(Utils.constrainValueInRange(this.getEnergy() - (20.0 * 1.2 * dt), 0.0, 100.0));
            this.setDesire(Utils.constrainValueInRange(this.getDesire() + (40.0 * dt), 0.0, 100.0));

            if(this.getPosition().distanceTo(this.getMateTarget().getPosition()) < 8.0){
                this.setDesire(0.0);
                this.getMateTarget().setDesire(0.0);

                if(!this.isPregnant() && Utils.RAND.nextDouble() < PREGNANT_PROBABILITY_SHEEP){
                    this.setBaby(new Sheep(this, this.getMateTarget()));
                }

                this.setMateTarget(null);
            }
        }

        if(this.dangerSource == null) searchDangerousAnimals();
        if (this.dangerSource != null) {
            this.setState(State.DANGER);
        } else if (this.getDesire() < DESIRE_THRESHOLD_SHEEP) {
            this.setState(State.NORMAL);
        }
    }

    public void searchDangerousAnimals(){
        List<Animal> dangers = this.getRegionMngr().getAnimalsInRange(this, a->a.getDiet() == Diet.CARNIVORE);
        this.dangerSource = this.dangerStrategy.select(this, dangers);
    }

    
}