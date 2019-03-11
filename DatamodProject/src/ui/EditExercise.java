package ui;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import dbsystem.Equipment;
import dbsystem.Exercise;
import dbsystem.ExerciseGroup;
import dbsystem.WithGr;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import dbsystem.DBController;
import javax.swing.JTextField;

public class EditExercise extends JFrame {
	private static final long serialVersionUID = 2L;
	JList<String> grNames, fgrNames, eqNames;
	JPanel mainPanel;
	JTextPane eInfo, grInfo, eqInfo;
	JButton save, addGroup, removeGroup, setEquip, removeEquip;
	DefaultListModel<String> grNamesList, fgrNamesList, eqNamesList;
	JTextField eName, eqSets, eqKilos, grInt;
	JTextArea eDesc;
	JLabel eNameLabel, eqSetsLabel, eqKilosLabel, grIntLabel;
	Font font = new Font("serif", Font.PLAIN, 14);
	static ArrayList<ExerciseGroup> fullGrList;
	static ArrayList<Equipment> fullEqList;
	ArrayList<Equipment> activeEqList = new ArrayList<Equipment>();
	ArrayList<ExerciseGroup> activeFGrList = new ArrayList<ExerciseGroup>();
	ArrayList<WithGr> activeGrList = new ArrayList<WithGr>();
	final static int width = 1000;
	final static int height = 600;
	DBController dbc;
	Exercise active;
	WorkoutList master;
	public EditExercise(DBController dbc, Exercise active, WorkoutList master) {
		super(active == null ? "New exercise" : "Edit exercise " + active.id);
		this.dbc = dbc;
		this.active = active;
		this.master = master;
		if (fullEqList == null) {
			System.out.println("Fetch eq");
			fullEqList = dbc.fetchEquipment(0, 0);
			System.out.println(fullEqList.size());
		}
		if (fullGrList == null) {
			fullGrList = dbc.fetchGroups(0, 0);
			Collections.sort(fullGrList);
		}
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		mainPanel.setPreferredSize(new Dimension(width, height));
		
		
		eInfo = new JTextPane();
		eInfo.setContentType("text/html");
		eInfo.setEditable(false);
		eInfo.setPreferredSize(new Dimension(200, 200));
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 2;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.PAGE_START;
		mainPanel.add(eInfo, c);
		
		c.gridy = 2;
		c.weighty = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		eNameLabel = new JLabel("Name: ");
		eNameLabel.setFont(font);
		mainPanel.add(eNameLabel, c);
		
		c.gridx = 1;
		eName = new JTextField();
		eName.setFont(font);
		mainPanel.add(eName, c);
		
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		save = new JButton("Save");
		save.addActionListener(new ButtonControl());
		save.setActionCommand("save");
		mainPanel.add(save, c);
		
		c.gridy = 4;
		c.gridheight = 2;
		c.weighty = 0.2;
		eDesc = new JTextArea();
		eDesc.setFont(font);
		mainPanel.add(eDesc, c);
		
		c.gridy = 0;
		c.gridx = 2;
		c.gridheight = 2;
		c.gridwidth = 1;
		c.weighty = 1.0;
		grNamesList = new DefaultListModel<String>();
		grNames = new JList<String>(grNamesList);
		grNames.setFont(font);
		grNames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		grNames.setLayoutOrientation(JList.VERTICAL);
		grNames.setVisibleRowCount(-1);
		grNames.addListSelectionListener(new GrSelObject());
		JScrollPane grNamesScroll = new JScrollPane(grNames);
		grNamesScroll.setPreferredSize(new Dimension(200, 200));
		mainPanel.add(grNamesScroll, c);
		
		c.weighty = 0;
		c.gridheight = 1;
		c.gridy = 2;
		setEquip = new JButton("Set Equipment");
		setEquip.addActionListener(new ButtonControl());
		setEquip.setActionCommand("setEquip");
		mainPanel.add(setEquip, c);
		
		c.gridy = 3;
		removeEquip = new JButton("Remove Equipment");
		removeEquip.addActionListener(new ButtonControl());
		removeEquip.setActionCommand("removeEquip");
		mainPanel.add(removeEquip, c);
		
		c.gridy = 4;
		addGroup = new JButton("<< Add Group");
		addGroup.addActionListener(new ButtonControl());
		addGroup.setActionCommand("addGroup");
		mainPanel.add(addGroup, c);
		
		c.gridy = 5;
		removeGroup = new JButton("Remove Group >>");
		removeGroup.addActionListener(new ButtonControl());
		removeGroup.setActionCommand("removeGroup");
		mainPanel.add(removeGroup, c);
		
		
	
		c.gridy = 0;
		c.gridx = 3;
		c.gridheight = 1;
		c.gridwidth = 2;
		fgrNamesList = new DefaultListModel<String>();
		fgrNames = new JList<String>(fgrNamesList);
		fgrNames.setFont(font);
		fgrNames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fgrNames.setLayoutOrientation(JList.VERTICAL);
		fgrNames.setVisibleRowCount(-1);
		fgrNames.addListSelectionListener(new FGrSelObject());
		JScrollPane fgrNamesScroll = new JScrollPane(fgrNames);
		fgrNamesScroll.setPreferredSize(new Dimension(200, 100));
		mainPanel.add(fgrNamesScroll, c);
		
		c.gridy = 1;
		eqNamesList = new DefaultListModel<String>();
		eqNames = new JList<String>(eqNamesList);
		eqNames.setFont(font);
		eqNames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		eqNames.setLayoutOrientation(JList.VERTICAL);
		eqNames.setVisibleRowCount(-1);
		eqNames.addListSelectionListener(new EqSelObject());
		JScrollPane eqNamesScroll = new JScrollPane(eqNames);
		eqNamesScroll.setPreferredSize(new Dimension(200, 100));
		mainPanel.add(eqNamesScroll, c);
		
		c.gridx = 5;
		c.gridheight = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		grInfo = new JTextPane();
		grInfo.setFont(font);
		grInfo.setContentType("text/html");
		grInfo.setEditable(false);
		grInfo.setPreferredSize(new Dimension(200, 200));
		mainPanel.add(grInfo, c);
		
		c.gridy = 1;
		eqInfo = new JTextPane();
		eqInfo.setFont(font);
		eqInfo.setContentType("text/html");
		eqInfo.setEditable(false);
		eqInfo.setPreferredSize(new Dimension(200, 200));
		mainPanel.add(eqInfo, c);
		
		c.gridy = 2;
		c.weighty = 0;
		c.gridx = 3;
		eqSetsLabel = new JLabel("Sets: ");
		eqSetsLabel.setFont(font);
		mainPanel.add(eqSetsLabel, c);
		
		c.gridx = 4;
		eqSets = new JTextField();
		eqSets.setFont(font);
		eqSets.setText("20");
		mainPanel.add(eqSets, c);
		
		c.gridx = 3;
		c.gridy = 3;
		eqKilosLabel = new JLabel("Kilos: ");
		eqKilosLabel.setFont(font);
		mainPanel.add(eqKilosLabel, c);
		
		c.gridx = 4;
		eqKilos = new JTextField();
		eqKilos.setFont(font);
		eqKilos.setText("5.0");
		mainPanel.add(eqKilos, c);
		
		c.gridy = 4;
		c.gridx = 3;
		grIntLabel = new JLabel("Intensity: ");
		grIntLabel.setFont(font);
		mainPanel.add(grIntLabel, c);
		
		c.gridx = 4;
		grInt = new JTextField();
		grInt.setFont(font);
		grInt.setText("5");
		mainPanel.add(grInt, c);
		
		
		
		if (active != null) {
			eDesc.setText(active.description);
			eName.setText(active.name);
			eInfo.setText(active.toDescString());
		}
		
		add(mainPanel);
		pack();
		setVisible(true);
		
		loadGrList();
		loadFGrList();
		loadEqList();
		removeEquip.setEnabled(active.eq != null);
		addGroup.setEnabled(false);
		removeGroup.setEnabled(false);
		setEquip.setEnabled(false);
	}
	void loadFGrList() {
		int index = fgrNames.getSelectedIndex();
		fgrNamesList.clear();
		activeFGrList.clear();
		fgrNamesList.addElement("New Group...");
		for (ExerciseGroup gr : fullGrList) {
			boolean cont = false;
			for (WithGr wgr : activeGrList) {
				if (wgr.gr == gr) {
					cont = true;
					break;
				}
			}
			if (!cont) {
				activeFGrList.add(gr);
				fgrNamesList.addElement(gr.toListString());
			}
		}
		fgrNames.setSelectedIndex(index);
	}
	void loadGrList() {
		grNamesList.clear();
		if (active == null) return;
		for (WithGr wgr : active.groups) {
			grNamesList.addElement(wgr.toEListString());
			activeGrList.add(wgr);
		}
	}
	void loadEqList() {
		eqNamesList.clear();
		eqNamesList.addElement("New Equipment...");
		for (Equipment eq : fullEqList) {
			eqNamesList.addElement(eq.toListString());
			activeEqList.add(eq);
		}
	}
	class FGrSelObject implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) return;
			if (fgrNames.getSelectedIndex() <= 0) {
				addGroup.setEnabled(false);
				grInfo.setText("");
				return;
			}
			addGroup.setEnabled(true);
			grInfo.setText(activeFGrList.get(fgrNames.getSelectedIndex() - 1).toDescString());
		}
	}
	class GrSelObject implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) return;
			if (grNames.getSelectedIndex() < 0) {
				removeGroup.setEnabled(false);
				return;
			}
			removeGroup.setEnabled(true);
			grInfo.setText(activeGrList.get(grNames.getSelectedIndex()).gr.toDescString());
		}
	}
	class EqSelObject implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) return;
			if (e.getValueIsAdjusting()) return;
			if (eqNames.getSelectedIndex() <= 0) {
				eqInfo.setText("");
				setEquip.setEnabled(false);
				return;
			}
			setEquip.setEnabled(true);
			eqInfo.setText(activeEqList.get(eqNames.getSelectedIndex() - 1).toDescString());
		}
	}
	class ButtonControl implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if (command.contentEquals("addGroup")) {
				if (fgrNames.getSelectedIndex() <= 0) return;
				ExerciseGroup gr = activeFGrList.get(fgrNames.getSelectedIndex() - 1);
				Integer intensity;
				try { intensity = Integer.valueOf(grInt.getText()); } catch (Exception ex) { intensity = null; }
				if (intensity == null || intensity < 1 || intensity > 10) return;
				WithGr wgr = active.createWithGr(intensity, gr, dbc);
				grNamesList.addElement(wgr.toEListString());
				activeGrList.add(wgr);
				loadFGrList();
			} else if (command.contentEquals("removeGroup")) {
				if (grNames.getSelectedIndex() < 0) return;
				WithGr wgr = activeGrList.get(grNames.getSelectedIndex());
				wgr.destroy(dbc);
				activeGrList.remove(grNames.getSelectedIndex());
				grNamesList.remove(grNames.getSelectedIndex());
				loadFGrList();
			} else if (command.contentEquals("setEquip")) {
				if (eqNames.getSelectedIndex() <= 0) return;
				Equipment eq = activeEqList.get(eqNames.getSelectedIndex() - 1);
				Integer sets;
				try { sets = Integer.valueOf(eqSets.getText()); } catch (Exception ex) { sets = null; }
				if (sets == null || sets <= 0) return;
				Double kilos;
				try { kilos = Double.valueOf(eqKilos.getText()); } catch (Exception ex) { kilos = null; }
				if (kilos == null || kilos < 0) return;
				if (active.eq == null) {
					active.createWithEq(sets, kilos, eq, dbc);
				} else if (active.eq.EquipmentId == eq.id) {
					active.eq.kilos = kilos;
					active.eq.sets = sets;
					active.eq.save(dbc);
				} else {
					active.eq.destroy(dbc);
					active.eq = null;
					active.createWithEq(sets, kilos, eq, dbc);
				}
				removeEquip.setEnabled(true);
			} else if (command.contentEquals("removeEquip")) {
				if (active.eq == null) return;
				active.eq.destroy(dbc);
				active.eq = null;
				removeEquip.setEnabled(false);
			} else if (command.contentEquals("save")) {
				String name = eName.getText();
				if (name == null || name.isEmpty()) return;
				String description = eDesc.getText();
				active.name = name;
				active.description = description;
				active.save(dbc);
			}
			eInfo.setText(active.toDescString());
			master.refreshExercises();
		}
	}
}
