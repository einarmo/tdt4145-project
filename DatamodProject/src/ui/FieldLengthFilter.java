package ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class FieldLengthFilter extends DocumentFilter {
	private int lim;
	public FieldLengthFilter(int lim) {
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
