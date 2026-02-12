package simulator.model;
import java.util.Vector;
import javax.swing.text.Position;
import org.json.JSONArray;
import org.json.JSONObject;
import simulator.misc.Utils;
import simulator.misc.Vector2D;

public abstract class Animal implements Entity, AnimalInfo{
    private String geneticCode;
    private Diet diet;
    protected State state;
    private Vector2D pos;
    private Vector2D dest;
    protected double energy;
    private double speed;
    private double age;
    private double desire;
    private double sightRange;
    private Animal mateTarget;
    private Animal baby;
    private AnimalMapView regionMngr;
    private SelectionStrategy mateStrategy;
    private Boolean pregnant;

    protected Animal(String geneticCode, Diet diet, double sightRange, double initSpeed, SelectionStrategy mateStrategy, Vector2D pos) {
        if (geneticCode == null || geneticCode.isBlank()) {
            throw new IllegalArgumentException("El código genético no puede estar vacío.");
        }
        if (sightRange <= 0) {
            throw new IllegalArgumentException("El rango de visión debe ser un número positivo.");
        }
        if (initSpeed <= 0) {
            throw new IllegalArgumentException("La velocidad inicial debe ser un número positivo.");
        }
        if (mateStrategy == null) {
            throw new IllegalArgumentException("La estrategia de apareamiento no puede ser nula.");
        }
        if (diet == null) {
            throw new IllegalArgumentException("La dieta no puede ser nula.");
        }

        this.geneticCode = geneticCode;
        this.diet = diet;
        this.sightRange = sightRange;
        this.mateStrategy = mateStrategy;
        this.pos = pos;
        this.speed = Utils.getRandomizedParameter(initSpeed, 0.1);
        this.state = State.NORMAL;
        this.energy = 100.0;
        this.desire = 0.0;
        this.dest = null;
        this.mateTarget = null;
        this.baby = null;
        this.regionMngr = null;
        this.pregnant = false;
    }

    protected Animal(Animal p1, Animal p2){
        this.dest = null;
        this.mateTarget = null;
        this.baby = null;
        this.regionMngr = null;
        this.state = State.NORMAL;
        this.desire = 0.0;
        this.geneticCode = p1.getGeneticCode();
        this.diet = p1.getDiet();
        this.mateStrategy = p2.getmateStrategy();
        this.energy = (p1.getEnergy() + p2.getEnergy())/2;
        this.pos = p1.getPosition().plus(Vector2D.getRandomVector(-1,1).scale(60.0*(Utils.RAND.nextGaussian()+1)));
        this.sightRange = Utils.getRandomizedParameter((p1.getSightRange()+p2.getSightRange())/2,0.2);
        this.speed = Utils.getRandomizedParameter((p1.getSpeed()+p2.getSpeed())/2, 0.2);
    }

    public String getGeneticCode(){ return this.geneticCode;}

    public Diet getDiet(){ return this.diet;}

    public State getState(){ return this.state;}

    public Vector2D getPos(){ return this.pos;}

    public Vector2D getDest(){ return this.dest;}

    public double getEnergy(){ return this.energy;}

    public double getSpeed(){ return this.speed;}

    public double getAge(){ return this.age;}

	public double getSightRange(){ return this.sightRange;}

	public boolean isPregnant() { return this.baby != null; }

    public double getDesire(){ return this.desire;}

    public SelectionStrategy getmateStrategy(){ return this.mateStrategy;}

    public AnimalMapView getRegionMngr(){ return this.regionMngr;}

    public Animal getBaby(){ return this.baby;}

    public Animal getMateTarget(){ return this.mateTarget;}

    private void init(AnimalMapView regMngr){
        regionMngr = regMngr;

        if(this.pos == null){
            double x = Utils.RAND.nextDouble() * regMngr.getWidth();
            double y = Utils.RAND.nextDouble() * regMngr.getHeight();
            this.pos = new Vector2D(x, y);
        }else {
            this.pos = adjustPosition(this.pos.getX(), this.pos.getY(), regMngr.getWidth(), regMngr.getHeight());
        }

        double destX = Utils.RAND.nextDouble() * regMngr.getWidth();
        double destY = Utils.RAND.nextDouble() * regMngr.getHeight();
        this.dest = new Vector2D(destX, destY);
    }

    public Animal deliverBaby() {
        Animal babyToDeliver = this.baby;
        this.baby = null;
        return babyToDeliver;
    }

    protected void move(double speed){
        pos = pos.plus(dest.minus(pos).direction().scale(speed));
    }

    protected void setState(State state) {
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
            setDeadStateAction();
            break;

    
	}

    @Override
    public JSONObject asJSON() {
        JSONObject jo = new JSONObject();
        JSONArray posArray = new JSONArray();
        posArray.put(this.pos.getX());
        posArray.put(this.pos.getY());


        jo.put("pos", posArray);
        jo.put("gcode", this.geneticCode);
        jo.put("diet", this.diet.toString());
        jo.put("state", this.state.toString());

        return jo;
    }

    protected boolean isOutOfMap(Vector2D p) {
        return p.getX() <= 0 || p.getX() >= regionMngr.getWidth() - 1 || p.getY() <= 0 || p.getY() >= regionMngr.getHeight() - 1;
    }
}