package simulator.model;

public class Wolf extends Animal {
    
}

/*
* `abstract protected void setNormalStateAction()`: se implementa en las subclases para ejecutar una acción
  complementaria al cambio de estado correspondiente (p.ej, en la clase `Sheep` pone `mateTarget` y `dangerSource` a
  `null` -- ver la clase [`Sheep`](#la-clase-sheep)).
* `abstract protected void setMateStateAction()`: se implementa en las subclases para ejecutar una acción complementaria
  al cambio de estado correspondiente (p.ej, en la clase `Sheep` pone `dangerSource` a `null` -- ver la clase [
  `Sheep`](#la-clase-sheep)).
* `abstract protected void setHungerStateAction()`: se implementa en las subclases para ejecutar una acción
  complementaria al cambio de estado correspondiente (p.ej, en la clase `Wolf` pone `mateTarget` a `null` -- ver la
  clase [`Wolf`](#la-clase-wolf)).
* `abstract protected void setDangerStateAction()`: se implementa en las subclases para ejecutar una acción
  complementaria al cambio de estado correspondiente (p.ej, en la clase `Sheep` pone `mateTarget` a `null` -- ver la
  clase [`Sheep`](#la-clase-sheep)).
* `abstract protected void setDeadStateAction()`: se implementa en las subclases para ejecutar una acción complementaria
  al cambio de estado correspondiente (p.ej, en la clase `Sheep` pone `mateTarget` y `dangerSource` a `null` -- ver la
  clase [`Sheep`](#la-clase-sheep)). 
  */