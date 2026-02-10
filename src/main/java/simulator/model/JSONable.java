package simulator.model;

public interface JSONable {
  default public JSONObject asJSON() {
    return new JSONObject();
  }
}