package com.rmjtromp.chatemojis.utils.bukkit;

import java.util.Arrays;

/**
 * @author Melvin
 * @since 2.2.1
 */
public enum Version {

	V1_7,
	V1_8,
	V1_9,
	V1_10,
	V1_11,
	V1_12,
	V1_13,
	V1_14,
	V1_15,
	V1_16,
	V1_17;
	
	/**
	 * Returns the {@link Version} enumeration of the server version
	 * @return {@link Version}
	 */
	private static Version serverVersion = null;
	public static Version getServerVersion() {
		if(serverVersion != null) return serverVersion;
		String v = BukkitUtils.getServerVersion().substring(1);
		for(Version version : Version.values()) {
			if(v.startsWith(version.toString().replace(".", "_"))) return (serverVersion = version);
		}
		return null;
	}
	
	private Version() {}
	
	/**
	 * Returns true is the version is older <u>than</u> 1.13
	 * @return Whether or not version is a legacy version
	 */
	public boolean isLegacy() {
		return isOlderThan(V1_13);
	}
	
	/**
	 * Returns whether or not the version instance is older than
	 * the parameter version.
	 * @param version to compare to
	 * @return Whether or not version is older
	 */
	public boolean isOlderThan(Version version) {
		return Arrays.asList(Version.values()).indexOf(this) < Arrays.asList(Version.values()).indexOf(version);
	}
	
	/**
	 * Returns whether or not the version instance is newer than
	 * the parameter version.
	 * @param version to compare to
	 * @return Whether or not version is newer
	 */
	public boolean isNewerThan(Version version) {
		return Arrays.asList(Version.values()).indexOf(this) > Arrays.asList(Version.values()).indexOf(version);
	}
	

	/**
	 * Returns the server version as a beautified string. (Ex. 1.16)
	 * @return Beautified Version String
	 */
	@Override
	public String toString() {
		return super.toString().substring(1).replace("_", ".");
	}
	
}