/*package edu.utdallas.mavs.divas.gui.mvp.view.swing.frames.agentspec;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.xmlbeans.XmlOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import divas.spec.agent.AgentAttribute;
import divas.spec.agent.DivasAttribute;
import divas.spec.agent.DivasAttribute.Constraint;
import divas.spec.agent.DivasTask;
import divas.spec.agent.MetaAgentDocument;
import divas.spec.agent.MetaAgentDocument.MetaAgent;
import edu.utdallas.mavs.divas.gui.mvp.view.swing.StyleHelper;
import edu.utdallas.mavs.divas.utils.ResourceManager;

public class MetaAgentSpecFrame extends JFrame
{
    private static final long                              serialVersionUID = 1L;
    private final static Logger                            logger           = LoggerFactory.getLogger(MetaAgentSpecFrame.class);
    private static MetaAgentSpecFrame                      self;
    private static HashMap<String, MetaAgentDocument>      metaSpecs        = new HashMap<String, MetaAgentDocument>();
    private static HashMap<String, DefaultMutableTreeNode> metaNodes        = new HashMap<String, DefaultMutableTreeNode>();

    private static HashMap<Object, Object>                 testmap          = new HashMap<Object, Object>();

    Vector<DivasAttribute>                                 names;
    Vector<DivasTask>                                      taskNames;

    private JPanel                                         DivasAttributesPanel;
    private JPanel                                         tasksPanel;
    private JPanel                                         leftPanel;
    private JPanel                                         rightPanel;
    private JPanel                                         topDisplayPanel;
    private JPanel                                         rightRightPanel;
    private JPanel                                         centerDisplayPanel;
    private JScrollPane                                    DivasAttributeDisplayScrollPane;
    private JPanel                                         DivasAttributeDisplayPanel;
    private JPanel                                         DivasTaskDisplayPanel;
    private JPanel                                         topListSelectPanel;
    private JPanel                                         DivasAttributeDisplayPaneltemp;
    private JPanel                                         selectNamePanel;
    private JPanel                                         selectCheckBoxPanel;
    private JPanel                                         DivasAttributeListPanel;
    private JPanel                                         DivasTaskListPanel;

    @SuppressWarnings("rawtypes")
    private JList                                          DivasAttributeAddList;
    @SuppressWarnings("rawtypes")
    private JList                                          DivasTaskAddList;

    private JTextField                                     maName;
    private JTextField                                     maParentName;

    private MetaAgentDocument                              maDoc;
    private MetaAgent                                      ma;
    private MetaAgentDocument                              selectedMetaAgentDoc;
    private JTree                                          agentList;
    int                                                    attrHeight       = 24;

    JButton                                                saveButton       = new JButton();

    private static File                                    metaSpecFolder;
    private static File                                    attrFolder;
    private static File                                    tasksFolder;

    public enum DivasAttributeType
    {
        Number, DecimalNumber, Vector, Words, Color, Percentage, TrueFalse, EnvObject;
    }

    @SuppressWarnings("rawtypes")
    public MetaAgentSpecFrame()
    {
        super("Agent Mold Specification");

        names = new Vector<DivasAttribute>();
        taskNames = new Vector<DivasTask>();

        logger.debug(System.getProperty("user.home") + System.getProperty("file.separator") + "Divas" + System.getProperty("file.separator") + "metaagentspecs" + System.getProperty("file.separator"));
        metaSpecFolder = new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Divas" + System.getProperty("file.separator") + "metaagentspecs"
                + System.getProperty("file.separator"));
        attrFolder = new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Divas" + System.getProperty("file.separator") + "attributes"
                + System.getProperty("file.separator"));
        tasksFolder = new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Divas" + System.getProperty("file.separator") + "tasks" + System.getProperty("file.separator"));

        if(!attrFolder.isDirectory())
            try
            {
                throw new Exception("Attribute folder doesn't exist");
            }
            catch(Exception e1)
            {
                e1.printStackTrace();
            }

        if(!metaSpecFolder.isDirectory())
            try
            {
                throw new Exception("Meta Agent folder doesn't exist");
            }
            catch(Exception e1)
            {
                e1.printStackTrace();
            }

        this.setLayout(new BorderLayout());
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        leftPanel = new JPanel();
        rightPanel = new JPanel();
        this.add(leftPanel, BorderLayout.WEST);
        this.add(rightPanel, BorderLayout.EAST);

        leftPanel.setLayout(new BorderLayout());
        rightPanel.setLayout(new BorderLayout());

        DivasAttributesPanel = new JPanel();
        tasksPanel = new JPanel();

        DivasAttributesPanel.setLayout(new BorderLayout());
        tasksPanel.setLayout(new BorderLayout());

        rightRightPanel = new JPanel();
        centerDisplayPanel = new JPanel();
        rightRightPanel.setLayout(new BorderLayout());
        centerDisplayPanel.setLayout(new BorderLayout());

        JPanel namePanel = new JPanel(new GridLayout(0, 1));
        JPanel nameEnterPanel = new JPanel(new GridLayout(0, 1));

        rightRightPanel.add(DivasAttributesPanel, BorderLayout.NORTH);
        rightRightPanel.add(tasksPanel, BorderLayout.SOUTH);

        JLabel nameLabel = new JLabel("Name:  ");
        JLabel parentLabel = new JLabel("Parent:  ");
        parentLabel.setPreferredSize(new Dimension(60, 20));
        nameLabel.setPreferredSize(new Dimension(60, 20));
        namePanel.add(nameLabel);
        namePanel.add(parentLabel);
        maName = new JTextField("None Selected");
        maParentName = new JTextField("None Selected");
        maName.setPreferredSize(new Dimension(350, 27));
        maParentName.setPreferredSize(new Dimension(350, 27));
        nameEnterPanel.add(maName);
        nameEnterPanel.add(maParentName);

        JPanel rightRightButtonPanel = new JPanel(new GridLayout(1, 0));
        JPanel rightRightButtonPanel2 = new JPanel(new GridLayout(1, 0));
        JPanel rightButtonPanel = new JPanel(new GridLayout(1, 0));
        JPanel leftButtonPanel = new JPanel(new GridLayout(1, 0));

        JButton addDivasAttributeButton = new JButton("Add Attribute");
        addDivasAttributeButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                addDivasAttribute();
            }

        });

        JButton createDivasAttributeButton = new JButton("New Attribute");
        createDivasAttributeButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                createDivasAttribute();
            }
        });

        JButton editDivasAttributeButton = new JButton("Edit Attribute");
        editDivasAttributeButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                editDivasAttribute();
            }
        });

        JButton addTaskButton = new JButton("Add Task");
        addTaskButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                addTask();
            }

        });

        JButton removeTaskButton = new JButton("RAT");
        removeTaskButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                removeTask();
            }

        });

        JButton createTaskButton = new JButton("New Task");
        createTaskButton.setEnabled(false);
        createTaskButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                createTask();
            }
        });

        JButton editTaskButton = new JButton("Edit Task");
        editTaskButton.setEnabled(false);
        editTaskButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                editTask();
            }
        });

        rightRightButtonPanel2.add(removeTaskButton);
        rightRightButtonPanel2.add(addTaskButton);
        rightRightButtonPanel2.add(editTaskButton);
        rightRightButtonPanel2.add(createTaskButton);

        rightRightButtonPanel.add(addDivasAttributeButton);
        rightRightButtonPanel.add(editDivasAttributeButton);
        rightRightButtonPanel.add(createDivasAttributeButton);

        saveButton = new JButton("Save Agent Mold");
        saveButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                save();
            }
        });
        rightButtonPanel.add(saveButton);

        centerDisplayPanel.add(rightButtonPanel, BorderLayout.SOUTH);
        DivasAttributesPanel.add(rightRightButtonPanel, BorderLayout.SOUTH);
        tasksPanel.add(rightRightButtonPanel2, BorderLayout.SOUTH);

        JButton newAgentButton = new JButton("New Agent Mold");
        newAgentButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                newMetaAgent();
            }
        });
        leftButtonPanel.add(newAgentButton);

        JButton deleteAgentButton = new JButton("Delete Agent Mold");
        deleteAgentButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                deleteMetaAgent();
            }
        });
        leftButtonPanel.add(deleteAgentButton);

        leftPanel.add(leftButtonPanel, BorderLayout.NORTH);

        maDoc = MetaAgentDocument.Factory.newInstance();
        ma = maDoc.addNewMetaAgent();

        topDisplayPanel = new JPanel();
        DivasAttributeDisplayScrollPane = new JScrollPane();
        DivasAttributeDisplayPanel = new JPanel(new GridLayout(0, 1));
        DivasTaskDisplayPanel = new JPanel(new GridLayout(0, 1));
        // DivasAttributeDisplayPanel.setPreferredSize(new
        // Dimension(970,10000));
        DivasAttributeDisplayPaneltemp = new JPanel();
        DivasAttributeDisplayPaneltemp.add(DivasAttributeDisplayPanel);
        DivasAttributeDisplayScrollPane.setViewportView(DivasAttributeDisplayPaneltemp);

        DivasAttributeDisplayScrollPane.setBorder(BorderFactory.createLineBorder(Color.black));

        rightPanel.add(rightRightPanel, BorderLayout.EAST);
        rightPanel.add(centerDisplayPanel, BorderLayout.CENTER);

        centerDisplayPanel.add(topDisplayPanel, BorderLayout.NORTH);
        centerDisplayPanel.add(DivasAttributeDisplayScrollPane, BorderLayout.CENTER);

        topDisplayPanel.add(namePanel, BorderLayout.WEST);
        topDisplayPanel.add(nameEnterPanel, BorderLayout.EAST);

        JPanel DivasAttributeListPane = makeTopList();

        // DivasAttributeListPane.setPreferredSize(new Dimension(770,20));
        // DivasAttributeListPane.setBorder(BorderFactory.createLineBorder(Color.black));

        DivasAttributeDisplayPanel.add(DivasAttributeListPane);

        topListSelectPanel = new JPanel();
        selectNamePanel = new JPanel(new GridLayout(0, 1));
        selectCheckBoxPanel = new JPanel(new GridLayout(0, 1));

        topListSelectPanel.add(selectNamePanel, BorderLayout.WEST);
        topListSelectPanel.add(selectCheckBoxPanel, BorderLayout.EAST);

        DivasAttributesPanel.add(topListSelectPanel, BorderLayout.NORTH);

        DivasAttributeListPanel = new JPanel();
        DivasTaskListPanel = new JPanel();

        DivasAttributesPanel.add(DivasAttributeListPanel, BorderLayout.CENTER);
        tasksPanel.add(DivasTaskListPanel, BorderLayout.CENTER);

        DivasAttributeAddList = new JList();
        DivasTaskAddList = new JList();

        // ListModel bigData = new AbstractListModel() {
        // public int getSize() { return Short.MAX_VALUE; }
        // public Object getElementAt(int index) { return "Index " + index; }
        // };
        //
        // DivasAttributeAddList.setModel(bigData);

        JScrollPane DivasAttributeAddListscrollPane = new JScrollPane();
        DivasAttributeAddListscrollPane.getViewport().setView(DivasAttributeAddList);
        DivasAttributeListPanel.add(DivasAttributeAddListscrollPane);
        DivasAttributeAddListscrollPane.setPreferredSize(new Dimension(270, 400));

        JScrollPane DivasTaskAddListscrollPane = new JScrollPane();
        DivasTaskAddListscrollPane.getViewport().setView(DivasTaskAddList);
        DivasTaskListPanel.add(DivasTaskAddListscrollPane);
        DivasTaskAddListscrollPane.setPreferredSize(new Dimension(270, 300));

        JLabel physicalLabel = new JLabel("Physical: ");
        JLabel mentalLabel = new JLabel("Mental: ");

        mentalLabel.setPreferredSize(new Dimension(120, 20));
        physicalLabel.setPreferredSize(new Dimension(120, 20));

        JCheckBox mentalCB = new JCheckBox();
        JCheckBox physicalCB = new JCheckBox();

        mentalCB.setPreferredSize(new Dimension(120, 20));
        physicalCB.setPreferredSize(new Dimension(120, 20));

        DivasTaskDisplayPanel.setLayout(new FlowLayout());

        selectNamePanel.add(physicalLabel);
        selectCheckBoxPanel.add(physicalCB);
        selectNamePanel.add(mentalLabel);
        selectCheckBoxPanel.add(mentalCB);

        leftPanel.setPreferredSize(new Dimension(304, 880));
        rightPanel.setPreferredSize(new Dimension(1288, 880));

        centerDisplayPanel.setPreferredSize(new Dimension(878, 880));
        rightRightPanel.setPreferredSize(new Dimension(410, 880));

        leftPanel.setBorder(BorderFactory.createBevelBorder(1));
        rightPanel.setBorder(BorderFactory.createBevelBorder(1));
        topDisplayPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        this.setSize(1600, 900);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        self = this;

        DivasTask tempTask = DivasTask.Factory.newInstance();

        tempTask.setName("Change Color");
        AgentAttribute tempa = tempTask.addNewDivasInput();
        tempa.setName("Light Color");
        tempa.setType(DivasAttributeType.Vector.toString());

        AgentAttribute tempb = tempTask.addNewDivasResult();
        tempb.setName("Light Color");
        tempb.setType(DivasAttributeType.Vector.toString());

        File savefile = new File(tasksFolder + System.getProperty("file.separator") + tempTask.getName() + ".task");

        try
        {
            tempTask.save(savefile, new XmlOptions().setSavePrettyPrint().setSavePrettyPrintIndent(4));
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        fillAgentList();
        fillDivasAttributeAddList();
        fillDivasTaskAddList();
    }

    // BUG = CANNOT CHANGE AGENT NAME

    public static File getAttrFolder()
    {
        return attrFolder;
    }

    protected void editTask()
    {
        // TODO Auto-generated method stub

    }

    protected void createTask()
    {
        // TODO Auto-generated method stub

    }

    protected void removeTask()
    {

        int size = ma.getDivasTaskArray().length;
        for(int i = 0; i < size; i++)
        {
            logger.debug("removing task " + ma.getDivasTaskArray(0).getName());
            ma.removeDivasTask(0);
        }
        fillDivasAttributePanel(selectedMetaAgentDoc, 0);
    }

    protected void addTask()
    {
        Object or = DivasTaskAddList.getSelectedValue();

        DivasTaskAddList.getSelectedIndex();
        if(or != null)
        {
            // logger.debug(or);
            int testint = DivasTaskAddList.getSelectedIndex();
            // logger.debug(names.get(testint));
            DivasTask a = taskNames.get(testint);

            if(selectedMetaAgentDoc != null)
            {
                DivasTask newa = ma.addNewDivasTask();
                newa.set(a.copy());
                logger.debug(a.toString());
                logger.debug(newa.toString());
                fillDivasAttributePanel(selectedMetaAgentDoc, 0);
            }
            else
            {
                logger.debug("No Agent Selected");
            }
        }
        else
        {
            logger.debug("Nothing Selected");
        }

    }

    private JPanel makeTopList()
    {
        JPanel DivasAttributeListPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));

        JLabel attrName = new JLabel("Name");
        JLabel attrCategory = new JLabel("Category");
        JLabel attrType = new JLabel("Type");
        JLabel attrUnit = new JLabel("Unit");
        JLabel attrMin = new JLabel("Min");
        JLabel attrMax = new JLabel("Max");
        JLabel attrLoc = new JLabel("Owner of DivasAttribute");
        JLabel attrDefault = new JLabel("Default");
        JLabel attrDelete = new JLabel("Delete");

        Font myfont = new Font("Ariel", Font.BOLD, 12);

        attrLoc.setFont(myfont);
        attrName.setFont(myfont);
        attrCategory.setFont(myfont);
        attrType.setFont(myfont);
        attrUnit.setFont(myfont);
        attrMin.setFont(myfont);
        attrMax.setFont(myfont);
        attrDefault.setFont(myfont);
        attrDelete.setFont(myfont);

        JPanel attrNamePanel = new JPanel();
        JPanel attrCategoryPanel = new JPanel();
        JPanel attrTypePanel = new JPanel();
        JPanel attrUnitPanel = new JPanel();
        JPanel attrMinPanel = new JPanel();
        JPanel attrMaxPanel = new JPanel();
        JPanel attrLocPanel = new JPanel();
        JPanel attrDefaultPanel = new JPanel();
        JPanel attrDeletePanel = new JPanel();

        attrLocPanel.add(attrLoc);
        attrNamePanel.add(attrName);
        attrCategoryPanel.add(attrCategory);
        attrTypePanel.add(attrType);
        attrUnitPanel.add(attrUnit);
        attrMinPanel.add(attrMin);
        attrMaxPanel.add(attrMax);
        attrDefaultPanel.add(attrDefault);
        attrDeletePanel.add(attrDelete);

        attrNamePanel.setBorder(BorderFactory.createLineBorder(Color.black));
        attrCategoryPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        attrTypePanel.setBorder(BorderFactory.createLineBorder(Color.black));
        attrUnitPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        attrMinPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        attrMaxPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        attrLocPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        attrDefaultPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        attrDeletePanel.setBorder(BorderFactory.createLineBorder(Color.black));

        attrNamePanel.setPreferredSize(new Dimension(200, attrHeight));
        attrCategoryPanel.setPreferredSize(new Dimension(120, attrHeight));
        attrTypePanel.setPreferredSize(new Dimension(120, attrHeight));
        attrUnitPanel.setPreferredSize(new Dimension(80, attrHeight));
        attrMinPanel.setPreferredSize(new Dimension(80, attrHeight));
        attrMaxPanel.setPreferredSize(new Dimension(80, attrHeight));
        attrDefaultPanel.setPreferredSize(new Dimension(130, attrHeight));
        attrDeletePanel.setPreferredSize(new Dimension(50, attrHeight));

        // DivasAttributeListPane.add(attrLocPanel);
        DivasAttributeListPane.add(attrNamePanel);
        DivasAttributeListPane.add(attrCategoryPanel);
        DivasAttributeListPane.add(attrTypePanel);
        DivasAttributeListPane.add(attrUnitPanel);
        DivasAttributeListPane.add(attrMinPanel);
        DivasAttributeListPane.add(attrMaxPanel);
        DivasAttributeListPane.add(attrDefaultPanel);
        DivasAttributeListPane.add(attrDeletePanel);
        return DivasAttributeListPane;
    }

    private void fillAgentList()
    {
        if(agentList != null)
            leftPanel.remove(agentList);
        File files[];
        files = ResourceManager.getFiles(metaSpecFolder, "agentmold");

        metaSpecs.clear();
        metaNodes.clear();
        for(File f : files)
        {
            // logger.debug(withoutExt(f.getName()));

            try
            {
                MetaAgentDocument mad = MetaAgentDocument.Factory.parse(f);
                MetaAgent testma = mad.getMetaAgent();

                logger.debug(testma.getName());
                logger.debug(testma.getParent());
                metaSpecs.put(testma.getName(), mad);

                DefaultMutableTreeNode mynode = new DefaultMutableTreeNode(testma.getName());
                metaNodes.put(testma.getName(), mynode);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

        }

        Iterator<MetaAgentDocument> mi = metaSpecs.values().iterator();
        String rootname = "";
        while(mi.hasNext())
        {
            logger.debug("Hi");
            MetaAgentDocument nextmad = mi.next();
            MetaAgent nextma = nextmad.getMetaAgent();
            DefaultMutableTreeNode parentNode = metaNodes.get(nextma.getParent());
            if(parentNode != null)
                parentNode.add(metaNodes.get(nextma.getName()));

            if(nextma.getParent().equals(""))
            {
                rootname = nextma.getName();
            }

        }
        agentList = new JTree(metaNodes.get(rootname));

        agentList.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        agentList.addTreeSelectionListener(new TreeSelectionListener()
        {
            @Override
            public void valueChanged(TreeSelectionEvent e)
            {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) agentList.getLastSelectedPathComponent();

                 if nothing is selected 
                if(node == null)
                    return;

                 retrieve the node that was selected 
                String nodeName = (String) node.getUserObject();
                logger.debug(nodeName);
                MetaAgentDocument mymad = metaSpecs.get(nodeName);

                MetaAgentDocument mad = (MetaAgentDocument) mymad.copy();
                logger.debug(mad.getMetaAgent().getName());
                fillDivasAttributePanel(mad, 0);
                selectedMetaAgentDoc = mad;

                 React to the node selection. 
                MetaAgent selectedAgent = mad.getMetaAgent();

                maDoc = mad;
                ma = selectedAgent;

                maParentName.setText(selectedAgent.getParent());
                maName.setText(selectedAgent.getName());

            }
        });

        DefaultTreeCellRenderer renderer2 = new DefaultTreeCellRenderer();
        renderer2.setOpenIcon(null);
        renderer2.setClosedIcon(null);
        renderer2.setLeafIcon(null);
        agentList.setCellRenderer(renderer2);

        leftPanel.add(agentList, BorderLayout.CENTER);
        leftPanel.validate();

    }

    private void fillDivasAttributePanel(MetaAgentDocument metaAgentDocument, int depth)
    {

        DivasAttributeDisplayPanel.removeAll();

        this.validate();

        DivasAttributeDisplayPanel.add(DivasTaskDisplayPanel);

        MetaAgent meta = metaAgentDocument.getMetaAgent();

        DivasTask[] dt = meta.getDivasTaskArray();
        logger.debug("len = " + dt.length);
        if(depth == 0)
        {
            DivasTaskDisplayPanel.removeAll();
            DivasTaskDisplayPanel.add(new JLabel("Available Tasks:"));

        }

        for(int i = 0; i < dt.length; i++)
        {
            DivasTaskDisplayPanel.add(new JLabel(dt[i].getName() + " , "));
        }

        String parentName = meta.getParent();
        if(!parentName.equals(""))
        {

            fillDivasAttributePanel(metaSpecs.get(parentName), depth + 1);

        }

        JPanel agentMoldName;
        agentMoldName = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));

        agentMoldName.setBorder(BorderFactory.createLineBorder(Color.black));
        JLabel textLabel;
        Font myfont = new Font("Serif", Font.BOLD, 16);
        if(depth != 0)
        {
            textLabel = new JLabel(meta.getName() + "   -   Attributes");
            textLabel.setFont(myfont);
            agentMoldName.add(textLabel);
            agentMoldName.setPreferredSize(new Dimension(860, attrHeight));
        }
        else
        {
            textLabel = new JLabel(meta.getName() + "   -   Attributes");
            textLabel.setFont(myfont);
            agentMoldName.add(textLabel);
            agentMoldName.setPreferredSize(new Dimension(860, attrHeight));
        }

        DivasAttributeDisplayPanel.add(agentMoldName);
        DivasAttributeDisplayPanel.add(makeTopList());

        DivasAttribute[] aa = meta.getDivasAttributeArray();

        for(int i = 0; i < aa.length; i++)
        {
            DivasAttributeDisplayPanel.add(makeDivasAttributePane(i, aa[i], depth, meta.getName()));
        }

        this.validate();

    }

    @SuppressWarnings("unchecked")
    public void fillDivasAttributeAddList()
    {

        File files[];
        files = ResourceManager.getFiles(attrFolder, "attribute");

        // String names[];
        names.clear();
        for(File f : files)
        {
            // logger.debug(withoutExt(f.getName()));

            try
            {
                DivasAttribute da = DivasAttribute.Factory.parse(f);

                logger.debug(da.getName());
                names.add(da);

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

        }

        // DivasAttributeAddList.setListData(names);

        //
        //
        //
        //
        //
        ListModel<?> listData = new AbstractListModel<Object>()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public int getSize()
            {
                return names.size();
            }

            @Override
            public Object getElementAt(int index)
            {
                DivasAttribute d = names.get(index);
                return d.getName();
            }
        };

        DivasAttributeAddList.setModel(listData);

    }

    @SuppressWarnings("unchecked")
    public void fillDivasTaskAddList()
    {

        File files[];
        files = ResourceManager.getFiles(tasksFolder, "task");

        // String names[];
        taskNames.clear();
        for(File f : files)
        {
            // logger.debug(withoutExt(f.getName()));

            try
            {
                DivasTask dt = DivasTask.Factory.parse(f);

                logger.debug(dt.getName());
                taskNames.add(dt);

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

        }

        ListModel<?> listData = new AbstractListModel<Object>()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public int getSize()
            {
                return taskNames.size();
            }

            @Override
            public Object getElementAt(int index)
            {
                DivasTask dt = taskNames.get(index);
                return dt.getName();
            }
        };

        DivasTaskAddList.setModel(listData);

    }

    private JPanel makeDivasAttributePane(int numb, DivasAttribute a, int depth, String name)
    {
        JPanel DivasAttributePane = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));

        // JLabel attrLoc = new JLabel(name);

        JLabel attrName = new JLabel(a.getName());

        JLabel attrCategory = new JLabel(a.getCategory().toString());
        JLabel attrType = new JLabel(a.getType());
        JLabel attrUnit = new JLabel(a.getUnit());

        Constraint[] cons = a.getConstraintArray();

        JLabel attrMin = new JLabel();
        JLabel attrMax = new JLabel();
        for(Constraint con : cons)
        {
            if(con.getName().equals("min"))
                attrMin = new JLabel(con.getValue());

            if(con.getName().equals("max"))
                attrMax = new JLabel(con.getValue());

        }
        JLabel attrDefault = new JLabel(a.getDefault());
        if(a.getType().equals(DivasAttributeType.Vector.toString()))
        {
            attrDefault = new JLabel("x: " + a.getDefaultx() + " y: " + a.getDefaulty() + " z: " + a.getDefaultz());
        }

        JButton attrDelete = new JButton("X");
        attrDelete.setName(Integer.toString(numb));
        if(depth != 0)
        {
            attrDelete.setEnabled(false);
        }

        attrDelete.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                // DivasAttribute[] aa =
                // selectedMetaAgentDoc.getMetaAgent().getDivasAttributeArray();
                // for(int i = 0; i < aa.length; i++)
                // {
                // DivasAttribute myatr = aa[i];
                // if(myatr.getName().equals(((JButton)
                // e.getSource()).getName()))
                // {
                // logger.debug("Deleting: " + ((JButton)
                // e.getSource()).getName());
                // selectedMetaAgentDoc.getMetaAgent().removeDivasAttribute(i);
                // }
                // }

                // DivasAttribute test =
                // selectedMetaAgentDoc.getMetaAgent().getDivasAttributeArray(Integer.parseInt(((JButton)
                // e.getSource()).getName()));
                // logger.debug(test);
                selectedMetaAgentDoc.getMetaAgent().removeDivasAttribute(Integer.parseInt(((JButton) e.getSource()).getName()));
                fillDivasAttributePanel(selectedMetaAgentDoc, 0);

            }
        });

        JPanel attrNamePanel = new JPanel();
        JPanel attrCategoryPanel = new JPanel();
        JPanel attrTypePanel = new JPanel();
        JPanel attrUnitPanel = new JPanel();
        JPanel attrMinPanel = new JPanel();
        JPanel attrMaxPanel = new JPanel();
        JPanel attrDefaultPanel = new JPanel();
        JPanel attrDeletePanel = new JPanel(new GridLayout(1, 1));

        if(depth == 0)
        {
            // logger.debug("hi");

            testmap.put(attrNamePanel, a);
            testmap.put(attrCategoryPanel, a);
            testmap.put(attrTypePanel, a);
            testmap.put(attrUnitPanel, a);
            testmap.put(attrMinPanel, a);
            testmap.put(attrMaxPanel, a);
            testmap.put(attrDefaultPanel, a);

            attrNamePanel.addMouseListener(new MouseListener()
            {
                @Override
                public void mouseEntered(MouseEvent e)
                {}

                @Override
                public void mouseExited(MouseEvent e)
                {}

                @Override
                public void mousePressed(MouseEvent e)
                {}

                @Override
                public void mouseReleased(MouseEvent e)
                {}

                @Override
                public void mouseClicked(MouseEvent e)
                {
                    DivasAttribute testa = (DivasAttribute) testmap.get(e.getSource());
                    logger.debug(testa.getName());
                    // Next create modify Dialogues (for each) here.
                }
            });

            attrCategoryPanel.addMouseListener(new MouseListener()
            {
                @Override
                public void mouseEntered(MouseEvent e)
                {}

                @Override
                public void mouseExited(MouseEvent e)
                {}

                @Override
                public void mousePressed(MouseEvent e)
                {}

                @Override
                public void mouseReleased(MouseEvent e)
                {}

                @Override
                public void mouseClicked(MouseEvent e)
                {
                    DivasAttribute testa = (DivasAttribute) testmap.get(e.getSource());
                    logger.debug(testa.getCategory().toString());
                }
            });

            attrTypePanel.addMouseListener(new MouseListener()
            {
                @Override
                public void mouseEntered(MouseEvent e)
                {}

                @Override
                public void mouseExited(MouseEvent e)
                {}

                @Override
                public void mousePressed(MouseEvent e)
                {}

                @Override
                public void mouseReleased(MouseEvent e)
                {}

                @Override
                public void mouseClicked(MouseEvent e)
                {
                    DivasAttribute testa = (DivasAttribute) testmap.get(e.getSource());
                    logger.debug(testa.getType());
                }
            });

            attrUnitPanel.addMouseListener(new MouseListener()
            {
                @Override
                public void mouseEntered(MouseEvent e)
                {}

                @Override
                public void mouseExited(MouseEvent e)
                {}

                @Override
                public void mousePressed(MouseEvent e)
                {}

                @Override
                public void mouseReleased(MouseEvent e)
                {}

                @Override
                public void mouseClicked(MouseEvent e)
                {
                    DivasAttribute testa = (DivasAttribute) testmap.get(e.getSource());
                    logger.debug(testa.getUnit());
                }
            });

            attrMinPanel.addMouseListener(new MouseListener()
            {
                @Override
                public void mouseEntered(MouseEvent e)
                {}

                @Override
                public void mouseExited(MouseEvent e)
                {}

                @Override
                public void mousePressed(MouseEvent e)
                {}

                @Override
                public void mouseReleased(MouseEvent e)
                {}

                @Override
                public void mouseClicked(MouseEvent e)
                {
                    DivasAttribute testa = (DivasAttribute) testmap.get(e.getSource());
                    logger.debug(testa.getConstraintArray(0).getValue());
                }
            });

            attrMaxPanel.addMouseListener(new MouseListener()
            {
                @Override
                public void mouseEntered(MouseEvent e)
                {}

                @Override
                public void mouseExited(MouseEvent e)
                {}

                @Override
                public void mousePressed(MouseEvent e)
                {}

                @Override
                public void mouseReleased(MouseEvent e)
                {}

                @Override
                public void mouseClicked(MouseEvent e)
                {
                    DivasAttribute testa = (DivasAttribute) testmap.get(e.getSource());
                    logger.debug(testa.getConstraintArray(1).getValue());
                }
            });

            attrDefaultPanel.addMouseListener(new MouseListener()
            {
                @Override
                public void mouseEntered(MouseEvent e)
                {}

                @Override
                public void mouseExited(MouseEvent e)
                {}

                @Override
                public void mousePressed(MouseEvent e)
                {}

                @Override
                public void mouseReleased(MouseEvent e)
                {}

                @Override
                public void mouseClicked(MouseEvent e)
                {
                    DivasAttribute testa = (DivasAttribute) testmap.get(e.getSource());
                    logger.debug(testa.getDefault());
                }
            });

        }

        attrNamePanel.add(attrName);
        attrCategoryPanel.add(attrCategory);
        attrTypePanel.add(attrType);
        attrUnitPanel.add(attrUnit);
        attrMinPanel.add(attrMin);
        attrMaxPanel.add(attrMax);
        attrDefaultPanel.add(attrDefault);
        attrDeletePanel.add(attrDelete);

        attrNamePanel.setBorder(BorderFactory.createLineBorder(Color.black));

        if(depth == 0)
        {
            attrNamePanel.setBackground(Color.white);
            attrCategoryPanel.setBackground(Color.white);
            attrTypePanel.setBackground(Color.white);
            attrUnitPanel.setBackground(Color.white);
            attrMinPanel.setBackground(Color.white);
            attrMaxPanel.setBackground(Color.white);
            attrDefaultPanel.setBackground(Color.white);
            attrDeletePanel.setBackground(Color.white);
        }

        attrCategoryPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        attrTypePanel.setBorder(BorderFactory.createLineBorder(Color.black));
        attrUnitPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        attrMinPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        attrMaxPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        attrDefaultPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        attrDeletePanel.setBorder(BorderFactory.createLineBorder(Color.black));

        attrNamePanel.setPreferredSize(new Dimension(200, attrHeight));
        attrCategoryPanel.setPreferredSize(new Dimension(120, attrHeight));
        attrTypePanel.setPreferredSize(new Dimension(120, attrHeight));
        attrUnitPanel.setPreferredSize(new Dimension(80, attrHeight));
        attrMinPanel.setPreferredSize(new Dimension(80, attrHeight));
        attrMaxPanel.setPreferredSize(new Dimension(80, attrHeight));
        attrDefaultPanel.setPreferredSize(new Dimension(130, attrHeight));
        attrDeletePanel.setPreferredSize(new Dimension(50, attrHeight));

        // DivasAttributePane.add(attrLocPanel);
        DivasAttributePane.add(attrNamePanel);
        DivasAttributePane.add(attrCategoryPanel);
        DivasAttributePane.add(attrTypePanel);
        DivasAttributePane.add(attrUnitPanel);
        DivasAttributePane.add(attrMinPanel);
        DivasAttributePane.add(attrMaxPanel);
        DivasAttributePane.add(attrDefaultPanel);
        DivasAttributePane.add(attrDeletePanel);

        return DivasAttributePane;
    }

    protected void deleteMetaAgent()
    {
        // TODO Auto-generated method stub

    }

    protected void editMetaAgent()
    {
        // TODO Auto-generated method stub

    }

    protected void newMetaAgent()
    {
        logger.debug("rawr??");
        NewMetaAgentDialogue newAgent = new NewMetaAgentDialogue();
        MetaAgentDocument newMetaDoc = MetaAgentDocument.Factory.newInstance();
        MetaAgent newMA = newMetaDoc.addNewMetaAgent();
        newAgent.showDialog(newMA);

        if(!newAgent.isCancelled())
        {
            newMA.setParent(ma.getName());
            ma = newMA;
            maDoc = newMetaDoc;

            // DefaultMutableTreeNode nn = new
            // DefaultMutableTreeNode(newMA.getName());
            // DefaultMutableTreeNode curnode = metaNodes.get(ma.getParent());
            // curnode.add(nn);
            //
            // metaSpecs.put(newMA.getName(), newMetaDoc);
            // metaNodes.put(newMA.getName(), nn);
            //
            // agentList.expandPath(new TreePath(curnode.getPath()));
            // agentList.setSelectionPath(new TreePath(nn.getPath()));
            //
            // agentList.validate();

            save();

            // agentList.setSelectionPath(tp);
            // agentList.expandPath(new TreePath(curnode.getPath()));
            // agentList.setSelectionPath(new TreePath(nn.getPath()));
            // agentList.validate();

        }
    }

    protected void addDivasAttribute()
    {
        Object or = DivasAttributeAddList.getSelectedValue();

        DivasAttributeAddList.getSelectedIndex();
        if(or != null)
        {
            // logger.debug(or);
            int testint = DivasAttributeAddList.getSelectedIndex();
            // logger.debug(names.get(testint));
            DivasAttribute a = names.get(testint);

            if(selectedMetaAgentDoc != null)
            {
                DivasAttribute newa = ma.addNewDivasAttribute();
                newa.set(a.copy());
                logger.debug(a.toString());
                logger.debug(newa.toString());
                fillDivasAttributePanel(selectedMetaAgentDoc, 0);
            }
            else
            {
                logger.debug("No Agent Selected");
            }
        }
        else
        {
            logger.debug("Nothing Selected");
        }

    }

    protected void createDivasAttribute()
    {
        DivasAttributeDialog attrDialog = new DivasAttributeDialog();
        attrDialog.showDialog();

        fillDivasAttributeAddList();
    }

    protected void editDivasAttribute()
    {

        Object or = DivasAttributeAddList.getSelectedValue();

        DivasAttributeAddList.getSelectedIndex();
        if(or != null)
        {
            // logger.debug(or);
            int testint = DivasAttributeAddList.getSelectedIndex();
            // logger.debug(names.get(testint));
            DivasAttribute a = names.get(testint);

            EditDivasAttributeDialog attrDialog = new EditDivasAttributeDialog();
            attrDialog.showDialog(a);

            fillDivasAttributeAddList();
        }
        else
        {
            logger.debug("Nothing Selected");
        }

    }

    // NEXT - Work (fix) agents new DivasAttributes are being saved without
    // pressing save.

    protected void save()
    {
        String pathName = ma.getName();
        // ma.setName(maName.getText());
        // ma.setParent(maParentName.getText());
        logger.debug(maDoc.xmlText(new XmlOptions().setSavePrettyPrint().setSavePrettyPrintIndent(4)));

        File savefile = new File(metaSpecFolder + System.getProperty("file.separator") + ma.getName() + ".agentmold");

        try
        {
            maDoc.save(savefile, new XmlOptions().setSavePrettyPrint().setSavePrettyPrintIndent(4));
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        maDoc.xmlText(new XmlOptions().setSavePrettyPrint().setSavePrettyPrintIndent(4));

        fillAgentList();
        DefaultMutableTreeNode newNode = metaNodes.get(pathName);
        agentList.expandPath(new TreePath(newNode.getPath()));
        agentList.setSelectionPath(new TreePath(newNode.getPath()));

        // AgentSpecFrame.updateAgentTypeBox();
    }

    @Override
    public void setVisible(boolean b)
    {
        super.setVisible(b);
        this.setState(Frame.NORMAL);
    }

    public static void main(String args[])
    {
        StyleHelper.setNimbusLookAndFeel();

        new MetaAgentSpecFrame();
    }

    protected static String withoutExt(String filename)
    {
        int extStart = filename.indexOf('.');
        return (extStart < 0) ? filename : filename.substring(0, extStart);
    }

    public static MetaAgentSpecFrame getFrame()
    {
        return self;
    }

    public static File getMetaSpecFolder()
    {
        return metaSpecFolder;
    }
}
*/