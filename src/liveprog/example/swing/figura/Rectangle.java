package liveprog.example.swing.figura;

import java.awt.Graphics2D;

import org.esfinge.liveprog.annotation.LiveClass;

/**
 * Figura que representa um retangulo.
 */
@LiveClass
public class Rectangle extends Shape
{
	// algura do retangulo
	private int height;
	
	// comprimento do retangulo
	private int width;
	
	
	/**
	 * Retorna a altura do retangulo.
	 * 
	 * @return a altura do retangulo
	 */
	public int getHeight()
	{
		return height;
	}

	/**
	 * Atribui a altura do triangulo.
	 * 
	 * @param altura a altura do triangulo
	 */
	public void setHeight(int altura)
	{
		this.height = altura;
	}

	/**
	 * Retorna o comprimento do retangulo.
	 * 
	 * @return o comprimento do retangulo
	 */
	public int getWidth()
	{
		return width;
	}

	/**
	 * Atribui o comprimento do retangulo.
	 * 
	 * @param comprimento o comprimento do retangulo
	 */
	public void setWidth(int comprimento)
	{
		this.width = comprimento;
	}

	@Override
	public void drawShape(Graphics2D g)
	{		
		if ( this.isFill() )
			g.fillRect(this.getPosX(),  this.getPosY(), 
					this.width, this.height);
		else
			g.drawRect(this.getPosX(),  this.getPosY(), 
					this.width, this.height);
		
//		g.drawString("Rect", this.getPosX(), this.getPosY()-10);
	}
}
