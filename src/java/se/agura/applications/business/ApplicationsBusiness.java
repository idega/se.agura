/*
 * $Id: ApplicationsBusiness.java,v 1.2 2004/12/13 23:49:55 laddi Exp $
 * Created on 13.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package se.agura.applications.business;

import java.util.Collection;

import com.idega.block.process.business.CaseBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.User;


/**
 * Last modified: $Date: 2004/12/13 23:49:55 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.2 $
 */
public interface ApplicationsBusiness extends CaseBusiness {

	/**
	 * @see se.agura.applications.business.ApplicationsBusinessBean#getNotifiableUserCases
	 */
	public Collection getNotifiableUserCases(User user) throws java.rmi.RemoteException;

	/**
	 * @see se.agura.applications.business.ApplicationsBusinessBean#getNumberOfNotifiableUserCases
	 */
	public int getNumberOfNotifiableUserCases(User user) throws java.rmi.RemoteException;

	/**
	 * @see se.agura.applications.business.ApplicationsBusinessBean#getUserCases
	 */
	public Collection getUserCases(User user, String viewType, int startingCase, int numberOfCases) throws java.rmi.RemoteException;

	/**
	 * @see se.agura.applications.business.ApplicationsBusinessBean#getNumberOfUserCases
	 */
	public int getNumberOfUserCases(User user, String viewType) throws java.rmi.RemoteException;

	/**
	 * @see se.agura.applications.business.ApplicationsBusinessBean#getGroupCases
	 */
	public Collection getGroupCases(Group group, String viewType, int startingCase, int numberOfCases) throws java.rmi.RemoteException;

	/**
	 * @see se.agura.applications.business.ApplicationsBusinessBean#getNumberOfGroupCases
	 */
	public int getNumberOfGroupCases(Group group, String viewType) throws java.rmi.RemoteException;

	/**
	 * @see se.agura.applications.business.ApplicationsBusinessBean#getViewTypeActive
	 */
	public String getViewTypeActive() throws java.rmi.RemoteException;

	/**
	 * @see se.agura.applications.business.ApplicationsBusinessBean#getViewTypeInactive
	 */
	public String getViewTypeInactive() throws java.rmi.RemoteException;

}
