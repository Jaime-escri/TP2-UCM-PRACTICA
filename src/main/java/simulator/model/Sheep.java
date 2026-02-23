package simulator.model;

import simulator.misc.Vector2D;

public class Sheep extends Animal {
    private Animal dangerSource;
    private SelectionStrategy dangerStrategy;

    public Sheep(SelectionStrategy mateStrategy, SelectionStrategy dangerStrategy,  Vector2D pos){
        super("Sheep", Diet.HERVIBORE, 40.0, 35.0, mateStrategy, pos);
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
                
                break;
        
            default:
                break;
        }
    }


    public void updateNormal(double dt){
        if(this.getPosition().distanceTo(this.getDestination()) < 8.0){
            this.setDestination(Vector2D.get_random_vector(0, this.getRegionMngr().getWidth()));
        }
        this.move(this.getSpeed()*dt*Math.exp((this.getEnergy()-100.0)*0.007));
        this.addAge(dt);
        this.setEnergy(dt*20.0);
        this.setDesire(dt*40.0);
        assert(this.getEnergy()>= 0.0 && this.getEnergy() <= 100.0);
        assert(this.getDesire() >=0.0 && this.getEnergy() <= 100.0);
        
        searchDangerousAnimal();
        
    }

    public void searchDangerousAnimal(){
        //TODO: Buscar el siguiente animal más peligroso que esté en su rango de visión. 
        if(this.dangerSource == null){

        }else{
            this.setState(State.DANGER);
            if(this.getDesire() > 65.0){
                this.setState(State.MATE);
            }
        }
        
    }

    public void updateDanger(double dt){
        if(dangerSource != null && this.getState() == State.DEAD) dangerSource = null;
        if(dangerSource == null) updateNormal(dt);
        else{
            this.setDestination(this.getPosition().plus(this.getPosition().minus(dangerSource.getPosition()).direction()));
            this.move(2.0*this.getSpeed()*dt*Math.exp((this.getEnergy()-100.0)*0.007));
            this.addAge(dt);
        }

        if(dangerSource == null ){
            
        }
    }
}