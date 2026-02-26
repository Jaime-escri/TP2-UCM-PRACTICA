package simulator.control;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.model.AnimalInfo;
import simulator.model.MapInfo;
import simulator.model.Simulator;
import simulator.view.SimpleObjectViewer;

public class Controller {
    private Simulator sim;

    public Controller(Simulator sim){
        this.sim = sim;
    }


    public void loadData(JSONObject data){
        if(data.has("regions")){
            JSONArray regionArray = data.getJSONArray("regions");
            for(int i= 0; i < regionArray.length(); i++){
                JSONObject region = regionArray.getJSONObject(i);
                JSONArray rows = region.getJSONArray("row");
                JSONArray cols = region.getJSONArray("col");
                JSONObject spec = region.getJSONObject("spec");

                int r1 = rows.getInt(0);
                int r2 = rows.getInt(1);
                int c1 = cols.getInt(0);
                int c2 = cols.getInt(1);

                for(int r = r1; r <= r2; r++){
                    for(int c = c1; c<=c2;c++){
                        this.sim.setRegion(r, c, spec);
                    }
                }
            }
        }

        if(data.has("animals")){
            JSONArray animalArray = data.getJSONArray("animals");
            for(int i = 0; i < animalArray.length(); i++){
                JSONObject animal = animalArray.getJSONObject(i);
                JSONArray amount = animal.getJSONArray("amount");
                int n = amount.getInt(0);
                JSONObject spec = animal.getJSONObject("spec");

                for(int j = 0; j < n; j++){
                    this.sim.addAnimal(spec);
                }
            }
        }
    }

    private List<SimpleObjectViewer.ObjInfo> toAnimalsInfo(List<? extends AnimalInfo> animals) {
    List<SimpleObjectViewer.ObjInfo> ol = new ArrayList<>(animals.size());
    for (AnimalInfo a : animals) {
        // Se extrae el código genético (etiqueta), la posición X e Y, y un tamaño (ej. 8)
        ol.add(new SimpleObjectViewer.ObjInfo(a.getGeneticCode(), (int) a.getPosition().getX(), (int) a.getPosition().getY(), 8));
    }
    return ol;
}


   public void run(double t, double dt, boolean sv, OutputStream out) {
        JSONObject result = new JSONObject(); // Para el JSON final
        PrintStream p = new PrintStream(out);
        SimpleObjectViewer view = null;

        // Guardamos el estado inicial
        result.put("in", sim.asJSON());

        if (sv) {
            MapInfo m = sim.getMapInfo();
            view = new SimpleObjectViewer("[ECOSYSTEM]", m.getWidth(), m.getHeight(), m.getCols(), m.getRows());
            view.update(toAnimalsInfo(sim.getAnimals()), sim.getTime(), dt);
        }

        // Bucle de simulación
        while (sim.getTime() <= t) {
            sim.advance(dt);
            if (sv) view.update(toAnimalsInfo(sim.getAnimals()), sim.getTime(), dt);
        }

        // Guardamos el estado final y lo imprimimos todo junto
        result.put("out", sim.asJSON());
        p.println(result.toString(2)); // El '2' es para que el JSON sea legible

        if (sv) view.close();
    }
}
