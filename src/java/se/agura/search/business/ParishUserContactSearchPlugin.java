/*
 * $Id: ParishUserContactSearchPlugin.java,v 1.2 2005/04/07 18:24:42 eiki Exp $ Created
 * on Mar 18, 2005
 * 
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package se.agura.search.business;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.ejb.FinderException;
import se.agura.search.SearchConstants;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.search.business.SearchPlugin;
import com.idega.core.search.business.SearchQuery;
import com.idega.core.search.data.AdvancedSearchQuery;
import com.idega.idegaweb.IWMainApplication;
import com.idega.user.block.search.business.SearchEngine;
import com.idega.user.block.search.business.UserContactSearch;
import com.idega.user.business.GroupBusiness;
import com.idega.user.business.UserBusiness;
import com.idega.user.business.UserStatusBusiness;
import com.idega.user.data.Group;

/**
 * 
 * 
 * Last modified: $Date: 2005/04/07 18:24:42 $ by $Author: eiki $
 * 
 * Extends the UserContactSearch to support AdvancedSearchQueries. Searches
 * parishes for user contact info by workplace,profession, name etc.
 * 
 * @author <a href="mailto:eiki@idega.com">Eirikur S. Hrafnsson</a>
 * @version $Revision: 1.2 $
 */
public class ParishUserContactSearchPlugin extends UserContactSearch implements SearchPlugin, SearchConstants {

	public ParishUserContactSearchPlugin() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.block.search.business.UserContactSearch#getAdvancedSearchSupportedParameters()
	 */
	public List getAdvancedSearchSupportedParameters() {
		List parameters = new ArrayList();
		parameters.add(CONTACT_SEARCH_WORD_PARAMETER_NAME);
		parameters.add(CONTACT_PROFESSION_PARAMETER_NAME);
		parameters.add(CONTACT_WORKPLACE_PARAMETER_NAME);
		return parameters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.block.search.business.UserContactSearch#getSupportsAdvancedSearch()
	 */
	public boolean getSupportsAdvancedSearch() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.block.search.business.UserContactSearch#getSupportsSimpleSearch()
	 */
	public boolean getSupportsSimpleSearch() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.block.search.business.UserContactSearch#getUsers(com.idega.core.search.business.SearchQuery)
	 */
	protected Collection getUsers(SearchQuery searchQuery) {
		if (searchQuery instanceof AdvancedSearchQuery) {
			// SLOW but effective way of getting everybody...
			Collection users = new ArrayList();
			String searchWord = (String) searchQuery.getSearchParameters().get(CONTACT_SEARCH_WORD_PARAMETER_NAME);
			if (searchWord != null && !"".equals(searchWord)) {
				users = doSimpleSearch(users, searchWord);
				if (users == null) {
					users = new ArrayList();
				}
			}
			
			String parishGroupId = (String) searchQuery.getSearchParameters().get(CONTACT_WORKPLACE_PARAMETER_NAME);
			if (parishGroupId != null) {
				try {
//					Collection allParishPeople = getGroupBusiness().getUsersFromGroupRecursive(  
//					getGroupBusiness().getGroupByGroupID(Integer.parseInt(parishGroupId)));	
					
					//only get the parish and one level down
					Group parish = getGroupBusiness().getGroupByGroupID(Integer.parseInt(parishGroupId));
					Collection childGroups = getGroupBusiness().getChildGroups(parish);
					Collection headUsers = getGroupBusiness().getUsers(parish);
					List parishans = new ArrayList();
					parishans.addAll(headUsers);
				
					if(childGroups!=null && !childGroups.isEmpty()){
						Iterator iter = childGroups.iterator();
						while (iter.hasNext()) {
							Group group = (Group) iter.next();
							Collection children = getGroupBusiness().getUsers(group);
							if(children!=null && !children.isEmpty()){
								parishans.addAll(children);
							}
						}
						
					}			
					
					if (!users.isEmpty()) {
						users.retainAll(parishans);
					}
					else {
						users = parishans;
					}
				}
				catch (NumberFormatException e) {
					// e.printStackTrace();
					// must have been the no value option
				}
				catch (RemoteException e) {
					e.printStackTrace();
				}
				catch (FinderException e) {
					e.printStackTrace();
				}
			}
			
			
			String professionStatusId = (String) searchQuery.getSearchParameters().get(
					CONTACT_PROFESSION_PARAMETER_NAME);
			if (professionStatusId != null) {
				try {
					Collection usersWithStatus = getUserStatusBusiness().getAllUsersWithStatus(
							Integer.parseInt(professionStatusId));
					if (!users.isEmpty()) {
						users.retainAll(usersWithStatus);
					}
					else {
						users = usersWithStatus;
					}
				}
				catch (NumberFormatException e) {
					// e.printStackTrace();
					// must have been the no value option
				}
			}
			return users;
		}
		else {
			return super.getUsers(searchQuery);
		}
	}

	/**
	 * @param users
	 * @param searchWord
	 * @return
	 */
	protected Collection doSimpleSearch(Collection users, String searchWord) {
		try {
			SearchEngine userSearch = (SearchEngine) IBOLookup.getServiceInstance(iwma.getIWApplicationContext(), SearchEngine.class);
			searchWord = searchWord.replace('*', '%');
			users = userSearch.getSimpleSearchResults(searchWord);
		}
		catch (IBOLookupException e) {
			e.printStackTrace();
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		return users;
	}

	protected UserStatusBusiness getUserStatusBusiness() {
		try {
			return (UserStatusBusiness) IBOLookup.getServiceInstance(
					IWMainApplication.getDefaultIWApplicationContext(), UserStatusBusiness.class);
		}
		catch (IBOLookupException ible) {
			throw new IBORuntimeException(ible);
		}
	}

	protected UserBusiness getUserBusiness() {
		try {
			return (UserBusiness) IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(),
					UserBusiness.class);
		}
		catch (IBOLookupException ible) {
			throw new IBORuntimeException(ible);
		}
	}

	protected GroupBusiness getGroupBusiness() {
		try {
			return (GroupBusiness) IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(),
					GroupBusiness.class);
		}
		catch (IBOLookupException ible) {
			throw new IBORuntimeException(ible);
		}
	}
}
