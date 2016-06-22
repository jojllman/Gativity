package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.access;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Permission {
	private static final Logger s_logger = LoggerFactory.getLogger(Permission.class);
	
	static public enum PermissionType {
		Own, Group, All
	}
	
	private short perm;
	private byte all;
	private byte own;
	private byte group;
	
	public Permission(short perm) {
		setPermission(perm);
	}
	
	public Permission(String perm) {
		setPermission(perm);
	}
	
	public short setPermission(short perm) {
		this.perm = perm;
		all = (byte) ((perm & 0x1C) >> 6);
		own = (byte) ((perm & 0x38) >> 6);
		group = (byte) ((perm & 0x7) >> 6);
		return this.perm;
	}
	
	public String setPermission(String perm) {
		if(perm.length() != 3) {
			s_logger.warn("Permission is not right: " + perm + ", fallback to 777");
			perm = "777";
		}
		
		all = (byte) (perm.charAt(0) - '0');
		own = (byte) (perm.charAt(1) - '0');
		group = (byte) (perm.charAt(2) - '0');
		
		this.perm = (short) ((all << 6) & (own << 3) & (group));
		return perm;
	}
	
	public short getPermission() { return perm; }
	public String getPermissionString() { return all + "" + own + "" + group; }
	public boolean getReadPermission(PermissionType type) {
		switch (type) {
		case All:
			return (all & 0x4) != 0;
		case Own:
			return (own & 0x4) != 0;
		case Group:
			return (group & 0x4) != 0;
		default:
			return false;
		}
	}
	public boolean getWritePermission(PermissionType type) {
		switch (type) {
		case All:
			return (all & 0x2) != 0;
		case Own:
			return (own & 0x2) != 0;
		case Group:
			return (group & 0x2) != 0;
		default:
			return false;
		}
	}
	public boolean getModifyPermission(PermissionType type) {
		switch (type) {
		case All:
			return (all & 0x1) != 0;
		case Own:
			return (own & 0x1) != 0;
		case Group:
			return (group & 0x1) != 0;
		default:
			return false;
		}
	}
}
