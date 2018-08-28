package liveprog.example.swing;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.esfinge.liveprog.LiveClassFactory;
import org.esfinge.liveprog.LiveClassFactoryBuilder;

import liveprog.example.swing.figura.Circle;
import liveprog.example.swing.figura.DrawingPanel;
import liveprog.example.swing.figura.Rectangle;

/**
 * Classe principal para demonstrar o uso do framework LiveProg. 
 */
public class Main
{
	public static void main(String[] args) throws Exception
	{
		// inicializa a fabrica de objetos dinamicos
		LiveClassFactory factory = new LiveClassFactoryBuilder()
											.inTestMode()
											.monitoringDirectory("bin")
											.includingSubdirs()
											.usingDatabaseFile("db/liveclass.db")
											.build();
		
		// cria o retangulo como objeto dinamico
		Rectangle r1 = factory.createLiveObject(Rectangle.class);
		r1.setPosX(60);
		r1.setPosY(60);
		r1.setHeight(50);
		r1.setWidth(100);
		r1.setFill(true);
		
		// cria o retangulo como objeto estatico
		Rectangle r2 = new Rectangle();
		r2.setPosX(400);
		r2.setPosY(300);
		r2.setHeight(100);
		r2.setWidth(100);
		r2.setColor(Color.RED);
		r2.setStroke(2);
		
		// cria o circulo como objeto dinamico
		Circle c1 = factory.createLiveObject(Circle.class);
		c1.setPosX(70);
		c1.setPosY(300);
		c1.setDiameter(100);
		c1.setColor(Color.BLUE);
		c1.setStroke(3);
		
		// cria o circulo como objeto estatico
		Circle c2 = new Circle();
		c2.setPosX(250);
		c2.setPosY(100);
		c2.setDiameter(150);
		c2.setColor(Color.YELLOW);
		c2.setFill(true);
		
		// painel de desenho
		DrawingPanel drawingPanel = new DrawingPanel();
		
		// registra o painel como um observador de classes dinamicas
		factory.addLiveClassObserver(Rectangle.class, drawingPanel);
		factory.addLiveClassObserver(Circle.class, drawingPanel);
		
		// botoes para desenhar as figuras
		JButton btnRect = new JButton("Retangulos");
		btnRect.addActionListener(evt -> {drawingPanel.clear(); 
		                                  drawingPanel.addShapes(r1,r2);});
		
		JButton btnCirc = new JButton("Circulos");
		btnCirc.addActionListener(evt -> {drawingPanel.clear(); 
		                                  drawingPanel.addShapes(c1,c2);});
				
		JButton btnAllShapes = new JButton("Todos");
		btnAllShapes.addActionListener(evt -> {drawingPanel.clear(); 
		                                       drawingPanel.addShapes(r1,r2,c1,c2);});
		
		JButton btnClear = new JButton("Limpar");
		btnClear.addActionListener(evt -> drawingPanel.clear());

		//
		JPanel pnlButtons = new JPanel();
		pnlButtons.add(btnRect);
		pnlButtons.add(btnCirc);
		pnlButtons.add(btnAllShapes);
		pnlButtons.add(btnClear);
		
		// janela principal
		JFrame frame = new JFrame("Demonstrando o framework LiveProg com Java Swing!");
		frame.getContentPane().add(drawingPanel, BorderLayout.CENTER);
		frame.getContentPane().add(pnlButtons, BorderLayout.SOUTH);
		frame.setSize(600,  600);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
