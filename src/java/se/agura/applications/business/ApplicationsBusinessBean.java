/*
 * $Id: ApplicationsBusinessBean.java,v 1.4 2004/12/21 14:02:18 laddi Exp $
 * Created on 7.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package se.agura.applications.business;

import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.FinderException;

import se.agura.AguraConstants;

import com.idega.block.process.business.CaseBusinessBean;
import com.idega.data.IDOException;
import com.idega.user.data.Group;
import com.idega.user.data.User;


/**
 * Last modified: $Date: 2004/12/21 14:02:18 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.4 $
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
	
	public Collection getGroupCases(Group group, String viewType, int startingCase, int numberOfCases) {
		try {
			String[] statuses = null;
			if (viewType.equals(getViewTypeActive())) {
				statuses = getActiveGroupCaseStatuses();
			}
			else {
				statuses = getInactiveGroupCaseStatuses();
			}
			
			return getCaseHome().findAllCasesForGroupByStatuses(group, statuses, startingCase, numberOfCases);
		}
		catch (FinderException fe) {
			return new ArrayList();
		}
	}
	
	public int getNumberOfGroupCases(Group group, String viewType) {
		try {
			String[] statuses = null;
			if (viewType.equals(getViewTypeActive())) {
				statuses = getActiveGroupCaseStatuses();
			}
			else {
				statuses = getInactiveGroupCaseStatuses();
			}
			
			return getCaseHome().getCountOfAllCasesForGroupByStatuses(group, statuses);
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
		return getParish(user.getPrimaryGroup());
	}
	
	private Group getParish(Group group) {
		if (group != null) {
			if (group.getGroupType().equals(AguraConstants.GROUP_TYPE_PARISH)) {
				return group;
			}
			return getParish((Group) group.getParentNode());
		}
		return null;
	}
}