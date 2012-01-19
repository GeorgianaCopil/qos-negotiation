package jade.core;

class VersionManager {
	
	public String getVersion() {
		String version = "$Version$"; // The $ surrounded Version keyword is automatically replaced by the target doTag of build.xml
		return version;
	}
	
	public String getRevision() {
		String revision = "6427"; // The $ surrounded WCREV keyword is automatically replaced by WCREV with subversion
		return revision;
	}
	
	public String getDate() {
		String date = "2011/07/12 10:49:20"; // The $ surrounded WCDATE keyword is automatically replaced by WCREV with subversion
		return date;
	}
}
