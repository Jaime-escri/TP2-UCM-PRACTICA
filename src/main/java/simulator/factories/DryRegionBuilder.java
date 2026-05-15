package simulator.factories;



import org.json.JSONObject;

import simulator.model.DryRegion;
import simulator.model.Region;

public class DryRegionBuilder extends Builder<Region> {
    public DryRegionBuilder(){
        super("dry", "Dry food supply");
    }

    protected Region createInstance(JSONObject data) {
        double r1;
        if(data.has("r1")){
            r1 = data.getDouble("r1");
        }else{
            r1 = 0.95;
        }

        double r2;
        if(data.has("r2")){
            r2 = data.getDouble("r2");
        }else{
            r2 = 0.95;
        }

        double n;
        if(data.has("n")){
            n = data.getDouble("n");
        }else{
            n = 10.0;
        }

        double m;
        if(data.has("m")){
            m = data.getDouble("m");
        }else{
            m = 5.0;
        }

        return new DryRegion(r1, r2, n, m);
    }

    public void  fillInData(JSONObject o){
        o.put("r1", "factor de reduccion de la comida en sequia");
        o.put("r2", "factor de reduccion de r1");
        o.put("n", "duracion del periodo normal");
        o.put("m", "duracion maxima de la sequia");
    }
}

