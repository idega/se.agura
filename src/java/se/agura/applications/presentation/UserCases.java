/*
 * $Id: UserCases.java,v 1.3 2004/12/09 14:04:12 laddi Exp $
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.idega.block.process.business.CaseBusiness;
import com.idega.block.process.business.CaseCodeManager;
import com.idega.block.process.data.Case;
import com.idega.block.process.data.CaseStatus;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.builder.data.ICPage;
import com.idega.event.IWPageEventListener;
import com.idega.idegaweb.IWException;
import com.idega.presentation.CollectionNavigator;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Break;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.user.data.Group;
import com.idega.util.IWTimestamp;


/**
 * Last modified: $Date: 2004/12/09 14:04:12 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.3 $
 */
public class UserCases extends ApplicationsBlock implements IWPageEventListener {

	private static final String PARAMETER_VIEW_TYPE = "app_view_type";
	
	private Map pageMap;
	protected int iNumberOfEntries = 10;
	
	protected String iHeaderRowStyleClass;
	protected String iTextRowStyleClass;
	
	/* (non-Javadoc)
	 * @see se.agura.applications.presentation.ApplicationsBlock#present(com.idega.presentation.IWContext)
	 */
	public void present(IWContext iwc) {
		add(getStatusNavigator(iwc));
		add(new Break());
		add(getCaseTable(iwc));
	}
	
	protected Table getCaseTable(IWContext iwc) {
		CollectionNavigator navigator = getCollectionNavigator(iwc);
		
		Table table = new Table();
		table.setWidth(iWidth);
		table.setCellpadding(iCellpadding);
		table.setCellspacing(0);
		int column = 1;
		int row = 1;
		
		table.add(getHeader(getResourceBundle().getLocalizedString("applications.nr", "Nr.")), column++, row);
		table.add(getHeader(getResourceBundle().getLocalizedString("applications.type", "Type")), column++, row);
		table.add(getHeader(getResourceBundle().getLocalizedString("applications.date", "Date")), column++, row);
		table.add(getHeader(getResourceBundle().getLocalizedString("applications.handler", "Handler")), column++, row);
		table.add(getHeader(getResourceBundle().getLocalizedString("applications.status", "Status")), column++, row);
		if (iHeaderRowStyleClass != null) {
			table.setRowStyleClass(row, iHeaderRowStyleClass);
		}
		
		int startingEntry = navigator.getStart(iwc);
		Collection cases = getCases(iwc, startingEntry);
		Iterator iter = cases.iterator();
		while (iter.hasNext()) {
			try {
				row++;
				column = 1;
				
				Case element = (Case) iter.next();
				Group handler = element.getHandler();
				IWTimestamp created = new IWTimestamp(element.getCreated());
				String code = element.getCode();
				CaseStatus caseStatus = element.getCaseStatus();
				String status = caseStatus.getStatus();
				CaseBusiness caseBusiness = CaseCodeManager.getInstance().getCaseBusinessOrDefault(element.getCaseCode(), iwc);
				
				ICPage page = getPage(code, status);
				if (page != null) {
					Link link = getLink(element.getPrimaryKey().toString());
					String parameter = caseBusiness.getPrimaryKeyParameter();
					if (parameter != null) {
						link.addParameter(parameter, element.getPrimaryKey().toString());
					}
					link.setPage(page);
					table.add(link, column++, row);
				}
				else {
					table.add(getText(element.getPrimaryKey().toString()), column++, row);
				}
				
				table.add(getText(caseBusiness.getLocalizedCaseDescription(element, iwc.getCurrentLocale())), column++, row);
				table.add(getText(created.getLocaleDate(iwc.getCurrentLocale(), IWTimestamp.SHORT)), column++, row);
				if (handler != null) {
					table.add(getText(handler.getName()), column++, row);
				}
				else {
					table.add(getText("-"), column++, row);
				}
				table.add(getText(caseBusiness.getLocalizedCaseStatusDescription(caseStatus, iwc.getCurrentLocale())), column++, row);
				
				if (iTextRowStyleClass != null) {
					table.setRowStyleClass(row, iTextRowStyleClass);
				}
			}
			catch (IBOLookupException ile) {
				log(ile);
			}
			catch (RemoteException re) {
				log(re);
			}
		}
		
		return table;
	}
	
	private Table getStatusNavigator(IWContext iwc) {
		Table table = new Table(1, 1);
		table.setCellpaddingAndCellspacing(0);
		
		Text text = getText(Text.NON_BREAKING_SPACE + "|" + Text.NON_BREAKING_SPACE);
		try {
			table.add(getObject(iwc, getResourceBundle().getLocalizedString("applications.active_cases", "Active cases"), getBusiness(iwc).getViewTypeActive()), 1, 1);
			table.add(text, 1, 1);
			table.add(getObject(iwc, getResourceBundle().getLocalizedString("favorite.inactive_cases", "Inactive cases"), getBusiness(iwc).getViewTypeInactive()), 1, 1);
		}
		catch (RemoteException re) {
			log(re);
		}
		return table;
	}
	
	protected CollectionNavigator getCollectionNavigator(IWContext iwc) {
		int size = getCaseCount(iwc);
		CollectionNavigator navigator = new CollectionNavigator(size);
		navigator.setPadding(0);
		navigator.setUseShortText(true);
		navigator.setNumberOfEntriesPerPage(iNumberOfEntries);
		navigator.setWidth(iWidth);
		if (iTextStyleClass != null) {
			navigator.setTextStyle(iTextStyleClass);
		}
		if (iLinkStyleClass != null) {
			navigator.setLinkStyle(iLinkStyleClass);
		}
		
		return navigator;
	}
	
	private PresentationObject getObject(IWContext iwc, String name, String viewType) {
		try {
			if (getSession(iwc).getViewType().equals(viewType)) {
				return new Text(name);
			}
			else {
				Link link = getLink(name);
				link.setEventListener(UserCases.class);
				link.addParameter(PARAMETER_VIEW_TYPE, viewType);
				return link;
			}
		}
		catch (RemoteException re) {
			throw new IBORuntimeException(re);
		}
	}

	protected Collection getCases(IWContext iwc, int startingEntry) {
		try {
			return getBusiness(iwc).getUserCases(iwc.getCurrentUser(), getSession(iwc).getViewType(), startingEntry, iNumberOfEntries);
		}
		catch (RemoteException re) {
			log(re);
			return new ArrayList();
		}
	}
	
	protected int getCaseCount(IWContext iwc) {
		try {
			return getBusiness(iwc).getNumberOfUserCases(iwc.getCurrentUser(), getSession(iwc).getViewType());
		}
		catch (RemoteException re) {
			log(re);
			return 0;
		}
	}
	
	protected ICPage getPage(String caseCode, String caseStatus) {
		if (pageMap != null) {
			Map statusMap = (Map) pageMap.get(caseCode);
			if (statusMap != null) {
				return (ICPage) statusMap.get(caseStatus);
			}
		}
		return null;
	}
	
	public void setPage(String caseCode, String caseStatus, ICPage page) {
		if (pageMap == null) {
			pageMap = new HashMap();
		}
		
		Map statusMap = (Map) pageMap.get(caseCode);
		if (statusMap == null) {
			statusMap = new HashMap();
		}
		statusMap.put(caseStatus, page);
		pageMap.put(caseCode, statusMap);
	}
	
	/* (non-Javadoc)
	 * @see com.idega.event.IWPageEventListener#actionPerformed(com.idega.presentation.IWContext)
	 */
	public boolean actionPerformed(IWContext iwc) throws IWException {
		try {
			getSession(iwc).setViewType(iwc.getParameter(PARAMETER_VIEW_TYPE));
			return true;
		}
		catch (RemoteException re) {
			log(re);
		}
		return false;
	}
	
	/**
	 * @param numberOfEntries The numberOfEntries to set.
	 */
	public void setNumberOfEntries(int numberOfEntries) {
		iNumberOfEntries = numberOfEntries;
	}
	
	/**
	 * @param headerRowStyleClass The headerRowStyleClass to set.
	 */
	public void setHeaderRowStyleClass(String headerRowStyleClass) {
		iHeaderRowStyleClass = headerRowStyleClass;
	}
	
	/**
	 * @param textRowStyleClass The textRowStyleClass to set.
	 */
	public void setTextRowStyleClass(String textRowStyleClass) {
		iTextRowStyleClass = textRowStyleClass;
	}
}