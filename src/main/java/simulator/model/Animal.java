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

    protected Animal(Animal p1, Animal p2){
        this.geneticCode = p1.getGeneticCode();
        this.diet = p1.getDiet();
        this.mateStrategy = p2.mateStrategy;
        this.dest = null;
        this.baby = null;
        this.mateTarget = null;
        this.regionMngr = null;
        this.state = State.NORMAL;
        this.desire = 0.0;
        this.energy = (p1.getEnergy() + p2.getEnergy()) /2;
        this.pos = p1.getPosition().plus(Vector2D.getRandomVector(-1,1).scale(60.0*(Utils.RAND.nextGaussian()+1)));
        this.sightRange = Utils.getRandomizedParameter((p1.getSightRange()+p2.getSightRange())/2,0.2);
        this.speed = Utils.getRandomizedParameter((p1.getSpeed()+p2.getSpeed())/2, 0.2);
    }

    void init(AnimalMapView regMngr){
        this.regionMngr = regMngr;
        if(this.pos == null){
            double x = Utils.RAND.nextDouble() * (regionMngr.getWidth() - 1);
            doble y = Utils.RAND.nextDouble() * (regionMngr.getHeight() - 1);
            this.pos = new Vector2D(x,y);
        }
    }

}