package edu.utdallas.mavs.divas.gui.mvp.view.swing.dialog;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadEnvironmentDialog
{
    @SuppressWarnings("unused")
    private final static Logger logger          = LoggerFactory.getLogger(LoadEnvironmentDialog.class);

    private static final String environmentPath = System.getProperty("user.home") + System.getProperty("file.separator") + "Divas" + System.getProperty("file.separator") + "environment"
                                                        + System.getProperty("file.separator");

    public static File getEnvFile(JFrame parent)
    {
        File file = new File(environmentPath);

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
                return "Environment File (*.env)";
            }

            @Override
            public boolean accept(File f)
            {
                return(f.getName().toLowerCase().endsWith(".env") || f.isDirectory());
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
        return (JOptionPane.showConfirmDialog(parent, "Loading an environment will terminate the current simulation.\nAre you sure you want to do this?", "Stop Simulation?",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION);
    }
}
