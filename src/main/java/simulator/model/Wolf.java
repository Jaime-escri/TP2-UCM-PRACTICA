package simulator.model;

import java.util.List;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Wolf extends Animal {
    private Animal huntTarget;
    private SelectionStrategy huntingStrategy;

    public Wolf(SelectionStrategy mateStrategy, SelectionStrategy huntingStrategy,  Vector2D pos){
        super("Wolf", Diet.CARNIVORE, 50.0, 60.0, mateStrategy, pos);
        this.huntingStrategy = huntingStrategy;
    }

    protected Wolf(Wolf p1, Animal p2){
        super(p1,p2);
        this.huntingStrategy = p1.getMateStrategy();
        this.huntTarget = null;
    }

    public void update(double dt){
        switch (this.getState()) {
            case DEAD: break;
            case HUNGER:
                updateHunger(dt);
                break;
            case DANGER:
                break;
            case MATE: 
                updateMate(dt);
                break;
            case NORMAL:
                updateNormal(dt);
                break;
        
            default:
                break;
        }

        //Comprobar si está fuera del mapa
        this.adjustPosition();

        //Comprobar su energía y edad para matarlo o no
        if(this.getEnergy() == 0.0 || this.getAge() > 14.0){
            this.setState(State.DEAD);
        }

        //Pedir comida al gestor
        this.askFood(dt);
    }

    //Métodos abstractos heredados de Animal
    protected void setNormalStateAction(){
        this.huntTarget = null;
        this.setMateTarget(null);
    }
    protected void setMateStateAction(){
        this.huntTarget = null;
    }
    protected void setHungerStateAction(){
        this.setMateTarget(null);
    }
    protected void setDangerStateAction(){}
    protected void setDeadStateAction(){
        this.huntTarget = null;
        this.setMateTarget(null);
    }

    //Métodos del diagrama de estados:

    public void moveNormal(double dt){
         if(this.getPosition().distanceTo(this.getDestination()) < 8.0){
            this.setDestination(Vector2D.get_random_vector(0, this.getRegionMngr().getWidth()));
        }
        this.move(this.getSpeed()*dt*Math.exp((this.getEnergy()-100.0)*0.007));
        this.addAge(dt);
        this.setEnergy(Utils.constrainValueInRange(18.0*dt, 0.0, 100.0));
        this.setDesire(Utils.constrainValueInRange(30.0*dt, 0.0, 100.0));
    }

    public void updateNormal(double dt){
        moveNormal(dt);
        if(this.getEnergy() < 50.0) this.setState(State.HUNGER);
        else if(this.getDesire() > 65.0) this.setState(State.MATE);
    }

    public void updateHunger(double dt){
        if(this.huntTarget == null || this.huntTarget.getState() == State.DEAD || this.getPosition().distanceTo(this.huntTarget.getPosition()) > this.getSightRange()){
            List<Animal> presas = this.getRegionMngr().getAnimalsInRange(this, a -> a.getDiet() == Diet.HERBIVORE);
            this.huntTarget = this.huntingStrategy.select(this, presas);
        }

        if(this.huntTarget == null){
            moveNormal(dt);
        }else{
            this.setDestination(huntTarget.getPosition());
            this.move(3.0*this.getSpeed()*dt*Math.exp((this.getEnergy()-100.0)*0.007));
            this.addAge(dt);
            this.setEnergy(Utils.constrainValueInRange(this.getEnergy() - (18.0*1.2*dt), 0.0, 100.0));
            this.setDesire(Utils.constrainValueInRange(this.getDesire() - (30.0*dt), 0.0, 100.0));
            
            if(this.getPosition().distanceTo(this.huntTarget.getPosition()) < 8.0){
                this.huntTarget.setState(State.DEAD);
                this.huntTarget = null;
                this.setEnergy(Utils.constrainValueInRange(this.getEnergy() + 50.0, 0.0, 100.0));
            }
        }

        if(this.getEnergy() > 50.0){
            if(this.getDesire() < 65.0) this.setState(State.NORMAL);
            else this.setState(State.MATE);
        }
    }

    public void updateMate(double dt){
        if(this.getMateTarget() != null &&
            (this.getMateTarget().getState() == State.DEAD 
            || this.getPosition().distanceTo(this.getMateTarget().getPosition()) > this.getSightRange())){
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

            this.move(3.0*this.getSpeed()*dt*Math.exp((this.getEnergy()-100.0)*0.007));
            this.addAge(dt);
            this.setEnergy(Utils.constrainValueInRange(this.getEnergy() - (18.0 * 1.2 * dt), 0.0, 100.0));
            this.setDesire(Utils.constrainValueInRange(this.getDesire() + (30.0 * dt), 0.0, 100.0));

            if (this.getPosition().distanceTo(this.getMateTarget().getPosition()) < 8.0) {
                this.setDesire(0.0);
                this.getMateTarget().setDesire(0.0);

                if (!this.isPregnant() && Utils.RAND.nextDouble() < 0.9) {
                    this.setBaby(new Wolf(this, this.getMateTarget()));
                }
                this.setMateTarget(null);

            }
            if (this.getEnergy() < 50.0) {
                this.setState(State.HUNGER);
            } 
            else if (this.getDesire() < 65.0) {
                this.setState(State.NORMAL);
            }
        }
    }
}