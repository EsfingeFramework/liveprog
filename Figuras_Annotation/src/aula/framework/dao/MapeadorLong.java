package aula.framework.dao;

public class MapeadorLong implements MapeadorPropriedade<Long>
{
	@Override
	public String mapear(Long propriedade)
	{
		return ( propriedade.toString() );
	}

	@Override
	public Long restaurar(String valor)
	{
		return ( Long.valueOf(valor) );
	}
}
