/*
 * $Id: ApplicationsBusinessHomeImpl.java,v 1.5 2005/06/16 12:34:42 laddi Exp $
 * Created on Jun 16, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package se.agura.applications.business;

import com.idega.business.IBOHomeImpl;


/**
 * Last modified: $Date: 2005/06/16 12:34:42 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.5 $
 */
public class ApplicationsBusinessHomeImpl extends IBOHomeImpl implements ApplicationsBusinessHome {

	protected Class getBeanInterfaceClass() {
		return ApplicationsBusiness.class;
	}

	public ApplicationsBusiness create() throws javax.ejb.CreateException {
		return (ApplicationsBusiness) super.createIBO();
	}
}
