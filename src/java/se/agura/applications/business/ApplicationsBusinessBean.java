/*
 * $Id: ApplicationsBusinessBean.java,v 1.6 2005/01/12 10:00:13 laddi Exp $
 * Created on 7.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package se.agura.applications.business;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.FinderException;

import se.agura.AguraConstants;

import com.idega.block.process.business.CaseBusinessBean;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.data.IDOException;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.User;


/**
 * Last modified: $Date: 2005/01/12 10:00:13 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.6 $
 */
public class ApplicationsBusinessBean extends CaseBusinessBean implements ApplicationsBusiness {

  protected final static String IW_BUNDLE_IDENTIFIER = "se.agura";

	private static final String VIEW_ACTIVE = "app_active";
	private static final String VIEW_INACTIVE = "app_inactive";
	
	protected String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}

	public Collection getNotifiableUserCases(User user) {
		try {
			String[] statuses = { getCaseStatusGranted().getStatus(), getCaseStatusDenied().getStatus() };
			return getCaseHome().findAllCasesForUserByStatuses(user, statuses, -1, -1);
		}
		catch (FinderException fe) {
			return new ArrayList();
		}
	}
	
	public int getNumberOfNotifiableUserCases(User user) {
		try {
			String[] statuses = { getCaseStatusGranted().getStatus(), getCaseStatusDenied().getStatus() };
			return getCaseHome().getCountOfAllCasesForUserByStatuses(user, statuses);
		}
		catch (IDOException ie) {
			return 0;
		}
	}
	
	public Collection getUserCases(User user, String viewType, int startingCase, int numberOfCases) {
		try {
			String[] statuses = null;
			if (viewType.equals(getViewTypeActive())) {
				statuses = getActiveUserCaseStatuses();
			}
			else {
				statuses = getInactiveUserCaseStatuses();
			}
			
			return getCaseHome().findAllCasesForUserByStatuses(user, statuses, startingCase, numberOfCases);
		}
		catch (FinderException fe) {
			return new ArrayList();
		}
	}
	
	public int getNumberOfUserCases(User user, String viewType) {
		try {
			String[] statuses = null;
			if (viewType.equals(getViewTypeActive())) {
				statuses = getActiveUserCaseStatuses();
			}
			else {
				statuses = getInactiveUserCaseStatuses();
			}
			
			return getCaseHome().getCountOfAllCasesForUserByStatuses(user, statuses);
		}
		catch (IDOException fe) {
			return 0;
		}
	}
	
	public Collection getGroupCases(Collection groups, String viewType, int startingCase, int numberOfCases) {
		try {
			String[] statuses = null;
			if (viewType.equals(getViewTypeActive())) {
				statuses = getActiveGroupCaseStatuses();
			}
			else {
				statuses = getInactiveGroupCaseStatuses();
			}
			
			return getCaseHome().findAllCasesForGroupsByStatuses(groups, statuses, startingCase, numberOfCases);
		}
		catch (FinderException fe) {
			return new ArrayList();
		}
	}
	
	public int getNumberOfGroupCases(Collection groups, String viewType) {
		try {
			String[] statuses = null;
			if (viewType.equals(getViewTypeActive())) {
				statuses = getActiveGroupCaseStatuses();
			}
			else {
				statuses = getInactiveGroupCaseStatuses();
			}
			
			return getCaseHome().getCountOfAllCasesForGroupsByStatuses(groups, statuses);
		}
		catch (IDOException fe) {
			return 0;
		}
	}
	
	private String[] getActiveUserCaseStatuses() {
		String[] statuses = { getCaseStatusGranted().getStatus(), getCaseStatusOpen().getStatus(), getCaseStatusDenied().getStatus(), getCaseStatusMoved().getStatus(), getCaseStatusReady().getStatus() };
		return statuses;
	}
	
	private String[] getInactiveUserCaseStatuses() {
		String[] statuses = { getCaseStatusInactive().getStatus() };
		return statuses;
	}
	
	private String[] getActiveGroupCaseStatuses() {
		String[] statuses = { getCaseStatusOpen().getStatus(), getCaseStatusMoved().getStatus() };
		return statuses;
	}
	
	private String[] getInactiveGroupCaseStatuses() {
		String[] statuses = { getCaseStatusDenied().getStatus(), getCaseStatusGranted().getStatus(), getCaseStatusInactive().getStatus(), getCaseStatusReady().getStatus() };
		return statuses;
	}
	
	public String getViewTypeActive() {
		return VIEW_ACTIVE;
	}
	
	public String getViewTypeInactive() {
		return VIEW_INACTIVE;
	}
	
	public Group getUserParish(User user) {
		try {
			String[] groupTypes = { AguraConstants.GROUP_TYPE_PARISH, AguraConstants.GROUP_TYPE_PARISH_OFFICE };
			Collection groups = getUserBusiness().getGroupBusiness().getParentGroupsRecursive(user, groupTypes, true);
			if (groups != null) {
				Iterator iter = groups.iterator();
				while (iter.hasNext()) {
					return (Group) iter.next();
				}
			}
		}
		catch (RemoteException re) {
			log(re);
		}
		return user.getPrimaryGroup();
	}
	
	protected UserBusiness getUserBusiness() {
		try {
			return (UserBusiness) IBOLookup.getServiceInstance(getIWApplicationContext(), UserBusiness.class);
		}
		catch (IBOLookupException ible) {
			throw new IBORuntimeException(ible);
		}
	}
}