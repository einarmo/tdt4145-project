package ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;

import dbsystem.DBController;
import dbsystem.Equipment;

public class EditEquipment extends JFrame {
	private static final long serialVersionUID = 4L;
	JPanel mainPanel;
	JTextPane eqInfo;
	JButton save;
	JTextField eqname;
	JLabel nameLabel;
	JTextArea desc;
	Font font = new Font("serif", Font.PLAIN, 14);
	Equipment active;
	DBController dbc;
	EditExercise master;
	public EditEquipment(DBController dbc, Equipment active, EditExercise master) {
		super(active == null ? "New Equipment" : "Edit Equipment");
		this.dbc = dbc;
		this.active = active;
		this.master = master;
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		mainPanel.setPreferredSize(new Dimension(300, 600));
		mainPanel.setFont(font);
		
		eqInfo = new JTextPane();
		eqInfo.setContentType("text/html");
		eqInfo.setEditable(false);
		eqInfo.setPreferredSize(new Dimension(300, 400));
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.PAGE_START;
		mainPanel.add(eqInfo, c);
		
		c.gridy = 1;
		c.weighty = 0;
		c.gridwidth = 1;
		nameLabel = new JLabel("Name: ");
		nameLabel.setFont(font);
		mainPanel.add(nameLabel, c);
		
		c.gridx = 1;
		eqname = new JTextField();
		mainPanel.add(eqname, c);
		
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		c.gridheight = 2;
		desc = new JTextArea();
		mainPanel.add(desc, c);
		
		c.gridy = 4;
		c.gridheight = 1;
		save = new JButton("Save");
		save.setActionCommand("save");
		save.addActionListener(new ButtonControl());
		mainPanel.add(save, c);
		
		if (active != null) {
			eqInfo.setText(active.toDescString());
			eqname.setText(active.name);
			desc.setText(active.description);
		}
		
		add(mainPanel);
		pack();
		setVisible(true);
	}
	class ButtonControl implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if (command.contentEquals("save")) {
				String name = eqname.getText();
				if (name == null || name.isEmpty()) return;
				String description = desc.getText();
				if (active != null) {
					active.name = name;
					active.description = description;
					active.save(dbc);
					master.refreshEquip();
				} else {
					active = dbc.createEquipment(name, description);
					if (active == null) return;
					master.addEq(active);
					setTitle("Edit Equipment");
				}
				eqInfo.setText(active.toDescString());
			}
		}
	}
}
