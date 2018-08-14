package aula.framework.figura;

import java.awt.Graphics2D;

import org.esfinge.liveprog.annotation.LiveClass;

import aula.framework.figura.annotation.Persistir;

@LiveClass
public class Retangulo extends Figura
{
	@Persistir
	private int altura;
	
	@Persistir
	private int comprimento;
	
	
	public int getAltura()
	{
		return altura;
	}

	public void setAltura(int altura)
	{
		this.altura = altura;
	}

	public int getComprimento()
	{
		return comprimento;
	}

	public void setComprimento(int comprimento)
	{
		this.comprimento = comprimento;
	}
	
	@Override
	public void desenharForma(Graphics2D g)
	{		
		if ( this.isPreencher() )
			g.fillRect(this.getPosX(),  this.getPosY(), 
					this.comprimento, this.altura);
		else
			g.drawRect(this.getPosX(),  this.getPosY(), 
					this.comprimento, this.altura);
		
//		g.drawString("Rect", this.getPosX(),  this.getPosY() - 5);
	}
}
