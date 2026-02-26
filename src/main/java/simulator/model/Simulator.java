package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import simulator.factories.Factory;

public class Simulator implements JSONable{
    private Factory<Animal> animals;
    private Factory<Region> regions;
    private RegionManager regMnr;
    private List<Animal> animalList;
    private double time;

    public Simulator(int cols, int rows, int width, int height, Factory<Animal> animalsFactory, Factory<Region> regionsFactory){
        this.regMnr = new RegionManager(cols, rows, width, height);
        this.animalList = new ArrayList<>();
        this.animals = animalsFactory;
        this.regions = regionsFactory;
        this.time = 0.0;
    }

    private void setRegion(int row, int col, Region r){
        this.regMnr.setRegion(row,col,r);
    }

    public void setRegion(int row, int col, JSONObject rJson){
        Region r = this.regions.createInstance(rJson);
        setRegion(row, col, r);
    }

    private void addAnimal(Animal a){
        animalList.add(a);
        regMnr.registerAnimal(a);
    }

    public void addAnimal(JSONObject aJson){
        Animal a = this.animals.createInstance(aJson);
        addAnimal(a);
    }

    public MapInfo getMapInfo(){
        return regMnr;
    }

    public List<? extends AnimalInfo> getAnimals(){
        return Collections.unmodifiableList(animalList);
    }

    public double getTime(){
        return this.time;
    }

    public void advance(double dt){
        time += dt;

        animalList.removeIf(a -> {
            if (a.getState() == State.DEAD) {
                regMnr.unregisterAnimal(a);
                return true;
            }
            return false;
        });

        for (Animal a : animalList) {
            a.update(dt);
            regMnr.updateAnimalRegion(a);
        }

        regMnr.updateAllRegions(dt);

        List<Animal> newborns = new ArrayList<>();
        for (Animal a : animalList) {
            if (a.isPregnant()) {
                newborns.add(a.deliverBaby());
            }
        }

        for (Animal baby : newborns) {
            addAnimal(baby);
        }
    }

    @Override
    public JSONObject asJSON() {
        JSONObject obj = new JSONObject();
        obj.put("time", time);
        obj.put("state", regMnr.asJSON());
        return obj;
    }

    
}
