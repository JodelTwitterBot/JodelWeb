package org.cacert.gigi.output.template;

import java.util.Map;

/**
 * Represents some kind of data, that may be iterated over in a template.
 */
public interface IterableDataset {

	/**
	 * Moves to the next Dataset.
	 * 
	 * @param l
	 *            the language for l10n-ed strings
	 * @param vars
	 *            the variables used in this template. They need to be updated
	 *            for each line.
	 * @return true, iff there was a data-line "installed". False of this set is
	 *         already empty.
	 */
	public boolean next(Map<String, Object> vars);
}
