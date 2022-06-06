import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SampleJPanel
extends JPanel
{
	private static final long	serialVersionUID	= 1L;

	private final static String	s_STEP1_TEXT	= "场景加载";
	private final static String	s_STEP2_TEXT	= "第一次可见性分析";
	private final static String	s_STEP3_TEXT	= "自动观测";
	private final static String	s_STEP4_TEXT	= "修改传感器参数";
	private final static String	s_STEP5_TEXT	= "第二次可见性分析";
	private final static String	s_STEP6_TEXT	= "加密通信";
	private final static String	s_STEP7_TEXT	= "场景重置";

	/* package */JButton		m_Step1JButton;
	/* package */JButton		m_Step2JButton;
	/* package */JButton		m_Step3JButton;
	/* package */JButton		m_Step4JButton;
	/* package */JButton		m_Step5JButton;
	/* package */JButton		m_Step6JButton;
	/* package */JButton		m_Step7JButton;

	public SampleJPanel()
	{
		this.setLayout(new GridLayout(7, 1));

		this.m_Step1JButton = new JButton(s_STEP1_TEXT);
		this.m_Step1JButton.setEnabled(true);
		this.add(this.m_Step1JButton);

		this.m_Step2JButton = new JButton(s_STEP2_TEXT);
		this.m_Step2JButton.setEnabled(false);
		this.add(this.m_Step2JButton);

		this.m_Step3JButton = new JButton(s_STEP3_TEXT);
		this.m_Step3JButton.setEnabled(false);
		this.add(this.m_Step3JButton);

		this.m_Step4JButton = new JButton(s_STEP4_TEXT);
		this.m_Step4JButton.setEnabled(false);
		this.add(this.m_Step4JButton);

		this.m_Step5JButton = new JButton(s_STEP5_TEXT);
		this.m_Step5JButton.setEnabled(false);
		this.add(this.m_Step5JButton);

		this.m_Step6JButton = new JButton(s_STEP6_TEXT);
		this.m_Step6JButton.setEnabled(false);
		this.add(this.m_Step6JButton);

		this.m_Step7JButton = new JButton(s_STEP7_TEXT);
		this.m_Step7JButton.setEnabled(false);
		this.add(this.m_Step7JButton);

	}

	public void addActionListener(ActionListener al)
	{
		this.m_Step1JButton.addActionListener(al);
		this.m_Step2JButton.addActionListener(al);
		this.m_Step3JButton.addActionListener(al);
		this.m_Step4JButton.addActionListener(al);
		this.m_Step5JButton.addActionListener(al);
		this.m_Step6JButton.addActionListener(al);
		this.m_Step7JButton.addActionListener(al);
	}

	public void reset()
	{
		this.getStepOne().setEnabled(true);
		this.getStepTwo().setEnabled(true);
		this.getStepThree().setEnabled(false);
		this.getStepFour().setEnabled(false);
		this.getStepFive().setEnabled(false);
		this.getStepSix().setEnabled(true);
		this.getStepSeven().setEnabled(true);
	}

	public JButton getStepOne() { return this.m_Step1JButton; }
	public JButton getStepTwo() { return this.m_Step2JButton; }
	public JButton getStepThree() { return this.m_Step3JButton; }
	public JButton getStepFour() { return this.m_Step4JButton; }
	public JButton getStepFive() { return this.m_Step5JButton; }
	public JButton getStepSix() { return this.m_Step6JButton; }
	public JButton getStepSeven() { return this.m_Step7JButton; }

}