package simulator.model;

public interface MapInfo extends JSONable {
    public int getCols();
    public int getRows();
    public double getWidth();
    public double getHeight();
    public double getRegionWidth();
    public double getRegionHeight();
}