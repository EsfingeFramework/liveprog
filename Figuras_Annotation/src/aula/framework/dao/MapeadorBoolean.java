package aula.framework.dao;

public class MapeadorBoolean implements MapeadorPropriedade<Boolean>
{
	@Override
	public String mapear(Boolean propriedade)
	{
		return ( propriedade.toString() );
	}

	@Override
	public Boolean restaurar(String valor)
	{
		return ( Boolean.valueOf(valor) );
	}
}
