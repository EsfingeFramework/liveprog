package aula.framework.figura;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.esfinge.liveprog.ILiveClassObserver;

@SuppressWarnings("serial")
public class PainelDesenho extends JPanel implements ILiveClassObserver
{
	private List<Figura> listaFiguras;
	
	
	public PainelDesenho()
	{
		this.listaFiguras = new ArrayList<Figura>();
		this.setBackground(Color.WHITE);
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}
	
	public void adicionarFiguras(Figura... figuras)
	{
		this.listaFiguras.addAll(Arrays.asList(figuras));
		this.repaint();
	}
	
	public void removerFiguras(Figura... figuras)
	{
		this.listaFiguras.removeAll(Arrays.asList(figuras));
		this.repaint();
	}
	
	public void limpar()
	{
		this.listaFiguras.clear();
		this.repaint();
	}
	
	public Figura[] getFiguras()
	{
		return ( this.listaFiguras.toArray(
				new Figura[this.listaFiguras.size()]) );
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		for ( Figura figura : this.listaFiguras )
			figura.desenhar((Graphics2D) g);
	}

	@Override
	public void classReloaded(Class<?> newLiveClass)
	{
		this.repaint();
	}
}
