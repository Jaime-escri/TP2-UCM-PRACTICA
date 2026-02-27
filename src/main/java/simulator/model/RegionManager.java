package simulator.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Vector2D;

public class RegionManager implements AnimalMapView{
    //Dimensiones del mapa
    private int width;
    private int height;
    private int cols;
    private int rows;
    //Tamaño de la celda
    private double regionWidth;
    private double regionHeight;
    //Matriz
    private Region[][] regions;

    private Map<AnimalInfo, Region> animalRegion;

    public RegionManager(int cols, int rows, int width, int height){
        this.width = width;
        this.height = height;
        this.cols = cols;
        this.rows = rows;
        this.regionWidth = width/cols;
        this.regionHeight = height/rows;
        this.regions = new Region[rows][cols];
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                this.regions[i][j] = new DefaultRegion();
            }
        }

        this.animalRegion = new HashMap<>();
    }

    public Region getRegionInstance(AnimalInfo a){
        Vector2D pos = a.getPosition();
        int col = (int) (pos.getX() / regionWidth);
        int row = (int) (pos.getY() / regionHeight);
        if(col>=cols) col = cols-1;
        if(row >= rows) row = rows- 1;
        return regions[row][col];
    }

    //Hacer juan
    public void setRegion(int row, int col, Region r) {
        //Guardo la region 
        Region oldRegion = regions[row][col];

        // Cojo todos los animales de la antigua región y los añado uno a uno a la nueva region
        for (Animal a : oldRegion.getAnimals()) {
            r.addAnimal(a);

            // 3. Actualizar el mapa animalRegion para que cada animal apunte a la nueva región
            animalRegion.put(a, r);
        }

        // por último actualizo y pongo la nueva region en las posiciones solicitadas por el usuario
        regions[row][col] = r;
    }

    public void registerAnimal(Animal a) {
        //Llamar al método init del animal pasándole una referencia a este gestor (this) esto es vital para que el animal pueda consultar el mapa después
        a.init(this);
        //Obtengo al region
        Region r = getRegionInstance(a);

        //Una vez obtenida la region, añado al animal
        r.addAnimal(a);

        //Aviso que el animal esta en la region
        animalRegion.put(a, r); 
    }

    public void unregisterAnimal(Animal a) {
        Region r = animalRegion.get(a);
        if (r != null) {
            r.removeAnimal(a);
            animalRegion.remove(a);
        }
    }

    public void updateAnimalRegion(Animal a) {
        Region currentReg = animalRegion.get(a);
        Region newReg = getRegionInstance(a);
    
        if (currentReg != newReg) {
            currentReg.removeAnimal(a);
            newReg.addAnimal(a);
            animalRegion.put(a, newReg);
        }
    }

    public void updateAllRegions(double dt) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                regions[i][j].update(dt);
            }
        }
    }

    @Override
    public double getfood(AnimalInfo a, double dt) {
        Region r = animalRegion.get(a);
        if (r != null) {
            return r.getfood(a, dt);
        }
        return 0.0;
    }

    @Override
    public List<Animal> getAnimalsInRange(Animal e, Predicate<Animal> filter) {
        List<Animal> result = new java.util.ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                for (Animal a : regions[i][j].getAnimals()) {
                    if (a != e && e.getPosition().distanceTo(a.getPosition()) <= e.getSightRange() && filter.test(a)) {
                        result.add(a);
                    }
                }
            }
        }
        return result;
    }
    //Hasta aquí el TODO
    @Override public int getCols() { return cols; }
    @Override public int getRows() { return rows; }
    @Override public int  getWidth() { return width; }
    @Override public int getHeight() { return this.height; }
    @Override public double getRegionWidth() { return regionWidth; }
    @Override public double getRegionHeight() { return regionHeight; }

    @Override
    public JSONObject asJSON() {
        JSONObject out = new JSONObject();
        JSONArray regionsArray = new JSONArray();
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JSONObject regionData = new JSONObject();

                // Cada elemento de la lista es un JSONObject con fila, columna y los datos de
                // la región
                regionData.put("row", i);
                regionData.put("col", j);

                // "data" contiene lo que devuelve el asJSON() de la clase Region
                regionData.put("data", regions[i][j].asJSON());

                regionsArray.put(regionData);
            }
        }

        // La clave principal debe llamarse "regions"
        out.put("regions", regionsArray);

        return out;
    }

}
