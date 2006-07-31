package com.vscorp.ui.swing.redirector;

import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class RedirectorTest extends JFrame {

	//----------------------------------------------------------------------
	RedirectorTest() {
		super("Test");
	}

	//----------------------------------------------------------------------
	public void activated(WindowEvent anEvent) {
		System.out.println(anEvent.toString());
	}

	//----------------------------------------------------------------------
	public void closed(WindowEvent anEvent) {
		System.out.println(anEvent.toString());
	}

	//----------------------------------------------------------------------
	public void closing(WindowEvent anEvent) {
		System.out.println(anEvent.toString());
		System.exit(0);
	}

	//----------------------------------------------------------------------
	public void deactivated(WindowEvent anEvent) {
		System.out.println(anEvent.toString());
	}

	//----------------------------------------------------------------------
	public void deiconified(WindowEvent anEvent) {
		System.out.println(anEvent.toString());
	}

	//----------------------------------------------------------------------
	public void iconified(WindowEvent anEvent) {
		System.out.println(anEvent.toString());
	}

	//----------------------------------------------------------------------
	public void opened(WindowEvent anEvent) {
		System.out.println(anEvent.toString());
	}

	//----------------------------------------------------------------------
	public static void main(String[] args) {
		RedirectorTest test = new RedirectorTest();
		test.addWindowListener(new WindowRedirector(test,
													null,//"activated",
													"closed",
													"closing",
													"deactivated",
													"deiconified",
													"iconified",
													"opened"));

		test.setSize(200, 200);
		test.setVisible(true);
	}
}
