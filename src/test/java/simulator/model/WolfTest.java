package simulator.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import simulator.misc.Vector2D;

public class WolfTest {
    @Test
    void sheepStartsWithFullEnergy(){
        Wolf w = new Wolf(new SelectFirst(), new SelectFirst(), new Vector2D(100, 100));
        assertEquals(100.0, w.getEnergy());  
    }

    @Test
    void sheepStartsWithNormalState(){
        Wolf w = new Wolf(new SelectFirst(), new SelectFirst(), new Vector2D(100, 100));
        assertEquals(State.NORMAL, w.getState());
    }

    @Test
    void sheepStartsWithNoDesire(){
        Sheep s = new Sheep(new SelectFirst(), new SelectFirst(), new Vector2D(100, 100));
        assertEquals(0.0, s.getDesire());
    }

    
}
