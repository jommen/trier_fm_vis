package de.hochschuleTrier.fmv.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.hochschuleTrier.fmv.model.impl.featureDiagram.FeatureDiagramSchema;
import de.hochschuleTrier.fmv.model.impl.featureDiagram.FeatureModel;
import de.hochschuleTrier.fmv.model.interfaces.featureDiagram.IFeatureModel;

public class NewFeaturePopupForm extends JPanel {
	private IFeatureModel newFeatureNodeModel;
	private final JPanel formPanel;
	private int formPanelIndex;

	private JTextField nameTextField;
	private JComboBox<String> nodeTypeField;
	private JComboBox<String> groupTypeField;
	private JCheckBox optionalCheckBox;

	public NewFeaturePopupForm() {
		this.newFeatureNodeModel = new FeatureModel();
		this.formPanelIndex = 0;
		this.formPanel = new JPanel(new GridBagLayout());

		this.addNameFieldToPanel();
		this.addOptionalFieldToPanel();
		this.addNodeTypeFieldToPanel();
		this.addGroupTypeFieldToPanel();

		final int eb = 5;
		this.setBorder(BorderFactory.createEmptyBorder(eb, eb, eb, eb));
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.add(Box.createVerticalStrut(5));
		final JScrollPane formPanelScroll = new JScrollPane(this.formPanel);
		this.add(formPanelScroll);
	}

	private void addNameFieldToPanel() {
		this.formPanel.add(new JLabel("Name", SwingConstants.RIGHT), this.getLabelConstraints());
		this.nameTextField = new JTextField();
		this.formPanel.add(this.nameTextField, this.getFieldConstraints());
		this.formPanelIndex++;
	}

	private void addOptionalFieldToPanel() {
		this.formPanel.add(new JLabel("Optional", SwingConstants.RIGHT), this.getLabelConstraints());
		this.optionalCheckBox = new JCheckBox();
		this.formPanel.add(this.optionalCheckBox, this.getFieldConstraints());
		this.formPanelIndex++;
	}

	private void addNodeTypeFieldToPanel() {
		this.formPanel.add(new JLabel("Node type", SwingConstants.RIGHT), this.getLabelConstraints());
		this.nodeTypeField = new JComboBox<String>(new String[] { FeatureDiagramSchema.NODETYPE_FEATURE, FeatureDiagramSchema.NODETYPE_FEATUREGROUP });
		this.nodeTypeField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				if (NewFeaturePopupForm.this.getNodeTypeField().getSelectedItem() != null
						&& NewFeaturePopupForm.this.getNodeTypeField().getSelectedItem().equals(FeatureDiagramSchema.NODETYPE_FEATUREGROUP)) {
					NewFeaturePopupForm.this.getGroupTypeField().setEnabled(true);
				}
				else {
					NewFeaturePopupForm.this.getGroupTypeField().setEnabled(false);
				}
			}
		});
		this.formPanel.add(this.nodeTypeField, this.getFieldConstraints());
		this.formPanelIndex++;
	}

	private void addGroupTypeFieldToPanel() {
		this.formPanel.add(new JLabel("Group type", SwingConstants.RIGHT), this.getLabelConstraints());
		this.groupTypeField = new JComboBox<String>(new String[] { FeatureDiagramSchema.GROUPTYPE_ALTERNATIVE, FeatureDiagramSchema.GROUPTYPE_OR });
		this.groupTypeField.setEnabled(false);
		this.formPanel.add(this.groupTypeField, this.getFieldConstraints());
		this.formPanelIndex++;
	}

	private GridBagConstraints getLabelConstraints() {
		return new GridBagConstraints(0, this.formPanelIndex, 1, 1, 0.2, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0);
	}

	private GridBagConstraints getFieldConstraints() {
		return new GridBagConstraints(1, this.formPanelIndex, 1, 1, 1.0, 1.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
	}

	public IFeatureModel getNewFeatureNodeModel() {
		this.newFeatureNodeModel.setName(this.nameTextField.getText());
		this.newFeatureNodeModel.setNodeType((String) this.nodeTypeField.getSelectedItem());
		if (this.newFeatureNodeModel.getNodeType().equals(FeatureDiagramSchema.NODETYPE_FEATUREGROUP)) {
			this.newFeatureNodeModel.setGroupType((String) this.groupTypeField.getSelectedItem());
		}
		else {
			this.newFeatureNodeModel.setGroupType(FeatureDiagramSchema.GROUPTYPE_NONE);
		}
		this.newFeatureNodeModel.setOptional(this.optionalCheckBox.isSelected());

		return this.newFeatureNodeModel;
	}

	public int showDialog() {
		this.clearForm();
		return this.showJOptionDialog();
	}

	public int showDialog(final IFeatureModel model) {
		if (model == null) {
			return this.showDialog();
		}
		this.newFeatureNodeModel = model;
		this.optionalCheckBox.setSelected(this.newFeatureNodeModel.isOptional());
		this.nameTextField.setText(this.newFeatureNodeModel.getName());
		this.nodeTypeField.setSelectedItem(this.newFeatureNodeModel.getNodeType());
		this.groupTypeField.setSelectedItem(this.newFeatureNodeModel.getGroupType());
		return this.showJOptionDialog();
	}

	private int showJOptionDialog() {
		return JOptionPane.showOptionDialog(null, this, "Create new node", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[] { "OK", "Cancel" }, "OK");
	}

	private void clearForm() {
		this.optionalCheckBox.setSelected(false);
		this.nameTextField.setText("");
		this.nodeTypeField.setSelectedIndex(0);
		this.groupTypeField.setSelectedIndex(0);
	}

	private JComboBox<String> getNodeTypeField() {
		return this.nodeTypeField;
	}

	private JComboBox<String> getGroupTypeField() {
		return this.groupTypeField;
	}

}