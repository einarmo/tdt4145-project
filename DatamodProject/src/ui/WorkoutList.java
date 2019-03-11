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
import java.util.Date;

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
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AbstractDocument;

import dbsystem.DBController;
import dbsystem.Equipment;
import dbsystem.Exercise;
import dbsystem.WithEx;
import dbsystem.Workout;

public class WorkoutList extends JFrame {
	private static final long serialVersionUID = 1L;
	JList<String> wNames, eNames, feNames;
	JPanel mainPanel;
	JTextPane wInfo, eInfo, wOutput;
	JButton wSave, wDelete, wAdd, wRemove, swapUp, swapDown, editExercise, removeExercise;
	DefaultListModel<String> wNamesList, eNamesList, feNamesList;
	String[] wInputLabelText = {"Performance (0-10)", "Shape (0-10)", "dd", "mm", "yyyy", "hh"};
	JLabel[] wInputLabels = new JLabel[wInputLabelText.length];
	JFormattedTextField[] wTextFields = new JFormattedTextField[wInputLabelText.length];
	JTextArea wNoteField = new JTextArea();
	Font font = new Font("serif", Font.PLAIN, 14);
	ArrayList<Workout> activeWList = new ArrayList<Workout>();
	ArrayList<WithEx> activeEList = new ArrayList<WithEx>();
	ArrayList<Exercise> activeFEList = new ArrayList<Exercise>();
	final static int width = 1400;
	final static int height = 600;
	DBController dbc;
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
		c.gridwidth = wInputLabels.length;
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
			wTextFields[i].setColumns(i == 4 ? 4 : 2);
			((AbstractDocument) wTextFields[i].getDocument()).setDocumentFilter(new WFieldFilter(i == 4 ? 4 : 2));
			mainPanel.add(wTextFields[i], c);
		}
		
		c.weighty = 1.0;
		c.weightx = 1.0;
		c.gridy = 6;
		c.gridwidth = wInputLabels.length;
		c.gridheight = 2;
		c.gridx = 0;
		wNoteField.setFont(font);
		JScrollPane wNoteScroll = new JScrollPane(wNoteField);
		mainPanel.add(wNoteScroll, c);
		
		c.gridy = 0;
		c.weighty = 1.0;
		c.gridx = wInputLabels.length + 1;
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
		
		c.gridx = wInputLabels.length;
		c.gridheight = 1;
		c.gridy = 4;
		c.weighty = 0;
		wSave = new JButton("Save");
		wSave.setFont(font);
		wSave.addActionListener(new ButtonControl());
		wSave.setActionCommand("wsave");
		mainPanel.add(wSave, c);
		
		c.gridy = 5;
		wDelete = new JButton("Delete");
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
		c.gridx = wInputLabels.length;
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
		c.gridx = wInputLabels.length + 2;
		c.gridy = 0;
		c.gridheight = 4;
		JScrollPane feNamesScroll = new JScrollPane(feNames);
		feNamesScroll.setPreferredSize(new Dimension(200, 200));

		mainPanel.add(feNamesScroll, c);
		
		c.gridx = wInputLabels.length + 1;
		
		c.weighty = 0.2;
		wAdd = new JButton("<< Add");
		wAdd.setActionCommand("wAdd");
		wAdd.addActionListener(new ButtonControl());
		c.gridy = 4;
		c.gridheight = 1;
		mainPanel.add(wAdd, c);
		
		wRemove = new JButton("Remove >>");
		wRemove.setActionCommand("wRemove");
		wRemove.addActionListener(new ButtonControl());
		c.gridy = 5;
		mainPanel.add(wRemove, c);
		
		swapUp = new JButton("Move up");
		swapUp.setActionCommand("swapUp");
		swapUp.addActionListener(new ButtonControl());
		c.gridy = 6;
		mainPanel.add(swapUp, c);
		
		swapDown = new JButton("Move down");
		swapDown.setActionCommand("swapDown");
		swapDown.addActionListener(new ButtonControl());
		c.gridy = 7;
		mainPanel.add(swapDown, c);
		
		editExercise = new JButton("Edit/New");
		editExercise.setActionCommand("editExercise");
		editExercise.addActionListener(new ButtonControl(this));
		c.gridy = 4;
		c.gridx = wInputLabels.length + 2;
		mainPanel.add(editExercise, c);
		
		removeExercise = new JButton("Delete");
		removeExercise.setActionCommand("removeExercise");
		removeExercise.addActionListener(new ButtonControl(this));
		c.gridy = 5;
		c.gridx = wInputLabels.length + 2;
		mainPanel.add(removeExercise, c);
		
		add(mainPanel);
		pack();
		setVisible(true);
	}
	public void setWList(ArrayList<Workout> workouts) {
		wNamesList.clear();
		activeWList.clear();
		wNamesList.addElement("New Workout...");
		Collections.sort(workouts);
		for (Workout w : workouts) {
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
	public void addFE(Exercise ex) {
		activeFEList.add(ex);
		feNamesList.addElement(ex.toListString());
	}
	final int WORKOUT = 0;
	final int EXERCISE = 1;
	void setOutput(int field, String text) {
		switch(field) {
		case WORKOUT:
			wOutput.setText(text);
			break;
		case EXERCISE:
			break;
		}
	}
	class WFieldFilter extends DocumentFilter {
		private int lim;
		public WFieldFilter(int lim) {
			this.lim = lim;
		}
		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
				throws BadLocationException {
			int overLimit = (fb.getDocument().getLength() + text.length()) - lim - length;
			if (overLimit > 0) {
				text = text.substring(0, text.length() - overLimit);
			}
			if (text.length() > 0) {
				super.replace(fb, offset, length, text, attrs);
			}
		}
	}
	class WSelObject implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() || wNames.getSelectedIndex() < 0) return;
			if (wNames.getSelectedIndex() == 0) {
				wTextFields[0].setText("5");
				wTextFields[1].setText("5");
				Calendar c = Calendar.getInstance();
				wTextFields[2].setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
				wTextFields[3].setText(String.valueOf(c.get(Calendar.MONTH) + 1));
				wTextFields[4].setText(String.valueOf(c.get(Calendar.YEAR)));
				wTextFields[5].setText(String.valueOf(c.get(Calendar.HOUR_OF_DAY)));
				wNoteField.setText("");
				wInfo.setText("");
				setEList(null);
				wAdd.setEnabled(false);
				wRemove.setEnabled(false);
				wDelete.setEnabled(false);
				return;
			}
			wAdd.setEnabled(true);
			wRemove.setEnabled(true);
			wDelete.setEnabled(true);
			Workout w = activeWList.get(wNames.getSelectedIndex() - 1);
			wTextFields[0].setText(w.performance.toString());
			wTextFields[1].setText(w.shape.toString());
			Calendar c = Calendar.getInstance();
			c.setTime(w.timestamp);
			wTextFields[2].setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
			wTextFields[3].setText(String.valueOf(c.get(Calendar.MONTH) + 1));
			wTextFields[4].setText(String.valueOf(c.get(Calendar.YEAR)));
			wTextFields[5].setText(String.valueOf(c.get(Calendar.HOUR_OF_DAY)));
			wNoteField.setText(w.note);
			wInfo.setText(w.toDescString());
			setEList(w);
		}
	}
	class FESelObject implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() || feNames.getSelectedIndex() < 0) return;
			if (feNames.getSelectedIndex() == 0) {
				eInfo.setText("");
				return;
			}
			Exercise ex = activeFEList.get(feNames.getSelectedIndex() - 1);
			eInfo.setText(ex.toDescString());
		}
	}
	class ESelObject implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() || eNames.getSelectedIndex() < 0) return;
			WithEx ex = activeEList.get(eNames.getSelectedIndex());
			eInfo.setText(ex.ex.toDescString());
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
					setOutput(WORKOUT, "Performance must be an integer between 0 and 10");
					return;
				}
				Integer shape;
				try { shape = Integer.valueOf(wTextFields[1].getText()); } catch (Exception ex) { shape = null; }
				if (shape == null || shape < 0 || shape > 10) {
					setOutput(WORKOUT, "Shape must be an integer between 0 and 10");
					return;
				}
				Calendar c = Calendar.getInstance();
				try {
					c.set(Integer.valueOf(wTextFields[4].getText()),
							Integer.valueOf(wTextFields[3].getText()) - 1,
							Integer.valueOf(wTextFields[2].getText()),
							Integer.valueOf(wTextFields[5].getText()), 0, 0);
				} catch (Exception ex) {
					setOutput(WORKOUT, "Date must be valid");
					return;
				}
				String note = wNoteField.getText();
				if (wNames.getSelectedIndex() == 0) {
					Workout w = dbc.createWorkout(new Timestamp(c.getTime().getTime()), performance, shape, note);
					wNamesList.addElement(w.toListString());
					activeWList.add(w);
				} else {
					Workout w = activeWList.get(wNames.getSelectedIndex()-1);
					w.performance = performance;
					w.shape = shape;
					w.timestamp = new Timestamp(c.getTime().getTime());
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
				w.destroy(dbc);
				activeWList.remove(wNames.getSelectedIndex() - 1);
				wNamesList.remove(wNames.getSelectedIndex());
				refreshWList();
				wNames.setSelectedIndex(index == wNamesList.size() ? index - 1 : index);
			} else if (command.equals("wRemove")) {
				if (wNames.getSelectedIndex() <= 0 || eNames.getSelectedIndex() < 0) return;
				int index = eNames.getSelectedIndex();
				WithEx wx = activeEList.get(index);
				wx.destroy(dbc);
				activeEList.remove(index);
				eNamesList.remove(index);
				eNames.setSelectedIndex(index == eNamesList.size() ? index - 1 : index);
				refreshWList();
			} else if (command.equals("wAdd")) {
				if (wNames.getSelectedIndex() <= 0 || feNames.getSelectedIndex() <= 0) return;
				Workout w = activeWList.get(wNames.getSelectedIndex() - 1);
				Exercise ex = activeFEList.get(feNames.getSelectedIndex() - 1);
				int max = -1;
				for (WithEx we : w.exercises) {
					if (max < we.intorder) {
						max = we.intorder;
					}
				}
				WithEx we = w.createWithEx(max + 1, ex, dbc);
				activeEList.add(we);
				eNamesList.addElement(we.toWString());
				refreshWList();
			} else if (command.equals("swapUp") || command.equals("swapDown")) {
				int index = eNames.getSelectedIndex();
				if (index == 0 && command.equals("swapUp")
						|| index == (activeEList.size() - 1) && command.equals("swapDown")) return;
				WithEx we = activeEList.get(index);
				WithEx we2 = activeEList.get(index + (command.equals("swapUp") ? -1 : 1));
				we.swapOrder(dbc, we2);
				eNames.setSelectedIndex(index + (command.equals("swapUp") ? -1 : 1));
				sortEList();
			} else if (command.equals("editExercise")) {
				int index = feNames.getSelectedIndex();
				if (index < 0) return;
				if (index == 0) {
					new EditExercise(dbc, null, master);
				} else {
					editExercise.setEnabled(false);
					removeExercise.setEnabled(false);
					EditExercise editEx = new EditExercise(dbc, activeFEList.get(index - 1), master);
					editEx.addWindowListener(new WindowAdapter() {
						public void windowClosing(WindowEvent e) {
							editExercise.setEnabled(true);
							removeExercise.setEnabled(true);
						}
					});
				}
			} else if (command.equals("removeExercise")) {
				int index = feNames.getSelectedIndex();
				if (index <= 0 ) return;
				Exercise ex = activeFEList.get(index - 1);
				ex.destroy(dbc);
				activeFEList.remove(index - 1);
				feNamesList.remove(index);
				refreshWList();
				if (wNames.getSelectedIndex() >= 1) {
					setEList(activeWList.get(wNames.getSelectedIndex() - 1));
				}
			}
		}
	}
}
