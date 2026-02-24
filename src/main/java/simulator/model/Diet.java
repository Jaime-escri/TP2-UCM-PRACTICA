package simulator.model;

public enum Diet {
    HERBIVORE,
    CARNIVORE;

    public boolean isHervibore(){
        return this == HERBIVORE;
    }

    public boolean isCarnivore(){
        return this == CARNIVORE;
    }
}