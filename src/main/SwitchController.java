package main;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.swing.JFrame;

public class SwitchController{
	public static ScheduledExecutorService scheduler;
	public static void main(String args[]){
		scheduler = Executors.newScheduledThreadPool(2);
	    JFrame frame = new MyFrame();
    }
}
