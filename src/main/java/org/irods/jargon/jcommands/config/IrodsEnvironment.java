/**
 * 
 */
package org.irods.jargon.jcommands.config;

/**
 * Pojo representing irods_environment.json
 * 
 * @author mcc
 *
 */
public class IrodsEnvironment {

	private String host = "";
	private int port = 1247;
	private String zone = "";
	private String user = "";
	private String resource = "";

	/**
	 * 
	 */
	public IrodsEnvironment() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host
	 *            the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the zone
	 */
	public String getZone() {
		return zone;
	}

	/**
	 * @param zone
	 *            the zone to set
	 */
	public void setZone(String zone) {
		this.zone = zone;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the resource
	 */
	public String getResource() {
		return resource;
	}

	/**
	 * @param resource
	 *            the resource to set
	 */
	public void setResource(String resource) {
		this.resource = resource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("IrodsEnvironment [");
		if (host != null) {
			builder.append("host=").append(host).append(", ");
		}
		builder.append("port=").append(port).append(", ");
		if (zone != null) {
			builder.append("zone=").append(zone).append(", ");
		}
		if (user != null) {
			builder.append("user=").append(user).append(", ");
		}
		if (resource != null) {
			builder.append("resource=").append(resource);
		}
		builder.append("]");
		return builder.toString();
	}

}
