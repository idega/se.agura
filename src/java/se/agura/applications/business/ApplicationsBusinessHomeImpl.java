/*
 * $Id: ApplicationsBusinessHomeImpl.java,v 1.1 2004/12/08 16:02:34 laddi Exp $
 * Created on 8.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package se.agura.applications.business;



import com.idega.business.IBOHomeImpl;


/**
 * Last modified: $Date: 2004/12/08 16:02:34 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public class ApplicationsBusinessHomeImpl extends IBOHomeImpl implements ApplicationsBusinessHome {

	protected Class getBeanInterfaceClass() {
		return ApplicationsBusiness.class;
	}

	public ApplicationsBusiness create() throws javax.ejb.CreateException {
		return (ApplicationsBusiness) super.createIBO();
	}

}
