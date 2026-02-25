package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;
import simulator.model.Sheep;

public class SheepBuilder extends Builder<Animal>{
    private Factory<SelectionStrategy> strategyFactory;

    public SheepBuilder(Factory<SelectionStrategy> strategyFactory){
        super("sheep" , "Builder for sheep");
        this.strategyFactory = strategyFactory;
    }

    protected Animal createInstance(JSONObject data){
        SelectionStrategy mateStrategy = new SelectFirst();
        SelectionStrategy dangerStrategy = new SelectFirst();

        if(data.has("mate_strategy")){
            mateStrategy = strategyFactory.createInstance(data.getJSONObject("mate_strategy"));
        }

        if(data.has("danger_strategy")){
            dangerStrategy = strategyFactory.createInstance(data.getJSONObject("danger_strategy"));
        }

        Vector2D pos = null;
        if(data.has("pos")){
            JSONObject p = data.getJSONObject("pos");
            JSONArray x_range = p.getJSONArray("x_range");
            JSONArray y_range = p.getJSONArray("y_range");

            double x = x_range.getDouble(0) + Utils.RAND.nextDouble() * (x_range.getDouble(1) - x_range.getDouble(0));
            double y = y_range.getDouble(0) + Utils.RAND.nextDouble() * (y_range.getDouble(1) - y_range.getDouble(0));
            pos = new Vector2D(x,y);
        }

        return new Sheep(mateStrategy, dangerStrategy, pos);
    }

}
