package org.bhate.Appendine.server;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class Input {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private String input;

	@Persistent
	private String source;

	@Persistent
	private Date date;

	public Input(String input, String source) {
		this.input = input;
		this.source = source;
		this.date = new Date();
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getInput() {
		return input;
	}

	public Key getKey() {
		return key;
	}

	public String getSource() {
		return source;
	}

	public Date getDate() {
		return date;
	}

}