// Java API

import agi.core.*;
import agi.swing.plaf.metal.*;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;

public class Main
{
	public static void main(String[] args)
	{
		try
		{
			AgCore_JNI.xInitThreads();
			MetalLookAndFeel.setCurrentTheme(AgMetalThemeFactory.getDefaultMetalTheme());
			UIManager.setLookAndFeel(MetalLookAndFeel.class.getName());
			JFrame.setDefaultLookAndFeelDecorated(true);
			
			Runnable r = new Runnable()
			{
				public void run()
				{
					try
					{
						MainWindow mw = new MainWindow();
						mw.setVisible(true);
						mw.setLocationRelativeTo(null);
					}
					catch(AgCoreException ce)
					{
						ce.printHexHresult();
						ce.printStackTrace();
					}
					catch(Throwable t)
					{
						t.printStackTrace();
					}
				}
			};
			SwingUtilities.invokeLater(r);
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}
}