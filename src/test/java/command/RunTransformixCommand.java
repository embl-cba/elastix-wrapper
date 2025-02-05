package command;

import de.embl.cba.elastix.commands.TransformixCommand;
import net.imagej.ImageJ;

public class RunTransformixCommand
{
    public static void main(final String... args)
    {
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
        ij.command().run( TransformixCommand.class, true );
    }
}
