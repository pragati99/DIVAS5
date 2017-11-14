package edu.utdallas.mavs.divas.gui.mvp.view.swing.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicRadioButtonUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.View;

import edu.utdallas.mavs.divas.gui.mvp.model.SimEntity;
import edu.utdallas.mavs.divas.gui.mvp.view.swing.StyleHelper;

/**
 * Source: adapted from http://java-swing-tips.blogspot.com/2008/03/jtable-pagination-example-using.html
 */
public class SimSummaryPanel extends JPanel
{
    private static final long                serialVersionUID    = 1L;

    private static final String              DEFAULT_SEARCH_TEXT = "Id, Type...";
    private static final int                 MAX_ITEMS_PER_PAGE  = 21;

    private final String[]                   columnNames         = { "Id", "Name", "Type", "Cell" };
    private final JTextField                 searchTextField     = new JTextField(DEFAULT_SEARCH_TEXT);
    private final JButton                    searchButton        = new JButton("Search");
    private final JCheckBox                  autoRefreshCheckBox = new JCheckBox("Auto Refresh ");
    private final LinkViewRadioButtonUI      ui                  = new LinkViewRadioButtonUI();
    private static int                       LR_PAGE_SIZE        = 1;
    private final Box                        box                 = Box.createHorizontalBox();
    private final DefaultTableModel          model               = new SimSummaryPanelData(columnNames);
    private final TableRowSorter<TableModel> sorter              = new TableRowSorter<TableModel>(model);
    private final SimSummaryTable            table               = new SimSummaryTable(model);
    private int                              currentPage         = 1;
    private final Map<Integer, JDialog>      currentOpenDialogs  = Collections.synchronizedMap(new HashMap<Integer, JDialog>());

    public SimSummaryPanel()
    {
        super(new BorderLayout());
        initialize();
    }

    private void initialize()
    {
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension());
        table.setShowGrid(false);
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        table.setRowSorter(sorter);

        setupSearchTextArea();
        initSearchBar(MAX_ITEMS_PER_PAGE, 1);
        add(box, BorderLayout.NORTH);

        JScrollPane grid = new JScrollPane(table);
        grid.setPreferredSize(new Dimension(580, 300));
        add(grid, BorderLayout.WEST);
    }

    private void setupSearchTextArea()
    {
        searchTextField.registerKeyboardAction(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                searchButton.doClick();
                searchTextField.requestFocusInWindow();
                searchTextField.selectAll();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), JComponent.WHEN_FOCUSED);

        searchTextField.addKeyListener(new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {}

            @Override
            public void keyReleased(KeyEvent e)
            {}

            @Override
            public void keyPressed(KeyEvent e)
            {

                if(searchTextField.getText().equals(DEFAULT_SEARCH_TEXT))
                {
                    searchTextField.setText("");
                }
            }
        });
    }

    public void clearData()
    {
        table.clearSelection();

        clearModel();

        model.fireTableDataChanged();
        initSearchBar(MAX_ITEMS_PER_PAGE, 1);

        searchTextField.setText(DEFAULT_SEARCH_TEXT);
    }

    public void updateData(Map<Integer, SimEntity> simEntities)
    {
        loadEntities(simEntities);
        initSearchBar(MAX_ITEMS_PER_PAGE, 1);
    }

    public void refreshData(final Map<Integer, SimEntity> simEntities)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                loadEntities(simEntities);
                initSearchBar(MAX_ITEMS_PER_PAGE, currentPage);
                searchTextField.requestFocusInWindow();
            }
        });
    }

    private void loadEntities(Map<Integer, SimEntity> simEntities)
    {
        table.setSimEntities(simEntities);

        clearModel();

        for(SimEntity s : simEntities.values())
        {
            model.addRow(new Object[] { s.getId(), s.getName(), s.getType().toString(), s.getCellID() });
            updateCurrentOpenDialogs(s);
        }

        model.fireTableDataChanged();
    }

    protected void updateCurrentOpenDialogs(SimEntity s)
    {
        JDialog dialog = currentOpenDialogs.get(s.getId());
        if(dialog != null && dialog.isShowing())
        {
            StatePanel contentPane = new StatePanel(s.getState());
            dialog.setContentPane(contentPane);
            dialog.revalidate();
            dialog.repaint();
        }
    }

    private void clearModel()
    {
        for(int i = model.getRowCount() - 1; i >= 0; i--)
        {
            model.removeRow(i);
        }
    }

    private void initSearchBar(final int itemsPerPage, final int currentPageIndex)
    {
        this.currentPage = currentPageIndex;

        sorter.setRowFilter(makeRowFilter(itemsPerPage, currentPageIndex - 1));

        ArrayList<JRadioButton> paginationButtons = new ArrayList<JRadioButton>();

        /*
         * "maxPageIndex" gives one blank page if the module of the division is not zero.
         * e.g. rowCount=100, maxPageIndex=100
         */
        int rowCount = model.getRowCount();
        int v = rowCount % itemsPerPage == 0 ? 0 : 1;
        int maxPageIndex = rowCount / itemsPerPage + v;

        int endPageIndex = currentPageIndex + 2 * LR_PAGE_SIZE;
        if(endPageIndex > maxPageIndex)
            endPageIndex = maxPageIndex;

        int startPageIndex = (maxPageIndex - currentPageIndex > 2 * LR_PAGE_SIZE) ? currentPageIndex : maxPageIndex - 2 * LR_PAGE_SIZE;
        if(startPageIndex <= 0)
            startPageIndex = 1;

        if(currentPageIndex > 1 && maxPageIndex > 2 * LR_PAGE_SIZE + 1)
        {
            paginationButtons.add(makePNRadioButton(itemsPerPage, currentPageIndex - 1, "Prev"));
        }

        // if I only have one page, I don't want to see pagination buttons
        if(startPageIndex <= endPageIndex && maxPageIndex >= 2 * LR_PAGE_SIZE)
        {
            for(int i = startPageIndex; i <= endPageIndex; i++)
            {
                paginationButtons.add(makeRadioButton(itemsPerPage, currentPageIndex, i - 1));
            }
        }
        if(currentPageIndex + 2 * LR_PAGE_SIZE < maxPageIndex)
        {
            paginationButtons.add(makePNRadioButton(itemsPerPage, currentPageIndex + 1, "Next"));
        }

        box.removeAll();
        box.add(searchTextField);
        box.add(searchButton);
        box.add(autoRefreshCheckBox);
        ButtonGroup bg = new ButtonGroup();
        box.add(Box.createHorizontalGlue());
        for(JRadioButton r : paginationButtons)
        {
            box.add(r);
            bg.add(r);
        }
        // box.add(Box.createHorizontalGlue());
        box.revalidate();
        box.repaint();
        paginationButtons.clear();
    }

    private JRadioButton makeRadioButton(final int itemsPerPage, final int current, final int target)
    {
        JRadioButton radio = new JRadioButton(" " + String.valueOf(target + 1) + " ")
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void fireStateChanged()
            {
                ButtonModel model = getModel();
                if(!model.isEnabled())
                {
                    setForeground(Color.GRAY);
                }
                else if(model.isPressed() && model.isArmed())
                {
                    setForeground(Color.GREEN);
                }
                else if(model.isSelected())
                {
                    setForeground(Color.RED);
                    // }else if(isRolloverEnabled() && model.isRollover()) {
                    // setForeground(Color.BLUE);
                }
                super.fireStateChanged();
            }
        };
        radio.setForeground(Color.BLUE);
        radio.setUI(ui);
        if(target + 1 == current)
        {
            radio.setSelected(true);
        }
        radio.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                initSearchBar(itemsPerPage, target + 1);
            }
        });
        return radio;
    }

    private JRadioButton makePNRadioButton(final int itemsPerPage, final int target, String title)
    {
        JRadioButton radio = new JRadioButton(title);
        radio.setForeground(Color.BLUE);
        radio.setUI(ui);
        radio.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                initSearchBar(itemsPerPage, target);
            }
        });
        return radio;
    }

    private RowFilter<TableModel, Integer> makeRowFilter(final int itemsPerPage, final int target)
    {
        return new RowFilter<TableModel, Integer>()
        {
            @Override
            public boolean include(Entry<? extends TableModel, ? extends Integer> entry)
            {
                int ei = entry.getIdentifier();
                return target * itemsPerPage <= ei && ei < target * itemsPerPage + itemsPerPage;
            }
        };
    }

    public JButton getSearchButton()
    {
        return searchButton;
    }

    public String getSearchQuery()
    {
        return (searchTextField.getText().equals(DEFAULT_SEARCH_TEXT)) ? "" : searchTextField.getText();
    }

    public JCheckBox getSearchAutoRefreshCheckBox()
    {
        return autoRefreshCheckBox;
    }

    class SimSummaryTable extends JTable
    {

        private static final long       serialVersionUID = 1L;

        private Map<Integer, SimEntity> simEntities      = new HashMap<Integer, SimEntity>();

        public SimSummaryTable(TableModel dm)
        {
            super(dm);
        }

        public void setSimEntities(Map<Integer, SimEntity> simEntities)
        {
            this.simEntities = simEntities;
        }

        @Override
        public boolean isCellEditable(int row, int column)
        {
            return false;
        }

        @Override
        public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend)
        {
            super.changeSelection(rowIndex, columnIndex, toggle, extend);
            final SimEntity simEntity = simEntities.get(Integer.valueOf(table.getValueAt(rowIndex, 0).toString()));
            Object state = simEntity.getState();
            String title = String.format("Details for %s %s", simEntity.getType(), simEntity.getId());
            JDialog dialog = showDetailsDialog(state, title);
            currentOpenDialogs.put(simEntity.getId(), dialog);
            dialog.addWindowListener(new WindowAdapter()
            {
                @Override
                public void windowClosing(WindowEvent e)
                {
                    currentOpenDialogs.remove(simEntity.getId());
                    super.windowClosing(e);
                }
            });
        }

        @Override
        public Component prepareRenderer(TableCellRenderer tcr, int row, int column)
        {
            Component c = super.prepareRenderer(tcr, row, column);
            if(isRowSelected(row))
            {
                c.setForeground(getSelectionForeground());
                c.setBackground(getSelectionBackground());
            }
            else
            {
                c.setForeground(getForeground());
                c.setBackground(getBackground());
            }
            return c;
        }

        private JDialog showDetailsDialog(Object state, String title)
        {
            StyleHelper.setNimbusLookAndFeel();
            StatePanel contentPane = new StatePanel(state);
            JFrame owner = new JFrame();
            JDialog dialog = new JDialog(owner, title, false);
            dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            dialog.setContentPane(contentPane);
            dialog.setVisible(true);
            dialog.setSize(350, contentPane.getEntriesCount() * (MAX_ITEMS_PER_PAGE - 2));
            dialog.setResizable(true);
            dialog.setLocationRelativeTo(this);
            return dialog;
        }
    }

    class SimSummaryPanelData extends DefaultTableModel
    {
        private static final long serialVersionUID = 1L;

        public SimSummaryPanelData(Object[] columnNames)
        {
            super(null, columnNames);
        }

        @Override
        public Class<?> getColumnClass(int column)
        {
            return (column == 0) ? Integer.class : Object.class;
        }
    }

    class LinkViewRadioButtonUI extends BasicRadioButtonUI
    {
        public LinkViewRadioButtonUI()
        {
            super();
        }

        @Override
        public Icon getDefaultIcon()
        {
            return null;
        }

        private Dimension size     = new Dimension();
        private Rectangle viewRect = new Rectangle();
        private Rectangle iconRect = new Rectangle();
        private Rectangle textRect = new Rectangle();

        @Override
        public synchronized void paint(Graphics g, JComponent c)
        {
            AbstractButton b = (AbstractButton) c;
            ButtonModel model = b.getModel();
            Font f = c.getFont();
            g.setFont(f);
            FontMetrics fm = c.getFontMetrics(f);

            Insets i = c.getInsets();
            size = b.getSize(size);
            viewRect.x = i.left;
            viewRect.y = i.top;
            viewRect.width = size.width - i.right - viewRect.x;
            viewRect.height = size.height - i.bottom - viewRect.y;
            iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;
            textRect.x = textRect.y = textRect.width = textRect.height = 0;

            String text = SwingUtilities.layoutCompoundLabel(c, fm, b.getText(), null, b.getVerticalAlignment(), b.getHorizontalAlignment(), b.getVerticalTextPosition(), b.getHorizontalTextPosition(), viewRect, iconRect, textRect, 0);

            if(c.isOpaque())
            {
                g.setColor(b.getBackground());
                g.fillRect(0, 0, size.width, size.height);
            }
            if(text == null)
                return;

            g.setColor(b.getForeground());
            if(!model.isSelected() && !model.isPressed() && !model.isArmed() && b.isRolloverEnabled() && model.isRollover())
            {
                g.drawLine(viewRect.x, viewRect.y + viewRect.height, viewRect.x + viewRect.width, viewRect.y + viewRect.height);
            }
            View v = (View) c.getClientProperty(BasicHTML.propertyKey);
            if(v != null)
            {
                v.paint(g, textRect);
            }
            else
            {
                paintText(g, b, textRect, text);
            }
        }
    }

}
