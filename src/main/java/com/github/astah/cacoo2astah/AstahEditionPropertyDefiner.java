package com.github.astah.cacoo2astah;
import com.github.astah.cacoo2astah.util.AstahAPIUtils;

import ch.qos.logback.core.PropertyDefinerBase;

public class AstahEditionPropertyDefiner extends PropertyDefinerBase {
	private AstahAPIUtils utils = new AstahAPIUtils();

	@Override
	public String getPropertyValue() {
		String edition = utils.getEdition();
		if (edition.isEmpty()) {
			edition = "professional";
		}
		return edition;
	}
}