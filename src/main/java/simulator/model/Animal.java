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

    final static double INIT_ENERGY = 100.0;
    final static double MUTATION_TOLERANCE = 0.2;
    final static double NEARBY_FACTOR = 60.0;
    final static double COLLISION_RANGE = 8;
    final static double HUNGER_DECAY_EXP_FACTOR = 0.007;
    final static double MAX_ENERGY = 100.0;
    final static double MAX_DESIRE = 100.0;

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
        this.pos = p1.getPosition().plus(Vector2D.get_random_vector(-1,1).scale(60.0*(Utils.RAND.nextGaussian()+1)));
        this.sightRange = Utils.getRandomizedParameter((p1.getSightRange()+p2.getSightRange())/2,0.2);
        this.speed = Utils.getRandomizedParameter((p1.getSpeed()+p2.getSpeed())/2, 0.2);
    }

    void init(AnimalMapView regMngr){
        this.regionMngr = regMngr;
        if(this.pos == null){
            double x = Utils.RAND.nextDouble() * (regionMngr.getWidth() - 1);
            double y = Utils.RAND.nextDouble() * (regionMngr.getHeight() - 1);
            this.pos = new Vector2D(x,y);
        }
        double destX = Utils.RAND.nextDouble() * (regionMngr.getWidth() - 1);
        double destY = Utils.RAND.nextDouble() * (regionMngr.getHeight() - 1);
        this.dest = new Vector2D(destX, destY);
    }

    protected void move(double speed){
        if(pos != null && dest != null){
            pos = pos.plus(dest.minus(pos).direction().scale(speed));
        }
    }

    protected void setState(State state){
        this.state = state;
        switch (state) {
            case NORMAL:
                setNormalStateAction();
                break;
            case HUNGER:
                setHungerStateAction();
                break;
            case MATE:
                setMateStateAction();
                break;
            case DANGER:
                setDangerStateAction();
                break;
            case DEAD:
                setDangerStateAction();
                break;
            default: 
                break;
        }
    }

    abstract protected void setNormalStateAction();
    abstract protected void setMateStateAction();
    abstract protected void setHungerStateAction();
    abstract protected void setDangerStateAction();
    abstract protected void setDeadStateAction();

    public Animal deliverBaby(){
        Animal b = this.baby;
        this.baby = null; //La madre deja de estar embarazada y entregamos al bebé
        return b;
    }


    public void setDestination(Vector2D other){
        this.dest = other;
    }

    public void addAge(double a){
        this.age += a;
    }

    public void setEnergy(double e) {
        this.energy = e;
    }

    public void setDesire(double d) {
        this.desire = d;
    }

    //Getters y setters
    public State getState(){
        return this.state;
    }
	public Vector2D getPosition(){
        return this.pos;
    }
	public String getGeneticCode(){
        return this.geneticCode;
    }
	public Diet getDiet(){
        return this.diet;
    }
	public double getSpeed(){
        return this.speed;
    }
	public double getSightRange(){
        return this.sightRange;
    }
	public double getEnergy(){
        return this.energy;
    }
	public double getAge(){
        return this.age;
    }
	public Vector2D getDestination(){
        return this.dest;
    }
	public boolean isPregnant(){
        return this.baby != null;
    }
    public double getDesire(){
        return this.desire;
    }
    public AnimalMapView getRegionMngr(){
        return this.regionMngr;
    }

    protected void setMateTarget(Animal mateTarget) { 
        this.mateTarget = mateTarget; 
    }
    protected void setBaby(Animal baby) { 
        this.baby = baby; 
    }
    protected SelectionStrategy getMateStrategy() { 
        return mateStrategy; 
    }

    protected void setPosition(double x, double y){
        this.pos = new Vector2D(x,y);
    }

    protected Animal getMateTarget(){
        return this.mateTarget;
    }

    //Wolf y sheep comparten el adjustPosition:
    public void adjustPosition(){
        double x = this.getPosition().getX();
        double y = this.getPosition().getY();
        double width = this.getRegionMngr().getWidth();
        double height = this.getRegionMngr().getHeight();

        while (x >= width) x = (x - width);
        while (x < 0) x = (x + width);
        while (y >= height) y = (y - height);
        while (y < 0) y = (y + height);

        setPosition(x, y);
    }



    //Ambos, si no están DEAD, piden comida:
    public void askFood(double dt){
        if(this.getState() != State.DEAD){
            double askFood = this.getRegionMngr().getfood(this, dt);
            this.setEnergy(askFood); //Mirar que la comida esté entre 0.0 y 100
        }
    }
}