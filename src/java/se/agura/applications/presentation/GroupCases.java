/*
 * $Id: GroupCases.java,v 1.1 2004/12/08 16:02:34 laddi Exp $
 * Created on 7.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package se.agura.applications.presentation;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.idega.block.process.data.Case;
import com.idega.core.builder.data.ICPage;
import com.idega.presentation.CollectionNavigator;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;



/**
 * Last modified: $Date: 2004/12/08 16:02:34 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public class GroupCases extends UserCases {

	protected Table getCaseTable(IWContext iwc) {
		CollectionNavigator navigator = getCollectionNavigator(iwc);
		int startingEntry = navigator.getStart(iwc);
		Collection cases = getCases(iwc, startingEntry);
		int numberOfCases = cases.size();
		if (numberOfCases > 0) {
			Table table = new Table();
			table.setWidth(iWidth);
			table.setCellpadding(iCellpadding);
			table.setCellspacing(0);
			int column = 1;
			int row = 1;
			
			table.add(getHeader(getResourceBundle().getLocalizedString("applications.nr", "Nr.")), column++, row);
			table.add(getHeader(getResourceBundle().getLocalizedString("applications.type", "Type")), column++, row);
			table.add(getHeader(getResourceBundle().getLocalizedString("applications.user", "User")), column++, row);
			table.add(getHeader(getResourceBundle().getLocalizedString("applications.date", "Date")), column++, row);
			table.add(getHeader(getResourceBundle().getLocalizedString("applications.status", "Status")), column++, row);
			if (iHeaderRowStyleClass != null) {
				table.setRowStyleClass(row, iHeaderRowStyleClass);
			}
			
			Iterator iter = cases.iterator();
			while (iter.hasNext()) {
				row++;
				column = 1;
				
				Case element = (Case) iter.next();
				User owner = element.getOwner();
				IWTimestamp created = new IWTimestamp(element.getCreated());
				String code = element.getCode();
				String status = element.getStatus();
				
				ICPage page = getPage(code, status);
				if (page != null) {
					Link link = getLink(element.getPrimaryKey().toString());
					String parameter = getParameter(code);
					if (parameter != null) {
						link.addParameter(parameter, element.getPrimaryKey().toString());
					}
					link.setPage(page);
					table.add(link, column++, row);
				}
				else {
					table.add(getText(element.getPrimaryKey().toString()), column++, row);
				}
				
				table.add(getText(getResourceBundle().getLocalizedString("case_code."+code, code)), column++, row);
				table.add(getText(owner.getName()), column++, row);
				table.add(getText(created.getLocaleDate(iwc.getCurrentLocale(), IWTimestamp.SHORT)), column++, row);
				table.add(getText(getResourceBundle().getLocalizedString("case_status."+status, status)), column++, row);
				
				if (iTextRowStyleClass != null) {
					table.setRowStyleClass(row, iTextRowStyleClass);
				}
			}
			
			return table;
		}
		else {
			Table table = new Table(1, 1);
			table.setCellpaddingAndCellspacing(0);
			table.add(getText(getResourceBundle().getLocalizedString("applications.no_cases_found", "No cases found")));
			return table;
		}
	}
	
	protected Collection getCases(IWContext iwc, int startingEntry) {
		try {
			User user = iwc.getCurrentUser();
			Group group = user.getPrimaryGroup();
			return getBusiness(iwc).getGroupCases(group, getSession(iwc).getViewType(), startingEntry, iNumberOfEntries);
		}
		catch (RemoteException re) {
			log(re);
			return new ArrayList();
		}
		catch (NullPointerException npe) {
			log(npe);
			return new ArrayList();
		}
	}
	
	protected int getCaseCount(IWContext iwc) {
		try {
			User user = iwc.getCurrentUser();
			Group group = user.getPrimaryGroup();
			return getBusiness(iwc).getNumberOfGroupCases(group, getSession(iwc).getViewType());
		}
		catch (RemoteException re) {
			log(re);
			return 0;
		}
		catch (NullPointerException npe) {
			log(npe);
			return 0;
		}
	}
	
}