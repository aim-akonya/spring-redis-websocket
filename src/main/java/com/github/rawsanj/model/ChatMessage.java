package com.github.rawsanj.model;

public class ChatMessage {

	private Integer id;
	private String message;
	private String hostname;
	private Long usersOnline;

	/**
	 * 
	 */
	public ChatMessage() {
		
	}

	/**
	 * @param id
	 * @param message
	 * @param hostname
	 * @param usersOnline
	 */
	public ChatMessage(Integer id, String message, String hostname, Long usersOnline) {
		super();
		this.id = id;
		this.message = message;
		this.hostname = hostname;
		this.usersOnline = usersOnline;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the hostname
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * @param hostname the hostname to set
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	/**
	 * @return the usersOnline
	 */
	public Long getUsersOnline() {
		return usersOnline;
	}

	/**
	 * @param usersOnline the usersOnline to set
	 */
	public void setUsersOnline(Long usersOnline) {
		this.usersOnline = usersOnline;
	}

}
