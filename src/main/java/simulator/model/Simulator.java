package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONObject;
import simulator.factories.Factory;

public class Simulator implements JSONable {
    private Factory<Animal> animalsFactory;
    private Factory<Region> regionsFactory;
    private RegionManager regionManager;
    private List<Animal> animals;
    private double time;

    public Simulator(int cols, int rows, int width, int height, Factory<Animal> animalsFactory, Factory<Region> regionsFactory) {
        this.animalsFactory = animalsFactory;
        this.regionsFactory = regionsFactory;
        this.regionManager = new RegionManager(cols, rows, width, height);
        this.animals = new ArrayList<>();
        this.time = 0.0;
    }

    private void setRegion(int row, int col, Region r) {
        regionManager.setRegion(row, col, r);
    }

    public void setRegion(int row, int col, JSONObject rJson) {
        Region r = regionsFactory.createInstance(rJson);
        setRegion(row, col, r);
    }

    private void addAnimal(Animal a) {
        animals.add(a);
        regionManager.registerAnimal(a);
    }

    public void addAnimal(JSONObject aJson) {
        Animal a = animalsFactory.createInstance(aJson);
        addAnimal(a);
    }

    public void advance(double dt) {
        time += dt;

        animals.removeIf(a -> {
            if (a.getState() == State.DEAD) {
                regionManager.unregisterAnimal(a);
                return true;
            }
            return false;
        });

        // 3. Actualizar animales vivos
        for (Animal a : animals) {
            a.update(dt);
            regionManager.updateAnimalRegion(a);
        }

        // 4. Actualizar regiones
        regionManager.updateAllRegions(dt);

        // 5. Nacimiento de bebés
        List<Animal> newborns = new ArrayList<>();
        for (Animal a : animals) {
            if (a.isPregnant()) {
                newborns.add(a.deliverBaby());
            }
        }
        // Añadimos los bebés después del bucle para evitar errores de concurrencia
        for (Animal baby : newborns) {
            addAnimal(baby);
        }
    }

    public MapInfo getMapInfo() {
        return regionManager;
    }

    public List<? extends AnimalInfo> getAnimals() {
        return Collections.unmodifiableList(animals);
    }

    public double getTime() {
        return time;
    }

    @Override
    public JSONObject asJSON() {
        JSONObject obj = new JSONObject();
        obj.put("time", time);
        obj.put("state", regionManager.asJSON());
        return obj;
    }
}
