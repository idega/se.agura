package se.agura;

import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.user.data.GroupType;
import com.idega.user.data.GroupTypeHome;
import com.idega.user.data.Status;
import com.idega.user.data.StatusHome;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="laddi@idega.is">Thorhallur Helgason</a>
 * @version 1.0
 * Created on December 6th, 2004
 */
public class IWBundleStarter implements IWBundleStartable,AguraConstants {

	public void start(IWBundle starterBundle) {
		insertStartData();
	}
	
	public void stop(IWBundle starterBundle) {
		// nothing to do
	}

	protected void insertStartData() {
		
		insertGroupTypes();
		
		//inserts the "profession" statuses used in the detailed search
		insertStatuses();
	}

	/**
	 * 
	 */
	private void insertGroupTypes() {
		insertGroupType(GROUP_TYPE_PRIESTS);
		insertGroupType(GROUP_TYPE_ASSISTANTS);
		insertGroupType(GROUP_TYPE_EMPLOYEES);
		insertGroupType(GROUP_TYPE_MEETING);
		insertGroupType(GROUP_TYPE_PARISH);
		insertGroupType(GROUP_TYPE_PARISH_OFFICE);
		insertGroupType(GROUP_TYPE_SUBSTITUTES);
		insertGroupType(GROUP_TYPE_SUPERVISOR);
	}
	
	private void insertStatuses() {
		
		insertStatus(USER_STATUS_SUPERVISOR);
		insertStatus(USER_STATUS_ASSISTANT);
		insertStatus(USER_STATUS_WELFARE_WORKER_ASSISTANT);
		insertStatus(USER_STATUS_REAL_ESTATE_EMPLOYEE);
		insertStatus(USER_STATUS_PARISH_PEDAGOGUE);
		insertStatus(USER_STATUS_POLITICIAN);
		insertStatus(USER_STATUS_OFFICE_ECONOMY);
		insertStatus(USER_STATUS_OFFICE_COMMITTEE);
		insertStatus(USER_STATUS_OFFICE_HUMAN_RESOURCES);
		insertStatus(USER_STATUS_CREMATORIUM_EMPLOYEE);
		insertStatus(USER_STATUS_GRAVEYARD_EMPLOYEE);
		insertStatus(USER_STATUS_MUSICIAN);
		insertStatus(USER_STATUS_PRIEST);
		insertStatus(USER_STATUS_CLEANING_CREW);
		insertStatus(USER_STATUS_ATTENDANT);
	}
	
	private void insertStatus(String statusKey){
		try {
			StatusHome usHome = (StatusHome) com.idega.data.IDOLookup.getHome(Status.class);
			Status status;
			try {
				status = usHome.findByStatusKey(statusKey); 
			}
			catch (FinderException fe) {
				try {
					status = usHome.create();
					status.setStatusKey(statusKey);
					status.store();
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