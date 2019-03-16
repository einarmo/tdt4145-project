package ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import dbsystem.DBController;
import dbsystem.Exercise;
import dbsystem.WithEx;

public class ExerciseStats extends JFrame {
	private static final long serialVersionUID = 6L;
	JTextPane infoPane, wInfo;
	JButton refresh;
	JPanel mainPanel;
	Timestamp start, end;
	JList<String> wNames;
	Font font = new Font("serif", Font.PLAIN, 14);
	DefaultListModel<String> wNamesList;
	ArrayList<WithEx> activeWList = new ArrayList<WithEx>();
	Exercise active;
	DBController dbc;
	WorkoutList master;
	public ExerciseStats(Timestamp start, Timestamp end, Exercise active, DBController dbc, WorkoutList master) {
		super("View Exercise statistics");
		this.start = start;
		this.end = end;
		this.dbc = dbc;
		this.master = master;
		this.active = active;
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setPreferredSize(new Dimension(600, 600));
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.PAGE_START;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		infoPane = new JTextPane();
		infoPane.setContentType("text/html");
		infoPane.setEditable(false);
		infoPane.setPreferredSize(new Dimension(200, 200));
		mainPanel.add(infoPane, c);
		
		c.gridy = 1;
		c.weighty = 0;
		c.gridwidth = 3;
		refresh = new JButton("Refresh");
		refresh.addActionListener(new ButtonControl());
		mainPanel.add(refresh, c);
		
		c.gridy = 0;
		c.gridx = 1;
		c.gridwidth = 1;
		wNamesList = new DefaultListModel<String>();
		wNames = new JList<String>(wNamesList);
		wNames.setFont(font);
		wNames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		wNames.setLayoutOrientation(JList.VERTICAL);
		wNames.setVisibleRowCount(-1);
		wNames.addListSelectionListener(new WSelObject());
		JScrollPane wNamesScroll = new JScrollPane(wNames);
		wNamesScroll.setPreferredSize(new Dimension(200, 200));
		mainPanel.add(wNamesScroll, c);
		
		c.gridx = 2;
		wInfo = new JTextPane();
		wInfo.setEditable(false);
		wInfo.setContentType("text/html");
		wInfo.setPreferredSize(new Dimension(200, 200));
		mainPanel.add(wInfo, c);
		
		add(mainPanel);
		pack();
		setVisible(true);
		resetContent();
		setWList();
	}
	String generateStatString(ResultSet rs) {
		Integer sumperf = 0;
		Integer sumshape = 0;
		Integer count = 0;
		Integer tcount = 0;
		try {
			if (rs.next()) {
				count = rs.getInt("wcnt");
				tcount = rs.getInt("tcnt");
				sumshape = rs.getInt("sumshape");
				sumperf = rs.getInt("sumperf");
			}
		} catch (Exception e) {
			System.out.println("Failed to load stat result: " + e.getMessage());
		}
		int dcount = tcount == 0 ? 1 : tcount;
		return "<br><br>"
			+ (start != null ? "From: " + start.toString() + "<br>" : "")
			+ (end != null ? "To: " + end.toString() + "<br>" : "")
			+ "Total: " + tcount + " across " + count + " workouts in period.<br>"
			+ "Total performance: " + sumperf + "<br>"
			+ "Total shape: " + sumshape + "<br>"
			+ "Average performance: " + (Double.valueOf(sumperf)/dcount) + "<br>"
			+ "Average shape: " + (Double.valueOf(sumshape)/dcount) + "<br></html>";
	}
	void resetContent() {
		ResultSet rs = dbc.getExerciseStats(active.id, start, end);
		infoPane.setText(active.toDescString() + generateStatString(rs));
	}
	void setWList() {
		activeWList.clear();
		wNamesList.clear();
		HashSet<Long> added = new HashSet<Long>();
		for (WithEx we : active.workouts) {
			if ((start == null || we.wo.timestamp.getTime() > start.getTime())
					&& (end == null || we.wo.timestamp.getTime() < end.getTime())) {
				if (!added.contains(we.WorkoutId)) {
					activeWList.add(we);
					wNamesList.addElement(we.wo.toListString());
					we.wo.tmpcount = 1;
					added.add(we.WorkoutId);
				} else {
					we.wo.tmpcount++;
				}
			}
		}
	}
	class ButtonControl implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			resetContent();
			setWList();
		}
	}
	class WSelObject implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() || wNames.getSelectedIndex() < 0) return;
			wInfo.setText(activeWList.get(wNames.getSelectedIndex()).wo.toStatDescString());
		}
	}
}
