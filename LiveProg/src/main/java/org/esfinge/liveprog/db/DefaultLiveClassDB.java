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

import org.esfinge.liveprog.instrumentation.ClassInfo;
import org.esfinge.liveprog.instrumentation.InstrumentationService;
import org.sqlite.SQLiteConfig;

/**
 * Classe para gerenciamento das versoes de classes dinamicas.
 */
public class DefaultLiveClassDB implements ILiveClassDB
{
	// instancia singleton
	private static DefaultLiveClassDB _singleton;
	
	// caminho para o arquivo do BD
	private static String _dbFilePath;
	
	
	/**
	 * Especifica o caminho para o arquivo do banco de dados.
	 * 
	 * @param path o caminho para o arquivo de banco de dados
	 */
	public static void setDatabaseFilePath(String path)
	{
		_dbFilePath = path;
	}
	
	/**
	 * Recupera a instancia para gerenciamento das versoes de classes dinamicas.
	 * 
	 * @return a instancia para gerenciamento das versoes de classes dinamicas  
	 * @throws IllegalStateException caso o caminho para o arquivo do banco de dados ainda nao tenha sido especificado 
	 * @throws SQLException em caso de erros com o banco de dados
	 * @see #setDatabaseFilePath(String)
	 */
	public static DefaultLiveClassDB getInstance() throws IllegalStateException, SQLException
	{
		if ( _dbFilePath == null )
			throw new IllegalStateException("Database file path not set!");
		
		if ( _singleton == null )
		{
			_singleton = new DefaultLiveClassDB();
			_singleton.start();
		}
		
		return ( _singleton );
	}
	
	@Override
	public ClassInfo getLiveClass(String className, boolean testMode) throws SQLException
	{
		// obtem a conexao com o BD
		Connection conn = this.getConnection();
	
		// obtem as versoes da classe salvas no BD
		TableKeysInfo keysInfo = this.getTableKeysInfo(conn, className);
	
		// nao ha versao salva no BD para essa classe
		if ( keysInfo.getProductionVersion() < 0 )
			return ( null );
		
		// bytecode da classe
		byte[] classBytecode;
		
		if ( testMode )
			classBytecode = this.readFromClassVersionTable(conn, keysInfo.getClassId(), keysInfo.getTestVersion());
		else
			classBytecode = this.readFromClassVersionTable(conn, keysInfo.getClassId(), keysInfo.getProductionVersion());
		
		// recupera as informacoes da classe
		ClassInfo classInfo = InstrumentationService.inspect(classBytecode);
		
		// obtem os bytecodes das classes internas..
		List<byte[]> innerClassesBytecodeList = this.readFromInnerClassesTable(conn, keysInfo.getVersionId());
		
		// recupera as informacoes das classes internas
		for ( byte[] innerClassBytecode : innerClassesBytecodeList )
			classInfo.addInnerClassInfo(InstrumentationService.inspect(innerClassBytecode));
		
		// fecha a conexao com o BD
		conn.close();
		
		return ( classInfo );
	}
	
	@Override
	public void saveLiveClass(String className, ClassInfo classInfo) throws SQLException
	{
		// obtem a conexao com o BD
		Connection conn = this.getConnection();

		// obtem as versoes da classe salvas no BD
		TableKeysInfo keysInfo = this.getTableKeysInfo(conn, className);

		// nao ha versao salva no BD para essa classe
		if ( keysInfo.getProductionVersion() < 0 )
		{
			// primeira versao				
			int version = 1;
			
			// salva as informacoes da classe
			int classId = this.saveToLiveClassTable(conn, className, version);
			
			// salva a versao e o bytecode da classe
			int versionId = this.saveToClassVersionTable(conn, classId, version, classInfo.getBytecode());
			
			// salva as classes internas da classe
			for ( ClassInfo innerClass : classInfo.getInnerClassesInfo() )
				this.saveInnerClass(conn, versionId, innerClass);
		}
		else
		{
			// ja existe uma versao salva, atualiza a tabela de versoes..
			
			// a versao de producao eh diferente da versao de testes?
			if ( keysInfo.getProductionVersion() != keysInfo.getTestVersion() )
			{
				// remove a versao de teste atual para incluir a nova versao
				// (e as classes internas associadas a essa versao)
				this.removeFromClassVersionTable(conn, keysInfo.getVersionId());
			}
			
			// insere na tabela de versoes
			int versionId = this.saveToClassVersionTable(conn, keysInfo.getClassId(), keysInfo.getProductionVersion()+1, classInfo.getBytecode());
				
			// salva as classes internas da classe
			for ( ClassInfo innerClass : classInfo.getInnerClassesInfo() )
				this.saveInnerClass(conn, versionId, innerClass);
		}
		
		// fecha a conexao com o BD
		conn.close();
	}

	@Override
	public ILiveClassDBVersion getLiveClassVersion(String className) throws SQLException
	{
		// obtem a conexao com o BD
		Connection conn = this.getConnection();

		// obtem as versoes da classe salvas no BD
		TableKeysInfo keysInfo = this.getTableKeysInfo(conn, className);
		
		// fecha a conexao com o BD
		conn.close();

		return ( keysInfo );
	}
	
	@Override
	public List<ILiveClassDBVersion> getAllLiveClassVersion() throws SQLException
	{
		// obtem a conexao com o BD
		Connection conn = this.getConnection();

		// obtem as versoes da classe salvas no BD
		List<TableKeysInfo> keysInfoList = this.getAllTableKeysInfo(conn);

		// fecha a conexao com o BD
		conn.close();

		return ( new ArrayList<ILiveClassDBVersion>(keysInfoList) );
	}
	
	@Override
	public boolean commitLiveClass(String className) throws SQLException
	{
		// obtem a conexao com o BD
		Connection conn = this.getConnection();
		
		// obtem as versoes da classe salvas no BD
		TableKeysInfo keysInfo = this.getTableKeysInfo(conn, className);
		
		// 
		int updated = 0;
		
		// a versao de producao eh diferente da versao de testes?
		if ( keysInfo.getProductionVersion() < keysInfo.getTestVersion() )
		{
			// atualiza a versao de producao para a versao de testes
			updated = this.updateProductionVersion(conn, className, keysInfo.getTestVersion());
		}

		// fecha a conexao com o BD
		conn.close();
		
		// verifica se atualizou algum registro
		return ( updated > 0 );
	}

	@Override
	public boolean rollbackLiveClass(String className) throws SQLException
	{
		// obtem a conexao com o BD
		Connection conn = this.getConnection();
		
		// obtem as versoes da classe salvas no BD
		TableKeysInfo keysInfo = this.getTableKeysInfo(conn, className);
		
		// 
		int updated = 0;
		
		// a versao de producao superior que a primeira versao?
		if ( keysInfo.getProductionVersion() > 1 )
		{
			// retrocede uma versao
			int newVersion = keysInfo.getProductionVersion() - 1;
			
			// descarta as versoes de testes
			while ( keysInfo.getTestVersion() > newVersion )
			{
				// apaga as versoes de teste
				this.removeFromClassVersionTable(conn, keysInfo.getVersionId());
				
				//
				keysInfo = this.getTableKeysInfo(conn, className);
			}
			
			// atualiza a versao de producao para a versao de testes
			updated = this.updateProductionVersion(conn, className, newVersion);
		}

		// fecha a conexao com o BD
		conn.close();
		
		// verifica se atualizou algum registro
		return ( updated > 0 );
	}

	/**
	 * Inicializa as configuracoes para acesso ao banco de dados.
	 * 
	 * @throws SQLException em caso de erros com o banco de dados
	 */
	private void start() throws SQLException
	{
		// obtem a conexao com o BD
		Connection conn = this.getConnection();
		
		try
		{
			// cria as tabelas do BD, caso nao existam
			String sql = "CREATE TABLE IF NOT EXISTS LiveClass (" + 
						 " id integer PRIMARY KEY AUTOINCREMENT," + 
						 " className text NOT NULL," + 
						 " currentVersion integer);";

			// cria a tabela de versao corrente
			Statement stmt = conn.createStatement();
			stmt.execute(sql);

			// cria a tabela de versoes das classes
			sql = "CREATE TABLE IF NOT EXISTS ClassVersion (" + 
				  "	id integer PRIMARY KEY AUTOINCREMENT,"     + 
				  " id_class integer NOT NULL,"                + 
				  " version integer NOT NULL,"                 +
				  "	bytecode BLOB NOT NULL,"                   +
				  " FOREIGN KEY (id_class) REFERENCES LiveClass (id) " + 
				  " ON DELETE CASCADE ON UPDATE NO ACTION);";

			stmt.execute(sql);
			
			// cria a tabela de classes internas das classes
			sql = "CREATE TABLE IF NOT EXISTS InnerClasses (" + 
				  "	id integer PRIMARY KEY AUTOINCREMENT,"    + 
				  " id_version integer NOT NULL,"             + 
				  "	bytecode BLOB NOT NULL,"                  +
				  " FOREIGN KEY (id_version) REFERENCES ClassVersion (id) " + 
				  " ON DELETE CASCADE ON UPDATE NO ACTION);";

			stmt.execute(sql);

			// TODO: debug..
			System.out.println("Connection to SQLite has been established.");
		}		
		
		finally
		{
			conn.close();
		}
	}
	
	/**
	 * Obtem uma conexao com o banco de dados.
	 * 
	 * @return uma conexao com o banco de dados 
	 * 
	 * @throws SQLException em caso de erros com o banco de dados
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
	 * Salva as informacoes da classe dinamica na tabela LiveClass.
	 * 
	 * @param conn a conexao com o banco de dados
	 * @param className o nome da classe dinamica cujas informacoes serao salvas
	 * @param currentVersion a versao de producao da classe dinamica
	 * @return a chave criada na tabela LiveClass para a classe informada
	 * @throws SQLException em caso de erros com o banco de dados
	 */
	private int saveToLiveClassTable(Connection conn, String className, int currentVersion) throws SQLException
	{
		PreparedStatement pStmt = conn.prepareStatement("INSERT INTO LiveCLass (className, currentVersion) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
		pStmt.setString(1, className);
		pStmt.setInt(2, currentVersion);
		
		pStmt.executeUpdate();
		ResultSet rs = pStmt.getGeneratedKeys();
		rs.next();
		
		return ( rs.getInt(1) );
	}
	
	/**
	 * Salva as informacoes da classe dinamica na tabela ClassVersion.
	 * 
	 * @param conn a conexao com o banco de dados
	 * @param classId a chave da classe dinamica na tabela LiveClass
	 * @param classVersion a versao da classe dinamica a ser salva
	 * @param classBytecode o bytecode da classe dinamica a ser salvo
	 * @return a chave criada na tabela ClassVersion para a classe informada
	 * @throws SQLException em caso de erros com o banco de dados
	 */
	private int saveToClassVersionTable(Connection conn, int classId, int classVersion, byte[] classBytecode ) throws SQLException
	{
		PreparedStatement pStmt = conn.prepareStatement("INSERT INTO ClassVersion (id_class, version, bytecode) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		pStmt.setInt(1, classId);
		pStmt.setInt(2, classVersion);
		pStmt.setBytes(3, classBytecode);
		
		pStmt.executeUpdate();
		ResultSet rs = pStmt.getGeneratedKeys();
		rs.next();
		
		return ( rs.getInt(1) );
	}
	
	/**
	 * Salva as informacoes da classe interna da classe dinamica na tabela InnerClasses.
	 * 
	 * @param conn a conexao com o banco de dados
	 * @param versionId a chave da versao da classe dinamica na tabela ClassVersion
	 * @param innerClassBytecode o bytecode da classe interna da classe dinamica
	 * @return a chave criada na tabela InnerClasses para a classe interna informada
	 * @throws SQLException em caso de erros com o banco de dados
	 */
	private int saveToInnerClassesTable(Connection conn, int versionId, byte[] innerClassBytecode ) throws SQLException
	{
		PreparedStatement pStmt = conn.prepareStatement("INSERT INTO InnerClasses (id_version, bytecode) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
		pStmt.setInt(1, versionId);
		pStmt.setObject(2, innerClassBytecode);
		
		pStmt.executeUpdate();
		ResultSet rs = pStmt.getGeneratedKeys();
		rs.next();
		
		return ( rs.getInt(1) );
	}

	/**
	 * Le as informacoes da classe dinamica na tabela ClassVersion.
	 * 
	 * @param conn a conexao com o banco de dados
	 * @param classId a chave da classe dinamica na tabela LiveClass
	 * @param version a versao da classe dinamica a ser recuperada
	 * @return o bytecode da classe dinamica na versao informada
	 * @throws SQLException em caso de erros com o banco de dados
	 */
	private byte[] readFromClassVersionTable(Connection conn, int classId, int version) throws SQLException
	{
		PreparedStatement pStmt = conn.prepareStatement("SELECT bytecode FROM ClassVersion WHERE id_class = ? AND version = ?");
		pStmt.setInt(1, classId);
		pStmt.setInt(2, version);
		
		ResultSet rs = pStmt.executeQuery();
		
		if ( rs.next() )
			return ( rs.getBytes(1) );
		
		return ( null );
	}
	
	/**
	 * Le as informacoes das classes internas na tabela InnerClasses.
	 * 
	 * @param conn a conexao com o banco de dados
	 * @param versionId a chave da versao da classe dinamica na tabela ClassVersion
	 * @return a lista de bytecodes das classes internas associadas a classe dinamica na versao informada
	 * @throws SQLException em caso de erros com o banco de dados
	 */
	private List<byte[]> readFromInnerClassesTable(Connection conn, int versionId) throws SQLException
	{
		PreparedStatement pStmt = conn.prepareStatement("SELECT bytecode FROM InnerClasses WHERE id_version = ?");
		pStmt.setInt(1, versionId);
		
		ResultSet rs = pStmt.executeQuery();

		List<byte[]> bytecodeList = new ArrayList<byte[]>();
		
		while ( rs.next() )
			bytecodeList.add( rs.getBytes(1) );
		
		return ( bytecodeList );
	}
	
	/**
	 * Apaga as informacoes de uma versao de classe dinamica das tabelas ClassVersion e InnerClasses.
	 * 
	 * @param conn a conexao com o banco de dados
	 * @param versionId a chave da versao da classe dinamica na tabela ClassVersion a ser apagada
	 * @throws SQLException em caso de erros com o banco de dados
	 */
	private void removeFromClassVersionTable(Connection conn, int versionId) throws SQLException 
	{
		PreparedStatement pStmt = conn.prepareStatement("DELETE FROM ClassVersion WHERE id = ?");
		pStmt.setInt(1, versionId);
		
		pStmt.executeUpdate();
	}
	
	/**
	 * Salva as informacoes das classes internas no banco de dados.
	 * 
	 * @param conn a conexao com o banco de dados
	 * @param versionId a chave da versao da classe na tabela ClassVersion
	 * @param innerClassInfo as informacoes da classe interna
	 * @throws SQLException em caso de erros com o banco de dados
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
	 * Obtem as chaves e versoes da classe informada.
	 * 
	 * @param conn a conexao com o banco de dados
	 * @param className o nome da classe dinamica cujas informacoes serao retornadas
	 * @return as chaves e versoes da classe dinamica informada
	 * @throws SQLException em caso de erros com o banco de dados
	 */
	private TableKeysInfo getTableKeysInfo(Connection conn, String className) throws SQLException
	{
		String SQL = "SELECT l.id, c.id, l.currentVersion, MAX(c.version) AS max_version " + 
					 "FROM LiveClass l, ClassVersion c " + 
					 "WHERE l.id = c.id_class AND l.className = ? " +
					 "GROUP BY l.id";
		
		PreparedStatement pStmt = conn.prepareStatement(SQL);
		pStmt.setString(1,  className);
		ResultSet rs = pStmt.executeQuery();
		
		TableKeysInfo keysInfo = new TableKeysInfo();
		keysInfo.setName(className);
		
		if ( rs.next() )
		{
			keysInfo.setClassId(rs.getInt(1));
			keysInfo.setVersionId(rs.getInt(2));
			keysInfo.setProductionVersion(rs.getInt(3));
			keysInfo.setTestVersion(rs.getInt(4));
		}
		
		return ( keysInfo );
	}
	
	/**
	 * Obtem as chaves e versoes de todas classes salvas no banco de dados.
	 * 
	 * @param conn a conexao com o banco de dados
	 * @return lista com as chaves e versoes de todas as classes dinamicas salvas no banco de dados
	 * @throws SQLException em caso de erros com o banco de dados
	 */
	private List<TableKeysInfo> getAllTableKeysInfo(Connection conn) throws SQLException
	{
		String SQL = "SELECT l.className, l.id, c.id, l.currentVersion, MAX(c.version) AS max_version " + 
					 "FROM LiveClass l, ClassVersion c " + 
					 "WHERE l.id = c.id_class " +
					 "GROUP BY l.id";
		
		PreparedStatement pStmt = conn.prepareStatement(SQL);
		ResultSet rs = pStmt.executeQuery();
		
		List<TableKeysInfo> keysInfoList = new ArrayList<TableKeysInfo>();
		
		while ( rs.next() )
		{
			TableKeysInfo keysInfo = new TableKeysInfo();
			keysInfo.setName(rs.getString(1));
			keysInfo.setClassId(rs.getInt(2));
			keysInfo.setVersionId(rs.getInt(3));
			keysInfo.setProductionVersion(rs.getInt(4));
			keysInfo.setTestVersion(rs.getInt(5));
			
			keysInfoList.add(keysInfo);
		}
		
		return ( keysInfoList );
	}
	
	/**
	 * Atualiza a versao de producao da classe dinamica LiveClass.
	 * 
	 * @param conn a conexao com o banco de dados
	 * @param className o nome da classe dinamica 
	 * @param version a nova versao de producao da classe dinamica
	 * @return o numero de registros que foram atualizados (1 - OK, 0 - NOK)
	 * @throws SQLException em caso de erros com o banco de dados
	 */
	private int updateProductionVersion(Connection conn, String className, int version) throws SQLException
	{
		PreparedStatement pStmt = conn.prepareStatement("UPDATE LiveCLass SET currentVersion=? WHERE className=?");
		pStmt.setInt(1, version);
		pStmt.setString(2, className);
		
		return ( pStmt.executeUpdate() );
	}
	
	/**
	 * Classe auxiliar para armazenar as chaves e versoes de uma classe dinamica armazenada no banco de dados.
	 */
	private class TableKeysInfo implements ILiveClassDBVersion
	{
		// o nome da classe dinamica
		private String name
		;
		
		// a chave da classe dinamica na tabela LiveClass
		private int classId;
		
		// a chave da versao da classe dinamica na tabela ClassVersion
		private int versionId;
		
		// a versao de producao da classe dinamica (armazenada na tabela LiveClass)
		private int productionVersion;
		
		// a versao mais atual da classe dinamica (armazenada na tabela ClassVersion)
		private int testVersion;

		
		/**
		 * Construtor padrao.
		 */
		public TableKeysInfo()
		{
			this.name = null;
			this.classId = -1;
			this.versionId = -1;
			this.productionVersion = -1;
			this.testVersion = -1;
		}
		
		/**
		 * Atribui o nome da classe dinamica.
		 * 
		 * @param className o nome da classe dinamica
		 */
		public void setName(String className)
		{
			this.name = className;
		}

		@Override
		public String getName()
		{
			return name;
		}

		/**
		 * Atribui a chave da classe dinamica lida da tabela LiveClass.
		 * 
		 * @param classId a chave da classe dinamica lida da tabela LiveClass
		 */
		public void setClassId(int classId)
		{
			this.classId = classId;
		}

		/**
		 * Retorna a chave da classe dinamica lida da tabela LiveClass.
		 * 
		 * @return a chave da classe dinamica lida da tabela LiveClass
		 */
		public int getClassId()
		{
			return classId;
		}
		
		/**
		 * Atribui a chave da versao da classe dinamica lida da tabela ClassVersion.
		 *  
		 * @param versionId a chave da classe dinamica lida da tabela ClassVersion
		 */
		public void setVersionId(int versionId)
		{
			this.versionId = versionId;
		}
		
		/**
		 * Retorna a chave da versao da classe dinamica lida da tabela ClassVersion.
		 * 
		 * @return a chave da versao da classe dinamica lida da tabela ClassVersion
		 */
		public int getVersionId()
		{
			return versionId;
		}

		/**
		 * Atribui a versao de producao da classe dinamica lida da tabela LiveClass.
		 * 
		 * @param productionVersion a versao de producao da classe dinamica lida da tabela LiveClass
		 */
		public void setProductionVersion(int productionVersion)
		{
			this.productionVersion = productionVersion;
		}

		/**
		 * Retorna a versao de producao da classe dinamica lida da tabela LiveClass.
		 * 
		 * @return a versao de producao da classe dinamica lida da tabela LiveClass
		 */
		public int getProductionVersion()
		{
			return productionVersion;
		}

		/**
		 * Atribui a versao de testes da classe dinamica lida da tabela ClassVersion.
		 * 
		 * @param testVersion a versao de testes da classe dinamica lida da tabela ClassVersion
		 */
		public void setTestVersion(int testVersion)
		{
			this.testVersion = testVersion;
		}
		
		@Override
		public int getTestVersion()
		{
			return testVersion;
		}

		@Override
		public int getCurrentVersion()
		{
			return ( this.getProductionVersion() );
		}
	}
}
