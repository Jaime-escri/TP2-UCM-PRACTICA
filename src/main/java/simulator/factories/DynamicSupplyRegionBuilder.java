package simulator.factories;



import org.json.JSONObject;
import simulator.model.DynamicSupplyRegion;
import simulator.model.Region;

public class DynamicSupplyRegionBuilder extends Builder<Region> {
    public DynamicSupplyRegionBuilder(){
        super("dynamic", "Dynamic Supply Region");
    }

    protected Region createInstance(JSONObject data) {
        double factor;
        if(!data.has("factor")){
            factor = 2.0;
        }else{
            factor = data.getDouble("factor");
        }

        double food;
        if(data.has("food")){
            food = data.getDouble("food");
        }else{
            food = 1000.0;
        }

        return new DynamicSupplyRegion(food, factor);
    }

    
}
