package de.hochschuleTrier.fmv.view;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

public class ApplicationFrame extends JFrame {

	private JSplitPane horizontalSplitPane;
	private JDesktopPane mainDesktopPane;

	public ApplicationFrame() {
		super("Feature-Model Visualizer");

		this.setLookAndFeel();
		this.setJMenuBar(new ApplicationMenuBar(this));
		this.initFrame();

		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setExtendedState(Frame.MAXIMIZED_BOTH);
		this.setVisible(true);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ApplicationFrame.this.horizontalSplitPane.setDividerLocation(0.8);
			}
		});
	}

	private void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		}
		catch (final Exception e) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			catch (final Exception e1) {
			}
		}
	}

	private void initFrame() {
		final JPanel borderPanel = new JPanel(new BorderLayout());

		final JPanel buttonPanel = new ApplicationButtonPanel();
		borderPanel.add(buttonPanel, BorderLayout.NORTH);

		this.horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		this.horizontalSplitPane.setOneTouchExpandable(true);
		borderPanel.add(this.horizontalSplitPane);

		final JTabbedPane contextTabbedPane = new JTabbedPane();
		contextTabbedPane.add("Overview", new MinimapPanel());
		contextTabbedPane.add("Tree", new JScrollPane(new TreePanel()));

		this.mainDesktopPane = new ApplicationMainDesktopPane();
		this.horizontalSplitPane.add(this.mainDesktopPane, JSplitPane.LEFT);
		this.horizontalSplitPane.add(contextTabbedPane, JSplitPane.RIGHT);

		this.setContentPane(borderPanel);
	}

}