package de.maxgb.minecraft.second_screen.shared;

public class ClientVersion {

	public static class ClientInfo {
		public final String id;
		public final int version;

		public ClientInfo(String id, int version) {
			this.id = id;
			this.version = version;
		}
	}

	public static boolean isUpdateAvailable(String id, int version) {
		if (id.endsWith("ANDROID4")) {
			if (version < 14) {
				return true;
			}
		}
		if(id.endsWith("universal")){
			if(version<1){
				return true;
			}
		}
		return false;
	}

	public static boolean isUpdateNecessary(String id, int version) {
		if (id.equals("ANDROID4")) {
			if (version < 13) {
				return true;
			}
		}
		if(id.endsWith("universal")){
			if(version<1){
				return true;
			}
		}
		return false;
	}

	public static boolean isUpdateAvailable(ClientInfo info) {
		return isUpdateAvailable(info.id, info.version);
	}
}
