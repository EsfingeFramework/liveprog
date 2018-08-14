package aula.framework.figura;

import java.awt.Graphics2D;

import org.esfinge.liveprog.annotation.LiveClass;

import aula.framework.figura.annotation.Persistir;

@LiveClass
public class Circulo extends Figura
{
	@Persistir
	private int raio;
	

	public int getRaio()
	{
		return raio;
	}

	public void setRaio(int raio)
	{
		this.raio = raio;
	}

	@Override
	public void desenharForma(Graphics2D g)
	{
		if ( this.isPreencher() )
			g.fillOval(this.getPosX(),  this.getPosY(), 
					2 * this.raio, 2 * this.raio);
		else
			g.drawOval(this.getPosX(),  this.getPosY(), 
					2 * this.raio, 2 * this.raio);
		
//		g.drawString("Circ", this.getPosX(),  this.getPosY() - 5);
	}
}
