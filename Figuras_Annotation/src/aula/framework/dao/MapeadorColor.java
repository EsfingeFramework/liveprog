package aula.framework.dao;

import java.awt.Color;

public class MapeadorColor implements MapeadorPropriedade<Color>
{
	@Override
	public String mapear(Color propriedade)
	{
		return ( String.valueOf(propriedade.getRGB()) );
	}

	@Override
	public Color restaurar(String valor)
	{
		return ( new Color(Integer.parseInt(valor)) );
	}
}
