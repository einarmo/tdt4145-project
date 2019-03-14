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
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;

import dbsystem.DBController;
import dbsystem.ExerciseGroup;

public class EditGroup extends JFrame {
	private static final long serialVersionUID = 3L;
	JPanel mainPanel;
	JTextPane grInfo;
	JButton save;
	JTextField grname;
	JLabel nameLabel;
	Font font = new Font("serif", Font.PLAIN, 14);
	ExerciseGroup active;
	DBController dbc;
	EditExercise master;
	public EditGroup(DBController dbc, ExerciseGroup active, EditExercise master) {
		super(active == null ? "New Exercise Group" : "Edit Exercise Group");
		this.dbc = dbc;
		this.active = active;
		this.master = master;
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		mainPanel.setPreferredSize(new Dimension(300, 600));
		mainPanel.setFont(font);
		
		grInfo = new JTextPane();
		grInfo.setContentType("text/html");
		grInfo.setEditable(false);
		grInfo.setPreferredSize(new Dimension(300, 400));
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.PAGE_START;
		mainPanel.add(grInfo, c);
		
		c.gridy = 1;
		c.weighty = 0;
		c.gridwidth = 1;
		nameLabel = new JLabel("Name: ");
		nameLabel.setFont(font);
		mainPanel.add(nameLabel, c);
		
		c.gridx = 1;
		grname = new JTextField();
		mainPanel.add(grname, c);
		
		c.gridy = 2;
		c.gridx = 0;
		c.gridwidth = 2;
		save = new JButton("Save");
		save.setActionCommand("save");
		save.addActionListener(new ButtonControl());
		mainPanel.add(save, c);
		
		if (active != null) {
			grInfo.setText(active.toDescString());
			grname.setText(active.name);
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
				String name = grname.getText();
				if (name == null || name.isEmpty()) return;
				if (active != null) {
					active.name = name;
					active.save(dbc);
					master.refreshGroup();
				} else {
					active = new ExerciseGroup(name);
					if (!active.initialize(dbc)) return;
					master.addFGr(active);
					setTitle("Edit Exercise Group");
				}
			}
		}
	}
}
