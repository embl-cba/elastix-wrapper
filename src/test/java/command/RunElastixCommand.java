package command;

import de.embl.cba.elastix.commands.ElastixCommand;
import net.imagej.ImageJ;

public class RunElastixCommand
{
    public static void main(final String... args)
    {
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
        ij.command().run( ElastixCommand.class, true );
    }
}
