package simulator.model;

  import org.junit.jupiter.api.Test;

import simulator.misc.Vector2D;

import static org.junit.jupiter.api.Assertions.*;

  public class SheepTest {

    @Test
    void sheepStartsWithFullEnergy(){
        Sheep s = new Sheep(new SelectFirst(), new SelectFirst(), new Vector2D(100, 100));
        assertEquals(100.0, s.getEnergy());  
    }

    @Test
    void sheepStartsWithNormalState(){
        Sheep s = new Sheep(new SelectFirst(), new SelectFirst(), new Vector2D(100, 100));
        assertEquals(State.NORMAL, s.getState());
    }

    @Test
    void sheepStartsWithNoDesire(){
        Sheep s = new Sheep(new SelectFirst(), new SelectFirst(), new Vector2D(100, 100));
        assertEquals(0.0, s.getDesire());
    }

    /*@Test
    void sheepChangeStateFromNormalToMate(){
        Sheep s = new Sheep(new SelectFirst(), new SelectFirst(), new Vector2D(100,100));
        RegionManager r = new RegionManager(20, 0, 0, 0)
        Simulator sim = new Simulator(20, 15, 800, 600);
        r.registerAnimal(s);
        s.setDesire(100.0);
        s.update(10.0);
        assertEquals(State.MATE, s.getState());
    }
    */
}
