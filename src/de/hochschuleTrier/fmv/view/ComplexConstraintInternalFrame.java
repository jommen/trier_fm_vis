package de.hochschuleTrier.fmv.view;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraintModel;
import de.hochschuleTrier.fmv.view.display.ComplexConstraintDisplay;

public class ComplexConstraintInternalFrame extends JInternalFrame implements InternalFrameListener {

	private final IComplexConstraintModel constraintModel;
	private ComplexConstraintDisplay constraintDisplay;

	public ComplexConstraintInternalFrame(final IComplexConstraintModel model) {
		super("Complex-Constraint Diagram", true, true, true, true);
		this.addInternalFrameListener(this);
		this.constraintModel = model;
		this.setSize(400, 400);
		this.init();
	}

	private void init() {
		this.constraintDisplay = new ComplexConstraintDisplay(this.constraintModel);
		this.add(this.constraintDisplay);
	}

	@Override
	public void internalFrameOpened(final InternalFrameEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void internalFrameClosing(final InternalFrameEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void internalFrameClosed(final InternalFrameEvent e) {
		this.constraintDisplay.getVisualization().getAction("layout").cancel();
	}

	@Override
	public void internalFrameIconified(final InternalFrameEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void internalFrameDeiconified(final InternalFrameEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void internalFrameActivated(final InternalFrameEvent e) {
		ApplicationModel.getInstance().setActiveDisplay(this.constraintDisplay);
	}

	@Override
	public void internalFrameDeactivated(final InternalFrameEvent e) {
		// TODO Auto-generated method stub

	}

}
