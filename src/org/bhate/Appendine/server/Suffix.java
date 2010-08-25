package org.bhate.Appendine.server;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class Suffix {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private String fix;

	@Persistent
	private boolean special;

	public Suffix(String fix) {
		this.fix = fix;
		this.special=false;
	}

	public void setFix(String fix) {
		this.fix = fix;
	}

	public String getFix() {
		return fix;
	}

	public Key getKey() {
		return key;
	}

	public boolean isSpecial() {
		return special;
	}
}