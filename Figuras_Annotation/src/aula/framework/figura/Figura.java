package aula.framework.figura;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import aula.framework.dao.MapeadorColor;
import aula.framework.figura.annotation.Mapeador;
import aula.framework.figura.annotation.Persistir;

public abstract class Figura
{
	@Persistir
	private int posX;
	
	@Persistir
	private int posY;
	
	@Persistir
	private boolean preencher;
	
	@Persistir
	private int traco;
	
	@Persistir
	@Mapeador(MapeadorColor.class)
	private Color cor;
	

	public Figura()
	{
		this.posX = 0;
		this.posY = 0;
		this.preencher = false;
		this.cor = Color.BLACK;
		this.traco = 1;
	}
	
	public int getPosX()
	{
		return posX;
	}

	public void setPosX(int posX)
	{
		this.posX = posX;
	}

	public int getPosY()
	{
		return posY;
	}

	public void setPosY(int posY)
	{
		this.posY = posY;
	}

	public boolean isPreencher()
	{
		return preencher;
	}

	public void setPreencher(boolean preencher)
	{
		this.preencher = preencher;
	}

	public Color getCor()
	{
		return cor;
	}

	public void setCor(Color cor)
	{
		this.cor = cor;
	}

	public int getTraco()
	{
		return traco;
	}

	public void setTraco(int traco)
	{
		this.traco = traco;
	}
	
	public final void desenhar(Graphics2D g)
	{
		this.prepararGraphics(g);
		this.desenharForma(g);
	}
	
	private void prepararGraphics(Graphics2D g)
	{
		// TODO: como acessar diretamente o atributo e pegar o valor do proxy??
//		g.setColor(this.cor);
		g.setColor(this.getCor());
		g.setStroke(new BasicStroke(this.getTraco()));
	}

	protected abstract void desenharForma(Graphics2D g);
}
