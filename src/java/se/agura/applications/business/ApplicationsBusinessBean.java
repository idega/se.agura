/*
 * $Id: ApplicationsBusinessBean.java,v 1.9 2005/02/23 08:49:46 laddi Exp $
 * Created on 7.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package se.agura.applications.business;

import java.io.File;
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
import com.idega.idegaweb.IWBundle;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.User;


/**
 * Last modified: $Date: 2005/02/23 08:49:46 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.9 $
 */
public class ApplicationsBusinessBean extends CaseBusinessBean implements ApplicationsBusiness {

  protected final static String IW_BUNDLE_IDENTIFIER = "se.agura";

	private static final String VIEW_ACTIVE = "app_active";
	private static final String VIEW_INACTIVE = "app_inactive";
	
	private static String DEFAULT_SMTP_MAILSERVER = "mail.agurait.com";
	private static String PROP_SYSTEM_SMTP_MAILSERVER = "messagebox_smtp_mailserver";
	private static String PROP_MESSAGEBOX_FROM_ADDRESS = "messagebox_from_mailaddress";
	private static String DEFAULT_MESSAGEBOX_FROM_ADDRESS = "no-reply@aguraintra.se";

	protected String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}

	public User getSupervisor(Group parish) {
		Collection supervisorGroups = parish.getChildGroups(new String[]{AguraConstants.GROUP_TYPE_SUPERVISOR}, true);
		Iterator iter = supervisorGroups.iterator();
		while (iter.hasNext()) {
			Group group = (Group) iter.next();
			try {
				Collection users = getUserBusiness().getUsersInGroup(group);
				Iterator iterator = users.iterator();
				while (iterator.hasNext()) {
					return (User) iterator.next();
				}
			}
			catch (RemoteException re) {
				throw new IBORuntimeException(re);
			}
		}
		return null;
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

	protected void sendMessage(String email, String cc, String subject, String body, File attachment) {
		String receiver = email.trim();
		String mailServer = DEFAULT_SMTP_MAILSERVER;
		String fromAddress = DEFAULT_MESSAGEBOX_FROM_ADDRESS;
		try {
			IWBundle iwb = getIWApplicationContext().getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER);
			mailServer = iwb.getProperty(PROP_SYSTEM_SMTP_MAILSERVER, DEFAULT_SMTP_MAILSERVER);
			fromAddress = iwb.getProperty(PROP_MESSAGEBOX_FROM_ADDRESS, DEFAULT_MESSAGEBOX_FROM_ADDRESS);
		}
		catch (Exception e) {
			System.err.println("MessageBusinessBean: Error getting mail property from bundle");
			e.printStackTrace();
		}

		try {
			if (attachment == null) {
				com.idega.util.SendMail.send(fromAddress, receiver, cc != null ? cc : "", "", mailServer, subject, body);
			}
			else {
				com.idega.util.SendMail.send(fromAddress, receiver, cc != null ? cc : "", "", mailServer, subject, body, attachment);
			}
		}
		catch (javax.mail.MessagingException me) {
			System.err.println("Error sending mail to address: " + email);
			me.printStackTrace(System.err);
		}
	}
}