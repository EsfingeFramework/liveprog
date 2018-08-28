package org.esfinge.liveprog.db;

import java.sql.SQLException;
import java.util.List;

import org.esfinge.liveprog.instrumentation.ClassInfo;

/**
 * Interface para a persistencia de classes dinamicas. 
 */
public interface ILiveClassDB
{
	/**
	 * Obtem as informacoes da classe dinamica no banco de dados.
	 * 
	 * @param className o nome da classe dinamica
	 * @param testMode <b>true</b> se a aplicacao estiver rodando em modo de testes - usa a versao mais recente da classe,
	 * <b>false</b> se a aplicacao estiver rodando em modo de producao
	 * @return as informacoes da classe dinamica salvas no banco de dados,
	 * ou <b>null</b> caso nenhuma versao da classe informada tenha sido salva anteriormente 
	 * @throws SQLException em caso de erros com o banco de dados
	 */
	public ClassInfo getLiveClass(String className, boolean testMode) throws SQLException;
	
	/**
	 * Salva as informacoes da classe dinamica no banco de dados.
	 * 
	 * Toda nova versao de classe dinamica eh armazenada na versao de testes,
	 * a nao ser que seja a primeira vez que esteja sendo persistida (eh salva como de testes e producao).
	 * 
	 * @param className o nome da classe dinamica
	 * @param classInfo informacoes da classe dinamica
	 * @param testMode <b>true</b> se a versao da classe dinamica for de producao,
	 * <b>false</b> se for de teste
	 * @throws SQLException em caso de erros com o banco de dados
	 * @see org.esfinge.liveprog.instrumentation.ClassInfo
	 */
	public void saveLiveClass(String className, ClassInfo classInfo) throws SQLException;
	
	
	/**
	 * Obtem as versoes da classe dinamica salvas no banco de dados.
	 * 
	 * @param className o nome da classe dinamica
	 * @return as versoes da classe dinamica salvas no banco de dados
	 * @throws SQLException em caso de erros com o banco de dados
	 * @see org.esfinge.liveprog.db.ILiveClassDBVersion
	 */
	public ILiveClassDBVersion getLiveClassVersion(String className) throws SQLException;

	/**
	 * Obtem as versoes de todas as classes dinamicas salvas no banco de dados.
	 * 
	 * @return lista as versoes de todas as classes dinamica salvas no banco de dados
	 * @throws SQLException em caso de erros com o banco de dados
	 * @see org.esfinge.liveprog.db.ILiveClassDBVersion
	 */
	public List<ILiveClassDBVersion> getAllLiveClassVersion() throws SQLException;
	
	/**
	 * Promove a versao de testes da classe dinamica para versao de producao.
	 * 
	 * @param className o nome da classe dinamica
	 * @return <b>true</b> se a versao de testes da classe foi promovida para a versao de producao,
	 * <b>false</b> caso contrario (i.e. se as versoes de producao e de testes forem iguais)
	 * @throws SQLException em cas de erros com o banco de dados
	 */
	public boolean commitLiveClass(String className) throws SQLException;

	/**
	 * Retrocede a versao de producao da classe dinamica para a versao anterior.
	 * 
	 * @param className o nome da classe dinamica
	 * @return <b>true</b> se a versao de producao da classe foi retrocedida para a versao anterior,
	 * <b>false</b> caso contrario (i.e. se a versao de producao atual for a versao original da classe dinamica)
	 * @throws SQLException em cas de erros com o banco de dados
	 */
	public boolean rollbackLiveClass(String className) throws SQLException;
}
