/*
 * $Id: ApplicationsBusinessHome.java,v 1.2 2004/12/13 23:49:55 laddi Exp $
 * Created on 13.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package se.agura.applications.business;



import com.idega.business.IBOHome;


/**
 * Last modified: $Date: 2004/12/13 23:49:55 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.2 $
 */
public interface ApplicationsBusinessHome extends IBOHome {

	public ApplicationsBusiness create() throws javax.ejb.CreateException, java.rmi.RemoteException;

}
