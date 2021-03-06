import agi.core.*;
import agi.core.awt.*;
import agi.core.logging.*;
import agi.customapplications.swing.*;
import agi.stk.core.swing.toolbars.animation.*;
import agi.stkengine.*;
import agi.stkobjects.*;
import agi.stkx.*;
import agi.stkx.awt.AgMapCntrlClass;
import agi.stkx.swing.*;
import agi.swing.*;
import agi.swing.msgviewer.*;
import agi.swing.plaf.metal.*;
import agi.swing.toolbars.msgviewer.*;
import sun.awt.windows.ThemeReader;

import javax.swing.*;
import javax.swing.plaf.metal.MetalTheme;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainWindow
//NOTE:  This sample derives/extends from CustomApplicationSTKSampleBaseJFrame in order to provide
//common sample help regarding Java properties, connect command toolbar, common STK Engine functionality.
//You application is not required to derive from this class or have the same features it provides, but rather
//from the standard JFrame, Frame, or other preference.
extends CustomApplicationSTKSampleBaseJFrame
implements ActionListener, IAgAnimationJToolBarEventsListener, IAgMsgViewerJToolBarEventsListener
{
	private static final long		serialVersionUID	= 1L;

	private final static String		s_TITLE				= "STK—Fabric卫星仿真系统";
	private final static String		s_DESCFILENAME		= "AppDescription.html";

	private AgSTKXApplicationClass	m_AgSTKXApplicationClass;
	private AgStkObjectRootClass	m_AgStkObjectRootClass;

	private JSplitPane				m_PrimarySplitPane;
	private AgGlobeJPanel			m_AgGlobeJPanel;
	private AgMapCntrlClass         m_AgMapCntrlClass;

	private JSplitPane				m_SecondarySplitPane;
	private AgMsgViewerJPanel		m_AgMsgViewerJPanel;

	private SampleCode m_SampleCode;
	private SampleJPanel m_SampleJPanel;

	private AgAnimationJToolBar		m_AgAnimationJToolBar;


	public MainWindow()
	throws Throwable
	{
		super(MainWindow.class.getResource(s_DESCFILENAME));

		// ================================================
		// Set the logging level to Level.FINEST to get
		// all AGI java console logging
		// ================================================
		ConsoleHandler ch = new ConsoleHandler();
		ch.setLevel(Level.OFF);
		ch.setFormatter(new AgFormatter());
		Logger.getLogger("agi").setLevel(Level.OFF);
		Logger.getLogger("agi").addHandler(ch);

		// =========================================
		// This must be called before all
		// AWT/Swing/StkUtil/Stkx/StkObjects calls
		// =========================================
		AgAwt_JNI.initialize_AwtDelegate();
		AgStkCustomApplication_JNI.initialize(true); // true parameter allows for smart auto class cast
		AgAwt_JNI.initialize_AwtComponents();

		this.getContentPane().setLayout(new BorderLayout());
		this.setTitle(s_TITLE);
		this.setIconImage(new AgAGIImageIcon().getImage());

		this.m_AgSTKXApplicationClass = new AgSTKXApplicationClass();
		super.setApp(this.m_AgSTKXApplicationClass);

		if(!this.m_AgSTKXApplicationClass.isFeatureAvailable(AgEFeatureCodes.E_FEATURE_CODE_ENGINE_RUNTIME))
		{
			String msg = "STK Engine Runtime license is required to run this sample.  Exiting!";
			JOptionPane.showMessageDialog(this, msg, "License Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

		this.m_AgStkObjectRootClass = new AgStkObjectRootClass();
		super.setRoot(this.m_AgStkObjectRootClass);

		this.m_SampleCode = new SampleCode(this.m_AgStkObjectRootClass);

		MetalTheme mt = AgMetalThemeFactory.getDefaultMetalTheme();
		Color awtColor = mt.getPrimaryControl();
		AgCoreColor stkxColor = AgAwtColorTranslator.fromAWTtoCoreColor(awtColor);

		this.m_AgGlobeJPanel = new AgGlobeJPanel();
		this.m_AgGlobeJPanel.getControl().setBackColor(stkxColor);
		this.m_AgGlobeJPanel.getControl().setBackground(awtColor);

		this.m_AgMapCntrlClass = new AgMapCntrlClass();
		this.m_AgMapCntrlClass.setBackColor(stkxColor);
		this.m_AgMapCntrlClass.setBackground(awtColor);

		this.m_PrimarySplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.m_AgMapCntrlClass, this.m_AgGlobeJPanel);
		this.m_PrimarySplitPane.setResizeWeight(0.5);

		this.m_SampleJPanel = new SampleJPanel();
		this.m_SampleJPanel.addActionListener(this);
		this.getContentPane().add(this.m_SampleJPanel, BorderLayout.EAST);

		this.m_AgMsgViewerJPanel = new AgMsgViewerJPanel();
		this.m_AgMsgViewerJPanel.getMsgViewerJToolBar().addMsgViewerJToolBarListener(this);
		this.getContentPane().add(this.m_AgMsgViewerJPanel, BorderLayout.SOUTH);

		this.m_SecondarySplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.m_PrimarySplitPane, this.m_AgMsgViewerJPanel);
		this.m_SecondarySplitPane.setResizeWeight(0.75);
		this.getContentPane().add(this.m_SecondarySplitPane, BorderLayout.CENTER);

		this.m_AgAnimationJToolBar = new AgAnimationJToolBar();
		this.m_AgAnimationJToolBar.addAnimationJToolBarListener(this);
		this.getContentPane().add(this.m_AgAnimationJToolBar, BorderLayout.NORTH);

		// Remove unwanted menu bars for this sample
		JMenu vdfJMenu = this.getCustomAppSTKSampleBaseJMenuBar().getVDFJMenu();
		this.getCustomAppSTKSampleBaseJMenuBar().remove(vdfJMenu);
//		JMenu sampleJMenu = this.getCustomAppSTKSampleBaseJMenuBar().getSampleJMenu();
//		this.getCustomAppSTKSampleBaseJMenuBar().remove(sampleJMenu);
		this.getCustomAppSTKSampleBaseJMenuBar().invalidate();
		this.getCustomAppSTKSampleBaseJMenuBar().repaint();

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.addWindowListener(new MainWindowAdapter());

		this.setSize(1000, 618);
	}

	public void actionPerformed(ActionEvent ae)
	{
		try
		{
			((Component)this).setCursor(new Cursor(Cursor.WAIT_CURSOR));

			Object source = ae.getSource();
			String accessData;
			if(source.equals(this.m_SampleJPanel.m_Step1JButton))
			{
				this.m_SampleCode.setUnits();
				this.m_SampleCode.loadScenario();

				this.m_SampleJPanel.m_Step2JButton.setEnabled(true);
				this.m_SampleJPanel.m_Step7JButton.setEnabled(true);
			}
			else if(source.equals(this.m_SampleJPanel.m_Step2JButton))
			{
				accessData = this.m_SampleCode.accessComputer();
				this.m_AgMsgViewerJPanel.writeMessage(accessData);

				this.m_SampleJPanel.m_Step2JButton.setEnabled(false);
				this.m_SampleJPanel.m_Step3JButton.setEnabled(true);
			}
			else if(source.equals(this.m_SampleJPanel.m_Step3JButton))
			{
				this.m_SampleCode.startObervation();

				this.m_SampleJPanel.m_Step3JButton.setEnabled(false);
				this.m_SampleJPanel.m_Step4JButton.setEnabled(true);
			}
			else if(source.equals(this.m_SampleJPanel.m_Step4JButton))
			{
				this.m_SampleCode.endObervation();

				this.m_SampleJPanel.m_Step4JButton.setEnabled(false);
				this.m_SampleJPanel.m_Step5JButton.setEnabled(true);
			}
			else if(source.equals(this.m_SampleJPanel.m_Step5JButton))
			{
				accessData = this.m_SampleCode.accessComputer();
				this.m_AgMsgViewerJPanel.writeMessage(accessData);

				this.m_SampleJPanel.m_Step5JButton.setEnabled(false);
				this.m_SampleJPanel.m_Step6JButton.setEnabled(true);
			}
			else if(source.equals(this.m_SampleJPanel.m_Step6JButton))
			{

				this.m_AgMsgViewerJPanel.writeMessage(this.m_SampleCode.encryptCommunication());

				this.m_SampleJPanel.m_Step7JButton.setEnabled(true);
			}
			else if(source.equals(this.m_SampleJPanel.m_Step7JButton))
			{
				this.m_SampleCode.reset();
				this.m_SampleJPanel.reset();
			}
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
		finally
		{
			((Component)this).setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

	public void onAnimationJToolBarAction(AgAnimationJToolBarEvent e)
	{
		try
		{
			int action = e.getAnimationJToolBarAction();
			if(action == AgAnimationJToolBarEvent.ACTION_ANIMATION_REWIND)
			{
				this.m_AgStkObjectRootClass.rewind();
			}
			else if(action == AgAnimationJToolBarEvent.ACTION_ANIMATION_PLAYFORWARD)
			{
				this.m_AgStkObjectRootClass.playForward();
			}
			else if(action == AgAnimationJToolBarEvent.ACTION_ANIMATION_PLAYBACKWARD)
			{
				this.m_AgStkObjectRootClass.playBackward();
			}
			else if(action == AgAnimationJToolBarEvent.ACTION_ANIMATION_PAUSE)
			{
				this.m_AgStkObjectRootClass.pause();
			}
			else if(action == AgAnimationJToolBarEvent.ACTION_ANIMATION_STEPFORWARD)
			{
				this.m_AgStkObjectRootClass.stepForward();
			}
			else if(action == AgAnimationJToolBarEvent.ACTION_ANIMATION_STEPBACKWARD)
			{
				this.m_AgStkObjectRootClass.stepBackward();
			}
			else if(action == AgAnimationJToolBarEvent.ACTION_ANIMATION_FASTER)
			{
				this.m_AgStkObjectRootClass.faster();
			}
			else if(action == AgAnimationJToolBarEvent.ACTION_ANIMATION_SLOWER)
			{
				this.m_AgStkObjectRootClass.slower();
			}
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}

	class MainWindowAdapter
	extends WindowAdapter
	{
		public void windowClosing(WindowEvent evt)
		{
			try
			{
				// Must dispose your control(s) before uninitializing the API
				MainWindow.this.m_AgGlobeJPanel.getControl().dispose();
				MainWindow.this.m_AgMapCntrlClass.dispose();

				// Reverse of the initialization order
				AgAwt_JNI.uninitialize_AwtComponents();
				AgStkCustomApplication_JNI.uninitialize();
				AgAwt_JNI.uninitialize_AwtDelegate();
			}
			catch(Throwable t)
			{
				t.printStackTrace();
			}
		}
	}

	public void onMsgViewerJToolBarAction(AgMsgViewerJToolBarEvent e)
	{
		try
		{
			int action = e.getMsgViewerJToolBarAction();
			if(action == AgMsgViewerJToolBarEvent.ACTION_MSGVIEWER_SAVEAS)
			{
				this.m_AgMsgViewerJPanel.saveMessages();
			}
			else if(action == AgMsgViewerJToolBarEvent.ACTION_MSGVIEWER_CLEAR)
			{
				this.m_AgMsgViewerJPanel.clearMessages();
			}
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}
}