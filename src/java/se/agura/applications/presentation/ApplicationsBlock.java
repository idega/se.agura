/*
 * $Id: ApplicationsBlock.java,v 1.3 2005/01/12 10:00:13 laddi Exp $
 * Created on 7.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package se.agura.applications.presentation;

import se.agura.applications.business.ApplicationsBusiness;
import se.agura.applications.business.ApplicationsSession;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.GenericButton;
import com.idega.presentation.ui.InterfaceObject;
import com.idega.presentation.ui.RadioButton;
import com.idega.user.business.UserBusiness;


/**
 * Last modified: $Date: 2005/01/12 10:00:13 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.3 $
 */
public abstract class ApplicationsBlock extends Block {

	private static final String IW_BUNDLE_IDENTIFIER = "se.agura";
	
	private IWBundle iwb;
	private IWResourceBundle iwrb;
	
	protected String iTextStyleClass;
	protected String iErrorTextStyleClass;
	protected String iLinkStyleClass;
	protected String iHeaderStyleClass;
	private String iInputStyleClass;
	private String iButtonStyleClass;
	private String iRadioStyleClass;
	
	protected String iWidth = Table.HUNDRED_PERCENT;
	protected int iCellpadding = 3;
	protected int iHeaderColumnWidth = 150;

	public void main(IWContext iwc) {
		iwb = getBundle(iwc);
		iwrb = getResourceBundle(iwc);
		
		present(iwc);
	}
	
	public abstract void present(IWContext iwc);
	
	protected ApplicationsBusiness getBusiness(IWApplicationContext iwac) {
		try {
			return (ApplicationsBusiness) IBOLookup.getServiceInstance(iwac, ApplicationsBusiness.class);
		}
		catch (IBOLookupException ible) {
			throw new IBORuntimeException(ible);
		}
	}

	protected UserBusiness getUserBusiness(IWApplicationContext iwac) {
		try {
			return (UserBusiness) IBOLookup.getServiceInstance(iwac, UserBusiness.class);
		}
		catch (IBOLookupException ible) {
			throw new IBORuntimeException(ible);
		}
	}

	protected ApplicationsSession getSession(IWUserContext iwuc) {
		try {
			return (ApplicationsSession) IBOLookup.getSessionInstance(iwuc, ApplicationsSession.class);
		}
		catch (IBOLookupException ible) {
			throw new IBORuntimeException(ible);
		}
	}

	protected Text getHeader(String string) {
		Text text = new Text(string);
		if (iHeaderStyleClass != null) {
			text.setStyleClass(iHeaderStyleClass);
		}
		return text;
	}
	
	protected Text getText(String string) {
		Text text = new Text(string);
		if (iTextStyleClass != null) {
			text.setStyleClass(iTextStyleClass);
		}
		return text;
	}
	
	protected Text getErrorText(String string) {
		Text text = new Text(string);
		if (iErrorTextStyleClass != null) {
			text.setStyleClass(iErrorTextStyleClass);
		}
		return text;
	}
	
	protected Link getLink(String string) {
		Link link = new Link(string);
		if (iLinkStyleClass != null) {
			link.setStyleClass(iLinkStyleClass);
		}
		return link;
	}
	
	protected InterfaceObject getInput(InterfaceObject input) {
		if (iInputStyleClass != null) {
			input.setStyleClass(iInputStyleClass);
		}
		return input;
	}
	
	protected GenericButton getButton(GenericButton button) {
		if (iButtonStyleClass != null) {
			button.setStyleClass(iButtonStyleClass);
		}
		return button;
	}
	
	protected RadioButton getRadioButton(RadioButton radioButton) {
		if (iRadioStyleClass != null) {
			radioButton.setStyleClass(iRadioStyleClass);
		}
		return radioButton;
	}
	
	protected CheckBox getCheckBox(CheckBox checkBox) {
		if (iRadioStyleClass != null) {
			checkBox.setStyleClass(iRadioStyleClass);
		}
		return checkBox;
	}
	
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#getBundleIdentifier()
	 */
	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}
	
	/**
	 * @return Returns the iwb.
	 */
	protected IWBundle getBundle() {
		return iwb;
	}
	
	/**
	 * @return Returns the iwrb.
	 */
	protected IWResourceBundle getResourceBundle() {
		return iwrb;
	}

	/**
	 * @param headerStyleClass The headerStyleClass to set.
	 */
	public void setHeaderStyleClass(String headerStyleClass) {
		iHeaderStyleClass = headerStyleClass;
	}
	
	/**
	 * @param linkStyleClass The linkStyleClass to set.
	 */
	public void setLinkStyleClass(String linkStyleClass) {
		iLinkStyleClass = linkStyleClass;
	}

	/**
	 * @param textStyleClass The textStyleClass to set.
	 */
	public void setTextStyleClass(String textStyleClass) {
		iTextStyleClass = textStyleClass;
	}
	/**
	 * @param cellpadding The cellpadding to set.
	 */
	public void setCellpadding(int cellpadding) {
		iCellpadding = cellpadding;
	}
	/**
	 * @param width The width to set.
	 */
	public void setWidth(String width) {
		iWidth = width;
	}
	
	public void setErrorTextStyleClass(String errorTextStyleClass) {
		iErrorTextStyleClass = errorTextStyleClass;
	}
	
	public void setInputStyleClass(String inputStyleClass) {
		iInputStyleClass = inputStyleClass;
	}
	
	public void setRadioStyleClass(String radioStyleClass) {
		iRadioStyleClass = radioStyleClass;
	}
	
	public void setButtonStyleClass(String buttonStyleClass) {
		iButtonStyleClass = buttonStyleClass;
	}
	
	public void setHeaderColumnWidth(int headerColumnWidth) {
		iHeaderColumnWidth = headerColumnWidth;
	}
}