package edu.utdallas.mavs.divas.gui.mvp.view.swing.dialog;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
public class LoadPlaybackDialog
{
    public static File getPlaybackFile(JFrame parent)
    {
        JFileChooser fileChooser = new JFileChooser();

        // fileChooser.setCurrentDirectory(Config.getFileProperty("EnvironmentFolder"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new FileFilter()
        {
            @Override
            public String getDescription()
            {
                return "Playback File (*.sim)";
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
}
