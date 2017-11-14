package edu.utdallas.mavs.divas.gui.mvp.view.swing.dialog;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadSimulationDialog
{
    @SuppressWarnings("unused")
    private final static Logger logger          = LoggerFactory.getLogger(LoadSimulationDialog.class);

    private static final String simSnapshotPath = System.getProperty("user.home") + System.getProperty("file.separator") + "Divas" + System.getProperty("file.separator") + "simsnapshot"
                                                        + System.getProperty("file.separator");

    public static File getEnvFile(JFrame parent)
    {
        File file = new File(simSnapshotPath);

        if(!file.exists())
        {
            file.mkdirs();
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(file);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new FileFilter()
        {
            @Override
            public String getDescription()
            {
                return "Simulation File (*.sim)";
            }

            @Override
            public boolean accept(File f)
            {
                return(f.getName().toLowerCase().endsWith(".sim") || f.isDirectory());
            }
        });

        if(fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION)
        {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    public static boolean isLoading(JFrame parent)
    {
        return (JOptionPane.showConfirmDialog(parent, "Loading a simulation from a file will terminate the current simulation.\nAre you sure you want to do this?", "Stop Simulation?",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION);
    }
}
