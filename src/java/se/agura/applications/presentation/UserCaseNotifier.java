/*
 * $Id: UserCaseNotifier.java,v 1.2 2005/01/11 10:04:25 laddi Exp $
 * Created on 13.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package se.agura.applications.presentation;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.idega.block.process.business.CaseBusiness;
import com.idega.block.process.business.CaseCodeManager;
import com.idega.block.process.data.Case;
import com.idega.block.process.data.CaseCode;
import com.idega.block.process.data.CaseStatus;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.builder.data.ICPage;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;


/**
 * Last modified: $Date: 2005/01/11 10:04:25 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.2 $
 */
public class UserCaseNotifier extends ApplicationsBlock {

	private Map iCodeMap;
	
	/* (non-Javadoc)
	 * @see se.agura.applications.presentation.ApplicationsBlock#present(com.idega.presentation.IWContext)
	 */
	public void present(IWContext iwc) {
		int numberOfCases = 0;
		try {
			numberOfCases = getBusiness(iwc).getNumberOfNotifiableUserCases(iwc.getCurrentUser());
		}
		catch (RemoteException re) {
			log(re);
		}
		
		if (numberOfCases > 0) {
			showNotifier(iwc);
		}
		else {
			add(getText(getResourceBundle().getLocalizedString("case_notifier.no_cases_with_changed_status", "No status changes on your ongoing errands.")));
		}
	}
	
	public void showNotifier(IWContext iwc) {
		Table table = new Table();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setWidth(Table.HUNDRED_PERCENT);
		int row = 1;
		
		Collection cases = null;
		try {
			cases = getBusiness(iwc).getNotifiableUserCases(iwc.getCurrentUser());
		}
		catch (RemoteException re) {
			throw new IBORuntimeException(re);
		}
		
		table.setCellpadding(iCellpadding);
		Iterator iter = cases.iterator();
		while (iter.hasNext()) {
			Case element = (Case) iter.next();
			try {
				CaseCode code = element.getCaseCode();
				CaseStatus status = element.getCaseStatus();
				CaseBusiness caseBusiness = CaseCodeManager.getInstance().getCaseBusinessOrDefault(code, iwc);
				
				ICPage page = getPage(code.getCode());
				if (page != null) {
					Link link = getLink(getResourceBundle().getLocalizedString("case_notifier." + code.getCode() + "." + status.getStatus(), "Your application for " + code.getDescription() + " has been " + status.getDescription()));
					String parameter = caseBusiness.getPrimaryKeyParameter();
					if (parameter != null) {
						link.addParameter(parameter, element.getPrimaryKey().toString());
					}
					link.setPage(page);

					table.add(link, 1, row++);
				}
			}
			catch (IBOLookupException ile) {
				log(ile);
			}
			catch (RemoteException re) {
				log(re);
			}
		}
		table.setCellpaddingLeft(1, 0);
		
		add(table);
	}
	
	private ICPage getPage(String code) {
		if (iCodeMap != null) {
			return (ICPage) iCodeMap.get(code);
		}
		return null;
	}
	
	public void setPage(String code, ICPage page) {
		if (iCodeMap == null) {
			iCodeMap = new HashMap();
		}
		iCodeMap.put(code, page);
	}
}