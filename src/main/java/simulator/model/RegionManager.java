package simulator.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.json.JSONObject;

import simulator.misc.Vector2D;

public class RegionManager implements AnimalMapView{
    //Dimensiones del mapa
    private double width;
    private double height;
    private int cols;
    private int rows;
    //Tamaño de la celda
    private double regionWidth;
    private double regionHeight;
    //Matriz
    private Region[][] regions;

    private Map<Animal, Region> animalRegion;

    public RegionManager(int cols, int rows, double width, double height){
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
        // TODO: Recuperar la región anterior en esa posición, añadir todos sus animales a 'r', 
        // actualizar el mapa animalRegion y poner 'r' en regions[row][col]
    }

    public void registerAnimal(Animal a) {
        //TODO: Registrar un animal nuevo
    }

    public void unregisterAnimal(Animal a) {
        // TODO: Eliminar al animal de su región actual usando animalRegion y luego quitarlo del mapa animalRegion
    }

    public void updateAnimalRegion(Animal a) {
        // TODO: Calcular su región actual (getRegionAt). 
        // Si no es la misma en la que estaba (según animalRegion), quitarlo de la vieja, meterlo en la nueva y actualizar mapa.
    }

    public void updateAllRegions(double dt) {
        // TODO: Iterar sobre la matriz 2D y llamar al update(dt) de cada región.
    }

    @Override
    public double getfood(AnimalInfo a, double dt) {
        // TODO: Buscar la región del animal en el mapa y pedirle comida.
        return 0.0;
    }

    @Override
    public List<Animal> getAnimalsInRange(Animal e, Predicate<Animal> filter) {
        // TODO: Calcular el rango de visión e iterar por las regiones afectadas para devolver los animales
        return null;
    }
    //Hasta aquí el TODO
    @Override public int getCols() { return cols; }
    @Override public int getRows() { return rows; }
    @Override public double getWidth() { return width; }
    @Override public double getHeight() { return height; }
    @Override public double getRegionWidth() { return regionWidth; }
    @Override public double getRegionHeight() { return regionHeight; }

    @Override
    public JSONObject asJSON() {
        JSONObject out = new JSONObject();
        // TODO: Rellenar con la representación JSON
        return out;
    }

}

