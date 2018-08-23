package liveprog.example.swing.figura;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.esfinge.liveprog.ILiveClassObserver;

/**
 * Painel de desenho para figuras. 
 */
@SuppressWarnings("serial")
public class DrawingPanel extends JPanel implements ILiveClassObserver
{
	// lista de figuras
	private List<Shape> shapeList;
	

	/**
	 * Construtor padrao.
	 */
	public DrawingPanel()
	{
		this.shapeList = new ArrayList<Shape>();
		this.setBackground(Color.WHITE);
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}

	/**
	 * Adiciona novas figuras para serem desenhadas.
	 * 
	 * @param shapes as figuras a serem adicionadas
	 */
	public void addShapes(Shape... shapes)
	{
		this.shapeList.addAll(Arrays.asList(shapes));
		this.repaint();
	}
	
	/**
	 * Remove as figuras a serem desenhadas.
	 * 
	 * @param shapes as figuras a serem removidas
	 */
	public void deleteShapes(Shape... shapes)
	{
		this.shapeList.removeAll(Arrays.asList(shapes));
		this.repaint();
	}
	
	/**
	 * Retorna as figuras sendo desenhadas.
	 * 
	 * @return as figuras sendo desenhadas
	 */
	public Shape[] getShapes()
	{
		return ( this.shapeList.toArray(
				new Shape[this.shapeList.size()]) );
	}
	
	/**
	 * Remove todas as figuras, limpando o plano de desenho.
	 */
	public void clear()
	{
		this.shapeList.clear();
		this.repaint();
	}
	
	@Override
	public void classReloaded(Class<?> newLiveClass)
	{
		// quando uma classe dinamica eh atualizada, redesenha 
		this.repaint();
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		for ( Shape shape : this.shapeList )
			shape.paint((Graphics2D) g);
	}
}
