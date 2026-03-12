package simulator.model;

public interface Observable<EcoSysObserver> {
    void addObserver(EcoSysObserver o);
    void removeObserver(EcoSysObserver o);
}
