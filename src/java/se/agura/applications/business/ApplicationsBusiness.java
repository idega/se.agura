/*
 * $Id: ApplicationsBusiness.java,v 1.5 2005/02/14 10:57:56 laddi Exp $
 * Created on 14.2.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
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
 * Last modified: $Date: 2005/02/14 10:57:56 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.5 $
 */
public interface ApplicationsBusiness extends CaseBusiness {

	/**
	 * @see se.agura.applications.business.ApplicationsBusinessBean#getSupervisor
	 */
	public User getSupervisor(Group parish) throws java.rmi.RemoteException;

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
	public Collection getGroupCases(Collection groups, String viewType, int startingCase, int numberOfCases) throws java.rmi.RemoteException;

	/**
	 * @see se.agura.applications.business.ApplicationsBusinessBean#getNumberOfGroupCases
	 */
	public int getNumberOfGroupCases(Collection groups, String viewType) throws java.rmi.RemoteException;

	/**
	 * @see se.agura.applications.business.ApplicationsBusinessBean#getViewTypeActive
	 */
	public String getViewTypeActive() throws java.rmi.RemoteException;

	/**
	 * @see se.agura.applications.business.ApplicationsBusinessBean#getViewTypeInactive
	 */
	public String getViewTypeInactive() throws java.rmi.RemoteException;

	/**
	 * @see se.agura.applications.business.ApplicationsBusinessBean#getUserParish
	 */
	public Group getUserParish(User user) throws java.rmi.RemoteException;

}
