/*
 * $Id: ApplicationsBlock.java,v 1.4 2006/04/09 11:47:23 laddi Exp $
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
 * Last modified: $Date: 2006/04/09 11:47:23 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.4 $
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
		this.iwb = getBundle(iwc);
		this.iwrb = getResourceBundle(iwc);
		
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
		if (this.iHeaderStyleClass != null) {
			text.setStyleClass(this.iHeaderStyleClass);
		}
		return text;
	}
	
	protected Text getText(String string) {
		Text text = new Text(string);
		if (this.iTextStyleClass != null) {
			text.setStyleClass(this.iTextStyleClass);
		}
		return text;
	}
	
	protected Text getErrorText(String string) {
		Text text = new Text(string);
		if (this.iErrorTextStyleClass != null) {
			text.setStyleClass(this.iErrorTextStyleClass);
		}
		return text;
	}
	
	protected Link getLink(String string) {
		Link link = new Link(string);
		if (this.iLinkStyleClass != null) {
			link.setStyleClass(this.iLinkStyleClass);
		}
		return link;
	}
	
	protected InterfaceObject getInput(InterfaceObject input) {
		if (this.iInputStyleClass != null) {
			input.setStyleClass(this.iInputStyleClass);
		}
		return input;
	}
	
	protected GenericButton getButton(GenericButton button) {
		if (this.iButtonStyleClass != null) {
			button.setStyleClass(this.iButtonStyleClass);
		}
		return button;
	}
	
	protected RadioButton getRadioButton(RadioButton radioButton) {
		if (this.iRadioStyleClass != null) {
			radioButton.setStyleClass(this.iRadioStyleClass);
		}
		return radioButton;
	}
	
	protected CheckBox getCheckBox(CheckBox checkBox) {
		if (this.iRadioStyleClass != null) {
			checkBox.setStyleClass(this.iRadioStyleClass);
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
		return this.iwb;
	}
	
	/**
	 * @return Returns the iwrb.
	 */
	protected IWResourceBundle getResourceBundle() {
		return this.iwrb;
	}

	/**
	 * @param headerStyleClass The headerStyleClass to set.
	 */
	public void setHeaderStyleClass(String headerStyleClass) {
		this.iHeaderStyleClass = headerStyleClass;
	}
	
	/**
	 * @param linkStyleClass The linkStyleClass to set.
	 */
	public void setLinkStyleClass(String linkStyleClass) {
		this.iLinkStyleClass = linkStyleClass;
	}

	/**
	 * @param textStyleClass The textStyleClass to set.
	 */
	public void setTextStyleClass(String textStyleClass) {
		this.iTextStyleClass = textStyleClass;
	}
	/**
	 * @param cellpadding The cellpadding to set.
	 */
	public void setCellpadding(int cellpadding) {
		this.iCellpadding = cellpadding;
	}
	/**
	 * @param width The width to set.
	 */
	public void setWidth(String width) {
		this.iWidth = width;
	}
	
	public void setErrorTextStyleClass(String errorTextStyleClass) {
		this.iErrorTextStyleClass = errorTextStyleClass;
	}
	
	public void setInputStyleClass(String inputStyleClass) {
		this.iInputStyleClass = inputStyleClass;
	}
	
	public void setRadioStyleClass(String radioStyleClass) {
		this.iRadioStyleClass = radioStyleClass;
	}
	
	public void setButtonStyleClass(String buttonStyleClass) {
		this.iButtonStyleClass = buttonStyleClass;
	}
	
	public void setHeaderColumnWidth(int headerColumnWidth) {
		this.iHeaderColumnWidth = headerColumnWidth;
	}
}