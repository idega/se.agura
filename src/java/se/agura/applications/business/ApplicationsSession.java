/*
 * $Id: ApplicationsSession.java,v 1.1 2004/12/08 16:02:34 laddi Exp $
 * Created on 7.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package se.agura.applications.business;


import com.idega.business.IBOSession;


/**
 * Last modified: $Date: 2004/12/08 16:02:34 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public interface ApplicationsSession extends IBOSession {

	/**
	 * @see se.agura.applications.business.ApplicationsSessionBean#getViewType
	 */
	public String getViewType() throws java.rmi.RemoteException;

	/**
	 * @see se.agura.applications.business.ApplicationsSessionBean#setViewType
	 */
	public void setViewType(String viewType) throws java.rmi.RemoteException;

}
