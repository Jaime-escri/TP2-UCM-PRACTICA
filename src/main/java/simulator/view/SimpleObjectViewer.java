package simulator.view;

import javax.swing.*;
import java.awt.*;
import simulator.model.*;

public class SimpleObjectViewer extends JFrame {
    private Simulator sim;
    private double dt;
    private JPanel canvas;

    public SimpleObjectViewer(Simulator sim, double dt) {
        super("Ecosystem Simulator Viewer");
        this.sim = sim;
        this.dt = dt;
        initGUI();
    }

    private void initGUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawSimulation(g);
            }
        };
        canvas.setPreferredSize(new Dimension(800, 600));
        mainPanel.add(canvas, BorderLayout.CENTER);
        this.setContentPane(mainPanel);
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void showViewer() {
        this.setVisible(true);
    }

    public void update() {
        canvas.repaint();
        try { Thread.sleep((long)(dt * 1000)); } catch (InterruptedException e) {}
    }

    private void drawSimulation(Graphics g) {
        // Dibujar cuadrícula de regiones
        int cols = sim.getRegionManager().getCols();
        int rows = sim.getRegionManager().getRows();
        int cellW = canvas.getWidth() / cols;
        int cellH = canvas.getHeight() / rows;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                g.drawRect(j * cellW, i * cellH, cellW, cellH);
            }
        }

        // Dibujar animales
        for (AnimalInfo a : sim.getAnimals()) {
            if (a.getDiet() == Diet.HERBIVORE) g.setColor(Color.GREEN);
            else g.setColor(Color.RED);

            // Escalar posición al tamaño de la ventana
            int x = (int) (a.getPosition().getX() * canvas.getWidth() / sim.getRegionManager().getWidth());
            int y = (int) (a.getPosition().getY() * canvas.getHeight() / sim.getRegionManager().getHeight());
            
            g.fillOval(x - 3, y - 3, 6, 6);
        }
    }
}
