package simulator.model;

public enum Diet {
    HERVIBORE,
    CARNIVORE;

    public boolean isHervibore(){
        return this == HERVIBORE;
    }

    public boolean isCarnivore(){
        return this == CARNIVORE;
    }
}