/*
 * $Id: ApplicationsBusinessHomeImpl.java,v 1.4 2005/02/14 10:57:56 laddi Exp $
 * Created on 14.2.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package se.agura.applications.business;




import com.idega.business.IBOHomeImpl;


/**
 * Last modified: $Date: 2005/02/14 10:57:56 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.4 $
 */
public class ApplicationsBusinessHomeImpl extends IBOHomeImpl implements ApplicationsBusinessHome {

	protected Class getBeanInterfaceClass() {
		return ApplicationsBusiness.class;
	}

	public ApplicationsBusiness create() throws javax.ejb.CreateException {
		return (ApplicationsBusiness) super.createIBO();
	}

}
