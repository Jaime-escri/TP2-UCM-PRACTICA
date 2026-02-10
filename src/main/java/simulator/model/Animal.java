package simulator.model;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public abstract class Animal implements Entity, AnimalInfo{
    private String geneticCode;
    private Diet diet;
    private State state;
    private Vector2D pos;
    private Vector2D dest;
    private double energy;
    private double speed;
    private double age;
    private double desire;
    private double sightRange;
    private Animal mateTarget;
    private Animal baby;
    private AnimalMapView regionMngr;
    private SelectionStrategy mateStrategy;

    protected Animal(String geneticCode, Diet diet, double sightRange, double initSpeed, SelectionStrategy mateStrategy, Vector2D pos){
        this.geneticCode = geneticCode;
        this.diet = diet;
        this.sightRange = sightRange;
        this.speed = Utils.getRandomizedParameter(initSpeed, 0.1);
        this.mateStrategy = mateStrategy;
        this.pos = pos;
        this.state = State.NORMAL;
        this.energy = 100.0;
        this.desire = 0.0;
        this.dest = null;
        this.mateTarget = null;
        this.baby = null;
        this.regionMngr = null;
    }

}