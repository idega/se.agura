/*
 * $Id: ParishUserContactSearchPlugin.java,v 1.6 2005/07/01 15:42:59 eiki Exp $
 * Created on Mar 18, 2005
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.FinderException;
import se.agura.AguraConstants;
import se.agura.applications.business.ApplicationsBusiness;
import se.agura.search.SearchConstants;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.contact.data.Phone;
import com.idega.core.search.business.SearchPlugin;
import com.idega.core.search.business.SearchQuery;
import com.idega.core.search.data.AdvancedSearchQuery;
import com.idega.core.search.data.BasicSearchResult;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.user.block.search.business.SearchEngine;
import com.idega.user.block.search.business.UserContactSearch;
import com.idega.user.business.GroupBusiness;
import com.idega.user.business.NoPhoneFoundException;
import com.idega.user.business.UserBusiness;
import com.idega.user.business.UserStatusBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.user.data.UserStatus;

/**
 * 
 * 
 * Last modified: $Date: 2005/07/01 15:42:59 $ by $Author: eiki $
 * 
 * Extends the UserContactSearch to support AdvancedSearchQueries. Searches
 * parishes for user contact info by workplace,profession, name etc.
 * 
 * @author <a href="mailto:eiki@idega.com">Eirikur S. Hrafnsson</a>
 * @version $Revision: 1.6 $
 */
public class ParishUserContactSearchPlugin extends UserContactSearch implements SearchPlugin, SearchConstants {

	protected final static String IW_BUNDLE_IDENTIFIER = "se.agura";
	public static final String USER_IW_BUNDLE_IDENTIFIER = "com.idega.user";
	IWResourceBundle iwrb;
	IWResourceBundle userIwrb;

	public ParishUserContactSearchPlugin() {
		super();
		IWMainApplication iwma = IWMainApplication.getDefaultIWMainApplication();
		IWContext iwc = IWContext.getInstance();
		iwrb = iwma.getBundle(IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
		userIwrb = iwma.getBundle(USER_IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
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
			boolean anyOtherSearchParameterSet = false;
			if (searchWord != null && !"".equals(searchWord)) {
				anyOtherSearchParameterSet = true;
				users = doSimpleSearch(users, searchWord);
				if (users == null) {
					users = new ArrayList();
				}
			}
			String parishGroupId = (String) searchQuery.getSearchParameters().get(CONTACT_WORKPLACE_PARAMETER_NAME);
			if (parishGroupId != null && !"novalue".equals(parishGroupId)) {
				try {
					// Collection allParishPeople =
					// getGroupBusiness().getUsersFromGroupRecursive(
					// getGroupBusiness().getGroupByGroupID(Integer.parseInt(parishGroupId)));
					// only get the parish and one level down
					Group parish = getGroupBusiness().getGroupByGroupID(Integer.parseInt(parishGroupId));
					
					List parishans = addUsersFromChildGroups(parish, null, 1, 3);
					
					if (anyOtherSearchParameterSet) {
						parishans.retainAll(users);
					}
					users = parishans;
					
					anyOtherSearchParameterSet = true;
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
			if (professionStatusId != null && !"default_key".equals(professionStatusId) && !"".equals(professionStatusId)) {
				try {
					Collection usersWithStatus = getUserStatusBusiness().getAllUsersWithStatus(
							Integer.parseInt(professionStatusId));
					if (anyOtherSearchParameterSet) {
						usersWithStatus.retainAll(users);
					}
					users = usersWithStatus;
					// just in case we add other parameters
					anyOtherSearchParameterSet = true;
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
	 * @param parish
	 * @param headUsers
	 * @return
	 * @throws RemoteException
	 * @throws FinderException
	 */
	protected List addUsersFromChildGroups(Group parish, List totalUsers, int currentLevel, int finalLevel) throws RemoteException, FinderException {
		Collection parentUsers = getGroupBusiness().getUsers(parish);
		
		if(totalUsers==null){
			totalUsers = new ArrayList();
		}
		
		if(parentUsers!=null && !parentUsers.isEmpty()){
			totalUsers.addAll(parentUsers);
		}
		
		
		if( currentLevel<finalLevel ){
			Collection childGroups = getGroupBusiness().getChildGroups(parish);
			
			if (childGroups != null && !childGroups.isEmpty()) {
				Iterator iter = childGroups.iterator();
				currentLevel++;
				while (iter.hasNext()) {
					Group group = (Group) iter.next();
					totalUsers = addUsersFromChildGroups(group,totalUsers,currentLevel,finalLevel);
				}
			}
		
		}
		
		return totalUsers;
	}

	/**
	 * @param users
	 * @param searchWord
	 * @return
	 */
	protected Collection doSimpleSearch(Collection users, String searchWord) {
		try {
			SearchEngine userSearch = (SearchEngine) IBOLookup.getServiceInstance(iwma.getIWApplicationContext(),
					SearchEngine.class);
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

	protected void fillSearchResultAbstract(BasicSearchResult result, User user) {
		// we want this empty because we add all our extra info to the
		// attributes map
	}

	protected void fillSearchResultAttributesMap(BasicSearchResult result, User user) {
		Map map = new LinkedHashMap();
		try {
			// profession
			addUserProfession(user, map);
			// work phone info
			addWorkPhoneInfo(user, map);
			// add parish
			addParishName(user, map);
			// mobile phone info
			addMobilePhoneInfo(user, map);
			// description from user account info
			addUserComment(user, map);
		}
		catch (RemoteException re) {
			re.printStackTrace();
		}
		result.setSearchResultAttributes(map);
	}

	/**
	 * @param user
	 * @param map
	 */
	protected void addUserComment(User user, Map map) {
		String comment = user.getMetaData(AguraConstants.USER_PROPERTY_COMMENTS);
		if (comment != null && !"".equals(comment)) {
			map.put("comment", comment);
		}
	}

	/**
	 * @param user
	 * @param map
	 * @throws RemoteException
	 */
	protected void addWorkPhoneInfo(User user, Map map) throws RemoteException {
		// work phone
		try {
			Phone p = getUserBusiness().getUsersWorkPhone(user);
			String number = p.getNumber();
			if(number!=null && !"null".equalsIgnoreCase(number)){
				map.put("workphone", iwrb.getLocalizedString("parish.usercontact.search.work", "work") + ": "
						+ number);
			}
		}
		catch (NoPhoneFoundException npfe) {
		}
	}

	/**
	 * @param user
	 * @param map
	 * @throws RemoteException
	 */
	protected void addMobilePhoneInfo(User user, Map map) throws RemoteException {
		// mobile phone
		try {
			Phone p = getUserBusiness().getUsersMobilePhone(user);
			String number = p.getNumber();
			if(number!=null && !"null".equalsIgnoreCase(number)){
				map.put("mobile", iwrb.getLocalizedString("parish.usercontact.search.mobile", "mobile") + ": "
						+ number);
			}
		}
		catch (NoPhoneFoundException npfe) {
		}
	}

	/**
	 * @param user
	 * @param map
	 * @throws RemoteException
	 */
	protected void addUserProfession(User user, Map map) throws RemoteException {
		Collection col = getUserStatusBusiness().getAllUserStatuses(((Integer) user.getPrimaryKey()).intValue());
		if (col != null && !col.isEmpty()) {
			// basic gets the first one...this should be a multiple
			// selection box
			UserStatus status = null;
			Iterator iter = col.iterator();
			while (iter.hasNext() && status == null) {
				UserStatus temp = (UserStatus) iter.next();
				if (temp.getDateTo() == null) {
					status = temp;
				}
			}
			String statusKey = status.getStatus().getStatusKey();
			map.put("profession", userIwrb.getLocalizedString(statusKey, statusKey));
		}
	}

	/**
	 * @param user
	 * @param map
	 */
	protected void addParishName(User user, Map map) {
		try {
			Group group = getApplicationsBusiness().getUserParish(user);
			if (group != null) {
				map.put("parish", group.getName());
			}
		}
		catch (RemoteException re) {
			re.printStackTrace();
		}
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

	protected ApplicationsBusiness getApplicationsBusiness() {
		try {
			return (ApplicationsBusiness) IBOLookup.getServiceInstance(
					IWMainApplication.getDefaultIWApplicationContext(), ApplicationsBusiness.class);
		}
		catch (IBOLookupException ible) {
			throw new IBORuntimeException(ible);
		}
	}
}
