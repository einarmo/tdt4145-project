package ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AbstractDocument;

import dbsystem.DBController;
import dbsystem.Exercise;
import dbsystem.ExerciseGroup;
import dbsystem.WithEx;
import dbsystem.WithGr;
import dbsystem.Workout;

public class WorkoutList extends JFrame {
	private static final long serialVersionUID = 1L;
	JList<String> wNames, eNames, feNames, grNames;
	JPanel mainPanel;
	JTextPane wInfo, eInfo, wOutput;
	JButton wSave, wDelete, wAdd, wRemove, swapUp, swapDown, editExercise, removeExercise, eStats, grStats, setWLimit;
	DefaultListModel<String> wNamesList, eNamesList, feNamesList, grNamesList;
	String[] wInputLabelText = {"Performance (0-10)", "Shape (0-10)"};
	JLabel[] wInputLabels = new JLabel[wInputLabelText.length];
	JFormattedTextField[] wTextFields = new JFormattedTextField[wInputLabelText.length];
	JTextArea wNoteField = new JTextArea();
	JTextField wLimit;
	Font font = new Font("serif", Font.PLAIN, 14);
	ArrayList<Workout> activeWList = new ArrayList<Workout>();
	ArrayList<WithEx> activeEList = new ArrayList<WithEx>();
	ArrayList<Exercise> activeFEList = new ArrayList<Exercise>();
	static ArrayList<Exercise> fullFEList = null;
	ArrayList<ExerciseGroup> activeGrList = new ArrayList<ExerciseGroup>();
	final static int width = 1600;
	final static int height = 600;
	DBController dbc;
	DateTimePicker dtp, eMinDtp, eMaxDtp;
	Integer wLimitNum;
	public WorkoutList(DBController dbc) {
		super("Workout Diary");
		this.dbc = dbc;
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setPreferredSize(new Dimension(width, height));
		GridBagConstraints c = new GridBagConstraints();
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		wNamesList = new DefaultListModel<String>();
		wNames = new JList<String>(wNamesList);
		wNames.setFont(font);
		wNames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		wNames.setLayoutOrientation(JList.VERTICAL);
		wNames.setVisibleRowCount(-1);
		wNames.addListSelectionListener(new WSelObject());
		JScrollPane wNamesScroll = new JScrollPane(wNames);
		
		c.weighty = 1.0;
		c.weightx = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.PAGE_START;
		c.gridwidth = 3;
		c.gridheight = 4;
		mainPanel.add(wNamesScroll, c);
		
		c.weighty = 0;
		c.gridy = 4;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 1;
		c.gridheight = 1;
		for (int i = 0; i < wInputLabels.length; i++) {
			c.gridx = i;
			c.weightx = 0;
			c.gridy = 4;
			wInputLabels[i] = new JLabel(wInputLabelText[i]);
			wInputLabels[i].setFont(font);
			mainPanel.add(wInputLabels[i], c);
			c.gridy = 5;
			c.weightx = 1;
			wTextFields[i] = new JFormattedTextField();
			wTextFields[i].setFont(font);
			wTextFields[i].setColumns(2);
			((AbstractDocument) wTextFields[i].getDocument()).setDocumentFilter(new FieldLengthFilter(2));
			mainPanel.add(wTextFields[i], c);
		}
		
		c.weightx = 1;
		c.weighty = 0;
		c.gridx = 2;
		c.gridy = 4;
		c.gridheight = 2;
		dtp = new DateTimePicker();
		mainPanel.add(dtp, c);
		
		
		c.gridheight = 1;
		c.weighty = 1.0;
		c.weightx = 1.0;
		c.gridy = 6;
		c.gridwidth = 3;
		c.gridheight = 2;
		c.gridx = 0;
		wNoteField.setFont(font);
		JScrollPane wNoteScroll = new JScrollPane(wNoteField);
		mainPanel.add(wNoteScroll, c);
		
		c.gridy = 0;
		c.weighty = 1.0;
		c.gridx = 4;
		c.gridheight = 2;
		c.gridwidth = 1;
		c.weightx = 0;
		wInfo = new JTextPane();
		wInfo.setFont(font);
		wInfo.setPreferredSize(new Dimension(200, 200));
		wInfo.setEditable(false);
		wInfo.setContentType("text/html");
		mainPanel.add(wInfo, c);
		
		c.gridy = 2;
		eInfo = new JTextPane();
		eInfo.setContentType("text/html");
		eInfo.setPreferredSize(new Dimension(200, 200));
		eInfo.setEditable(false);
		eInfo.setFont(font);
		mainPanel.add(eInfo, c);
		
		c.gridx = 3;
		c.gridheight = 1;
		c.gridy = 4;
		c.weighty = 0;
		wSave = new JButton("Save Workout");
		wSave.setFont(font);
		wSave.addActionListener(new ButtonControl());
		wSave.setActionCommand("wsave");
		mainPanel.add(wSave, c);
		
		c.gridy = 5;
		wDelete = new JButton("Delete Workout");
		wDelete.setFont(font);
		wDelete.setActionCommand("wDelete");
		wDelete.addActionListener(new ButtonControl());
		mainPanel.add(wDelete, c);
		
		c.gridy = 6;
		c.gridheight = 2;
		wOutput = new JTextPane();
		wOutput.setEditable(false);
		mainPanel.add(wOutput, c);
		
		eNamesList = new DefaultListModel<String>();
		eNames = new JList<String>(eNamesList);
		eNames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		eNames.setLayoutOrientation(JList.VERTICAL);
		eNames.setVisibleRowCount(-1);
		eNames.setFont(font);
		eNames.addListSelectionListener(new ESelObject());
		c.gridx = 3;
		c.gridy = 0;
		c.weightx = 0;
		c.gridheight = 4;
		// eNames.setPreferredSize(new Dimension(200, 400));
		JScrollPane eNamesScroll = new JScrollPane(eNames);
		eNamesScroll.setPreferredSize(new Dimension(200, 200));
		mainPanel.add(eNamesScroll, c);
		
		feNamesList = new DefaultListModel<String>();
		feNames = new JList<String>(feNamesList);
		feNames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		feNames.setLayoutOrientation(JList.VERTICAL);
		feNames.setVisibleRowCount(-1);
		feNames.setFont(font);
		feNames.addListSelectionListener(new FESelObject());
		c.gridx = 5;
		c.gridy = 0;
		c.gridheight = 4;
		c.gridwidth = 2;
		JScrollPane feNamesScroll = new JScrollPane(feNames);
		feNamesScroll.setPreferredSize(new Dimension(200, 200));

		mainPanel.add(feNamesScroll, c);
		
		c.gridx = 4;
		c.gridwidth = 1;
		c.weighty = 0.2;
		wAdd = new JButton("<< Add Exercise");
		wAdd.setActionCommand("wAdd");
		wAdd.addActionListener(new ButtonControl());
		c.gridy = 4;
		c.gridheight = 1;
		mainPanel.add(wAdd, c);
		
		wRemove = new JButton("Remove Exercise >>");
		wRemove.setActionCommand("wRemove");
		wRemove.addActionListener(new ButtonControl());
		c.gridy = 5;
		mainPanel.add(wRemove, c);
		
		swapUp = new JButton("Move Exercise up");
		swapUp.setActionCommand("swapUp");
		swapUp.addActionListener(new ButtonControl());
		c.gridy = 6;
		mainPanel.add(swapUp, c);
		
		swapDown = new JButton("Move Exercise down");
		swapDown.setActionCommand("swapDown");
		swapDown.addActionListener(new ButtonControl());
		c.gridy = 7;
		mainPanel.add(swapDown, c);
		
		editExercise = new JButton("Edit/New Exercise");
		editExercise.setActionCommand("editExercise");
		editExercise.addActionListener(new ButtonControl(this));
		c.gridy = 4;
		c.gridx = 5;
		c.gridwidth = 2;
		mainPanel.add(editExercise, c);
		
		removeExercise = new JButton("Delete Exercise");
		removeExercise.setActionCommand("removeExercise");
		removeExercise.addActionListener(new ButtonControl(this));
		c.gridy = 5;
		mainPanel.add(removeExercise, c);
		
		c.gridy = 0;
		c.gridheight = 4;
		c.weighty = 1;
		c.gridx = 7;
		c.gridwidth = 1;
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
		
		c.gridy = 6;
		c.gridheight = 1;
		c.weighty = 0;
		c.gridwidth = 2;
		c.gridx = 5;
		eStats = new JButton("Exercise-stats between: ");
		eStats.setActionCommand("eStats");
		eStats.addActionListener(new ButtonControl(this));
		mainPanel.add(eStats, c);
		
		c.gridwidth = 1;
		c.weightx = 0;
		c.gridy = 7;
		setWLimit = new JButton("Limit W");
		setWLimit.setActionCommand("wLimit");
		setWLimit.addActionListener(new ButtonControl());
		mainPanel.add(setWLimit, c);
		
		c.gridx = 6;
		wLimit = new JTextField();
		mainPanel.add(wLimit, c);
		
		c.gridx = 7;
		c.gridy = 6;
		eMinDtp = new DateTimePicker();
		mainPanel.add(eMinDtp, c);
		
		eMaxDtp = new DateTimePicker();
		c.gridy = 7;
		mainPanel.add(eMaxDtp, c);
		
		c.gridy = 4;
		grStats = new JButton("Group-stats between:");
		grStats.setActionCommand("grStats");
		grStats.addActionListener(new ButtonControl(this));
		mainPanel.add(grStats, c);
		
		c.gridy = 5;
		JTextPane statsInfo = new JTextPane();
		statsInfo.setEditable(false);
		statsInfo.setText("Leave invalid for no upper/lower limit");
		statsInfo.setFont(font);
		mainPanel.add(statsInfo, c);
		
		add(mainPanel);
		pack();
		setVisible(true);
		swapUp.setEnabled(false);
		swapDown.setEnabled(false);
		wAdd.setEnabled(false);
		wRemove.setEnabled(false);
		eStats.setEnabled(false);
		grStats.setEnabled(false);
	}
	public void setWList(ArrayList<Workout> workouts) {
		wNamesList.clear();
		activeWList.clear();
		wNamesList.addElement("New Workout...");
		Collections.sort(workouts);
		int ind = 0;
		for (Workout w : workouts) {
			if (wLimitNum != null && ind++ >= wLimitNum) break;
			wNamesList.addElement(w.toListString());
			activeWList.add(w);
		}
		wNames.setSelectedIndex(0);
	}
	public void sortWList() {
		Collections.sort(activeWList, Collections.reverseOrder());
		int index = wNames.getSelectedIndex();
		wNamesList.clear();
		wNamesList.addElement("New workout...");
		for (Workout w : activeWList) {
			wNamesList.addElement(w.toListString());
		}
		wNames.setSelectedIndex(index);
	}
	public void refreshWList() {
		for (int i = 1; i <= activeWList.size(); i++) {
			wNamesList.set(i, activeWList.get(i-1).toListString());
		}
	}
	public void setEList(Workout w) {
		eNamesList.clear();
		activeEList.clear();
		if (w == null) return;
		ArrayList<WithEx> ex = new ArrayList<WithEx>(w.exercises);
		Collections.sort(ex);
		for (WithEx e : ex) {
			eNamesList.addElement(e.toWString());
			activeEList.add(e);
		}
	}
	public void refreshExercises() {
		for (int i = 1; i <= activeFEList.size(); i++) {
			feNamesList.set(i, activeFEList.get(i-1).toListString());
		}
		if (feNames.getSelectedIndex() > 0) {
			Exercise e = activeFEList.get(feNames.getSelectedIndex() - 1);
			eInfo.setText(e.toDescString());
		}
		if (wNames.getSelectedIndex() > 0) {
			Workout w = activeWList.get(wNames.getSelectedIndex() - 1);
			wInfo.setText(w.toDescString());
			setEList(w);
		}
	}
	public void sortEList() {
		Collections.sort(activeEList);
		int index = eNames.getSelectedIndex();
		eNamesList.clear();
		for (WithEx we : activeEList) {
			eNamesList.addElement(we.toWString());
		}
		eNames.setSelectedIndex(index);
	}
	public void setFEList(ArrayList<Exercise> exercises) {
		fullFEList = exercises;
		feNamesList.clear();
		activeFEList.clear();
		feNamesList.addElement("New Exercise...");
		Collections.sort(exercises);
		for (Exercise e : exercises) {
			feNamesList.addElement(e.toListString());
			activeFEList.add(e);
		}
		feNames.setSelectedIndex(0);
	}
	public void filterFEList(ExerciseGroup gr) {
		activeFEList.clear();
		feNamesList.clear();
		feNamesList.addElement("New Exercise...");
		for (Exercise e : fullFEList) {
			boolean filter = false;
			for (WithGr wgr : e.groups) {
				if (wgr.GroupId == gr.id) {
					filter = true;
					break;
				}
			}
			if (filter) {
				activeFEList.add(e);
				feNamesList.addElement(e.toListString());
			}
		}
	}
	public void addFE(Exercise ex) {
		fullFEList.add(ex);
		int index = grNames.getSelectedIndex();
		if (index == 0) {
			setFEList(fullFEList);
		} else {
			filterFEList(activeGrList.get(index - 1));
		}
	}
	public void setGrList(ArrayList<ExerciseGroup> groups) {
		activeGrList.clear();
		grNamesList.clear();
		grNamesList.addElement("All groups");
		for (ExerciseGroup gr : groups) {
			grNamesList.addElement(gr.toListString());
			activeGrList.add(gr);
		}
	}
	public void refreshGrList() {
		grNamesList.clear();
		grNamesList.addElement("All groups");
		for (ExerciseGroup gr : activeGrList) {
			grNamesList.addElement(gr.toListString());
		}
	}
	
	class WSelObject implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() || wNames.getSelectedIndex() < 0) return;
			if (wNames.getSelectedIndex() == 0) {
				wTextFields[0].setText("5");
				wTextFields[1].setText("5");
				wNoteField.setText("");
				wInfo.setText("");
				dtp.setTime(Calendar.getInstance());
				setEList(null);
				wAdd.setEnabled(false);
				wRemove.setEnabled(false);
				wDelete.setEnabled(false);
				swapUp.setEnabled(false);
				swapDown.setEnabled(false);
				return;
			}
			wRemove.setEnabled(true);
			wDelete.setEnabled(true);
			Workout w = activeWList.get(wNames.getSelectedIndex() - 1);
			wTextFields[0].setText(w.performance.toString());
			wTextFields[1].setText(w.shape.toString());
			wNoteField.setText(w.note);
			wInfo.setText(w.toDescString());
			dtp.setTime(w.timestamp);
			setEList(w);
			eNames.setSelectedIndex(eNamesList.size()-1);
			swapUp.setEnabled(eNames.getSelectedIndex() > 0);
			swapDown.setEnabled(false);
			wRemove.setEnabled(eNames.getSelectedIndex() >= 0);
			wAdd.setEnabled(feNames.getSelectedIndex() > 0);
		}
	}
	class FESelObject implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() || feNames.getSelectedIndex() < 0) return;
			if (feNames.getSelectedIndex() == 0) {
				eInfo.setText("");
				wAdd.setEnabled(false);
				eStats.setEnabled(false);
				return;
			}
			wAdd.setEnabled(wNames.getSelectedIndex() > 0);
			Exercise ex = activeFEList.get(feNames.getSelectedIndex() - 1);
			eInfo.setText(ex.toDescString());
			eStats.setEnabled(true);
		}
	}
	class ESelObject implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() || eNames.getSelectedIndex() < 0) return;
			WithEx ex = activeEList.get(eNames.getSelectedIndex());
			eInfo.setText(ex.ex.toDescString());
			swapUp.setEnabled(eNames.getSelectedIndex() != 0);
			swapDown.setEnabled(eNames.getSelectedIndex() != eNamesList.size() - 1);
			wRemove.setEnabled(true);
		}
	}
	class GrSelObject implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			int index = grNames.getSelectedIndex();
			if (e.getValueIsAdjusting() || index < 0) return;
			if (index == 0) {
				setFEList(fullFEList);
				grStats.setEnabled(false);
			} else {
				filterFEList(activeGrList.get(index - 1));
				grStats.setEnabled(true);
			}
		}
	}
	class ButtonControl implements ActionListener {
		WorkoutList master;
		public ButtonControl() {
			super();
			this.master = null;
		}
		public ButtonControl(WorkoutList master) {
			super();
			this.master = master;
		}
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if (command.equals("wsave")) {
				Integer performance;
				try { performance = Integer.valueOf(wTextFields[0].getText()); } catch (Exception ex) { performance = null; }
				if (performance == null || performance < 0 || performance > 10) {
					wOutput.setText("Performance must be an integer between 0 and 10");
					return;
				}
				Integer shape;
				try { shape = Integer.valueOf(wTextFields[1].getText()); } catch (Exception ex) { shape = null; }
				if (shape == null || shape < 0 || shape > 10) {
					wOutput.setText("Shape must be an integer between 0 and 10");
					return;
				}
				String note = wNoteField.getText();
				Timestamp ts = dtp.getTime();
				if (ts == null) {
					wOutput.setText("Timestamp must be valid");
					return;
				}
				if (wNames.getSelectedIndex() == 0) {
					Workout w = dbc.createWorkout(ts, performance, shape, note);
					if (w == null) return;
					wNamesList.addElement(w.toListString());
					activeWList.add(w);
				} else {
					Workout w = activeWList.get(wNames.getSelectedIndex()-1);
					w.performance = performance;
					w.shape = shape;
					w.timestamp = ts;
					w.note = note;
					w.save(dbc);
					wNamesList.set(wNames.getSelectedIndex(), w.toListString());
					wInfo.setText(w.toDescString());
					sortWList();
				}
			} else if (command.equals("wDelete")) {
				int index = wNames.getSelectedIndex();
				if (index <= 0) return;
				Workout w = activeWList.get(wNames.getSelectedIndex() - 1);
				if (!w.destroy(dbc)) return;
				activeWList.remove(wNames.getSelectedIndex() - 1);
				wNamesList.remove(wNames.getSelectedIndex());
				refreshWList();
				wNames.setSelectedIndex(index == wNamesList.size() ? index - 1 : index);
			} else if (command.equals("wRemove")) {
				if (wNames.getSelectedIndex() <= 0 || eNames.getSelectedIndex() < 0) return;
				int index = eNames.getSelectedIndex();
				WithEx wx = activeEList.get(index);
				if (!wx.destroy(dbc)) return;
				activeEList.remove(index);
				eNamesList.remove(index);
				eNames.setSelectedIndex(index == eNamesList.size() ? index - 1 : index);
				refreshWList();
				wRemove.setEnabled(activeEList.size() > 0);
			} else if (command.equals("wAdd")) {
				if (wNames.getSelectedIndex() <= 0 || feNames.getSelectedIndex() <= 0) return;
				Workout w = activeWList.get(wNames.getSelectedIndex() - 1);
				Exercise ex = activeFEList.get(feNames.getSelectedIndex() - 1);
				int max = 0;
				for (WithEx we : w.exercises) {
					if (max < we.intorder) {
						max = we.intorder;
					}
				}
				WithEx we = w.createWithEx(max + 1, ex, dbc);
				if (we == null) return;
				activeEList.add(we);
				eNamesList.addElement(we.toWString());
				refreshWList();
			} else if (command.equals("swapUp") || command.equals("swapDown")) {
				int index = eNames.getSelectedIndex();
				if (index == 0 && command.equals("swapUp") || index < 0
						|| index == (activeEList.size() - 1) && command.equals("swapDown")) return;
				WithEx we = activeEList.get(index);
				WithEx we2 = activeEList.get(index + (command.equals("swapUp") ? -1 : 1));
				we.swapOrder(dbc, we2);
				eNames.setSelectedIndex(index + (command.equals("swapUp") ? -1 : 1));
				sortEList();
			} else if (command.equals("editExercise")) {
				int index = feNames.getSelectedIndex();
				if (index < 0) return;
				EditExercise editEx = new EditExercise(dbc, index == 0 ? null : activeFEList.get(index - 1), master,
						activeGrList);
				editExercise.setEnabled(false);
				removeExercise.setEnabled(false);
				editEx.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						editExercise.setEnabled(true);
						removeExercise.setEnabled(true);
					}
				});
			} else if (command.equals("removeExercise")) {
				int index = feNames.getSelectedIndex();
				if (index <= 0 ) return;
				Exercise ex = activeFEList.get(index - 1);
				if (!ex.destroy(dbc)) return;
				activeFEList.remove(index - 1);
				feNamesList.remove(index);
				refreshWList();
				if (wNames.getSelectedIndex() > 0) {
					setEList(activeWList.get(wNames.getSelectedIndex() - 1));
				}
				feNames.setSelectedIndex(index == feNamesList.size() ? index - 1 : index);
			} else if (command.contentEquals("eStats")) {
				int index = feNames.getSelectedIndex();
				if (index <= 0) return;
				Timestamp start = eMinDtp.getTime();
				Timestamp end = eMaxDtp.getTime();
				new ExerciseStats(start, end, activeFEList.get(index - 1), dbc, master);
			} else if (command.contentEquals("grStats")) {
				int index = grNames.getSelectedIndex();
				if (index <= 0) return;
				Timestamp start = eMinDtp.getTime();
				Timestamp end = eMaxDtp.getTime();
				new GroupStats(start, end, activeGrList.get(index - 1), dbc, master);
			}
		}
	}
}
