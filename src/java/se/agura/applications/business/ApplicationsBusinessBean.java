/*
 * $Id: ApplicationsBusinessBean.java,v 1.1 2004/12/08 16:02:34 laddi Exp $
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

import com.idega.block.process.business.CaseBusinessBean;
import com.idega.data.IDOException;
import com.idega.user.data.Group;
import com.idega.user.data.User;


/**
 * Last modified: $Date: 2004/12/08 16:02:34 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public class ApplicationsBusinessBean extends CaseBusinessBean implements ApplicationsBusiness {

	private static final String VIEW_ACTIVE = "app_active";
	private static final String VIEW_INACTIVE = "app_inactive";
	
	public Collection getUserCases(User user, String viewType, int startingCase, int numberOfCases) {
		try {
			String[] statuses = null;
			if (viewType.equals(getViewTypeActive())) {
				statuses = getActiveCaseStatuses();
			}
			else {
				statuses = getInactiveCaseStatuses();
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
				statuses = getActiveCaseStatuses();
			}
			else {
				statuses = getInactiveCaseStatuses();
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
				statuses = getActiveCaseStatuses();
			}
			else {
				statuses = getInactiveCaseStatuses();
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
				statuses = getActiveCaseStatuses();
			}
			else {
				statuses = getInactiveCaseStatuses();
			}
			
			return getCaseHome().getCountOfAllCasesForGroupByStatuses(group, statuses);
		}
		catch (IDOException fe) {
			return 0;
		}
	}
	
	private String[] getActiveCaseStatuses() {
		String[] statuses = { getCaseStatusGranted().getStatus(), getCaseStatusOpen().getStatus(), getCaseStatusDenied().getStatus(), getCaseStatusMoved().getStatus(), getCaseStatusReady().getStatus() };
		return statuses;
	}
	
	private String[] getInactiveCaseStatuses() {
		String[] statuses = { getCaseStatusInactive().getStatus() };
		return statuses;
	}
	
	public String getViewTypeActive() {
		return VIEW_ACTIVE;
	}
	
	public String getViewTypeInactive() {
		return VIEW_INACTIVE;
	}
}