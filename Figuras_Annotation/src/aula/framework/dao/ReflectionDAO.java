package aula.framework.dao;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import aula.framework.figura.Figura;


public class ReflectionDAO implements DAO
{
	// arquivo para salvar / ler as figuras
	private String arquivo;
	
	// guarda o mapeamento das classes de figuras
	// [classe, atributo, tipo]
	private Map<Class<? extends Figura>, Map<String,Class<?>>> cacheMapaFiguras;
	
	
	public ReflectionDAO(String arquivo)
	{
		this.arquivo = arquivo;
		this.cacheMapaFiguras = new HashMap<Class<? extends Figura>, 
								            Map<String,Class<?>>>();
	}

	@Override
	public void salvarFiguras(Figura... figuras)
	{
		try
		{
			// arquivo para salvar as figuras
			PrintWriter  pw = new PrintWriter(this.arquivo);
			
			for ( Figura f : figuras )
			{
				// obtem o nome da classe da figura
				pw.println("figura=" + f.getClass().getName());

				// obtem as propriedades da figura
				// [atributo, valor]
				for ( Entry<String,Object> prop : this.getPropriedades(f).entrySet() )
					pw.println(prop.getKey() + "=" + prop.getValue());
				
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
			System.out.println(linha);
			String[] props = linha.split("\\s");
			Figura fig = (Figura) Class.forName(props[0]).newInstance();
			Class<?> clazz = fig.getClass();
			
			// verifica se a classe ainda nao foi mapeada
			if (! this.cacheMapaFiguras.containsKey(clazz) )
				this.mapearFigura(fig);
			
			// obtem o mapa de atributos
			// [atributo, tipo]
			Map<String,Class<?>> mapaAtributos = this.cacheMapaFiguras.get(clazz);
			
			// 
			for ( int i = 1; i < props.length; i++ )
			{
				// [atributo, valor]
				String[] attrValor = props[i].split("=");
				
				// verifica o tipo do atributo
				Object valor;
				Class<?> tipoAttr = mapaAtributos.get(attrValor[0]);
				
				//boleano
				if ( tipoAttr.isAssignableFrom(boolean.class) || tipoAttr.isAssignableFrom(Boolean.class) )					 
					valor = Boolean.valueOf(attrValor[1]);
				
				// inteiro
				else if ( tipoAttr.isAssignableFrom(int.class) || tipoAttr.isAssignableFrom(Integer.class) )
					valor = Integer.valueOf(attrValor[1]); 
				
				// float
				else if ( tipoAttr.isAssignableFrom(float.class) || tipoAttr.isAssignableFrom(Float.class) )
					valor = Float.valueOf(attrValor[1]); 
				
				// double
				else if ( tipoAttr.isAssignableFrom(double.class) || tipoAttr.isAssignableFrom(Double.class) )
					valor = Double.valueOf(attrValor[1]); 
					
				// string
				else 
					valor = attrValor[1]; 
				
				// seta o atributo na figura
				this.obterMetodoSet(clazz, attrValor[0]).invoke(fig, valor);
			}

			//
			return ( fig );
		}
		catch (Exception e)
		{
			throw new RuntimeException("Erro ao recuperar a figura: " + linha , e);
		}		
	}	
	
	private Map<String,Object> getPropriedades(Figura f)
	{
		try
		{
			// obtem a classe da figura
			Class<?> clazz = f.getClass();
			
			// verifica se a classe ainda nao foi mapeada
			if (! this.cacheMapaFiguras.containsKey(clazz) )
				this.mapearFigura(f);

			// mapa de propriedades
			// [atributo, valor]
			Map<String,Object> props = new HashMap<String,Object>();
			for ( String atributo : this.cacheMapaFiguras.get(clazz).keySet() )
				props.put(atributo, this.obterMetodoGet(clazz, atributo).invoke(f));
			
			return ( props );
		}
		catch (Exception e)
		{
			throw new RuntimeException("Erro ao mapear a classe " + f.getClass(), e);
		}
	}
	
	private void mapearFigura(Figura fig)
	{
		Class<? extends Figura> clazz = fig.getClass();
		
		// [atributo, tipo]
		Map<String,Class<?>> propriedades = Arrays.asList(clazz.getMethods()).stream()
				.filter(m -> isMetodoGetter(m))
				.collect(Collectors.toMap(m -> this.obterNomeAtributo(m),
										  Method::getReturnType));
		
		// salva as propriedades da figura no cache
		this.cacheMapaFiguras.put(clazz, propriedades);
	}
	
	private String obterNomeAtributo(Method metodo)
	{
		// nome do metodo
		String nomeMetodo = metodo.getName();
		
		// metodo getXXX ou isXXX
		int indice = nomeMetodo.startsWith("is") ? 2 : 3;

		//
		return ( nomeMetodo.substring(indice, indice+1).toLowerCase() + nomeMetodo.substring(indice+1) );
	}
	
	private Method obterMetodoGet(Class<?> clazz, String nomeAtributo)
	{
		try
		{
			// tipo do atributo
			Class<?> tipoAttr = this.cacheMapaFiguras.get(clazz).get(nomeAtributo);
			
			if ( tipoAttr.isAssignableFrom(boolean.class) || tipoAttr.isAssignableFrom(Boolean.class) )
				return ( clazz.getMethod("is" + nomeAtributo.substring(0, 1).toUpperCase() 
						+ nomeAtributo.substring(1)) );
			else
				return ( clazz.getMethod("get" + nomeAtributo.substring(0, 1).toUpperCase() 
						+ nomeAtributo.substring(1)) );
		}
		catch (Exception e)
		{
			throw new RuntimeException("Erro ao obter metodo getter do atributo " + nomeAtributo, e);
		}
	}
	
	private Method obterMetodoSet(Class<?> clazz, String nomeAtributo)
	{
		try
		{
			return ( clazz.getMethod("set" + nomeAtributo.substring(0, 1).toUpperCase() + nomeAtributo.substring(1),
						this.cacheMapaFiguras.get(clazz).get(nomeAtributo)) );
		}
		catch (Exception e)
		{
			throw new RuntimeException("Erro ao obter metodo setter do atributo " + nomeAtributo, e);
		}
	}
		
	private boolean isMetodoGetter(Method m)
	{
		return ( m.getName().startsWith("get")    ||
				 m.getName().startsWith("is"))    && 
			     m.getParameterCount() == 0       &&
			     m.getReturnType() != void.class  &&				
			    !m.getName().equals("getClass");
	}
}
