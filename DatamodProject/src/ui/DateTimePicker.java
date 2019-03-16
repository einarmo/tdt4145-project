package ui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.sql.Timestamp;
import java.util.Calendar;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.AbstractDocument;

public class DateTimePicker extends JPanel {
	private static final long serialVersionUID = 5L;
	JLabel[] labels = new JLabel[4];
	JFormattedTextField[] fields = new JFormattedTextField[4];
	String[] labelText = new String[] {"dd", "mm", "yyyy", "hh"};
	Font font = new Font("serif", Font.PLAIN, 14);
	DateTimePicker() {
		super();
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1;
		c.weighty = 1;
		
		for (int i = 0; i < 4; i++) {
			c.gridx = i;
			c.gridy = 0;
			labels[i] = new JLabel();
			labels[i].setText(labelText[i]);
			labels[i].setFont(font);
			add(labels[i], c);
			
			c.gridy = 1;
			fields[i] = new JFormattedTextField();
			fields[i].setColumns(i == 2 ? 4 : 2);
			((AbstractDocument) fields[i].getDocument()).setDocumentFilter(new FieldLengthFilter(i == 2 ? 4 : 2));
			add(fields[i], c);
		}
	}
	public void setTime(Calendar c) {
		fields[0].setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
		fields[1].setText(String.valueOf(c.get(Calendar.MONTH) + 1));
		fields[2].setText(String.valueOf(c.get(Calendar.YEAR)));
		fields[3].setText(String.valueOf(c.get(Calendar.HOUR_OF_DAY)));
	}
	public void setTime(Timestamp ts) {
		Calendar c = Calendar.getInstance();
		c.setTime(ts);
		setTime(c);
	}
	Timestamp getTime() {
		Calendar c = Calendar.getInstance();
		try {
			c.set(Integer.valueOf(fields[2].getText()),
					Integer.valueOf(fields[1].getText()) - 1,
					Integer.valueOf(fields[0].getText()),
					Integer.valueOf(fields[3].getText()), 0, 0);
			return new Timestamp(c.getTime().getTime());
		} catch (Exception ex) {
			return null;
		}
	}
}
