package aula.framework.dao;

public class MapeadorNulo implements MapeadorPropriedade<Object>
{
	@Override
	public String mapear(Object propriedade)
	{
		return ( propriedade.toString() );
	}

	@Override
	public Object restaurar(String valor)
	{
		return ( valor );
	}
}
