package aula.framework;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.esfinge.liveprog.LiveClassFactory;
import org.esfinge.liveprog.monitor.FileSystemMonitor;
import org.esfinge.liveprog.monitor.IMonitor;

import aula.framework.dao.AnnotationDAO;
import aula.framework.dao.DAO;
import aula.framework.figura.Circulo;
import aula.framework.figura.PainelDesenho;
import aula.framework.figura.Retangulo;

public class Principal
{
	public static void main(String[] args)
	{
		try
		{
			// cria uma nova fabrica de objetos dinamicos
			IMonitor monitor = new FileSystemMonitor("bin", true);
			LiveClassFactory factory = new LiveClassFactory(monitor);

//			Retangulo r1 = new Retangulo();
			Retangulo r1 = factory.createObject(Retangulo.class);
			r1.setPosX(60);
			r1.setPosY(60);
			r1.setAltura(50);
			r1.setComprimento(100);
			r1.setPreencher(true);
			
//			Retangulo r2 = new Retangulo();
			Retangulo r2 = factory.createObject(Retangulo.class);
			r2.setPosX(200);
			r2.setPosY(170);
			r2.setAltura(100);
			r2.setComprimento(100);
			r2.setCor(Color.RED);
			r2.setTraco(2);
			
			Retangulo r3 = new Retangulo();
//			Retangulo r3 = factory.createObject(Retangulo.class);
			r3.setPosX(350);
			r3.setPosY(80);
			r3.setAltura(200);
			r3.setComprimento(70);
			r3.setCor(Color.CYAN);
			r3.setTraco(5);
			
//			Circulo c1 = new Circulo();
			Circulo c1 = factory.createObject(Circulo.class);
			c1.setPosX(70);
			c1.setPosY(200);
			c1.setRaio(50);
			c1.setCor(Color.BLUE);
			c1.setTraco(3);
			
//			Circulo c2 = new Circulo();
			Circulo c2 = factory.createObject(Circulo.class);
			c2.setPosX(220);
			c2.setPosY(30);
			c2.setRaio(50);
			c2.setCor(Color.YELLOW);
			c2.setPreencher(true);			
			
			PainelDesenho painelDesenho = new PainelDesenho();
			factory.addLiveClassObserver(Retangulo.class, painelDesenho);
			factory.addLiveClassObserver(Circulo.class, painelDesenho);
			
			DAO dao = new AnnotationDAO("figuras.txt", factory);
	//		DAO dao = new AnnotationDAO("figuras.txt");
					
			
			JButton btnRetangulos = new JButton("Retangulos");
			btnRetangulos.addActionListener(evt -> {painelDesenho.limpar(); 
			                                        painelDesenho.adicionarFiguras(r1,r2,r3);});
			
			JButton btnCirculos = new JButton("Circulos");
			btnCirculos.addActionListener(evt -> {painelDesenho.limpar(); 
			                                      painelDesenho.adicionarFiguras(c1,c2);});
					
			JButton btnTodos = new JButton("Todos");
			btnTodos.addActionListener(evt -> {painelDesenho.limpar(); 
			                                   painelDesenho.adicionarFiguras(r1,r2,r3,c1,c2);});
			
			JButton btnLimpar = new JButton("Limpar");
			btnLimpar.addActionListener(evt -> painelDesenho.limpar());
			
			JButton btnSalvar = new JButton("Salvar figuras");
			btnSalvar.addActionListener(evt -> dao.salvarFiguras(painelDesenho.getFiguras()));
			
			JButton btnCarregar = new JButton("Carregar figuras");
			btnCarregar.addActionListener(evt -> {painelDesenho.limpar(); 
			                                      painelDesenho.adicionarFiguras(dao.recuperarFiguras());});
			
			
			JPanel pnlBotoes1 = new JPanel();
			pnlBotoes1.add(btnRetangulos);
			pnlBotoes1.add(btnCirculos);
			pnlBotoes1.add(btnTodos);
			pnlBotoes1.add(btnLimpar);
			
			JPanel pnlBotoes2 = new JPanel();
			pnlBotoes2.add(btnSalvar);
			pnlBotoes2.add(btnCarregar);
	
			JPanel pnlBotoes = new JPanel(new GridLayout(2,1));
			pnlBotoes.add(pnlBotoes1);
			pnlBotoes.add(pnlBotoes2);
			
			JFrame janela = new JFrame("Demonstrando extensão com Annotation");
			janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			janela.getContentPane().add(painelDesenho, BorderLayout.CENTER);
			janela.getContentPane().add(pnlBotoes, BorderLayout.SOUTH);
			janela.setSize(600,  600);
			janela.setVisible(true);
		
			// inicia a verificacao por atualizacoes
			monitor.start();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
