package aula.framework.dao;

public interface MapeadorPropriedade<E>
{
	public String mapear(E propriedade);
	public E restaurar(String valor);
}
