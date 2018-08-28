package org.esfinge.liveprog.db;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Gerenciador padrao de versao da classes dinamicas para a implementacao padrao do LiveClassBD (SQLite). 
 */
@SuppressWarnings("serial")
public class DefaultDBVersionManager extends JFrame implements ILiveClassDBVersionManager
{
	// tabela com as versoes das classes dinamicas cadastradas no BD
	private JTable liveClassTable;
	
	// modelo com as informacoes das versoes das classes dinamicas do BD
	private LiveClassTableModel tableModel;
	
	// lista de observadores a serem notificados das 
	// versoes das classes dinamicas comitadas/retrocedidas no BD
	private List<ILiveClassDBVersionObserver> observers;

	// botao para atualizar a lista de versoes
	private JButton btnReload;
	
	// botao para executar o commit da versao selecionada
	private JButton btnCommit;
	
	// botao para executar o rollback da versao selecionada
	private JButton btnRollback;
	
	// interface com o BD
	private ILiveClassDB liveClassDB;
	
	
	/**
	 * Construtor padrao.
	 */
	public DefaultDBVersionManager(ILiveClassDB liveClassDB)
	{
		super("Gerenciador de Classes Dinamicas");
		this.liveClassDB = liveClassDB;
		this.observers = new ArrayList<ILiveClassDBVersionObserver>();
		
		this.add(this.generateForm());
		this.setSize(new Dimension(640, 400));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * Gera o painel principal da janela.
	 * 
	 * @return o painel principal da janela
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
		this.btnReload   = new JButton("Atualizar");
		this.btnReload.addActionListener((evt) -> executeReload());
		this.btnCommit   = new JButton("Commit");
		this.btnCommit.addActionListener((evt) -> executeCommit());
		this.btnRollback = new JButton("Rollback");
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
			ILiveClassDBVersion dbVersion = tableModel.getObjectAt(selectedRow);
			
			// pode executar commit?
			this.btnCommit.setEnabled(dbVersion.getCurrentVersion() < dbVersion.getTestVersion());
			
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
	 * Atualiza a tabela de classes dinamicas e suas versoes.
	 */
	private void executeReload()
	{
		try
		{
			this.tableModel.setData(this.liveClassDB.getAllLiveClassVersion());
			this.liveClassTable.repaint();
		}
		catch (SQLException e)
		{
			// TODO debug..
			e.printStackTrace();
		}
	}
	
	/**
	 * Executa o commit da versao da classe dinamica selecionada.
	 * 
	 * @param classBean a classe selecionada na tabela
	 */
	private void executeCommit()
	{
		try
		{
			// a linha selecionada
			int selectedRow = liveClassTable.getSelectedRow();
		
			if ( selectedRow >= 0 )
			{
				// obtem o bean da linha selecionada
				ILiveClassDBVersion dbVersion = tableModel.getObjectAt(selectedRow);

				// faz o commit no banco de dados
				this.liveClassDB.commitLiveClass(dbVersion.getName());
			
				// atualiza a tabela
				this.executeReload();
			
				// notifica os observadores
				this.notifyObservers(dbVersion.getName(), true);
			}
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
	private void executeRollback()
	{
		try
		{
			// a linha selecionada
			int selectedRow = liveClassTable.getSelectedRow();
		
			if ( selectedRow >= 0 )
			{
				// obtem o bean da linha selecionada
				ILiveClassDBVersion dbVersion = tableModel.getObjectAt(selectedRow);

				// faz o rollback no banco de dados
				this.liveClassDB.rollbackLiveClass(dbVersion.getName());
			
				// atualiza a tabela
				this.executeReload();
			
				// notifica os observadores
				this.notifyObservers(dbVersion.getName(), false);
			}
		}
		catch (IllegalStateException | SQLException e)
		{
			// TODO: debug..
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Modelo para a tabela de classes dinamicas e suas versoes.
	 */
	private class LiveClassTableModel extends AbstractTableModel
	{
		private List<ILiveClassDBVersion> data;

		
		LiveClassTableModel()
		{
			this.data = new ArrayList<ILiveClassDBVersion>();
		}
		
		@Override
		public int getColumnCount()
		{
			// [nome da classe, versao de producao, versao de testes]
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
			ILiveClassDBVersion dbVersion = this.data.get(rowIndex);
			
			switch ( columnIndex )
			{
				case 0: return ( dbVersion.getName());
				case 1: return ( dbVersion.getCurrentVersion());
				case 2: return ( dbVersion.getTestVersion());
				default: return "";
			}
		}
		
		@Override
		public String getColumnName(int column)
		{
			switch ( column )
			{
				case 0: return "Nome";
				case 1: return "Versão de Produção";
				case 2: return "Versão de Testes";
				default: return "";
			}
		}

		public void setData(List<ILiveClassDBVersion> newData)
		{
			this.data = newData;
			this.fireTableDataChanged();
		}
		
		public ILiveClassDBVersion getObjectAt(int index)
		{
			return ( this.data.get(index) );
		}
	}
	
	/**
	 * Renderizador da tabela de classes dinamicas e suas versoes.
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
