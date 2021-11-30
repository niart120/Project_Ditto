package main;

import javax.swing.JFrame;

public class MyFrame extends JFrame{

	/**
	 *
	 */
	private static final long serialVersionUID = -573964303682758677L;

	public MyFrame(){

		this.setLocationByPlatform(true);
		this.setSize(640, 640);
//	    this.setLocationRelativeTo(null);
//	    this.setExtendedState(JFrame.MAXIMIZED_BOTH);
//	    this.setUndecorated(true);
	    this.add(new InputPanel(this));
		this.setVisible(true);
	}
}