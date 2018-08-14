package aula.framework.dao;

public class MapeadorFloat implements MapeadorPropriedade<Float>
{
	@Override
	public String mapear(Float propriedade)
	{
		return ( propriedade.toString() );
	}

	@Override
	public Float restaurar(String valor)
	{
		return ( Float.valueOf(valor) );
	}
}
