package com.cognifide.maven.plugins.crx;

/**
 * AcHandling - Enum to represent the Access Control Options for package installs for CQ via command. According to Adobe, only supported options are:
 *
 * Ignore - default setting,  ignores the packaged access control and leaves the target unchanged.
 * Clear -  clears all access control on the target system.
 * Overwrite - applies the access control provided with the package to the target. This also removes the existing access controls.
 *
 * See for more details - http://dev.day.com/docs/en/crx/current/how_to/package_manager.html#Installing packages (CLI)
 */
public enum AcHandling {

	IGNORE("ignore"),
	CLEAR("clear"),
	OVERWRITE("overwrite");

	private final String id;

	private AcHandling(String id){
		this.id = id;
	}

	public static AcHandling ofId(String id){
		for(AcHandling handle : AcHandling.values()){
			if(handle.getId().equalsIgnoreCase(id)){
				return handle;
			}
		}
		return null;
	}

	public String getId(){
		return this.id;
	}
}
