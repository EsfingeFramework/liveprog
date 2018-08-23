package liveprog.example.swing.figura;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Classe base para figuras.
 */
public abstract class Shape
{
	// posicao X
	private int posX;
	
	// posicao Y
	private int posY;
	
	// se a figura deve ser preenchida ao desenhar
	private boolean fill;
	
	// espessura do traco a ser utilizado no desenho
	private int stroke;
	
	// cor para o desenho
	private Color color;
	

	/**
	 * Construtor padrao.
	 */
	public Shape()
	{
		this.posX = 0;
		this.posY = 0;
		this.fill = false;
		this.color = Color.BLACK;
		this.stroke = 1;
	}
	
	/**
	 * Retorna a posicao X da figura no plano de desenho.
	 * 
	 * @return a posicao X da figura
	 */
	public int getPosX()
	{
		return posX;
	}

	/**
	 * Atribui a posicao X da figura no plano de desenho.
	 * 
	 * @param posX a posicao X da figura
	 */
	public void setPosX(int posX)
	{
		this.posX = posX;
	}

	/**
	 * Retorna a posicao Y da figura no plano de desenho.
	 * 
	 * @return a posicao Y da figura
	 */
	public int getPosY()
	{
		return posY;
	}

	/**
	 * Atribui a posicao Y da figura no plano de desenho.
	 * 
	 * @param posY a posicao Y da figura
	 */
	public void setPosY(int posY)
	{
		this.posY = posY;
	}

	/**
	 * Verifica se a figura deve ser preenchida ao ser desenhada.
	 * 
	 * @return <b>true</b> para a figura ser desenhada preenchida, 
	 * <b>false</b> para ser somente delineada
	 */
	public boolean isFill()
	{
		return fill;
	}

	/**
	 * Atribui se a figura deve ser preenchida ao ser desenhada.
	 * 
	 * @param fill <b>true</b> para a figura ser desenhada preenchida, 
	 * <b>false</b> para ser somente delineada
	 */
	public void setFill(boolean fill)
	{
		this.fill = fill;
	}

	/**
	 * Retorna a cor de desenho da figura.
	 * 
	 * @return a cor da figura
	 */
	public Color getColor()
	{
		return color;
	}

	/**
	 * Atribui a cor de desenho da figura.
	 * 
	 * @param color a cor da figura
	 */
	public void setColor(Color color)
	{
		this.color = color;
	}

	/**
	 * Retorna a espessura do traco para desenho da figura.
	 *  
	 * @return a espessura do traco de desenho
	 */
	public int getStroke()
	{
		return stroke;
	}

	/**
	 * Atribui a espessura do traco para desenho da figura.
	 * 
	 * @param stroke a espessura do traco de desenho
	 */
	public void setStroke(int stroke)
	{
		this.stroke = stroke;
	}
	
	/**
	 * Realiza a pintura da figura no plano de desenho.
	 *  
	 * @param g o objeto grafico do plano de desenho
	 */
	public final void paint(Graphics2D g)
	{
		this.prepareGraphics(g);
		this.drawShape(g);
	}
	
	/**
	 * Prepara o objeto grafico com os valores das propriedades da figura.
	 *  
	 * @param g o objeto grafico do plano de desenho
	 */
	private void prepareGraphics(Graphics2D g)
	{
		g.setColor(this.getColor());
		g.setStroke(new BasicStroke(this.getStroke()));
	}

	/**
	 * Desenha a figura no plano de desenho.
	 * 
	 * @param g o objeto grafico do plano de desenho
	 */
	protected abstract void drawShape(Graphics2D g);
}
