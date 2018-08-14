package aula.framework.dao;

public class MapeadorInteger implements MapeadorPropriedade<Integer>
{
	@Override
	public String mapear(Integer propriedade)
	{
		return ( propriedade.toString() );
	}

	@Override
	public Integer restaurar(String valor)
	{
		return ( Integer.valueOf(valor) );
	}
}
