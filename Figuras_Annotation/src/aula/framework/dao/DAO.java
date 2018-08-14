package aula.framework.dao;

import aula.framework.figura.Figura;

public interface DAO
{
	public void salvarFiguras(Figura... figuras);
	public Figura[] recuperarFiguras();
}

