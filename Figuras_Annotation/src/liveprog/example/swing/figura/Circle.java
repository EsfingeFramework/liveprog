package liveprog.example.swing.figura;

import java.awt.Graphics2D;

import org.esfinge.liveprog.annotation.LiveClass;

/**
 * Figura que representa um circulo.
 */
@LiveClass
public class Circle extends Shape
{
	// diametro do circulo
	private int diameter;

	
	/**
	 * Retorna o diametro do circulo.
	 * 
	 * @return o diametro do circulo
	 */
	public int getDiameter()
	{
		return diameter;
	}

	/**
	 * Atribui o diametro do circulo.
	 *  
	 * @param diameter o diametro do circulo
	 */
	public void setDiameter(int diameter)
	{
		this.diameter = diameter;
	}

	@Override
	public void drawShape(Graphics2D g)
	{
		if ( this.isFill() )
			g.fillOval(this.getPosX(),  this.getPosY(), 
					this.diameter, this.diameter);
		else
			g.drawOval(this.getPosX(),  this.getPosY(), 
					this.diameter, this.diameter);

//		g.drawString("Circ", this.getPosX(), this.getPosY()-10);
	}
}
