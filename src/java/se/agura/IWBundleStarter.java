package se.agura;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.user.data.GroupType;
import com.idega.user.data.GroupTypeHome;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="laddi@idega.is">Thorhallur Helgason</a>
 * @version 1.0
 * Created on December 6th, 2004
 */
public class IWBundleStarter implements IWBundleStartable {

	public void start(IWBundle starterBundle) {
		insertStartData();
	}
	
	public void stop(IWBundle starterBundle) {
		// nothing to do
	}

	protected void insertStartData() {
		insertGroupType(AguraConstants.GROUP_TYPE_PRIESTS);
		insertGroupType(AguraConstants.GROUP_TYPE_ASSISTANTS);
		insertGroupType(AguraConstants.GROUP_TYPE_EMPLOYEES);
		insertGroupType(AguraConstants.GROUP_TYPE_MEETING);
		insertGroupType(AguraConstants.GROUP_TYPE_PARISH);
		insertGroupType(AguraConstants.GROUP_TYPE_PARISH_OFFICE);
		insertGroupType(AguraConstants.GROUP_TYPE_SUBSTITUTES);
		insertGroupType(AguraConstants.GROUP_TYPE_SUPERVISOR);
	}
	
	private void insertGroupType(String groupType) {
		try {
			GroupTypeHome gtHome = (GroupTypeHome) com.idega.data.IDOLookup.getHome(GroupType.class);
			GroupType grType;
			try {
				grType = gtHome.findByPrimaryKey(groupType);
			}
			catch (FinderException fe) {
				try {
					grType = gtHome.create();
					grType.setType(groupType);
					grType.setDescription("");
					grType.setVisibility(true);
					grType.store();
				}
				catch (CreateException ce) {
					ce.printStackTrace();
				}
			}
		}
		catch (IDOLookupException ile) {
			ile.printStackTrace();
		}
	}
}