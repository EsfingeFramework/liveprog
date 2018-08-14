package aula.framework.dao;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.esfinge.liveprog.LiveClassFactory;

import aula.framework.figura.Figura;
import aula.framework.figura.annotation.Mapeador;
import aula.framework.figura.annotation.Persistir;
import aula.framework.utils.AnnotationUtils;

public class AnnotationDAO implements DAO
{
	// arquivo para salvar / ler as figuras
	private String arquivo;
	
	//
	private LiveClassFactory objFactory; 
	
	
	// TODO: utilizando classe dinamica
	public AnnotationDAO(String arquivo, LiveClassFactory factory)
	{
		this.arquivo = arquivo;
		this.objFactory = factory;
	}
	
	@Override
	public void salvarFiguras(Figura... figuras)
	{
		try
		{
			// arquivo para salvar as figuras
			PrintWriter  pw = new PrintWriter(this.arquivo);
			
			for ( Figura fig : figuras )
			{
				// obtem o nome da classe da figura
				pw.println("figura=" + fig.getClass().getName());

				// obtem as propriedades anotadas da figura				
				for ( Field field : AnnotationUtils.getAnnotadedFields(fig.getClass(), Persistir.class) )
				{
					// verifica se tem um mapeador
					MapeadorPropriedade m = field.isAnnotationPresent(Mapeador.class) ? 
							field.getAnnotation(Mapeador.class).value().newInstance() : 
							AnnotationUtils.getMapeadorPropriedade(field.getType());
							
					// habilita o acesso
					field.setAccessible(true);
					
					pw.println(field.getName() + "=" + m.mapear(field.get(fig)));
				}
				
				// 
				pw.println("");
			}
			
			pw.close();
		}
		catch (Exception e)
		{
			throw new RuntimeException("Erro ao salvar o arquivo " + this.arquivo, e);
		}
	}

	@Override
	public Figura[] recuperarFiguras()
	{
		try
		{
			// arquivo com as figuras salvas
			Path arq = Paths.get(this.arquivo);
			
			// le as linhas do arquivo e junta tudo em uma unica String
			String linha = Files.readAllLines(arq).stream()
					.filter(line -> !line.trim().isEmpty())
					.collect(Collectors.joining(" "));
			
			// particiona em linhas com as propriedades de cada figura
			List<String> linhasFigura = Arrays.asList(linha.split("figura=")).stream()
					.filter(line -> !line.trim().isEmpty())
					.collect(Collectors.toList());
			
			// recupera as figuras, fazendo o parse das propriedades de cada linha 
			List<Figura> listaFiguras = linhasFigura.stream()
					.map(line -> parseFigura(line))
					.collect(Collectors.toList());
			
			// retorna como array
			return ( listaFiguras.toArray(new Figura[listaFiguras.size()]) );
		}
		catch (Exception e)
		{
			throw new RuntimeException("Erro ao recuperar figuras do arquivo " + this.arquivo, e);
		}
	}
	
	private Figura parseFigura(String linha)
	{
		try
		{
			String[] props = linha.split("\\s");
			
			// TODO: utilizando classe dinamica
			Figura fig = (Figura) this.objFactory.createObject(Class.forName(props[0]));
//			Figura fig = (Figura) Class.forName(props[0]).newInstance();

			Class<?> clazz = fig.getClass();
			
			// 
			for ( int i = 1; i < props.length; i++ )
			{
				// [atributo, valor]
				String[] attrValor = props[i].split("=");				
				
				Field field = AnnotationUtils.getField(clazz, attrValor[0]);
				if ( field == null )
					continue;
				
				// verifica se tem um mapeador
				MapeadorPropriedade m = field.isAnnotationPresent(Mapeador.class) ? 
						field.getAnnotation(Mapeador.class).value().newInstance() : 
						AnnotationUtils.getMapeadorPropriedade(field.getType());
				
				// restaura o valor 
				Object valor = m.restaurar(attrValor[1]); 
				
				// habilita o acesso
				field.setAccessible(true);
				
				// seta o atributo na figura
				field.set(fig, valor);
			}

			//
			return ( fig );
		}
		catch (Exception e)
		{
			throw new RuntimeException("Erro ao recuperar a figura: " + linha , e);
		}
	}
}
