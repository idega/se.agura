/*
 * $Id: ApplicationsSessionBean.java,v 1.1 2004/12/08 16:02:34 laddi Exp $
 * Created on 7.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package se.agura.applications.business;

import java.rmi.RemoteException;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.business.IBOSessionBean;


/**
 * Last modified: $Date: 2004/12/08 16:02:34 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public class ApplicationsSessionBean extends IBOSessionBean  implements ApplicationsSession{

	private String iViewType = null;

	private ApplicationsBusiness getBusiness() {
		try {
			return (ApplicationsBusiness) IBOLookup.getServiceInstance(getIWApplicationContext(), ApplicationsBusiness.class);
		}
		catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}
	
	/**
	 * @return Returns the viewType.
	 */
	public String getViewType() {
		if (iViewType == null) {
			try {
				iViewType = getBusiness().getViewTypeActive();
			}
			catch (RemoteException re) {
				log(re);
			}
		}
		return iViewType;
	}
	
	/**
	 * @param viewType The viewType to set.
	 */
	public void setViewType(String viewType) {
		iViewType = viewType;
	}
}