/*
 * $Id: UserDelegation.java,v 1.4 2005/01/19 20:33:04 laddi Exp $
 * Created on 19.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package se.agura.applications.presentation;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import javax.ejb.RemoveException;

import se.agura.AguraConstants;

import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Table;
import com.idega.presentation.text.Break;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;


/**
 * Last modified: $Date: 2005/01/19 20:33:04 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.4 $
 */
public class UserDelegation extends ApplicationsBlock {

	private static final String PARAMETER_GROUP = "ud_group_id";
	private static final String PARAMETER_ADD_USERS = "ud_add_users";
	private static final String PARAMETER_CONTAINED_USERS = "ud_contained_users";
	
	private Image iButtonImage;

	/* (non-Javadoc)
	 * @see se.agura.applications.presentation.ApplicationsBlock#present(com.idega.presentation.IWContext)
	 */
	public void present(IWContext iwc) {
		if (iwc.isLoggedOn()) {
			try {
				User user = iwc.getCurrentUser();
				Group group = user.getPrimaryGroup();
				parse(iwc, group);
				
				if (!group.getGroupType().equals(AguraConstants.GROUP_TYPE_EMPLOYEES) && !group.getGroupType().equals(AguraConstants.GROUP_TYPE_ASSISTANTS) && !group.getGroupType().equals(AguraConstants.GROUP_TYPE_SUBSTITUTES)) {
					Form form = new Form();
					form.addParameter(PARAMETER_GROUP, group.getPrimaryKey().toString());
					
					String[] substitutesType = { AguraConstants.GROUP_TYPE_SUBSTITUTES };
					Collection substitutes = group.getChildGroups(substitutesType, true);
					if (substitutes != null && !substitutes.isEmpty()) {
						Collection users = new TreeSet();
						Iterator iter = substitutes.iterator();
						while (iter.hasNext()) {
							users.addAll(getUserBusiness(iwc).getUsersInPrimaryGroup((Group) iter.next()));
						}

						form.add(getHeader(getResourceBundle().getLocalizedString("substitutes", "Substitutes")));
						form.add(new Break());
						form.add(getUserTable(users, group));
						form.add(new Break());
					}
					
					SubmitButton submit = null;
					if (iButtonImage != null) {
						submit = new SubmitButton(iButtonImage);
					}
					else {
						submit = (SubmitButton) getButton(new SubmitButton("delegate", "Delegate"));
					}
					form.add(submit);
					add(form);
				}
				else {
					add(getResourceBundle().getLocalizedString("can_not_delegate", "You don't have permission to delegate authority to other users."));
				}
			}
			catch (RemoteException re) {
				log(re);
			}
		}
	}
	
	private Table getUserTable(Collection users, Group primaryGroup) {
		Table table = new Table();
		table.setCellpadding(iCellpadding);
		table.setCellspacing(0);
		table.setColumns(2);
		int row = 1;
		
		Iterator iter = users.iterator();
		while (iter.hasNext()) {
			User user = (User) iter.next();
			
			CheckBox box = getCheckBox(new CheckBox(PARAMETER_ADD_USERS, user.getPrimaryKey().toString()));
			if (user.hasRelationTo(primaryGroup)) {
				table.add(new HiddenInput(PARAMETER_CONTAINED_USERS, user.getPrimaryKey().toString()), 1, row);
				box.setChecked(true);
			}
			table.add(box, 1, row);
			table.add(getText(user.getName()), 2, row++);
		}
		
		table.setCellpaddingLeft(1, 0);
		
		return table;
	}
	
	private void parse(IWContext iwc, Group group) throws RemoteException {
		if (iwc.isParameterSet(PARAMETER_GROUP)) {
			String[] usersToAdd = iwc.getParameterValues(PARAMETER_ADD_USERS);
			String[] usersContained = iwc.getParameterValues(PARAMETER_CONTAINED_USERS);
			
			Collection addUsers = usersToAdd != null ? Arrays.asList(usersToAdd) : new ArrayList();
			Collection containedUsers = usersContained != null ? Arrays.asList(usersContained) : new ArrayList();
			
			Collection removeUsers = new ArrayList(containedUsers);
			removeUsers.removeAll(addUsers);
			addUsers.removeAll(containedUsers);
			
			Iterator iter = removeUsers.iterator();
			while (iter.hasNext()) {
				String userID = (String) iter.next();
				try {
					User element = getUserBusiness(iwc).getUser(new Integer(userID));
					group.removeUser(element, iwc.getCurrentUser());
				}
				catch (RemoveException re) {
					log(re);
				}
			}

			iter = addUsers.iterator();
			while (iter.hasNext()) {
				String userID = (String) iter.next();
				User element = getUserBusiness(iwc).getUser(new Integer(userID));
				group.addGroup(element, new IWTimestamp().getTimestamp());
			}
		}
	}
	
	public void setButtonImage(Image buttonImage) {
		iButtonImage = buttonImage;
	}
}