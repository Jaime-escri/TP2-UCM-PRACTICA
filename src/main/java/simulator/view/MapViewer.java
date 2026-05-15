package simulator.view;

import simulator.model.AnimalInfo;
import simulator.model.MapInfo;
import simulator.model.State;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/*
 * An incomplete version of  the map viewer, to be completed by students.
 */

@SuppressWarnings("serial")
public class MapViewer extends AbstractMapViewer {

	// Anchura/altura de la simulación -- se supone que siempre van a ser
	// iguales al tamaño del componente
	//
	// Width/height of the simulation -- they will always be equal to the size
	// of the component
	//
	private int width;
	private int height;

	// Número de filas/columnas de la simulación (regiones)
	//
	// Number of rows/cols of the simulation (regions)
	//
	private int rows;
	private int cols;

	// Anchura/altura de una región
	//
	// Width/height of a region
	//
	int rWidth;
	int rHeight;

	// Mostramos sólo animales con este estado. Los posibles valores de currState
	// son null, y los valores de Animal.State.values(). Si es null mostramos todo.
	//
	// We show the animals that have this state. The possible values of currentState
	// are: null and the values returned by Animal.State.values(). If it is null we
	// show all animals.
	//
	State currentState;

	// En estos atributos guardamos la lista de animales y el tiempo que hemos
	// recibido la última vez para dibujarlos.
	//
	// The value of these attributes are the list of animals and the time that we
	// have received in the notification (those will be shown).
	//
	volatile private Collection<AnimalInfo> objs;
	volatile private Double time;

	// Una clase auxiliar para almacenar información sobre una especie.
	//
	// An auxiliary class to store information about species.
	//
	private static class SpeciesInfo {
		private Integer count;
		private Color color;

		SpeciesInfo(Color color) {
			count = 0;
			this.color = color;
		}
	}

	// Un mapa para la información sobre las especies.
	//
	// A map with the information for each species.
	//
	Map<String, SpeciesInfo> kindsInfo = new HashMap<>();

	// El font que usamos para dibujar texto.
	//
	// The font to be used for drawing text.
	//
	private Font textFont = new Font("Arial", Font.BOLD, 12);

	// Indica si mostramos el texto la ayuda o no.
	//
	// Indicates if the 'help' information is visible or hidden.
	//
	private boolean showHelp;

	public MapViewer() {
		initGUI();
	}

	private void initGUI() {

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyChar()) {
				case 'h':
					showHelp = !showHelp;
					repaint();
					break;
				case 's':
					// TODO Cambiar currState al siguiente (de manera circular). Después de null
					//      viene el primero de Animal.State.values() y después del último viene null.
					if(currentState == null) currentState = State.values()[0];
					else if(currentState.ordinal() == State.values().length-1) currentState = null;
					else currentState = State.values()[currentState.ordinal() + 1];
					repaint();
				default:
				}
			}

		});

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				// Esto es necesario para capturar las teclas cuando el ratón está sobre este
				// componente.
				//
				// This is needed to capture keystroke when the mouse is over this component.
				//
				requestFocus();
			}
		});

		// Por defecto mostramos todos los animales.
		//
		// By default, we show all animals.
		//
		currentState = null;

		// Por defecto mostramos el texto de ayuda.
		//
		// By default, the 'help' message is visible.
		//
		showHelp = true;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D gr = (Graphics2D) g;
		gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// Cambiar el font para dibujar texto.
		g.setFont(textFont);

		// Dibujar fondo blanco.
		gr.setBackground(Color.WHITE);
		gr.clearRect(0, 0, width, height);

		// Dibujar los animales, el tiempo, información sobre las especies, etc.
		if (objs != null) drawObjects(gr, objs, time);

		if(showHelp){
			drawStringWithRect(gr, 10,10, "h: toggle help");
			drawStringWithRect(gr, 10,30, "s: show animals of a specific state");
		}

	}

	private boolean visible(AnimalInfo a) {
		return currentState == null || a.getState() == currentState;
	}

	private void drawObjects(Graphics2D g, Collection<AnimalInfo> animals, Double time) {

		// TODO Dibujar el grid de regiones.
		for(int i = 0; i <= this.cols; i++){
			g.drawLine(i * rWidth, 0, i * rWidth, height);
		}

		for(int i = 0; i <= this.rows; i++){
			g.drawLine(0, i*rHeight ,width, i*rHeight);
		}


		// Dibujar los animales.
		
		for (AnimalInfo a : animals) {
			if (visible(a)) {

				// La información sobre la especie de 'a'.
				SpeciesInfo speciesInfo = kindsInfo.get(a.getGeneticCode());

				// TODO Si espInfo es null, añade una entrada correspondiente al mapa. Para el
				// color usa ViewUtils.getColor(a.getGeneticCode()).
				if(speciesInfo == null){
					speciesInfo = new SpeciesInfo(ViewUtils.getColor(a.getGeneticCode()));
					this.kindsInfo.put(a.getGeneticCode(), speciesInfo);
				}

				// TODO Incrementar el contador de la especie (es decir el contador dentro de
				// speciesInfo).
				speciesInfo.count += 1;

				// TODO Dibujar el animal en la posición correspondiente, usando el color
				// speciesInfo.color. Su tamaño tiene que ser relativo a su edad, por ejemplo
				// edad/2+2. Se puede dibujar usando fillRoundRect, fillRect o fillOval.
				g.setColor(speciesInfo.color);
				g.fillOval((int)a.getPosition().getX(), (int)a.getPosition().getY(),  (int)(a.getAge()/2 + 2),  (int)(a.getAge()/2 + 2));
			}
		}

		// TODO Dibujar la etiqueta del estado visible, usando currState.toString(), si no
		//      es null.
		if(currentState != null){
			drawStringWithRect(g,  10, 40, currentState.toString());
		}


		// TODO Dibujar la etiqueta del tiempo. Para escribir solo 3 decimales puede
		//      usar String.format("%.3f", time).
		drawStringWithRect(g, 10, 20, String.format("%.3f", time));

		// TODO Dibujar la información de todas la especies. Al final de la iteración
		//      poner el contador de la especie correspondiente a 0 (para resetear el cuento)
		int y = 20;
		for (Entry<String, SpeciesInfo> e : kindsInfo.entrySet()) {
			drawStringWithRect(g, 60, y, e.getKey()+ ": " + e.getValue().count);
			y+=20;
			e.getValue().count = 0;
		}
	}

	// Un método que dibujar un texto con un rectángulo.

	void drawStringWithRect(Graphics2D g, int x, int y, String s) {
		Rectangle2D rect = g.getFontMetrics().getStringBounds(s, g);
		g.drawString(s, x, y);
		g.drawRect(x - 1, y - (int) rect.getHeight(), (int) rect.getWidth() + 1, (int) rect.getHeight() + 5);
	}

	@Override
	public void update(List<AnimalInfo> objs, Double time) {
		// TODO Almacenar objs y time en los atributos correspondientes y llamar a
		this.time = time;
		this.objs = objs;
		repaint();
		//      repaint() para redibujar el componente.
		//
		//      Store objs and time in the corresponding fields, and call repaint() to
		//      redraw the component.
	}

	@Override
	public void reset(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Actualizar los atributos width, height, cols, rows, etc.
		this.width = map.getWidth();
		this.height = map.getHeight();
		this.cols = map.getCols();
		this.rows = map.getRows();
		this.rHeight = height/rows;
		this.rWidth = width/cols;
		this.time = time;

		// Esto cambia el tamaño del componente, y así cambia el tamaño de la ventana
		// porque en MapWindow llamamos a pack() después de llamar a reset.
		setPreferredSize(new Dimension(map.getWidth(), map.getHeight()));

		// Dibuja el estado.
		
		update(animals, time);
	}

}