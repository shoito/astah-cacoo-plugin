package com.github.astah.cacoo2astah.updater;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class UpdateCheck {
	@XmlAttribute(name="codebase")
	private String codeBase;
	
	@XmlAttribute
	private String version;

	public UpdateCheck() {
	}
	
	public String getCodeBase() {
		return codeBase;
	}

	public void setCodeBase(String codeBase) {
		this.codeBase = codeBase;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}