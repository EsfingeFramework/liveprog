package aula.framework.dao;

public class MapeadorDouble implements MapeadorPropriedade<Double>
{
	@Override
	public String mapear(Double propriedade)
	{
		return ( propriedade.toString() );
	}

	@Override
	public Double restaurar(String valor)
	{
		return ( Double.valueOf(valor) );
	}
}
