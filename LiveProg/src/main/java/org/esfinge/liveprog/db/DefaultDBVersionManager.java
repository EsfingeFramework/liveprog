package org.esfinge.liveprog.db;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.swingBean.actions.ApplicationAction;
import org.swingBean.descriptor.BeanTableModel;
import org.swingBean.descriptor.TableFieldDescriptor;
import org.swingBean.descriptor.XMLDescriptorFactory;
import org.swingBean.gui.JActButton;
import org.swingBean.gui.JBeanTable;

/**
 * Gerenciador padrao de versao da classes dinamicas para a implementacao padrao do LiveClassBD (SQLite). 
 */
@SuppressWarnings("serial")
public class DefaultDBVersionManager extends JFrame implements ILiveClassDBVersionManager
{
	// tabela com as versoes das classes dinamicas cadastradas no BD
	private JBeanTable liveClassTable;
	
	// modelo com as informacoes das versoes das classes dinamicas do BD
	private BeanTableModel<LiveClassBean> tableModel;
	
	// lista de observadores a serem notificados das 
	// versoes das classes dinamicas comitadas/retrocedidas no BD
	private List<ILiveClassDBVersionObserver> observers;

	// botao para atualizar a lista de versoes
	private JActButton btnReload;
	
	// botao para executar o commit da versao selecionada
	private JActButton btnCommit;
	
	// botao para executar o rollback da versao selecionada
	private JActButton btnRollback;
	
	// interface com o BD
	private ILiveClassDB liveClassDB;
	
	
	/**
	 * Construtor padrao.
	 */
	public DefaultDBVersionManager(ILiveClassDB liveClassDB)
	{
		super("Gerenciador de Classes Dinamicas");
		this.add(this.generateForm());
		this.setSize(new Dimension(640, 400));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.liveClassDB = liveClassDB;
		
		this.observers = new ArrayList<ILiveClassDBVersionObserver>();
	}
	
	/**
	 * Gera o painel principal da janela.
	 * 
	 * @return o painel principal da janela
	 */
	private JPanel generateForm()
	{
		// descritor XML do layout da Tabela de classes dinamicas cadastrados
		TableFieldDescriptor tableDescriptor = XMLDescriptorFactory.getTableFieldDescriptor(
				LiveClassBean.class, "liveclassmanager_descriptor.xml", "Classes Dinamicas");

		// Tabela das classes dinamicas e suas versoes
		this.tableModel = new BeanTableModel<LiveClassBean>(tableDescriptor);
		this.tableModel.setBeanList(this.getLiveClassList());

		this.liveClassTable = new JBeanTable(tableModel);
		this.liveClassTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.liveClassTable.getSelectionModel().addListSelectionListener((evt) -> updateButtons());
		
		// atualizar
		ApplicationAction actReload = new ApplicationAction() {
			public void execute()
			{
				tableModel.setBeanList(getLiveClassList());
			}
		};
		
		// commit
		ApplicationAction actCommit = new ApplicationAction() {
			public void execute()
			{
				int selectedRow = liveClassTable.getSelectedRow();
				if ( selectedRow >= 0 )
					executeCommit(tableModel.getBeanAt(selectedRow));
			}
		};
		
		// rollback
		ApplicationAction actRollback = new ApplicationAction() {
			public void execute()
			{
				int selectedRow = liveClassTable.getSelectedRow();
				if ( selectedRow >= 0 )
					executeRollback(tableModel.getBeanAt(selectedRow));
			}
		};
		
		// botoes de atualizar, commit e rollback
		this.btnReload   = new JActButton("Atualizar", actReload);
		this.btnCommit   = new JActButton("Commit", actCommit);
		this.btnRollback = new JActButton("Rollback", actRollback);
		
		this.btnCommit.setEnabled(false);
		this.btnRollback.setEnabled(false);
		
		// painel com os botoes
		JPanel pnlButtons = new JPanel();
		pnlButtons.add(btnReload);
		pnlButtons.add(btnCommit);
		pnlButtons.add(btnRollback);
		
		// painel principal
		JPanel pnlForm = new JPanel(new BorderLayout());
		pnlForm.add(new JScrollPane(liveClassTable), BorderLayout.CENTER);
		pnlForm.add(pnlButtons, BorderLayout.SOUTH);
		
		return ( pnlForm );
	}
	
	@Override
	public void addObserver(ILiveClassDBVersionObserver observer)
	{
		this.observers.add(observer);
	}

	@Override
	public void removeObserver(ILiveClassDBVersionObserver observer)
	{
		this.observers.remove(observer);
	}
	
	/**
	 * Notifica os observadores que uma versao de classe dinamica foi alterada no banco de dados.
	 * 
	 * @param className o nome da classe dinamica
	 * @param operation <b>true</b> para commit, <b>false</b> para rollback
	 */
	private void notifyObservers(String className, boolean operation)
	{
		// commit
		if ( operation )
			this.observers.forEach(obs -> obs.liveClassCommitted(className));
		
		//rollback
		else
			this.observers.forEach(obs -> obs.liveClassRolledBack(className));
	}

	/**
	 * Obtem a lista de descritores de versoes de classes dinamicas cadastradas no banco de dados.
	 *  
	 * @return a lista de descritores das versoes de classes dinamicas cadastradas no banco de dados
	 */
	private List<LiveClassBean> getLiveClassList()
	{
		List<LiveClassBean> beanList = new ArrayList<LiveClassBean>();
	
		try
		{
			// obtem os descritores de versoes do banco de dados
			for ( ILiveClassDBVersion lc : DefaultLiveClassDB.getInstance().getAllLiveClassVersion() )
			{
				LiveClassBean bean = new LiveClassBean();
				bean.setName(lc.getName());
				bean.setCurrentVersion(lc.getCurrentVersion());
				bean.setTestVersion(lc.getTestVersion());
				
				beanList.add(bean);
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
		
		return ( beanList );
	}
	
	/**
	 * Atualiza o status dos botoes de commit e rollback 
	 * conforme as versoes da classe dinamica selecionada na tabela.
	 */
	private void updateButtons()
	{
		// a linha selecionada
		int selectedRow = liveClassTable.getSelectedRow();
	
		if ( selectedRow >= 0 )
		{
			// obtem o bean da linha selecionada
			LiveClassBean bean = tableModel.getBeanAt(selectedRow);
			
			// pode executar commit?
			this.btnCommit.setEnabled(bean.getCurrentVersion() < bean.getTestVersion());
			
			// pode executar rollback?
			this.btnRollback.setEnabled(bean.getCurrentVersion() > 1);
		}
		else
		{
			// desabilita
			this.btnCommit.setEnabled(false);
			this.btnRollback.setEnabled(false);
		}
	}
	
	/**
	 * Executa o commit da versao da classe dinamica selecionada.
	 * 
	 * @param classBean a classe selecionada na tabela
	 */
	private void executeCommit(LiveClassBean classBean)
	{
		try
		{
			// faz o commit no banco de dados
			this.liveClassDB.commitLiveClass(classBean.getName());
			
			// atualiza a tabela
			tableModel.setBeanList(this.getLiveClassList());
			
			// notifica os observadores
			this.notifyObservers(classBean.getName(), true);
		}
		catch (IllegalStateException | SQLException e)
		{
			// TODO: debug..
			e.printStackTrace();
		}
	}
	
	/**
	 * Executa o rollback da versao da classe dinamica selecionada.
	 * 
	 * @param classBean a classe selecinada na tabela
	 */
	private void executeRollback(LiveClassBean classBean)
	{
		try
		{
			// faz o rollback no banco de dados
			this.liveClassDB.rollbackLiveClass(classBean.getName());
			
			// atualiza a tabela
			tableModel.setBeanList(this.getLiveClassList());
			
			// notifica os observadores
			this.notifyObservers(classBean.getName(), false);
		}
		catch (IllegalStateException | SQLException e)
		{
			// TODO: debug..
			e.printStackTrace();
		}
	}
	
	/**
	 * Classe para encapsular os descritores de versoes de uma classe dinamica.
	 * Necessario para o funcionamento do SwingBeans.
	 */
	public class LiveClassBean
	{
		// o nome da classe dinamica
		private String name;
		
		// atual versao de producao da classe 
		private int currentVersion;
		
		// atual versao de testes da classe
		private int testVersion;
		

		/**
		 * Retorna o nome da classe dinamica.
		 * 
		 * @return o nome da classe dinamica
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * Atribui o nome da classe dinamica.
		 * 
		 * @param name o nome da classe dinamica
		 */
		public void setName(String name)
		{
			this.name = name;
		}

		/**
		 * Retorna a atual versao de producao da classe dinamica.
		 * 
		 * @return a versao de producao da classe dinamica
		 */
		public int getCurrentVersion()
		{
			return currentVersion;
		}

		/**
		 * Atribui a atual versao de producao da classe dinamica.
		 * 
		 * @param currentVersion a versao de producao da classe dinamica
		 */
		public void setCurrentVersion(int currentVersion)
		{
			this.currentVersion = currentVersion;
		}

		/**
		 * Retorna a atual versao de testes da classe dinamica.
		 * 
		 * @return a versao de testes da classe dinamica
		 */
		public int getTestVersion()
		{
			return testVersion;
		}

		/**
		 * Atribui a atual versao de testes da classe dinamica.
		 * 
		 * @param testVersion a versao de testes da classe dinamica
		 */
		public void setTestVersion(int testVersion)
		{
			this.testVersion = testVersion;
		}
	}
}
