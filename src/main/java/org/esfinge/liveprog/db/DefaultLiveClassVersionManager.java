package org.esfinge.liveprog.db;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.esfinge.liveprog.util.Utils;

/**
 * <p>
 * Implementação padrão para o gerenciamento das versões de classes dinâmicas, utilizando componentes <i>Java Swing</i>.
 * <br>
 * A tela principal é uma janela <i>JFrame</i>, iniciada de forma não-visível.
 * Os labels dos componentes estão no idioma Português-BR. Para customizá-los para outros valores ou idiomas,
 * pode-se informar um <i>Map</i> no momento da construção contendo as seguintes chaves e valores:
 * <ul>
 * <li><i>title</i> - o título da janela principal
 * <li><i>button.refresh</i> - o label do botão que atualiza a tabela de versões das classes dinâmicas
 * <li><i>button.commit</i> - o label do botão que realiza o aceite da versão da classe dinâmica selecionada
 * <li><i>button.rollback</i> - o label do botão que realiza o descarte da versão da classe dinâmica selecionada
 * <li><i>table.classname</i> - o label da coluna de nome da tabela de versões
 * <li><i>table.version</i> - o label da coluna de versão da tabela de versões 
 * <li><i>table.safemode</i> - o label da coluna de versão do modo seguro da tabela de versões
 * </ul>
 * </p>
 * <p><i>
 * Default implementation, in Java Swing, providing LiveClasses versioning management.
 * <br>
 * The main window is a JFrame component that is initially invisible.
 * All labels are, by default, in the Portuguese-BR language. In order to customize their values, a Map may be used 
 * containing the following keys and values: 
 * <ul>
 * <li>title - the title of the main window
 * <li>button.refresh - the label of the button that refreshs the table of LiveClasses
 * <li>button.commit - the label of the button that commits the version of the selected LiveClass
 * <li>button.rollback - the label of the button that rolls back the version of the selected LiveClass
 * <li>table.classname - the label of the table's column that displays the name of the LiveClass
 * <li>table.version - the label of the table's column that displays the current version of the LiveClass 
 * <li>table.safemode - the label of the table's column that displays the safe mode version of the LiveClass
 * </ul>
 * </i></p>
 */
@SuppressWarnings("serial")
public class DefaultLiveClassVersionManager extends JFrame implements ILiveClassVersionManager
{
	// tabela com as versoes das classes dinamicas cadastradas no BD
	private JTable liveClassTable;
	
	// modelo com as informacoes das versoes das classes dinamicas do BD
	private LiveClassTableModel tableModel;
	
	// lista de observadores a serem notificados das 
	// versoes das classes dinamicas comitadas/retrocedidas no BD
	private List<ILiveClassVersionObserver> observers;

	// botao para atualizar a lista de versoes
	private JButton btnReload;
	
	// botao para executar o commit da versao selecionada
	private JButton btnCommit;
	
	// botao para executar o rollback da versao selecionada
	private JButton btnRollback;
	
	// interface com o BD
	private ILiveClassPersistence liveClassDB;
	
	// mapa dos labels dos componentes
	private Map<String,String> labelsMap;
	
	
	/**
	 * <p>
	 * Constrói uma nova janela para gerenciamento das versões de classes dinâmicas, utilizando os labels padrão dos componentes.
	 * </p>
	 * <p><i>
	 * Constructs a new window for LiveClasses versioning management, using the default Portuguese-BR labels.
	 * </i></p>
	 * 
	 * @param liveClassDB - gerenciador de persistência de classes dinâmicas
	 * <br><i>LiveClasses persistence manager</i>
	 */
	public DefaultLiveClassVersionManager(ILiveClassPersistence liveClassDB)
	{
		this(liveClassDB, null);
	}
	
	/**
	 * <p>
	 * Constrói uma nova janela para gerenciamento das versões de classes dinâmicas, permitindo customizar os labels dos componentes.
	 * </p>
	 * <p><i>
	 * Constructs a new window for LiveClasses versioning management, allowing the customization of the component's labels.
	 * </i></p>
	 * 
	 * @param liveClassDB - gerenciador de persistência de classes dinâmicas
	 * <br><i>LiveClasses persistence manager</i>
	 * @param labelsMap - mapa contendo os labels customizados dos componentes
	 * <br><i>the map containing the customized label's values</i>
	 */
	public DefaultLiveClassVersionManager(ILiveClassPersistence liveClassDB, Map<String,String> labelsMap)
	{
		// inicializa o mapa de labels com os valores padrao
		this.labelsMap = new HashMap<String,String>();
		this.labelsMap.put("title", "Gerenciador de Classes Dinâmicas");
		this.labelsMap.put("button.refresh", "Atualizar");
		this.labelsMap.put("button.commit", "Commit");
		this.labelsMap.put("button.rollback", "Rollback");
		this.labelsMap.put("table.classname", "Nome");
		this.labelsMap.put("table.version", "Versão atual");
		this.labelsMap.put("table.safemode", "Versão modo seguro");
		
		// atualiza os labels com os valores customizados
		if ( labelsMap != null )
			this.labelsMap.putAll(labelsMap);
		
		this.setTitle(this.labelsMap.get("title"));
		this.liveClassDB = liveClassDB;
		this.observers = new ArrayList<ILiveClassVersionObserver>();
		
		this.add(this.generateForm());
		this.setSize(new Dimension(640, 400));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * <p>
	 * Cria o painel principal da janela.
	 * </p>
	 * <p><i>
	 * Creates the main panel.
	 * </i></p>
	 * 
	 * @return o painel principal da janela
	 * <br><i>the main panel</i>
	 */
	private JPanel generateForm()
	{
		// Tabela das classes dinamicas e suas versoes
		this.tableModel = new LiveClassTableModel();
		this.liveClassTable = new JTable(this.tableModel);
		this.liveClassTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.liveClassTable.getSelectionModel().addListSelectionListener((evt) -> updateButtons());
		this.liveClassTable.getTableHeader().setFont(this.liveClassTable.getTableHeader().getFont().deriveFont(Font.BOLD));
		this.liveClassTable.setDefaultRenderer(Object.class, new LiveClassTableCellRenderer());
		this.executeReload();
		
		// botoes de atualizar, commit e rollback
		this.btnReload   = new JButton(this.labelsMap.get("button.refresh"));
		this.btnReload.addActionListener((evt) -> executeReload());
		this.btnCommit   = new JButton(this.labelsMap.get("button.commit"));
		this.btnCommit.addActionListener((evt) -> executeCommit());
		this.btnRollback = new JButton(this.labelsMap.get("button.rollback"));
		this.btnRollback.addActionListener((evt) -> executeRollback());
		
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
	public void addObserver(ILiveClassVersionObserver observer)
	{
		this.observers.add(observer);
	}

	@Override
	public void removeObserver(ILiveClassVersionObserver observer)
	{
		this.observers.remove(observer);
	}
	
	/**
	 * <p>
	 * Notifica os observadores sobre a alteração de uma versão de classe dinâmica.
	 * </p>
	 * <p><i>
	 * Notifies the observers about a LiveClass version change.
	 * </i></p>
	 * 
	 * @param liveClassName - nome da classe dinâmica
	 * <br><i>name of the changed LiveClass</i>
	 * @param operation <i>true</i> para commit, <i>false</i> para rollback
	 * <br><i>true for a commit, false for a rollback
	 */
	private void notifyObservers(String liveClassName, boolean operation)
	{
		// commit
		if ( operation )
			this.observers.forEach(obs -> obs.liveClassCommitted(liveClassName));
		
		//rollback
		else
			this.observers.forEach(obs -> obs.liveClassRolledBack(liveClassName));
	}

	/**
	 * <p>
	 * Habilita ou desabilita os botões de commit e rollback 
	 * conforme as versões da classe dinâmica selecionada na tabela.
	 * </p>
	 * <p><i>
	 * Enables or disables the commit and rollback buttons 
	 * according to the selected LiveClass.
	 * </i></p>
	 */
	private void updateButtons()
	{
		// a linha selecionada
		int selectedRow = liveClassTable.getSelectedRow();
	
		if ( selectedRow >= 0 )
		{
			// obtem o bean da linha selecionada
			ILiveClassVersionInfo dbVersion = tableModel.getObjectAt(selectedRow);
			
			// pode executar commit?
			this.btnCommit.setEnabled(dbVersion.getCurrentVersion() > dbVersion.getSafeModeVersion());
			
			// pode executar rollback?
			this.btnRollback.setEnabled(dbVersion.getCurrentVersion() > 1);
		}
		else
		{
			// desabilita
			this.btnCommit.setEnabled(false);
			this.btnRollback.setEnabled(false);
		}
	}
	
	/**
	 * <p>
	 * Atualiza a tabela de versões de classes dinâmicas.
	 * </p>
	 * <p><i>
	 * Refreshs the LiveClasses table. 
	 * </i></p>
	 */
	private void executeReload()
	{
		try
		{
			this.tableModel.setData(this.liveClassDB.getAllLiveClassesVersionInfo());
		}
		catch (Exception e)
		{
			// log: erro
			Utils.logError("Erro ao carregar tabela de versoes de classes dinamicas");
			Utils.logException(e);
		}
	}
	
	/**
	 * <p>
	 * Executa o commit da versão da classe dinâmica selecionada.
	 * </p>
	 * <p><i>
	 * Commits the version of the selected LiveClass.
	 * </i></p>
	 */
	private void executeCommit()
	{
		new Thread(()->{
			try
			{
				// a linha selecionada
				int selectedRow = liveClassTable.getSelectedRow();
			
				if ( selectedRow >= 0 )
				{
					// obtem o bean da linha selecionada
					ILiveClassVersionInfo dbVersion = tableModel.getObjectAt(selectedRow);
	
					// faz o commit no banco de dados
					liveClassDB.commitLiveClass(dbVersion.getClassName());
				
					// notifica os observadores
					notifyObservers(dbVersion.getClassName(), true);
					
					// atualiza a tabela
					executeReload();
				}
			}
			catch (Exception e)
			{
				// log: erro
				Utils.logError("Erro ao executar o commit de classe dinamica selecionada");
				Utils.logException(e);
			}
		}).start();
	}
	
	/**
	 * <p>
	 * Executa o rollback da versão da classe dinâmica selecionada.
	 * </p>
	 * <p><i>
	 * Rolls back the version of the selected LiveClass.
	 * </i></p>
	 */
	private void executeRollback()
	{
		new Thread(()->{
			try
			{
				// a linha selecionada
				int selectedRow = liveClassTable.getSelectedRow();
			
				if ( selectedRow >= 0 )
				{
					// obtem o bean da linha selecionada
					ILiveClassVersionInfo dbVersion = tableModel.getObjectAt(selectedRow);
	
					// faz o rollback no banco de dados
					liveClassDB.rollbackLiveClass(dbVersion.getClassName());
				
					// notifica os observadores
					notifyObservers(dbVersion.getClassName(), false);
					
					// atualiza a tabela
					executeReload();
				}
			}
			catch (Exception e)
			{
				// log: erro
				Utils.logError("Erro ao executar o rollback de classe dinamica selecionada");
				Utils.logException(e);
			}
		}).start();
	}
	
	
	/**
	 * <p>
	 * Modelo para a tabela de versões de classes dinâmicas.
	 * </p>
	 * <p><i>
	 * TableModel of the LiveClasses table. 
	 * </i></p>
	 */
	private class LiveClassTableModel extends AbstractTableModel
	{
		// as classes dinamicas lidas do banco de dados
		private List<ILiveClassVersionInfo> data;

		/**
		 * <p>
		 * Construtor padrão.
		 * </p>
		 * <p><i>
		 * Default constructor.
		 * </i></p>
		 */
		LiveClassTableModel()
		{
			this.data = new ArrayList<ILiveClassVersionInfo>();
		}
		
		@Override
		public int getColumnCount()
		{
			// [nome da classe, versao atual, versao de segurança]
			return ( 3 );
		}

		@Override
		public int getRowCount()
		{
			return ( data.size() );
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			// obtem o objeto
			ILiveClassVersionInfo dbVersion = this.data.get(rowIndex);
			
			switch ( columnIndex )
			{
				case 0: return ( dbVersion.getClassName());
				case 1: return ( dbVersion.getCurrentVersion());
				case 2: return ( dbVersion.getSafeModeVersion());
				default: return "";
			}
		}
		
		@Override
		public String getColumnName(int column)
		{
			switch ( column )
			{
				case 0: return labelsMap.get("table.classname");
				case 1: return labelsMap.get("table.version");
				case 2: return labelsMap.get("table.safemode");
				default: return "";
			}
		}

		/**
		 * <p>
		 * Atribui as informações das versões das classes dinâmicas.
		 * </p>
		 * <p><i>
		 * Sets the data of the LiveClasses versions.
		 * </i></p>
		 * 
		 * @param newData - informações das versões das classes dinâmicas
		 * <br><i>the data of the LiveClasses versions</i>
		 */
		public void setData(List<ILiveClassVersionInfo> newData)
		{
			this.data = newData;
			this.fireTableDataChanged();
		}
		
		/**
		 * <p>
		 * Obtém as informações da classe dinâmica da linha informada.
		 * </p>
		 * <p><i>
		 * Gets the LiveClass information of the specified index.
		 * </i></p>
		 * 
		 * @param index - linha da tabela de versões
		 * <br><i>the selected row of the LiveClasses table</i>
		 * @return
		 */
		public ILiveClassVersionInfo getObjectAt(int index)
		{
			return ( this.data.get(index) );
		}
	}
	
	/**
	 * <p>
	 * Renderizador para a tabela de versões de classes dinâmicas.
	 * </p>
	 * <p><i>
	 * TableCellRenderer of the LiveClasses table. 
	 * </i></p>
	 */
	private class LiveClassTableCellRenderer extends DefaultTableCellRenderer
	{
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setHorizontalAlignment(CENTER);

			if ( isSelected )
				this.setBorder(BorderFactory.createEmptyBorder());
			
			return ( comp );
		}
	}
}
