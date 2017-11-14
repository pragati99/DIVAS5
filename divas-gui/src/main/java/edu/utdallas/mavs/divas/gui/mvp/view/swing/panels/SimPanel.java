package edu.utdallas.mavs.divas.gui.mvp.view.swing.panels;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.utdallas.mavs.divas.core.client.dto.SimDto;
import edu.utdallas.mavs.divas.core.config.SimConfig;
import edu.utdallas.mavs.divas.core.sim.env.ReorganizationStrategy;
import edu.utdallas.mavs.divas.gui.mvp.model.SimEntity;
import edu.utdallas.mavs.divas.gui.mvp.view.swing.StyleHelper;
import edu.utdallas.mavs.divas.gui.mvp.view.swing.dialog.SimConfigDialog;

public class SimPanel extends JPanel
{
    private static final long                 serialVersionUID = 1L;

    private SimSummaryPanel                   simSummaryPanel;
    private JLabel                            statusLabel;
    private JLabel                            periodLabel;
    private JLabel                            cyclesLabel;
    private JLabel                            simTimeLabel;
    private JLabel                            agentCountLabel;
    private JLabel                            objectCountLabel;
    private SimpleDateFormat                  timeFormat;
    private JButton                           runButton;
    private JButton                           pauseButton;
    private JButton                           addAgentButton;
    private JButton                           optionsButton;
    private JComboBox<ReorganizationStrategy> reorgStrategy;
    private SimConfigDialog                   simConfigDialog;

    public SimPanel()
    {
        initialize();
    }

    private void setStatusLabel(String status)
    {
        statusLabel.setText(status);
    }

    private void setAgentCountLabel(int agentCount)
    {
        agentCountLabel.setText(String.valueOf(agentCount));
    }
    
    private void setObjectCountLabel(int objCount)
    {
        objectCountLabel.setText(String.valueOf(objCount));
    }

    private void setPeriodLabel(int period)
    {
        periodLabel.setText(((period < 0) ? "--" : String.valueOf(period)) + " ms");
    }

    private void setCyclesLabel(long cycles)
    {
        cyclesLabel.setText((cycles < 0) ? "--" : String.valueOf(cycles));
    }

    private void setSimTimeLabel(long simTime)
    {
        simTimeLabel.setText(((simTime < 0) ? "--" : timeFormat.format(new Date(simTime))));
    }

    private void initialize()
    {
        // set the look and feel
        StyleHelper.setNimbusLookAndFeel();

        setLayout(new BorderLayout());

        JPanel leftPanel = new JPanel(new BorderLayout());

        JPanel infoPanel = new JPanel(new BorderLayout());

        statusLabel = new JLabel();
        periodLabel = new JLabel();
        cyclesLabel = new JLabel();
        simTimeLabel = new JLabel();
        agentCountLabel = new JLabel();
        objectCountLabel = new JLabel();

        setPeriodLabel(-1);
        setCyclesLabel(-1);
        setSimTimeLabel(-1);

        timeFormat = new SimpleDateFormat("HH:mm:ss.S");
        timeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        runButton = new JButton("Run Simulation");
        runButton.setEnabled(true);

        pauseButton = new JButton("Pause Simulation");
        pauseButton.setEnabled(false);

        addAgentButton = new JButton("Add Agent");
        addAgentButton.setEnabled(false);

        optionsButton = new JButton("Options");
        optionsButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                optionsPressed();
            }
        });

        reorgStrategy = new JComboBox<ReorganizationStrategy>(ReorganizationStrategy.values());
        reorgStrategy.setSelectedIndex(0);
        reorgStrategy.setEnabled(false);

        simConfigDialog = new SimConfigDialog();

        JPanel buttonPanel = new JPanel(new GridLayout(0, 1));
        buttonPanel.add(runButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(addAgentButton);
        buttonPanel.add(optionsButton);
        buttonPanel.add(reorgStrategy);

        JPanel messagePanel = new JPanel(new GridLayout(0, 2));
        messagePanel.add(new JLabel("Status:"));
        messagePanel.add(statusLabel);
        messagePanel.add(new JLabel("Period:"));
        messagePanel.add(periodLabel);
        messagePanel.add(new JLabel("Cycle #:"));
        messagePanel.add(cyclesLabel);
        messagePanel.add(new JLabel("Simulation Time:"));
        messagePanel.add(simTimeLabel);
        messagePanel.add(new JLabel("Agent #:"));
        messagePanel.add(agentCountLabel);
        messagePanel.add(new JLabel("Object #:"));
        messagePanel.add(objectCountLabel);

        infoPanel.add(buttonPanel, BorderLayout.WEST);
        infoPanel.add(messagePanel, BorderLayout.CENTER);

        simSummaryPanel = new SimSummaryPanel();
        leftPanel.add(simSummaryPanel, BorderLayout.CENTER);
        leftPanel.add(infoPanel, BorderLayout.SOUTH);
        add(leftPanel, BorderLayout.WEST);

        setPaused();
    }

    private void optionsPressed()
    {
        simConfigDialog.showDialog(new JFrame(), SimConfig.getInstance());
    }

    public void setSimulationSummary(SimDto summary, int agentCount, int envObjCount)
    {
        reorgStrategy.setEnabled(summary.isSelfOrganized());
        reorgStrategy.setVisible(summary.isSelfOrganized());
        reorgStrategy.setSelectedItem(summary.getReorganizationStrategy());

        if(summary.getCycles() >= 0)
        {
            setPeriodLabel(summary.getPeriod());
            setCyclesLabel(summary.getCycles());
            setSimTimeLabel(summary.getSimTime());
            setAgentCountLabel(agentCount);
            setObjectCountLabel(envObjCount);
        }
    }

    public void setRunning()
    {
        setStatusLabel("Running");
        runButton.setEnabled(false);
        pauseButton.setEnabled(true);
        addAgentButton.setEnabled(true);
        optionsButton.setEnabled(true);
    }

    public void setPaused()
    {
        setStatusLabel("Stopped");
        runButton.setEnabled(true);
        pauseButton.setEnabled(false);
        addAgentButton.setEnabled(false);
        optionsButton.setEnabled(true);
    }

    public JButton getStartButton()
    {
        return runButton;
    }

    public JButton getPauseButton()
    {
        return pauseButton;
    }

    public JButton getAddAgentButton()
    {
        return addAgentButton;
    }

    public JButton getSearchButton()
    {
        return simSummaryPanel.getSearchButton();
    }

    public JButton getSaveOptionsButton()
    {
        return simConfigDialog.getSaveOptionsButton();
    }

    public JComboBox<ReorganizationStrategy> getReorganizationStrategyComboBox()
    {
        return reorgStrategy;
    }

    public void updateSearchResults(Map<Integer, SimEntity> simEntities)
    {
        simSummaryPanel.updateData(simEntities);
    }
    
    public void refreshSearchResults(Map<Integer, SimEntity> simEntities)
    {        
        simSummaryPanel.refreshData(simEntities);        
    }

    public String getSearchQuery()
    {
        return simSummaryPanel.getSearchQuery();
    }
    
    public JCheckBox getSearchAutoRefreshCheckBox()
    {     
        return simSummaryPanel.getSearchAutoRefreshCheckBox();
    }

    public void saveSimConfig()
    {
        simConfigDialog.saveSimConfig();
    }

    public void disableControls()
    {
        runButton.setEnabled(false);
        pauseButton.setEnabled(false);
        addAgentButton.setEnabled(false);
        reorgStrategy.setEnabled(false);
        optionsButton.setEnabled(false);
    }

    public void updateControls(boolean connected)
    {
        if(connected)
        {
            setPaused();
        }
        else
        {
            simSummaryPanel.clearData();
            disableControls();            
        }
    }
}
