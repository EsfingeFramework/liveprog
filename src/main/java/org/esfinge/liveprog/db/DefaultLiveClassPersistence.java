package org.esfinge.liveprog.db;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.esfinge.liveprog.instrumentation.InstrumentationHelper;
import org.esfinge.liveprog.reflect.ClassInfo;
import org.esfinge.liveprog.util.Utils;
import org.sqlite.SQLiteConfig;

/**
 * <p>
 * Implementa��o baseada em JDBC e SQLite para a persist�ncia de classes din�micas, utilizando o padr�o <i>Singleton</i>.
 * <br>
 * Por padr�o, as informa��es das classes din�micas s�o persistidas no arquivo <i>'liveclasses.db'</i> 
 * que fica armazenado no mesmo diret�rio da aplica��o. Para alter�-lo, utilize o m�todo {@link #setDatabaseFilePath(String)}
 * <u>antes do primeiro uso</u> desta classe.
 * </p>
 * <p><i>
 * Default implementation of LiveClasses persistence based on JDBC and SQLite, using the Singleton pattern.
 * <br>
 * By default, the database filename is 'liveclasses.db' and is stored in the same path of the running application. 
 * To change it, use the {@link #setDatabaseFilePath(String)} method <u>before the first use</u> of this class. 
 * </i></p>
 */
public class DefaultLiveClassPersistence implements ILiveClassPersistence
{
	// instancia singleton
	private static DefaultLiveClassPersistence _singleton;
	
	// caminho para o arquivo do BD
	private static String _dbFilePath = "liveclasses.db";
	
	
	/**
	 * <p>
	 * Construtor padr�o, privado para garantir o uso da classe somente via Singleton.
	 * </p>
	 * <p><i>
	 * Default private constructor to ensure the usage of the Singleton pattern.
	 * </i></p>
	 */
	private DefaultLiveClassPersistence()
	{		
	}
	
	/**
	 * <p>
	 * Especifica o nome e o caminho do arquivo de persist�ncia das classes din�micas.
	 * </p>
	 * <p><i>
	 * Sets the database filename and path for LiveClasses persistence.
	 * </i></p>
	 * 
	 * @param filePath - nome e caminho para o arquivo de base de dados
	 * <br><i>filename and path of the database file</i>
	 */
	public static void setDatabaseFilePath(String filePath)
	{
		_dbFilePath = filePath;
		
		// log: debug
		Utils.logDebug("database file: '" + filePath + "'");
	}
	
	/**
	 * <p>
	 * Obt�m a inst�ncia para persist�ncia de classes din�micas.
	 * </p>
	 * <p><i>
	 * Gets the Singleton instance for LiveClasses persistence. 
	 * </i></p>
	 * 
	 * @return o <i>Singleton</i> para a persist�ncia de classes din�micas
	 * <br><i>the Singleton instance for persistence of LiveClasses</i>   
	 * @throws IllegalStateException caso o arquivo de persist�ncia n�o tenha sido especificado corretamente
	 * <br><i>if the database file path was incorrectly set up</i>
	 * @throws SQLException em caso de erros com o banco de dados
	 * <br><i>if an error occurs when configuring the database</i>
	 * @see #setDatabaseFilePath(String)
	 */
	public static DefaultLiveClassPersistence getInstance() throws IllegalStateException, SQLException
	{
		if ( _dbFilePath == null )
			throw new IllegalStateException("Database file path not set!");
		
		if ( _singleton == null )
		{
			_singleton = new DefaultLiveClassPersistence();
			_singleton.configure();
		}
		
		return ( _singleton );
	}
	
	@Override
	public ClassInfo getLiveClassInfo(String liveClassName, boolean safeMode) throws Exception
	{
		// obtem a conexao com o BD
		Connection conn = this.getConnection();
	
		// obtem as versoes da classe salvas no BD
		TableKeysInfo keysInfo = this.getTableKeysInfo(conn, liveClassName);
		
		// nao ha versao salva no BD para essa classe
		if ( keysInfo.getSafeModeVersion() < 0 )
			return ( null );
		
		// bytecode da classe
		byte[] classBytecode;
		
		if ( safeMode )
			classBytecode = this.readFromClassVersionTable(conn, keysInfo.getClassId(), keysInfo.getSafeModeVersion());
		else
			classBytecode = this.readFromClassVersionTable(conn, keysInfo.getClassId(), keysInfo.getSafeModeVersion());
		
		// recupera as informacoes da classe
		ClassInfo classInfo = InstrumentationHelper.inspect(classBytecode);
		
		// obtem os bytecodes das classes internas..
		List<byte[]> innerClassesBytecodeList = this.readFromInnerClassesTable(conn, keysInfo.getVersionId());
		
		// recupera as informacoes das classes internas
		for ( byte[] innerClassBytecode : innerClassesBytecodeList )
			classInfo.addInnerClassInfo(InstrumentationHelper.inspect(innerClassBytecode));
		
		// fecha a conexao com o BD
		conn.close();
		
		return ( classInfo );
	}
	
	@Override
	public void saveLiveClassInfo(String liveClassName, ClassInfo liveClassInfo) throws SQLException
	{
		// obtem a conexao com o BD
		Connection conn = this.getConnection();

		// obtem as versoes da classe salvas no BD
		TableKeysInfo keysInfo = this.getTableKeysInfo(conn, liveClassName);
		
		// nao ha versao salva no BD para essa classe
		if ( keysInfo.getCurrentVersion() < 0 )
		{
			// primeira versao				
			int version = 1;
			
			// salva as informacoes da classe
			int classId = this.saveToLiveClassTable(conn, liveClassName, version);
			
			// salva a versao e o bytecode da classe
			int versionId = this.saveToClassVersionTable(conn, classId, version, liveClassInfo.getBytecode());
			
			// salva as classes internas da classe
			for ( ClassInfo innerClass : liveClassInfo.getInnerClassesInfo() )
				this.saveInnerClass(conn, versionId, innerClass);
		}
		else
		{
			// ja existe uma versao salva, atualiza a tabela de versoes..
			
			// verifica a versao do modo seguro
			if ( keysInfo.getSafeModeVersion() != keysInfo.getCurrentVersion() )
			{
				// remove a versao de teste atual para incluir a nova versao
				// (e as classes internas associadas a essa versao)
				this.removeFromClassVersionTable(conn, keysInfo.getVersionId());
			}
			
			// insere na tabela de versoes
			int versionId = this.saveToClassVersionTable(conn, keysInfo.getClassId(), keysInfo.getSafeModeVersion()+1, liveClassInfo.getBytecode());
				
			// salva as classes internas da classe
			for ( ClassInfo innerClass : liveClassInfo.getInnerClassesInfo() )
				this.saveInnerClass(conn, versionId, innerClass);
		}
		
		// fecha a conexao com o BD
		conn.close();
	}

	@Override
	public ILiveClassVersionInfo getLiveClassVersionInfo(String liveClassName) throws SQLException
	{
		// obtem a conexao com o BD
		Connection conn = this.getConnection();

		// obtem as versoes da classe salvas no BD
		TableKeysInfo keysInfo = this.getTableKeysInfo(conn, liveClassName);
		
		// fecha a conexao com o BD
		conn.close();

		return ( keysInfo );
	}
	
	@Override
	public List<ILiveClassVersionInfo> getAllLiveClassesVersionInfo() throws SQLException
	{
		// obtem a conexao com o BD
		Connection conn = this.getConnection();

		// obtem as versoes da classe salvas no BD
		List<TableKeysInfo> keysInfoList = this.getAllTableKeysInfo(conn);

		// fecha a conexao com o BD
		conn.close();

		return ( new ArrayList<ILiveClassVersionInfo>(keysInfoList) );
	}
	
	@Override
	public boolean commitLiveClass(String liveClassName) throws SQLException
	{
		// obtem a conexao com o BD
		Connection conn = this.getConnection();
		
		// obtem as versoes da classe salvas no BD
		TableKeysInfo keysInfo = this.getTableKeysInfo(conn, liveClassName);
		
		// 
		boolean updated = false;
		
		// a versao do modo seguro eh diferente da versao do modo padrao?
		if ( keysInfo.getSafeModeVersion() < keysInfo.getCurrentVersion() )
		{
			// atualiza a versao do modo seguro
			updated = this.updateSafeModeVersion(conn, liveClassName, keysInfo.getCurrentVersion());
		}
		
		// fecha a conexao com o BD
		conn.close();
		
		return ( updated );
	}

	@Override
	public boolean rollbackLiveClass(String liveClassName) throws SQLException
	{
		// obtem a conexao com o BD
		Connection conn = this.getConnection();
		
		// obtem as versoes da classe salvas no BD
		TableKeysInfo keysInfo = this.getTableKeysInfo(conn, liveClassName);
		
		// verifica se nao eh a primeira versao
		if ( keysInfo.getCurrentVersion() > 1 )
		{
			// retrocede uma versao
			int newVersion = keysInfo.getCurrentVersion() - 1;

			// verifica a versao do modo seguro
			if ( keysInfo.getSafeModeVersion() == keysInfo.getCurrentVersion() )
				this.updateSafeModeVersion(conn, liveClassName, newVersion);
			
			// 
			this.removeFromClassVersionTable(conn, keysInfo.getVersionId());
		}
		
		// fecha a conexao com o BD
		conn.close();
		
		return ( true );
	}

	/**
	 * <p>
	 * Configura o acesso ao banco de dados.
	 * </p>
	 * <p><i>
	 * Configures the database.
	 * </i></p>
	 * 
	 * @throws SQLException em caso de erros com o banco de dados
	 * <br><i>if an error occurs when configuring the database</i>
	 */
	private void configure() throws SQLException
	{
		// obtem a conexao com o BD
		Connection conn = this.getConnection();
		
		try
		{
			// cria as tabelas do BD, caso nao existam
			String SQL = "CREATE TABLE IF NOT EXISTS LiveClass (" + 
						 " id integer PRIMARY KEY AUTOINCREMENT," + 
						 " className text NOT NULL," + 
						 " safeModeVersion integer);";
			
			// log: debug
			Utils.logDebug(SQL);

			// cria a tabela de versao corrente
			Statement stmt = conn.createStatement();
			stmt.execute(SQL);

			// cria a tabela de versoes das classes
			SQL = "CREATE TABLE IF NOT EXISTS ClassVersion ("  + 
				  "	id integer PRIMARY KEY AUTOINCREMENT,"     + 
				  " id_class integer NOT NULL,"                + 
				  " version integer NOT NULL,"                 +
				  "	bytecode BLOB NOT NULL,"                   +
				  " FOREIGN KEY (id_class) REFERENCES LiveClass (id) " + 
				  " ON DELETE CASCADE ON UPDATE NO ACTION);";

			// log: debug
			Utils.logDebug(SQL);
			
			stmt.execute(SQL);
			
			// cria a tabela de classes internas das classes
			SQL = "CREATE TABLE IF NOT EXISTS InnerClasses (" + 
				  "	id integer PRIMARY KEY AUTOINCREMENT,"    + 
				  " id_version integer NOT NULL,"             + 
				  "	bytecode BLOB NOT NULL,"                  +
				  " FOREIGN KEY (id_version) REFERENCES ClassVersion (id) " + 
				  " ON DELETE CASCADE ON UPDATE NO ACTION);";

			// log: debug
			Utils.logDebug(SQL);
			
			stmt.execute(SQL);

			// log: info
			Utils.logInfo("Conexao com o banco de dados '" + Paths.get(_dbFilePath).toAbsolutePath() + "' estabelecida!");
		}		
		
		finally
		{
			conn.close();
		}
	}
	
	/**
	 * <p>
	 * Obt�m uma conex�o com o banco de dados.
	 * </p>
	 * <p><i>
	 * Gets a database connection.
	 * </i></p>
	 * 
	 * @return uma conex�o com o banco de dados 
	 * <br><i>a database connection</i>
	 * @throws SQLException em caso de erros com o banco de dados
	 * <br><i>if an error occurs when accessing the database</i>
	 */
	private Connection getConnection() throws SQLException
	{
		// arquivo do BD
		File dbFile = Paths.get(_dbFilePath).toFile();

		// URL JDBC
		String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
		
		// SQLite config
		SQLiteConfig config = new SQLiteConfig();
		config.enforceForeignKeys(true);

		// retorna a conexao com o BD
		return ( DriverManager.getConnection(url, config.toProperties()) );
	}
	
	/**
	 * <p>
	 * Persiste as informa��es da classe din�mica.
	 * </p>
	 * <p><i>
	 * Persists the information of the specified LiveClass.
	 * </i></p>
	 * 
	 * @param conn - conex�o com o banco de dados
	 * <br><i>the database connection</i>
	 * @param liveClassName - nome da classe din�mica
	 * <br><i>the name of the LiveClass</i> 
	 * @param safeModeVersion - vers�o do modo seguro da classe din�mica
	 * <br><i>the LiveClass safe mode version</i>
	 * @return a chave criada na tabela LiveClass para a classe din�mica persistida
	 * <br><i>the key created on the LiveClass table for the persisted LiveClass</i>
	 * @throws SQLException em caso de erros com o banco de dados
	 * <br><i>if an error occurs when accessing the database</i>
 	 */
	private int saveToLiveClassTable(Connection conn, String liveClassName, int safeModeVersion) throws SQLException
	{
		//
		String SQL = "INSERT INTO LiveCLass (className, safeModeVersion) VALUES (?, ?)";
		
		// log: debug
		Utils.logDebug(this.debugSQL(SQL, liveClassName, safeModeVersion));
		
		PreparedStatement pStmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
		pStmt.setString(1, liveClassName);
		pStmt.setInt(2, safeModeVersion);
		
		pStmt.executeUpdate();
		ResultSet rs = pStmt.getGeneratedKeys();
		rs.next();
		
		// chave gerada
		int result = rs.getInt(1);
		
		// log: debug
		Utils.logDebug(String.format("chave gerada -> %d", result));
		
		return ( result );
	}
	
	/**
	 * <p>
	 * Persiste as informa��es da classe din�mica da vers�o informada.
	 * </p>
	 * <p><i>
	 * Persists the information of the specified LiveClass version.
	 * </i></p>
	 * 
	 * @param conn - conex�o com o banco de dados
	 * <br><i>the database connection</i>
	 * @param classId - chave da classe din�mica na tabela LiveClass
	 * <br><i>key of the LiveClass, from the LiveClass table</i>
	 * @param classVersion - vers�o da classe din�mica
	 * <br><i>the LiveClass version</i>
	 * @param classBytecode - bytecode da classe din�mica a ser persistida
	 * <br><i>the LiveClass bytecode to be persisted</i>
	 * @return a chave criada na tabela ClassVersion para a classe din�mica persistida
	 * <br><i>the key created on the ClassVersion table for the persisted LiveClass</i>
	 * @throws SQLException em caso de erros com o banco de dados
	 * <br><i>if an error occurs when accessing the database</i>
	 */
	private int saveToClassVersionTable(Connection conn, int classId, int classVersion, byte[] classBytecode ) throws SQLException
	{
		//
		String SQL = "INSERT INTO ClassVersion (id_class, version, bytecode) VALUES (?, ?, ?)";
		
		// log: debug
		Utils.logDebug(this.debugSQL(SQL, classId, classVersion, "[bytecode]"));
		
		PreparedStatement pStmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
		pStmt.setInt(1, classId);
		pStmt.setInt(2, classVersion);
		pStmt.setBytes(3, classBytecode);
		
		pStmt.executeUpdate();
		ResultSet rs = pStmt.getGeneratedKeys();
		rs.next();
		
		// chave gerada
		int result = rs.getInt(1);
		
		// log: debug
		Utils.logDebug(String.format("chave gerada -> %d", result));
		
		return ( result );
	}
	
	/**
	 * <p>
	 * Persiste as informa��es da classe interna da vers�o informada da classe din�mica.
	 * </p>
	 * <p><i>
	 * Persists the inner class information of the specified LiveClass version.
	 * </i></p>
	 * 
	 * @param conn - conex�o com o banco de dados
	 * <br><i>the database connection</i>
	 * @param versionId - chave da vers�o da classe din�mica na tabela ClassVersion
	 * <br><i>key of the LiveClass version, from the ClassVersion table</i>
	 * @param innerClassBytecode - bytecode da classe interna a ser persistida
	 * <br><i>the inner class bytecode to be persisted</i>
	 * @return a chave criada na tabela InnerClasses para a classe interna persistida
	 * <br><i>the key created on the InnerClasses table for the persisted inner class</i>
	 * @throws SQLException em caso de erros com o banco de dados
	 * <br><i>if an error occurs when accessing the database</i>
	 */
	private int saveToInnerClassesTable(Connection conn, int versionId, byte[] innerClassBytecode ) throws SQLException
	{
		//
		String SQL = "INSERT INTO InnerClasses (id_version, bytecode) VALUES (?, ?)";
		
		// log: debug
		Utils.logDebug(this.debugSQL(SQL, versionId, "[bytecode]"));
		
		PreparedStatement pStmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
		pStmt.setInt(1, versionId);
		pStmt.setObject(2, innerClassBytecode);
		
		pStmt.executeUpdate();
		ResultSet rs = pStmt.getGeneratedKeys();
		rs.next();
		
		// chave gerada
		int result = rs.getInt(1);
		
		// log: debug
		Utils.logDebug(String.format("chave gerada -> %d", result));
		
		return ( result );
	}

	/**
	 * <p>
	 * Obt�m as informa��es da classe din�mica na vers�o informada.
	 * </p>
	 * <p><i>
	 * Gets the LiveClass information on the specified version.
	 * </i></p>
	 * 
	 * @param conn - conex�o com o banco de dados
	 * <br><i>the database connection</i>
	 * @param classId - chave da classe din�mica na tabela LiveClass
	 * <br><i>key of the LiveClass, from the LiveClass table</i>
	 * @param version - vers�o da classe din�mica a ser obtida
	 * <br><i>the LiveClass version to be retrieved</i>
	 * @return o bytecode da classe din�mica na vers�o informada
	 * <br><i>the LiveClass bytecode of its specified version</i>
	 * @throws SQLException em caso de erros com o banco de dados
	 * <br><i>if an error occurs when accessing the database</i>
	 */
	private byte[] readFromClassVersionTable(Connection conn, int classId, int version) throws SQLException
	{
		//
		String SQL = "SELECT bytecode FROM ClassVersion WHERE id_class = ? AND version = ?";
		
		// log: debug
		Utils.logDebug(this.debugSQL(SQL, classId, version));
		
		PreparedStatement pStmt = conn.prepareStatement(SQL);
		pStmt.setInt(1, classId);
		pStmt.setInt(2, version);
		
		ResultSet rs = pStmt.executeQuery();
		
		byte[] bytecode = null;
		
		if ( rs.next() )
			bytecode = rs.getBytes(1);
		
		// log: debug
		Utils.logDebug(String.format("classe encontrada -> %s", bytecode == null ? "false" : "true"));
		
		return ( bytecode );
	}
	
	/**
	 * <p>
	 * Obt�m as informa��es das classes internas relacionadas � vers�o informada da classe din�mica.
	 * </p>
	 * <p><i>
	 * Gets the inner classes information associated to the specified version of a LiveClass.
	 * </i></p>
	 * 
	 * @param conn - conex�o com o banco de dados
	 * <br><i>the database connection</i>
	 * @param versionId - chave da vers�o da classe din�mica na tabela ClassVersion
	 * <br><i>key of the LiveClass version, from the ClassVersion table</i>
	 * @return os bytecodes das classes internas relacionadas � vers�o informada da classe din�mica
	 * <br><i>the bytecode list of the inner classes associated to the specified version of a LiveClass</i>
	 * @throws SQLException em caso de erros com o banco de dados
	 * <br><i>if an error occurs when accessing the database</i>
	 */
	private List<byte[]> readFromInnerClassesTable(Connection conn, int versionId) throws SQLException
	{
		//
		String SQL = "SELECT bytecode FROM InnerClasses WHERE id_version = ?";
		
		// log: debug
		Utils.logDebug(this.debugSQL(SQL, versionId));
		
		PreparedStatement pStmt = conn.prepareStatement(SQL);
		pStmt.setInt(1, versionId);
		
		ResultSet rs = pStmt.executeQuery();

		List<byte[]> bytecodeList = new ArrayList<byte[]>();
		
		while ( rs.next() )
			bytecodeList.add( rs.getBytes(1) );
		
		// log: debug
		Utils.logDebug(String.format("classes internas -> %d", bytecodeList.size()));
		
		return ( bytecodeList );
	}
	
	/**
	 * <p>
	 * Apaga as informa��es de uma vers�o de classe din�mica.
	 * </p>
	 * <p><i>
	 * Deletes the information of the specified LiveClass version.
	 * </i></p>
	 * 
	 * @param conn - conex�o com o banco de dados
	 * <br><i>the database connection</i>
	 * @param versionId - chave da vers�o da classe din�mica a ser apagada
	 * <br><i>key of the LiveClass version to be deleted, from the ClassVersion table</i>
	 * @throws SQLException em caso de erros com o banco de dados
	 * <br><i>if an error occurs when accessing the database</i>
	 */
	private void removeFromClassVersionTable(Connection conn, int versionId) throws SQLException 
	{
		//
		String SQL = "DELETE FROM ClassVersion WHERE id = ?";
		
		// log: debug
		Utils.logDebug(this.debugSQL(SQL, versionId));
		
		PreparedStatement pStmt = conn.prepareStatement(SQL);
		pStmt.setInt(1, versionId);
				
		//
		int result = pStmt.executeUpdate();
		
		// log: debug
		Utils.logDebug(String.format("registros afetados -> %d", result));
	}
	
	/**
	 * <p>
	 * Persiste as informa��es de classe interna.
	 * </p>
	 * <p><i>
	 * Persists inner class information.
	 * </i></p>
	 * 
	 * @param conn - conex�o com o banco de dados
	 * <br><i>the database connection</i>
	 * @param versionId - chave da vers�o da classe din�mica na tabela ClassVersion
	 * <br><i>key of the LiveClass version, from the ClassVersion table</i>
	 * @param innerClassInfo - informa��es da classe interna
	 * <br><i>inner class information to be persisted</i>
	 * @throws SQLException em caso de erros com o banco de dados
	 * <br><i>if an error occurs when accessing the database</i>
	 */
	private void saveInnerClass(Connection conn, int versionId, ClassInfo innerClassInfo) throws SQLException
	{
		// salva na tabela de classes internas
		this.saveToInnerClassesTable(conn, versionId, innerClassInfo.getBytecode());
		
		// salva as classes internas das classes internas
		for ( ClassInfo innerClass : innerClassInfo.getInnerClassesInfo() )
			this.saveInnerClass(conn, versionId, innerClass);
	}
	
	/**
	 * <p>
	 * Obt�m as chaves e vers�es da classe din�mica informada.
	 * </p>
	 * <p><i>
	 * Gets the database keys and versions of the specified LiveClass.
	 * </i></p>
	 * 
	 * @param conn - conex�o com o banco de dados
	 * <br><i>the database connection</i>
	 * @param liveClassName - nome da classe din�mica
	 * <br><i>the name of the LiveClass</i> 
	 * @return as chaves e vers�es da classe din�mica informada
	 * <br><i>the database keys and versions of the specified LiveClass</i>
	 * @throws SQLException em caso de erros com o banco de dados
	 * <br><i>if an error occurs when accessing the database</i>
	 */
	private TableKeysInfo getTableKeysInfo(Connection conn, String liveClassName) throws SQLException
	{
		//
		String SQL = "SELECT l.id, c.id, l.safeModeVersion, MAX(c.version) AS currentVersion " + 
					 "FROM LiveClass l, ClassVersion c " + 
					 "WHERE l.id = c.id_class AND l.className = ? " +
					 "GROUP BY l.id";
		
		// log: debug
		Utils.logDebug(this.debugSQL(SQL, liveClassName));

		
		PreparedStatement pStmt = conn.prepareStatement(SQL);
		pStmt.setString(1,  liveClassName);
		ResultSet rs = pStmt.executeQuery();
		
		TableKeysInfo keysInfo = new TableKeysInfo();
		keysInfo.setClassName(liveClassName);
		
		if ( rs.next() )
		{
			keysInfo.setClassId(rs.getInt(1));
			keysInfo.setVersionId(rs.getInt(2));
			keysInfo.setSafeModeVersion(rs.getInt(3));
			keysInfo.setCurrentVersion(rs.getInt(4));
			
			// log: debug
			Utils.logDebug(keysInfo.toString());
		}
		
		return ( keysInfo );
	}
	
	/**
	 * <p>
	 * Obt�m as chaves e vers�es de todas classes din�micas persistidas no banco de dados.
	 * </p>
	 * <p><i>
	 * Gets the database keys and versions of all persisted LiveClasses.
	 * </i></p>
	 * 
	 * @param conn - conex�o com o banco de dados
	 * <br><i>the database connection</i>
	 * @return lista das chaves e vers�es de todas classes din�micas persistidas no banco de dados
	 * <br><i>the list of keys and versions of all persisted LiveClasses</i>
	 * @throws SQLException em caso de erros com o banco de dados
	 * <br><i>if an error occurs when accessing the database</i>
	 */
	private List<TableKeysInfo> getAllTableKeysInfo(Connection conn) throws SQLException
	{
		//
		String SQL = "SELECT l.className, l.id, c.id, l.safeModeVersion, MAX(c.version) AS currentVersion " + 
					 "FROM LiveClass l, ClassVersion c " + 
					 "WHERE l.id = c.id_class " +
					 "GROUP BY l.id";

		// log: debug
		Utils.logDebug(SQL);		
		
		PreparedStatement pStmt = conn.prepareStatement(SQL);
		ResultSet rs = pStmt.executeQuery();
		
		List<TableKeysInfo> keysInfoList = new ArrayList<TableKeysInfo>();
		
		while ( rs.next() )
		{
			TableKeysInfo keysInfo = new TableKeysInfo();
			keysInfo.setClassName(rs.getString(1));
			keysInfo.setClassId(rs.getInt(2));
			keysInfo.setVersionId(rs.getInt(3));
			keysInfo.setSafeModeVersion(rs.getInt(4));
			keysInfo.setCurrentVersion(rs.getInt(5));
			
			keysInfoList.add(keysInfo);
			
			// log: debug
			Utils.logDebug(keysInfo.toString());
		}
		
		return ( keysInfoList );
	}
	
	/**
	 * <p>
	 * Atualiza a vers�o de modo seguro da classe din�mica.
	 * </p>
	 * <p><i>
	 * Updates the LiveClass safe mode version.
	 * </i></p>
	 * 
	 * @param conn - conex�o com o banco de dados
	 * <br><i>the database connection</i>
	 * @param liveClassName - nome da classe din�mica
	 * <br><i>the name of the LiveClass</i> 
	 * @param version - nova vers�o de modo seguro da classe din�mica
	 * <br><i>the new safe mode version of the LiveClass</i>
	 * @return <i>true</i> se a vers�o de modo seguro foi atualizada, <i>false</i> caso contr�rio
	 * <br><i>true if the safe mode version was updated, false otherwise</i>
	 * @throws SQLException em caso de erros com o banco de dados
	 * <br><i>if an error occurs when accessing the database</i>
	 */
	private boolean updateSafeModeVersion(Connection conn, String liveClassName, int version) throws SQLException
	{
		// 
		String SQL = "UPDATE LiveCLass SET safeModeVersion=? WHERE className=?";

		// log: debug
		Utils.logDebug(this.debugSQL(SQL, version, liveClassName));
		
		PreparedStatement pStmt = conn.prepareStatement(SQL);
		pStmt.setInt(1, version);
		pStmt.setString(2, liveClassName);
		
		//
		int result = pStmt.executeUpdate();
		
		// log: debug
		Utils.logDebug(String.format("registros afetados -> %d", result));
		
		return ( result > 0 );
	}
	
	/**
	 * <p>
	 * M�todo utilit�rio interno para o log de debug de SQL.
	 * </p>
	 * <p><i>
	 * Internal utilitary method for SQL debugging log.
	 * </i></p>
	 * @param sql - cl�usula SQL para um objeto do tipo PreparedStatement
	 * <br><i>SQL clause for a PreparedStatement object</i>
	 * @param args- par�metros do PreparedStatement
	 * <br><i>PreparedStatement parameters</i>
	 * @return o SQL preenchido com os par�metros informados
	 * <br><i>the SQL clause filled with its informed parameters</i>
	 */
	private String debugSQL(String sql, Object... args)
	{
		StringBuffer buff = new StringBuffer(sql);
		
		for ( Object arg : args )
		{
			int index = buff.indexOf("?");
			buff.replace(index, index+1, arg.toString());
		}
		
		return ( buff.toString() );
	}
	
	
	/**
	 * <p>
	 * Classe auxiliar para mapear as chaves e vers�es de uma classe din�mica persistida no banco de dados.
	 * </p>
	 * <p><i>
	 * Utilitary class for mapping the database keys and versions of a persisted LiveClass.
	 * </i></p>
	 */
	private class TableKeysInfo implements ILiveClassVersionInfo
	{
		// o nome da classe dinamica
		private String name;
		
		// a chave da classe dinamica na tabela LiveClass
		private int classId;
		
		// a chave da versao da classe dinamica na tabela ClassVersion
		private int versionId;
		
		// a versao de modo seguro da classe dinamica (armazenada na tabela LiveClass)
		private int safeModeVersion;
		
		// a versao de modo padrao da classe dinamica (armazenada na tabela ClassVersion)
		private int currentVersion;

		
		/**
		 * <p>
		 * Construtor padr�o.
		 * </p>
		 * <p><i>
		 * Default constructor.
		 * </i></p>
		 */
		public TableKeysInfo()
		{
			this.name = null;
			this.classId = -1;
			this.versionId = -1;
			this.safeModeVersion = -1;
			this.currentVersion = -1;
		}
		
		/**
		 * <p>
		 * Atribui o nome da classe din�mica.
		 * </p>
		 * <p><i>
		 * Sets the LiveClass name.
		 * </i></p>
		 * 
		 * @param liveClassName - nome da classe din�mica
		 * <br><i>the name of the LiveClass</i>
		 */
		public void setClassName(String liveClassName)
		{
			this.name = liveClassName;
		}

		@Override
		public String getClassName()
		{
			return name;
		}

		/**
		 * <p>
		 * Atribui a chave da classe din�mica, lida da tabela LiveClass.
		 * </p>
		 * <p><i>
		 * Sets the key of the LiveClass, read from the LiveClass table.
		 * </i></p>
		 * 
		 * @param classId - chave da classe din�mica
		 * <br><i>the key of the LiveClass</i>
		 */
		public void setClassId(int classId)
		{
			this.classId = classId;
		}

		/**
		 * <p>
		 * Obt�m a chave da classe din�mica, lida da tabela LiveClass.
		 * </p>
		 * <p><i>
		 * Gets the key of the LiveClass, read from the LiveClass table.
		 * </i></p>
		 * 
		 * @return a chave da classe din�mica
		 * <br><i>the key of the LiveClass</i>
		 */
		public int getClassId()
		{
			return classId;
		}
		
		/**
		 * <p>
		 * Atribui a chave da vers�o atual da classe din�mica, lida da tabela ClassVersion.
		 * </p>
		 * <p><i>
		 * Sets the key of the LiveClass current version, read from the ClassVersion table.
		 * </i></p>
		 *  
		 * @param versionId - chave da vers�o atual da classe din�mica
		 * <br><i>the key of the LiveClass current version</i>
		 */
		public void setVersionId(int versionId)
		{
			this.versionId = versionId;
		}
		
		/**
		 * <p>
		 * Obt�m a chave da vers�o atual da classe din�mica, lida da tabela ClassVersion.
		 * </p>
		 * <p><i>
		 * Gets the key of the LiveClass current version, read from the ClassVersion table.
		 * </i></p>
		 * 
		 * @return a chave da vers�o atual da classe din�mica
		 * <br><i>the key of the LiveClass current version</i>
		 */
		public int getVersionId()
		{
			return versionId;
		}

		/**
		 * <p>
		 * Atribui a vers�o de modo seguro da classe din�mica, lida da tabela LiveClass.
		 * </p>
		 * <p><i>
		 * Sets the LiveClass safe mode version, read from the LiveClass table.
		 * </i></p>
		 * 
		 * @param safeModeVersion - vers�o atual de modo seguro da classe din�mica
		 * <br><i>the current safe mode version of the LiveClass</i>
		 */
		public void setSafeModeVersion(int safeModeVersion)
		{
			this.safeModeVersion = safeModeVersion;
		}
		
		@Override
		public int getSafeModeVersion()
		{
			return safeModeVersion;
		}

		/**
		 * <p>
		 * Atribui a vers�o atual da classe din�mica, lida da tabela ClassVersion.
		 * </p>
		 * <p><i>
		 * Sets the LiveClass current version, read from the ClassVersion table.
		 * </i></p>
		 * 
		 * @param currentVersion - vers�o atual da classe din�mica
		 * <br><i>the current version of the LiveClass</i>
		 */
		public void setCurrentVersion(int currentVersion)
		{
			this.currentVersion = currentVersion;
		}

		@Override
		public int getCurrentVersion()
		{
			return currentVersion;
		}

		@Override
		public String toString()
		{
			return "[name=" + name + ", classId=" + classId + ", versionId=" + versionId
					+ ", safeModeVersion=" + safeModeVersion + ", currentVersion=" + currentVersion + "]";
		}
	}
}
