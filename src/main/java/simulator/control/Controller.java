package simulator.control;

import java.io.OutputStream;
import java.io.PrintStream;

import org.json.JSONArray;
import org.json.JSONObject;

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
                int n = animal.getInt("amount");
                JSONObject spec = animal.getJSONObject("spec");

                for(int j = 0; j < n; j++){
                    this.sim.addAnimal(spec);
                }
            }
        }
    }

    public void run(double t, double dt, boolean sv, OutputStream out) {
        PrintStream p = new PrintStream(out);
        JSONObject result = new JSONObject();

        // INICIALIZAR VISUALIZADOR
        SimpleObjectViewer view = null;
        if (sv) {
            view = new SimpleObjectViewer(sim, dt);
            view.showViewer();
        }

        result.put("in", sim.asJSON());

        while (sim.getTime() < t) {
            sim.advance(dt);

            // ACTUALIZAR VISUALIZADOR
            if (sv && view != null) {
                view.update();
            }
        }

        result.put("out", sim.asJSON());
        p.println(result.toString());
        p.flush();
    }
}

/* public void run(double t, double dt, boolean sv, OutputStream out){
        PrintStream p = new PrintStream(out);
        JSONObject result = new JSONObject();
        result.put("in", sim.asJSON());
        while(sim.getTime()<t){
            sim.advance(dt);
        }
        result.put("out", sim.asJSON());
        p.println(result.toString());
        p.flush();
    } */