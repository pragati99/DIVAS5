package edu.utdallas.mavs.divas.gui.mvp.view.swing.panels;

import java.awt.BorderLayout;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.config.annotation.HideInGUI;

public class StatePanel extends JPanel
{
    private static final long serialVersionUID = 1L;
    private StateTableModel   stateTableModel;
    private JTable            table;

    public StatePanel(Object state)
    {
        super();
        this.setLayout(new BorderLayout());
        stateTableModel = new StateTableModel(state);
        table = new JTable(stateTableModel);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(60);
        table.setEnabled(false);
        this.add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public int getEntriesCount()
    {
        return stateTableModel.getAccessorsCount();
    }

    public void destroy()
    {
        if(stateTableModel != null)
            stateTableModel.destroy();
    }

    protected static Method[] loadAccessors(Object state)
    {
        ArrayList<Method> methods = new ArrayList<Method>();
        // add any public method that starts with "get" or "is", has no arguments, and is not annotated with
        // "HideInGUI", and is not "getClass()"
        for(Method m : state.getClass().getMethods())
            if(m.getAnnotation(HideInGUI.class) == null && (m.getName().startsWith("get") || m.getName().startsWith("is")) && m.getParameterTypes().length < 1 && !m.getName().equals("getClass"))
                methods.add(m);

        Method mArray[] = new Method[methods.size()];

        return methods.toArray(mArray);
    }

    public class StateTableModel extends AbstractTableModel
    {
        private static final long serialVersionUID = 1L;
        private Object            state;
        private Object[][]        tableData;
        protected Method[]        accessors;

        public StateTableModel(Object state)
        {
            this.state = state;
            accessors = loadAccessors(state);
            // allocate the table data based on the number of fields in the class
            tableData = new Object[accessors.length][2];
            updateTableData();
        }

        public int getAccessorsCount()
        {
            return accessors.length;
        }

        public void updateTableData()
        {
            int row = 0;

            // for each accessor in the class
            for(Method m : accessors)
            {
                try
                {
                    int nameStartIndex = 0;

                    if(m.getName().startsWith("get"))
                        nameStartIndex = 3;
                    else if(m.getName().startsWith("is"))
                        nameStartIndex = 2;

                    // set the attribute name
                    tableData[row][0] = new String(m.getName().subSequence(nameStartIndex, m.getName().length()) + ":");

                    // set the attribute's value
                    String strValue = "";

                    Object value = m.invoke(state);

                    if(m.getReturnType().equals(Vector3f.class))
                    {
                        Vector3f v = (Vector3f) value;
                        strValue = String.format("(%8.2f, %8.2f, %8.2f)", v.getX(), v.getY(), v.getZ());
                    }
                    else if(m.getReturnType().equals(Quaternion.class))
                    {
                        Quaternion q = (Quaternion) value;
                        strValue = String.format("(%8.2f, %8.2f, %8.2f, %8.2f)", q.getX(), q.getY(), q.getZ(), q.getW());
                    }
                    else
                    {
                        strValue = (value != null) ? value.toString() : "";
                    }

                    tableData[row][1] = strValue;

                    // move to next row
                    row++;
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            fireTableDataChanged();
        }

        @Override
        public int getColumnCount()
        {
            return tableData[0].length;
        }

        @Override
        public int getRowCount()
        {
            return tableData.length;
        }

        @Override
        public Object getValueAt(int row, int col)
        {
            return tableData[row][col];
        }

        @Override
        public String getColumnName(int col)
        {
            if(col == 0)
                return "Attribute";

            return "Value";
        }

        public void destroy()
        {}
    }
}
