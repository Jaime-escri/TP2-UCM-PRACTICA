package simulator.model;

import simulator.misc.Vector2D;

public class Sheep extends Animal {

    Animal dangerSource;
    SelectionStrategy dangerStrategy;

    public Sheep(SelectionStrategy mateStrategy, SelectionStrategy dangerStrategy,  Vector2D pos){
        super("Sheep", Diet.HERBIVORE, 40.0, 35.5, mateStrategy, pos);
    }

    protected Sheep(Sheep p1, Animal p2){
        super(p1, p2);
        this.dangerStrategy = p1.dangerStrategy;
        this.dangerSource = null;
    }

    public void setNormalStateAction(){

    }
    public void setHungerStateAction(){

    }
    public void setMateStateAction(){

    }
    public void setDangerStateAction(){

    }
    public void setDeadStateAction(){

    }

    /*NORMAL,
    MATE,
    HUNGER,
    DANGER, */

    @Override
    public void update(double dt) {
        if(this.getState() != State.DEAD){

            double energyBefore = this.getEnergy();
            if((this.getEnergy() + getFood(this, dt) > 100.0) this.energy = 100.0;
            else if(this.getEnergy() + getFood(this, dt) < 0.0) this.energy = 0.0;
            else this.energy += getFood(this, dt);
            switch (this.getState()) {
                case NORMAL:
                    updateNormalCase();
                    break;
                case MATE:
                    updateMateCase();
                    break;
                case HUNGER:
                    updateHungerCase();
                    break;
                case DANGER:
                    updateDangerCase();
                    break;
            }

            if(isOutOfMap(this.getPos())){
                this.state = State.NORMAL;
            }

            if(this.getEnergy() == 0.0 || this.getAge() == 8.0) setDeath();
        }
    }

    private void setDeath() {
        this.state = State.DEAD;
    }

    @Override
    public Vector2D getPosition() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPosition'");
    }

    @Override
    public Vector2D getDestination() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDestination'");
    }
}

/*
* `abstract protected void setNormalStateAction()`: se implementa en las subclases para ejecutar una acción
  complementaria al cambio de estado correspondiente (p.ej, en la clase `Sheep` pone `mateTarget` y `dangerSource` a
  `null` -- ver la clase [`Sheep`](#la-clase-sheep)).
* `abstract protected void setMateStateAction()`: se implementa en las subclases para ejecutar una acción complementaria
  al cambio de estado correspondiente (p.ej, en la clase `Sheep` pone `dangerSource` a `null` -- ver la clase [
  `Sheep`](#la-clase-sheep)).
* `abstract protected void setHungerStateAction()`: se implementa en las subclases para ejecutar una acción
  complementaria al cambio de estado correspondiente (p.ej, en la clase `Wolf` pone `mateTarget` a `null` -- ver la
  clase [`Wolf`](#la-clase-wolf)).
* `abstract protected void setDangerStateAction()`: se implementa en las subclases para ejecutar una acción
  complementaria al cambio de estado correspondiente (p.ej, en la clase `Sheep` pone `mateTarget` a `null` -- ver la
  clase [`Sheep`](#la-clase-sheep)).
* `abstract protected void setDeadStateAction()`: se implementa en las subclases para ejecutar una acción complementaria
  al cambio de estado correspondiente (p.ej, en la clase `Sheep` pone `mateTarget` y `dangerSource` a `null` -- ver la
  clase [`Sheep`](#la-clase-sheep)). 
  */
